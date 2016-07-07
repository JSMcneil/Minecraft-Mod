package simplyhorses.common.entities.vehicles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.src.SimplyID;
import net.minecraft.src.SparrowAPI;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import simplyhorses.common.SimplyHorses;
import simplyhorses.common.entities.IDraughtEntitySH;
import simplyhorses.common.entities.ILassoableSH;
import simplyhorses.common.entities.horses.EntityHorseDraughtSH;

public class EntityDraughtTechSH extends EntityNonLiving implements SparrowAPI, IDraughtEntitySH{

	private String simplyID;
	private Entity tiedToEntity;
	private DraughtHelper draughtHelper;
	
	public EntityDraughtTechSH(World par1World) {
		super(par1World);
		
		stepHeight = 1.0F;
		
        simplyID = "noID";
		tiedToEntity = null;
		draughtHelper = new DraughtHelper(this);
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();

		if (simplyID.equals("noID")){
			simplyID = worldObj.isRemote? "0": SimplyID.getNextSimplyID(this);
			if (!simplyID.equals("0")) System.out.println(this + ": " + simplyID);
		}
		
		if (draughtHelper.shouldBeSavedBy(this)){
			draughtHelper.onDraughtUpdate();
		}
		
	}
	
	public boolean draughtHalted(){
		return false;
	}
	
	@Override
	public boolean canDespawn(){
		return false;
	}
	
	@Override
	public void setDead(){
		if (!worldObj.isRemote){
			SimplyID.deleteID(worldObj, simplyID);
		}
		
		super.setDead();
	}
	
	/**Finds an entity based on the UUID passed in*/
	public Entity findEntity(UUID id)
    {
        for (int i = 0; i < worldObj.loadedEntityList.size(); i++)
        {
            Entity entity = (Entity)worldObj.loadedEntityList.get(i);

            if (entity != null && entity.getPersistentID() != null && entity.getDistanceToEntity(this) <= 50F && entity.getPersistentID().equals(id)){
            	return entity;
            }
        }
        
        System.out.println("findEntity: Found null entity");
        return null;
    }
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		
		nbttagcompound.setString("SimplyID", simplyID);
		
		if (draughtHelper.shouldBeSavedBy(this)){
			nbttagcompound.setTag("Assembly", this.draughtHelper.writeToNBT(new NBTTagList()));
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		
		simplyID = nbttagcompound.getString("SimplyID");
		
		if (nbttagcompound.hasKey("Assembly")){
			draughtHelper.readFromNBT(nbttagcompound.getTagList("Assembly"));
		}
	}
	
	public DraughtHelper getDraughtHelper() {
		return draughtHelper;
	}

	public void setDraughtHelper(DraughtHelper draughtHelper) {
		this.draughtHelper = draughtHelper;
	}
	
	//IDraughtEntitySH
	@Override
	public boolean handleTrainHook(Entity par1Entity) {
		if (par1Entity == this){
			draughtHelper.splitAfter(this);
			releaseFromLasso();
			return true;
		}
		
		if (par1Entity != this && par1Entity instanceof EntityDraughtTechSH){
			tiedToEntity = par1Entity;
			draughtHelper.incorporate((EntityDraughtTechSH) par1Entity);
			return true;
		}

		if (par1Entity instanceof EntityPlayer){
			tiedToEntity = par1Entity;
			return true;
		}
		
		return false;
	}

	//ILassoableSH
	@Override
	public boolean handleLasso(Entity par1Entity) {
		
		if (par1Entity instanceof EntityPlayer){
			tiedToEntity = par1Entity;
			return true;
		}
		
		return false;
	}

	@Override
	public Entity getTiedToEntity() {
		return tiedToEntity;
	}

	@Override
	public void setTiedToEntity(Entity par1Entity) {
		tiedToEntity = par1Entity;
	}

	@Override
	public void releaseFromLasso() {
		tiedToEntity = null;
	}
	
	@Override
	public double[] getLassoHookPosition(){
    	float f1 = (rotationYaw * (float)Math.PI) / 180F;
        float f2 = MathHelper.sin(f1);
        float f3 = MathHelper.cos(f1);
        
        int i = MathHelper.floor_double(posX - f2 * 1);
        int j = MathHelper.floor_double(posY);
        int k = MathHelper.floor_double(posZ + f3 * 1);
    	
        //TODO Return an actual hook position or sum'n
    	return new double[]{posX, posY, posZ};
    }
	
	@Override //TODO remove before releasing
	public void moveEntity(double x, double y, double z){
		if (draughtHelper != null && draughtHelper.shouldBeSavedBy(this) && draughtHelper.getSpeedMode() > 0){
			//System.out.println(this.entityId + ": " + x + ", " + y + ", " + z);
		}
		
		super.moveEntity(x, y, z);
	}

	//Start SparrowAPI
	public String getSimplyID() {
		return simplyID;
	}
	
	public void setSimplyID(String string){
		simplyID = string == null || string.equals("")? "noID": string;
	}
	
	@Override
	public boolean isStupidToAttack() {
		return true;
	}

	@Override
	public boolean doNotVaporize() {
		return true;
	}

	@Override
	public boolean isPredator() {
		return false;
	}

	@Override
	public boolean isHostile() {
		return false;
	}

	@Override
	public boolean isPeaceful() {
		return true;
	}

	@Override
	public boolean isPrey() {
		return false;
	}

	@Override
	public boolean isNeutral() {
		return true;
	}

	@Override
	public boolean isUnkillable() {
		return true;
	}

	@Override
	public boolean isThreatTo(Entity par1entity) {
		return false;
	}

	@Override
	public boolean isFriendOf(Entity par1entity) {
		return false;
	}

	@Override
	public boolean isNPC() {
		return false;
	}

	@Override
	public int isPet() {
		return 0;
	}

	@Override
	public Entity getPetOwner() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Entity getAttackingTarget() {
		return null;
	}

	@Override
	public float getSize() {
		return 0;
	}

	@Override
	public String getSpecies() {
		return "Vehicle";
	}

	@Override
	public int getGender() {
		return 0;
	}

	@Override
	public String customStringAndResponse(String s) {
		return null;
	}

}
