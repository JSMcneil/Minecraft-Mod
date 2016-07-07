package simplyhorses.common.entities.horses;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Random;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import simplyhorses.common.PacketHandlerSH;
import simplyhorses.common.SimplyHorses;
import simplyhorses.common.entities.ILassoableSH;
import simplyhorses.common.items.ItemLassoSH;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.src.*;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityHorseSH extends EntityAnimal implements SparrowAPI, ILassoableSH, IEntityAdditionalSpawnData
{
    private Entity tiedToEntity;
    private EntityFoalSH itsFoal;
    private ChunkCoordinates hitchCoordinates;
    private EntityAIGrazeSH entityaigraze = new EntityAIGrazeSH(this);
    private EntityAIHitchSH entityaihitch = new EntityAIHitchSH(this);
    private CoatHelper coatHelper = new CoatHelper(this);
    
    /**1 = Wild Horse, 2 = Mustang, 3 = Arabian, 4 = Clydesdale*/
    protected int breed;
    protected int grazeTimer;
    
    protected int foodWeight;
    protected boolean didHeal;
    protected double energy;
    
    private String simplyID;
    private String tiedToEntityID;
    
    /**Holds the final health from last tick to compare to final health from this tick.
     * Used to change the health DataWatcher.*/
    private int prevHealth2;
    private double prevEnergy;
    
    //TODO temp texture fix
    public int textureSlots;

    public EntityHorseSH(World world)
    {
        super(world);
        setSize(0.85F, 1.5F);
        
        simplyID = "noID";
        tiedToEntityID = "noID";
        
        health = getMaxHealth();
        prevHealth2 = 1;
        
        stepHeight = 1.1F;
        
        energy = getMaxEnergy();
        prevEnergy = 1;
        
        tiedToEntity = null;
        itsFoal = null;
        hitchCoordinates = null;
        
        grazeTimer = 0;
        breed = 0;
        
        textureSlots = getTextureSlots();
        
        getNavigator().setAvoidsWater(true);
        
        dataWatcher.addObject(15, new Integer(getMaxHealth())); //Health
        dataWatcher.addObject(16, new Integer(getMaxEnergy())); //Energy
        dataWatcher.addObject(18, new Integer(1)); //isFree()
        
        //TODO temp texture fix
        dataWatcher.addObject(21, new Integer(rand.nextInt(textureSlots)));
        tasks.addTask(0, new EntityAIFordWaterSH(this));
        tasks.addTask(1, new EntityAIPanic(this, 0.38F));
        tasks.addTask(2, new EntityAIMateHorsesSH(this, 0.2F));
        tasks.addTask(3, new EntityAIWanderSH(this, 0.2F));
        tasks.addTask(4, new EntityAIWatchClosestSH(this, EntityPlayer.class, 6.0F));
        tasks.addTask(5, new EntityAILookIdleSH(this));
        tasks.addTask(6, entityaigraze);
        tasks.addTask(7, entityaihitch); //Done... Sort of.
        tasks.addTask(8, new EntityAIFollowLassoSH(this, 0.325F, 3F, 3.5F));
    }
    
    @SideOnly(Side.CLIENT)
	public String getTexture()
	{
    	String id = textureID() == 0? "": "" + textureID();
    	
    	if (this instanceof EntityFoalSH){
    		return "/mods/SimplyHorses/textures/horses/foal" + id + ".png";
    	}
    	
    	switch(breed){
    	case 1:
    		return "/mods/SimplyHorses/textures/horses/mustang" + id + ".png";
    	case 2:
    		return "/mods/SimplyHorses/textures/horses/mustang" + id + ".png";
    	case 3:
    		return "/mods/SimplyHorses/textures/horses/arabian" + id + ".png";
    	case 4:
    		return "/mods/SimplyHorses/textures/horses/clydesdale" + id + ".png";
    	}
    	
    	return "/mods/SimplyHorses/textures/horses/mustang.png";
    	
    	//return "/mods/SimplyHorses/textures/horses/testing/horseGeneric_base.png";
	}
    
    //TODO temp texture fix
    public int getTextureSlots(){
    	return 81;
    }
    
    public int textureID(){
    	return dataWatcher.getWatchableObjectInt(21);
    }
    
    public void textureIDSet(int i){
    	dataWatcher.updateObject(21, i);
    }
    
    @Override
    public boolean isAIEnabled()
    {
        return true;
    }

    @Override
    protected void updateAITasks()
    {
		grazeTimer = entityaigraze.getgCountdown();
		
        if (!isWorking() && !getNavigator().noPath())
        {
            float var1 = this.getMoveHelper().getSpeed();

            if (var1 >= 0.4F) //if should be galloping
            {
            	if(!isSprinting()){
                    setSprinting(true);
            	}
            }
            else if (var1 <= 0.2F) //if should be walking (technically sneaking)
            {
            	if(!isSneaking()){
                    setWalking(true);
            	}
            }
            else //if should be trotting
            {
            	if(isSneaking() || isSprinting()){
                    setTrotting(true);
            	}
            }
        } else if (!isWorking() && getNavigator().noPath()){
        	setWalking(true);
        }
		
        super.updateAITasks();
    }

    @Override
    public int getMaxHealth()
    {
        return 10 * (SimplyHorses.tfcON? 50: 1);
    }

    public int getMaxEnergy()
    {
        return 10;
    }

    @Override
    protected boolean canDespawn()
    {
        return false;
    }
    
    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        
        //Assign an ID serverside
        if (simplyID.equals("noID"))
        {
        	setSimplyID(!worldObj.isRemote? SimplyID.getNextSimplyID(this): "0");
        	if (!simplyID.equals("0")) System.out.println(this + ": " + simplyID);
        }

        //Reset tiedToEntity after world is reloaded
        if (tiedToEntity == null && !tiedToEntityID.equals("noID"))
        {
            setTiedToEntity((Entity) findEntity(tiedToEntityID));
            tiedToEntityID = "noID";
        }
        
        //Do free checks
        if (!worldObj.isRemote && !isFree() && dataWatcher.getWatchableObjectInt(18) == 1){
        	dataWatcher.updateObject(18, 0);
        }
        
        // ^
        if (!worldObj.isRemote && isFree() && dataWatcher.getWatchableObjectInt(18) == 0){
        	dataWatcher.updateObject(18, 1);
        }
        
        //Check for dead tracked things
        if (itsFoal != null && itsFoal.isDead){
        	itsFoal = null;
        }
        
        // ^
        if (tiedToEntity != null && tiedToEntity.isDead){
        	setFree();
        }
        
        //Check if should be free
        if (tiedToEntity == this && !isHitched()){
        	setFree();
        }

        //Countdown for clientside grazing animations!
        if (worldObj.isRemote){
        	grazeTimer = Math.max(0, grazeTimer - 1);
        }
        
        //Re-check health for rendering purposes. Health can overrun due to grazing.
        if (health > getMaxHealth()){
        	health = getMaxHealth();
        }
        
        //Do health and energy checks for clientside rendering.
        if (!worldObj.isRemote){
        	if (dataWatcher.getWatchableObjectInt(15) != getHealth()){
     	       dataWatcher.updateObject(15, getHealth());
        	}
        	
        	if ((double) dataWatcher.getWatchableObjectInt(16)/100 != getEnergy()){
        		int dwEnergy = (int) (getEnergy() * 100);
        		dataWatcher.updateObject(16, dwEnergy);
        	}
        }
    }

    @Override
    public boolean interact(EntityPlayer entityplayer)
    {
    	if (super.interact(entityplayer)){
    		return true;
    	}
    	
    	ItemStack itemstack = entityplayer.getCurrentEquippedItem();
    	
		if(itemstack != null && isFeed(itemstack)){
			getFoodWeight(itemstack);
			
			if (health < getMaxHealth()){ 
				heal(foodWeight);
				didHeal = true;
			}
			else{
				didHeal = false;
			}
			
			return true;
		}
		
		return false;
    }
    
	public SparrowAPI findEntity(String par1string)
    {
        for (int i = 0; i < worldObj.loadedEntityList.size(); i++)
        {
            Entity entity = (Entity)worldObj.loadedEntityList.get(i);

            if ((entity instanceof SparrowAPI) && entity.getDistanceToEntity(this) <= 50F && ((SparrowAPI)entity).getSimplyID().equals(par1string))
            {
                return (SparrowAPI) entity;
            }
        }
        
        return null;
    }

	/**Transfers important stats and values to a new horse*/
    public void transferStatsTo(EntityHorseSH entityhorse)
    {
        entityhorse.setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
        entityhorse.renderYawOffset = renderYawOffset;
        
        //TODO temp texture fix
        if (entityhorse.breed == this.breed && entityhorse.isChild() == this.isChild()){
        	entityhorse.textureIDSet(this.textureID());
        }

        if (itsFoal != null)
        {
            entityhorse.setItsFoal(itsFoal);
            itsFoal.setMother(entityhorse);
        }

        entityhorse.setHitchCoordinates(hitchCoordinates);
        entityhorse.setTiedToEntity(tiedToEntity == this? null: getTiedToEntity());
    }

    protected void gUpdate()
    {
        heal(2);
    }

    public boolean handleLasso(Entity par1Entity)
    {
    	getNavigator().clearPathEntity();
    	
    	if (par1Entity != null && par1Entity == this){
    		setFree();
    	}
    	
    	if (par1Entity == null){
	        EntityPlayer entityplayer = worldObj.getClosestPlayerToEntity(this, 10D);
	        if (entityplayer != null){
	        	ItemStack itemstack = entityplayer.getCurrentEquippedItem();
		        
		        if (itemstack.itemID == SimplyHorses.lasso.itemID && entityplayer.getDistanceToEntity(this) <= 10F)
		        {
		            setTiedToEntity(entityplayer);
		            setHitchCoordinates(null);
		        }
	        }
    	}
    	else{
    		setTiedToEntity(par1Entity);
    	}
    	
    	return true;
    }

    public float healthPenalty()
    {
    	int healthWatcher = dataWatcher.getWatchableObjectInt(15);
    	
        if (healthWatcher > getMaxHealth())
        {
        	healthWatcher = getMaxHealth();
        }

        float f = 1.0F - (float)healthWatcher / (float)getMaxHealth();

        if (f < 0.0F)
        {
            return 0.0F;
        }
        else
        {
            return f;
        }
    }

    public float energyPenalty()
    {
    	double energyWatcher = (double) dataWatcher.getWatchableObjectInt(16)/100;
    	
        float f = 1.0F - (float)energyWatcher / (float)getMaxEnergy();

        if (f < 0.0F)
        {
            return 0.0F;
        }

        if (f > 1.0F)
        {
            return 1.0F;
        }
        else
        {
            return f;
        }
    }

    protected boolean getFoodWeight(ItemStack itemstack)
    {
        if (itemstack.itemID == SimplyHorses.sCubes.itemID)
        {
            foodWeight = 1;
            return true;
        }

        if (itemstack.itemID == Item.wheat.itemID)
        {
            foodWeight = 2;
            return true;
        }
        
        if (itemstack.itemID == Item.carrot.itemID){
        	foodWeight = 4;
        	return true;
        }

        if (itemstack.itemID == Item.appleRed.itemID)
        {
            foodWeight = 6;
            return true;
        }

        if (itemstack.itemID == Item.bread.itemID)
        {
            foodWeight = 10;
            return true;
        }
        
        if (itemstack.itemID == Item.cake.itemID)
        {
            foodWeight = 20;
            return true;
        }
        else
        {
            return false;
        }
    }

    protected boolean isFeed(ItemStack itemstack)
    {
        if (itemstack.itemID == SimplyHorses.sCubes.itemID)
        {
            return true;
        }

        if (itemstack.itemID == Item.wheat.itemID)
        {
            return true;
        }
        
        if (itemstack.itemID == Item.carrot.itemID) //Carrots
        {
        	return true;
        }

        if (itemstack.itemID == Item.appleRed.itemID)
        {
            return true;
        }

        if (itemstack.itemID == Item.bread.itemID)
        {
            return true;
        }

        return itemstack.itemID == Item.cake.itemID;
    }

    @Override
    public boolean isBreedingItem(ItemStack itemstack)
    {
        if (health == getMaxHealth() && energy == getMaxEnergy() && !(this instanceof EntityHorseWildSH) && !(this instanceof EntityFoalSH))
        {
            return itemstack.itemID == SimplyHorses.sCubes.itemID;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entity)
    {
        int i;

        if (entity instanceof EntityPlayer)
        {
            i = 2 + rand.nextInt(4 * (SimplyHorses.tfcON? 50: 1));
        }
        else
        {
            i = 1 + rand.nextInt(3 * (SimplyHorses.tfcON? 50: 1));
        }

        //TODO mobs should attack mustangs once armor is implemented or something idk.
        return entity.attackEntityFrom(DamageSource.generic, i);
    }
    
    /**Defined properly in EntityHorseDraughtSH and EntityHorseRideableSH*/
    public boolean isWorkingIdle(){
    	return false;
    }
    
    /**Defined properly in EntityHorseDraughtSH and EntityHorseRideableSH.*/
    public boolean isWorkingActive(){
    	return false;
    }
    
    public void showHeartsOrSmokeFX(boolean flag, int par1)
    {
    	if (!worldObj.isRemote){
    		if (this instanceof EntityHorseWildSH){
    			EntityHorseWildSH wildhorse = (EntityHorseWildSH) this;
    			if (!wildhorse.isFree() && !flag){
    				return;
    			}
    		}
    		
    		PacketHandlerSH.sendPacketParticleSpawn(this, flag? 1: 0, par1);
    		return;
    	}
    	
        String s = "heart";

        if (!flag)
        {
            s = "smoke";
        }

        for (int i = 0; i < par1; i++)
        {
            double d = rand.nextGaussian() * 0.02D;
            double d1 = rand.nextGaussian() * 0.02D;
            double d2 = rand.nextGaussian() * 0.02D;
            worldObj.spawnParticle(s, (posX + (double)(rand.nextFloat() * width * 2.0F)) - (double)width, posY + 0.5D + (double)(rand.nextFloat() * height), (posZ + (double)(rand.nextFloat() * width * 2.0F)) - (double)width, d, d1, d2);
        }
    }
    
    public void showExplosionParticle(){
    	if (!worldObj.isRemote){
    		PacketHandlerSH.sendPacketParticleSpawn(this, 3, 0);
    		return;
    	}
    	
    	worldObj.spawnParticle("largeexplode", posX, posY + (double)(height / 2.0F), posZ, 0.0D, 0.0D, 0.0D);
    	return;
    }

    @SideOnly(Side.CLIENT)
    public float nbSwing(float f)
    {
        if (grazeTimer <= 0)
        {
            return 0.4642F + 1.1066F * Math.max(healthPenalty(), energyPenalty());
        }

        if (grazeTimer >= 4 && grazeTimer <= 296)
        {
            return 2.3562F;
        }
        else
        {
            return 0.4642F + 1.1066F * Math.max(healthPenalty(), energyPenalty());
        }
    }

    @SideOnly(Side.CLIENT)
    public float ntSwing(float f)
    {
        if (grazeTimer <= 0)
        {
            return -0.4642F + -1F * Math.max(healthPenalty(), energyPenalty());
        }

        if (grazeTimer >= 4 && grazeTimer <= 296)
        {
            return -1.5708F;
        }
        else
        {
            return -0.4642F + -1F * Math.max(healthPenalty(), energyPenalty());
        }
    }

    @SideOnly(Side.CLIENT)
    public float headSwing(float f)
    {
        if (grazeTimer <= 0)
        {
            return 0.7854F + rotationPitch / (180F / (float)Math.PI);
        }

        if (grazeTimer >= 4 && grazeTimer <= 296)
        {
            float f1 = ((float)(grazeTimer - 4) - f) / 32F;
            return ((float)Math.PI / 5F) + ((float)Math.PI * 7F / 100F) * MathHelper.sin(f1 * 28.7F);
        }
        else
        {
            return 0.7854F + rotationPitch / (180F / (float)Math.PI);
        }
    }
    
    @Override
    public boolean canMateWith(EntityAnimal par1EntityAnimal)
    {
    	if (par1EntityAnimal == this) return false;
    	else if (!(par1EntityAnimal instanceof EntityHorseSH)) return false;
    	else if (isInLove() && par1EntityAnimal.isInLove()) return true;
    	
    	return false;
    }

    @Override
    public EntityAnimal createChild(EntityAgeable var1)
    {
    	EntityHorseSH entityhorse = (EntityHorseSH)var1;
        itsFoal = new EntityFoalSH(worldObj);

        if (rand.nextBoolean())
        {
            itsFoal.setBreed(getBreed());
        }
        else
        {
            itsFoal.setBreed(entityhorse.getBreed());
        }

        itsFoal.setMother(this);
        return itsFoal;
    }
    
    @Override
    public void setDead(){
    	if (!worldObj.isRemote){
    		SimplyID.deleteID(worldObj, getSimplyID());
    	}
    	super.setDead();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setString("MyID", getSimplyID());

        if (tiedToEntity != null)
        {
            if (tiedToEntity instanceof EntityHorseSH)
            {
                nbttagcompound.setString("LassoID", ((EntityHorseSH)tiedToEntity).getSimplyID());
            }

            /*if (tiedToEntity instanceof EntityWagonBase)
            {
                nbttagcompound.setString("LassoID", ((EntityWagonBase)tiedToEntity).getSimplyID());
            }*/
        }

        nbttagcompound.setDouble("Energy", energy);

        if (isHitched())
        {
        	nbttagcompound.setInteger("hitchX", this.hitchCoordinates.posX);
        	nbttagcompound.setInteger("hitchY", this.hitchCoordinates.posY);
        	nbttagcompound.setInteger("hitchZ", this.hitchCoordinates.posZ);
        }
        
        //TODO temp texture fix
        nbttagcompound.setInteger("textureID", textureID());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readEntityFromNBT(nbttagcompound);
        setSimplyID(nbttagcompound.getString("MyID"));
        energy = nbttagcompound.getDouble("Energy");
        
        if (nbttagcompound.hasKey("LassoID")){
        	tiedToEntityID = nbttagcompound.getString("LassoID");
        }

        if (nbttagcompound.hasKey("hitchX") && nbttagcompound.hasKey("hitchY") && nbttagcompound.hasKey("hitchZ"))
        {
            this.hitchCoordinates = new ChunkCoordinates(nbttagcompound.getInteger("hitchX"), nbttagcompound.getInteger("hitchY"), nbttagcompound.getInteger("hitchZ"));
        }
        
        //TODO temp texture fix
        if (nbttagcompound.hasKey("textureID")){
        	textureIDSet(nbttagcompound.getInteger("textureID"));
        }
    }

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		coatHelper.writeCoatData(data);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		coatHelper.readCoatData(data);
	}

    @Override
    protected String getLivingSound()
    {
        return null;
    }

    @Override
    protected String getHurtSound()
    {
        return null;
    }

    @Override
    protected String getDeathSound()
    {
        return null;
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.4F;
    }

    @Override
    protected int getDropItemId()
    {
        return 0;
    }

    @Override
    public boolean isMovementCeased()
    {
        return !isFree();
    }

    public String getSimplyID()
    {
        return simplyID;
    }

    private void setSimplyID(String id)
    {
        simplyID = id;
    }

    public EntityFoalSH getItsFoal()
    {
        return itsFoal;
    }

    public void setItsFoal(EntityFoalSH entityfoal)
    {
        itsFoal = entityfoal;
    }

    public Entity getTiedToEntity()
    {
        return tiedToEntity;
    }

    public void setTiedToEntity(Entity entity)
    {
    	tiedToEntity = entity;
    	
    	if (entity == null){
    		if (isFree()){
    			dataWatcher.updateObject(18, 1);
    		}
    	}
    	else{
    		setHitchCoordinates(null);
    		dataWatcher.updateObject(18, 0);
    	}
    	
    	if (!worldObj.isRemote){
        	PacketHandlerSH.sendPacketTiedToData(this, tiedToEntity, hitchCoordinates);
    	}
    }

    public ChunkCoordinates getHitchCoordinates()
    {
        return hitchCoordinates;
    }

    public void setHitchCoordinates(ChunkCoordinates coords)
    {
    	hitchCoordinates = coords;
    	
    	if (coords == null){
    		if (isFree()){
    			dataWatcher.updateObject(18, 1);
    		}
    	}
    	else{
    		setTiedToEntity(null);
        	dataWatcher.updateObject(18, 0);
        	entityaihitch.setHitched(this, coords);
    	}
    	
    	if (!worldObj.isRemote){
        	PacketHandlerSH.sendPacketTiedToData(this, tiedToEntity, hitchCoordinates);
    	}
    }
    
    public void releaseFromLasso(){
    	if (tiedToEntity != null && tiedToEntity instanceof EntityPlayer) setTiedToEntity(null);
    }
    
    protected void setFree()
    {
        setTiedToEntity(null);
        setHitchCoordinates(null);
        
        dataWatcher.updateObject(18, 1);
    }

    /**Horses are considered free if they are not currently lassoed, hitched, or working*/
    public boolean isFree()
    {
    	if (worldObj.isRemote){
    		return dataWatcher.getWatchableObjectInt(18) == 1;
    	}
    	
        return !isLassoed() && !isHitched() && !isWorking();
    }

    public boolean isLassoed()
    {
        return tiedToEntity != null;
    }

    public boolean isHitched()
    {
        return hitchCoordinates != null;
    }

    public boolean isWorking()
    {
        return isWorkingIdle();
    }

    public int getBreed()
    {
        return breed;
    }
    
    public void setBreed(int par1)
    {
    	if (par1 < 1 || par1 > 4) par1 = 2;
    	breed = par1;
    }
    
    public boolean isWalking(){
    	
    	
    	return isSneaking();
    }
    
    public double[] getLassoHookPosition(){
    	getLookHelper();
    	
    	float f1 = ((rotationYawHead) * (float)Math.PI) / 180F;
        float f2 = MathHelper.sin(f1);
        float f3 = MathHelper.cos(f1);
        
        double displacement = 0.75;
        double i = posX - f2 * displacement;
        double j = boundingBox.minY + height * 0.8D + 0.2D;
        double k = posZ + f3 * displacement;
        
    	return new double[]{i, j, k};
    }
    
    /**"walking" is synonymous with sneaking.*/
    public void setWalking(Boolean flag){
    	if (flag){
        	setSprinting(false);
        	setSneaking(true);
    	}
    	else{
    		setSneaking(false);
    	}
    	return;
    }
    
    /**Sprinting is the equivalent of galloping and is used in this way*/
    public void setSprinting(Boolean flag){
    	if (flag){
        	setSneaking(false);
        	setSprinting(true);
    	}
    	else{
    		setSprinting(false);
    	}
    	return;
    }
    
    /**"trotting" means neither walking (sneaking) nor sprinting. IE normal mob movement. Thus, this method sets both false if true and sets walking true if false*/
    public void setTrotting(Boolean flag){
    	if (flag){
    		setSprinting(false);
    		setWalking(false);
    	}
    	else{
    		setSprinting(false);
    		setWalking(true);
    	}
    }

    public double getEnergy(){
    	return energy;
    }

    public void setEnergy(double d)
    {
        energy = d;
    }
    
    public void setHealth(int i){
    	health = i;
    }
    
  //Start SparrowAPI interface

  	public boolean isStupidToAttack() {
  		return false;
  	}

  	public boolean doNotVaporize() {
  		return breed != 1;
  	}

  	public boolean isPredator() {
  		return false;
  	}

  	public boolean isHostile() {
  		return false;
  	}

  	public boolean isPeaceful() {
  		return true;
  	}

  	public boolean isPrey() {
  		return this instanceof EntityFoalSH && this.breed == 1;
  	}

  	public boolean isNeutral() {
  		return false;
  	}

  	public boolean isUnkillable() {
  		return false;
  	}

  	public boolean isThreatTo(Entity par1entity) {
  		return false;
  	}

  	public boolean isFriendOf(Entity par1entity) {
  		return par1entity instanceof EntityPlayer && !(this instanceof EntityHorseWildSH);
  	}

  	public boolean isNPC() {
  		return false;
  	}

  	public int isPet() {
  		switch(breed){
  		case 1:
  			return 0;
  		case 2:
  			return 2;
  		case 3:
  			return 2;
  		case 4: 
  			return 2;
  		case 5:
  			return 1;
  		}
  		
  		return 0;
  	}

  	public Entity getPetOwner() {
  		return null;
  	}

  	public String getName() {
  		return null;
  	}

  	public Entity getAttackingTarget() {
  		return null;
  	}

  	public float getSize() {
  		return 3.0F;
  	}

  	public String getSpecies() {
  		return "Horse";
  	}

  	public int getGender() {
  		return 0;
  	}

  	public String customStringAndResponse(String s) {
  		return null;
  	}

	public int getGrazeTimer() {
		return grazeTimer;
	}
	
	public void setGrazeTimer(int i) {
		grazeTimer = i;
	}

}
