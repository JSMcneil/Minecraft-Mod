package simplyhorses.common.entities.horses;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import simplyhorses.common.SimplyHorses;
import simplyhorses.common.entities.IDraughtEntitySH;
import simplyhorses.common.entities.vehicles.DraughtHelper;
import simplyhorses.common.entities.vehicles.EntitySwingletreeSH;
import simplyhorses.common.entities.vehicles.EntityWhippletreeSH;

public class EntityHorseDraughtSH extends EntityHorseTameSH implements IDraughtEntitySH {

	private EntityWhippletreeSH activeWhippletree;
	
	public EntityHorseDraughtSH(World world) {
		super(world);
		
		activeWhippletree = null;
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		if (activeWhippletree != null){
			if (activeWhippletree.isDead || !activeWhippletree.isEntityInWhippletree(this)){
				activeWhippletree = null;
			}
			
			else {
				rotationYawHead = activeWhippletree.rotationYaw;
				
				if (!worldObj.isRemote){
					updateGaits(activeWhippletree.getDraughtHelper());
				}
			}
		}
	}
	
	@Override
	public void fall(float f){
		if (this.isWorking()){
			return;
		} else{
			super.fall(f);
		}
	}
	
	//TODO test being called from whippletree vs not being called at all
	public void fallWorking(float f){
		super.fall(f);
	}
	
	public void updateGaits(DraughtHelper draughtHelper){
		switch (draughtHelper.getSpeedMode()){
		case 1: //Slow (walk)
			if(!isSneaking()){
                setWalking(true);
        	}
			break;
		case 2: //Medium (trot)
			if(isSneaking() || isSprinting()){
                setTrotting(true);
        	}
			break;
		case 3: //Fast (gallop)
			if(!isSprinting()){
                setSprinting(true);
        	}
			break;
		default:
			if(isSneaking() || isSprinting()){
                setTrotting(true);
        	}
		}
	}

	@Override
	public boolean handleTrainHook(Entity entity) {
		if (!worldObj.isRemote && entity instanceof EntityHorseDraughtSH && ((EntityHorseDraughtSH) entity).isTacked()){
			setUpWhippletree((EntityHorseDraughtSH) entity);
			return true;
		}
		
		return false;
	}
	
	public void setUpWhippletree(EntityHorseDraughtSH draughthorse) {
		if (draughthorse == this){
			this.setFree();
			
			EntitySwingletreeSH swingle = new EntitySwingletreeSH(worldObj, this);
			swingle.setPositionAndRotation(posX, posY, posZ, 0, 0);
			
			this.setActiveWhippletree(swingle);
			
			worldObj.spawnEntityInWorld(swingle);
			
			swingle.moveEntityWithHeading(0.0F, 0.5F);
		}else {
			this.setFree();
			draughthorse.setFree();
			
			if (worldObj.isRemote || draughthorse == null){
				return;
			}
			
			int intX = (int) ((posX + draughthorse.posX)/2);
			int intY = (int) ((posY + draughthorse.posY)/2);
			int intZ = (int) ((posZ + draughthorse.posZ)/2);
			
			EntityWhippletreeSH whippletree = new EntityWhippletreeSH(worldObj, this, draughthorse);
			whippletree.setLocationAndAngles(intX, intY, intZ, 0, 0);
			
			this.activeWhippletree = whippletree;
			draughthorse.activeWhippletree = whippletree;
			
			worldObj.spawnEntityInWorld(whippletree);
			
			whippletree.moveEntityWithHeading(0.0F, 0.5F);
		}
	}
	
	public void freeFromWhippletree(){
		activeWhippletree = null;
	}
	
	@Override
	public void applyEntityCollision(Entity par1Entity)
    {
		if (par1Entity instanceof EntityWhippletreeSH){
			if (((EntityWhippletreeSH) par1Entity).isEntityInWhippletree(this)){
				setActiveWhippletree((EntityWhippletreeSH) par1Entity);
				return;
			}
		}
		
		if (activeWhippletree != null){
			if(par1Entity == activeWhippletree){
				return;
			}
			
			if(par1Entity instanceof EntityHorseDraughtSH && ((EntityHorseDraughtSH) par1Entity).activeWhippletree == this.activeWhippletree){
				return;
			}
		}
		
		super.applyEntityCollision(par1Entity);
    }
	
	public void doTrample(Entity entity){
		if (activeWhippletree != null && entity instanceof EntityHorseDraughtSH){
			if (activeWhippletree.getDraughtHelper().isHorseInDraughtTeam((EntityHorseDraughtSH) entity)){
				return;
			}
		}
		
		super.doTrample(entity);
	}

	public boolean isBreedTack(ItemStack itemstack){
		return itemstack.itemID == SimplyHorses.harness.itemID;
	}
	
	public Item getBreedTack(){
		return SimplyHorses.harness;
	}
	
	@Override
	public boolean isWorkingIdle(){
		return activeWhippletree != null;
	}
	
	@Override
	public boolean isWorkingActive(){
		return isWorkingIdle() && activeWhippletree.getDraughtHelper().isMoving();
	}
	
	protected void quitWorking()
    {
		System.out.println("Quitting!");
		if (activeWhippletree != null){
			activeWhippletree.getDraughtHelper().findSpeedAllowance();
		}
    }

	public EntityWhippletreeSH getActiveWhippletree() {
		return activeWhippletree;
	}

	public void setActiveWhippletree(EntityWhippletreeSH activeWhippletree) {
		this.activeWhippletree = activeWhippletree;
	}

}
