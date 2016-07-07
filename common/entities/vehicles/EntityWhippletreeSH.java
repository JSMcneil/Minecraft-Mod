package simplyhorses.common.entities.vehicles;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import simplyhorses.common.PacketHandlerSH;
import simplyhorses.common.SimplyHorses;
import simplyhorses.common.entities.horses.EntityHorseDraughtSH;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityWhippletreeSH extends EntityDraughtTechSH implements IEntityAdditionalSpawnData{
	
	/**A two-index array that holds both horses controlled by this whippletree.
	 * [0] = left horse
	 * [1] = right horse*/
	protected EntityHorseDraughtSH[] horses;
	
	/**Populated by NBT, used to find and rehitch horses, and update the client via packet*/
	protected UUID[] loadList;
	
	/**Used when the world is reloaded to reassign horses*/
	protected boolean isLoaded;
	
	private int reloadTimer;
	
	public EntityWhippletreeSH(World par1World) {
		super(par1World);
        setSize(1.8F, 1.8F);
        
		isLoaded = false;
		reloadTimer = 0;
		createNewHorseArray();
	}
	
	public EntityWhippletreeSH(World world, EntityHorseDraughtSH horse, EntityHorseDraughtSH horse2){
		this(world);
		
		setHorses(new EntityHorseDraughtSH[]{horse, horse2});
		isLoaded = true;
		
	}
	
	@Override
    public String getTexture()
	{
		return "/mods/SimplyHorses/textures/vehicles/whippletree.png";
	}
	
	@Override
	public void onLivingUpdate(){
		
		if (!worldObj.isRemote && !isLoaded && loadList != null){
			loadHorses();
		}
		
		if (reloadTimer > 0){
			reloadTimer--;
			
			if (reloadTimer == 15){
				for (int i = 0; i < horses.length; i++){
					horses[i] = null;
				}
			}
			
			if (reloadTimer == 0){
				doubleCheckHorses();
			}
		}
		else for (int i = 0; i < horses.length; i++){ 
			if (horses[i] == null || horses[i].isDead){
				setDead();
				
				return;
			}
		}
		
		updateHorsePositions();

		super.onLivingUpdate();
	}
	
	/**Loads the UUIDs from loadList, finds the respective horses, and assigns them to this whippletree.*/
	public void loadHorses(){
		if (loadList == null){
			return;
		}
		
		setHorses(new EntityHorseDraughtSH[]{(EntityHorseDraughtSH) findEntity(loadList[0]), (EntityHorseDraughtSH) findEntity(loadList[1])});
		
		isLoaded = true;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, int par2){
		if (par1DamageSource.getEntity() instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) par1DamageSource.getEntity();
			
			if (!worldObj.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().itemID == SimplyHorses.lasso.itemID){
				setDead();
				return true;
			}
		}
		
		return super.attackEntityFrom(par1DamageSource, par2);
    }

	/**Called every tick from both sides to reposition the horses within the whippletree*/
	public void updateHorsePositions(){
		if (!isLoaded()){
			return;
		}
		
		float f1 = (rotationYaw * (float)Math.PI) / 180F;
        float f2 = MathHelper.sin(f1);
        float f3 = MathHelper.cos(f1);
        
		horses[0].setPositionAndRotation(posX + (double)(f3 * 0.625F) - (double)(f2 * 0.28F), posY, posZ + (double)(f2 * 0.625F) + (double)(f3 * 0.28F), rotationYaw, horses[0].rotationPitch);
		horses[0].rotationYaw = this.rotationYaw;
		
        horses[1].setPositionAndRotation(posX - (double)(f3 * 0.625F) - (double)(f2 * 0.28F), posY, posZ - (double)(f2 * 0.625F) + (double)(f3 * 0.28F), rotationYaw, horses[1].rotationPitch);
		horses[1].rotationYaw = this.rotationYaw;
	}
	
	@Override
	public void applyEntityCollision(Entity par1Entity)
    {
		if (isEntityInWhippletree(par1Entity)){
			return;
		}
		
		if (par1Entity.ridingEntity != null && isEntityInWhippletree(par1Entity.ridingEntity)){
			return;
		}
		
		super.applyEntityCollision(par1Entity);
    }
	
	/**Basically, checks the horsePair list to see if the passed in entity is indexed*/
	public boolean isEntityInWhippletree(Entity entity){
		for (int i = 0; i < horses.length; i++){
			if (horses[i] == entity){
				return true;
			}
		}
		
		return false;
	}
	
	/**Returns true if any of the indexes in horses[] are null*/
	public boolean isLoaded(){
		for (int i = 0; i < horses.length; i++){
			if (horses[i] == null){
				return false;
			}
		}
		
		return true;
	}
	
	/**Sets the new horse pair and sets the horses's whippletree variable to this*/
	public void setHorses(EntityHorseDraughtSH[] horseList){
		horses = horseList;
		
		for (int i = 0; i < horses.length; i++){
			if (horses[i] != null){
				horses[i].setActiveWhippletree(this);
			}
		}
		
		isLoaded = true;
		getDraughtHelper().findSpeedAllowance();
	}
	
	public EntityHorseDraughtSH[] getHorses(){
		return horses;
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		
		NBTTagList nbttaglist = new NBTTagList();
        for(int i = 0; i < horses.length; i++)
        {
            if(horses[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                if (horses[i].getPersistentID() != null)
                {
                	nbttagcompound1.setLong("Horse" + i + "PersistentIDMSB", horses[i].getPersistentID().getMostSignificantBits());
                	nbttagcompound1.setLong("Horse" + i + "PersistentIDLSB", horses[i].getPersistentID().getLeastSignificantBits());
                }
                nbttaglist.appendTag(nbttagcompound1);
            }
        }
        nbttagcompound.setTag("Horses", nbttaglist);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		
		NBTTagList nbttaglist = nbttagcompound.getTagList("Horses");
		loadList = getNewLoadList();
		if (nbttaglist.tagCount() > 0)
        for(int i = 0; i < loadList.length; i++)
        {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
            
            if (nbttagcompound1.hasKey("Horse" + i + "PersistentIDMSB") && nbttagcompound1.hasKey("Horse" + i + "PersistentIDLSB")){
            	loadList[i] = new UUID(nbttagcompound1.getLong("Horse" + i + "PersistentIDMSB"), nbttagcompound1.getLong("Horse" + i + "PersistentIDLSB"));
            }
        }
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		if (isLoaded()){
			for (int i = 0; i < horses.length; i++){
				data.writeInt(horses[i].entityId);
			}
		}
		else{
			for (int i = 0; i < horses.length; i++){
				System.out.println(this + ": Error writing spawn data! Pair not loaded!");
				data.writeInt(-1);
			}
		}
		
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		createNewHorseArray();
		
		int[] horseIDs = new int[horses.length];
		
		for (int i = 0; i < horseIDs.length; i++){
			horseIDs[i] = data.readInt();
		}
		
		EntityHorseDraughtSH[] newHorses = new EntityHorseDraughtSH[horseIDs.length];
		
		try{
			for (int i = 0; i < newHorses.length; i++){
				newHorses[i] = (EntityHorseDraughtSH) worldObj.getEntityByID(horseIDs[i]);
			}
		}catch(Exception e){
			System.out.println(this + ": Error reading spawn data.. IDs belong to nonhorses!");
			e.printStackTrace();
			return;
		}
		
		for (int i = 0; i < newHorses.length; i++){
			if (newHorses[i] == null){
				System.out.println(this + ": Error reading spawn data! Horse(s) not found!");
				reloadTimer = 20;
				
				return;
			}
		}
		
		setHorses(newHorses);
	}
	
	protected UUID[] getNewLoadList(){
		return new UUID[2];
	}

	private void doubleCheckHorses() {
		if (!isLoaded()){
			PacketHandlerSH.sendPacketWhippletreeRequest(this);
		}
	}

	public void respawn() {
		if (!worldObj.isRemote){
			EntityWhippletreeSH newWhippletree = new EntityWhippletreeSH(this.worldObj, this.horses[0], this.horses[1]);
			newWhippletree.setPositionAndRotation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
			
			getDraughtHelper().replaceTech(this, newWhippletree);
			setDead();
			worldObj.spawnEntityInWorld(newWhippletree);
		}
	}

	public float getHorsePower() {
		float power = 0.0F;
		
		for (int i = 0; i < horses.length; i++){
			if (horses[i] != null && horses[i].isTired()){
				power += 1;
			}
		}
		
		return power;
	}
	
	public void createNewHorseArray(){
		horses = new EntityHorseDraughtSH[2];
	}
}
