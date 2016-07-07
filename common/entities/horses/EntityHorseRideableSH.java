package simplyhorses.common.entities.horses;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import simplyhorses.common.entities.ISteerableSH;

public class EntityHorseRideableSH extends EntityHorseTameSH implements ISteerableSH{

	/**used within the class to easily handle changes in riding speed*/
	private int speedMode;
	
	/**Refers to whether the horse should move at its custom (currently breed-specific) gallop speed, as opposed to the base gallop speed*/
	private boolean speeding;
	
	/**Used to get the horse backing up, obviously. Set true by handleSpeedDown(). Set false by handlehardStop()*/
	private boolean reversing;
	
	private boolean wasStopped;
	private boolean turningL;
	private boolean turningR;
	
	private boolean lockHeading;

	private int jumpTicks;
	
	public EntityHorseRideableSH(World world) {
		super(world);
		speedMode = 0;
		speeding = false;
		reversing = false;
		wasStopped = false;
		turningL = false;
		turningR = false;
		jumpTicks = 0;
		gallopBonus = 0.4F;
		lockHeading = false;
		
		ignoreFrustumCheck = true;
	}
	
	@Override
    public void onLivingUpdate()
    {
		if (jumpTicks > 0){
			jumpTicks--;
		}
		
		if (!isTacked() && riddenByEntity != null){
			riddenByEntity.mountEntity(this);
		}
		
		if (!worldObj.isRemote && riddenByEntity != null && riddenByEntity instanceof EntityPlayer){
			onRiddenUpdate();
		}
		
		super.onLivingUpdate();
    }

	@Override
	public boolean interact(EntityPlayer entityplayer)
    {
		if ((isTacked() || entityplayer.capabilities.isCreativeMode) && !this.worldObj.isRemote && entityplayer.isSneaking() && (this.riddenByEntity == null || this.riddenByEntity == entityplayer))
        {
        	entityplayer.capabilities.isFlying = false;
			getNavigator().clearPathEntity();
			setFree();
			
			entityplayer.mountEntity(this);
			handleHardStop(0);
			return true;
        }
        else
        {
            return super.interact(entityplayer);
        }
    }
	
	@Override
	public void fall(float f){
		super.fall(f - 2);
	}
	
	public boolean isBreedTack(ItemStack itemstack){
		return itemstack.itemID == Item.saddle.itemID;
	}
	
	public Item getBreedTack(){
		return Item.saddle;
	}
	
	public boolean isWorkingIdle(){
    	return riddenByEntity != null;
    }
	
	public boolean isWorkingActive(){
		return getSpeedMode() != 0;
	}
	
	protected void quitWorking()
    {
		EntityPlayer rider = null;
				
		if (riddenByEntity != null && riddenByEntity instanceof EntityPlayer){
			rider = (EntityPlayer) riddenByEntity;
			riddenByEntity.mountEntity(this);
			handleHardStop(0);
		}
		
		if (rider != null){
			rider.addVelocity(-MathHelper.sin((rotationYaw * (float)Math.PI) / 180F) * 2F * 0.5F, 0.3D, MathHelper.cos((rotationYaw * (float)Math.PI) / 180F) * 2F * 0.5F);
		}
    }

	public boolean givesArrowBonus() {
		return false;
	}
	
	//Start Riding Logic
	/**Updates the horse for steering while being ridden by a player*/
	public void onRiddenUpdate() {
		EntityPlayer thisRider = (EntityPlayer)riddenByEntity;
		
		if (lockHeading){
			if (turningL && !turningR){
				rotationYaw = MathHelper.wrapAngleTo180_float(rotationYaw - 5);
			} else if (turningR && !turningL){
				rotationYaw = MathHelper.wrapAngleTo180_float(rotationYaw + 5);
			} else if (turningL && turningR){
				handleHardStop(0);
			}
		} else{ 
			float var3 = MathHelper.wrapAngleTo180_float(thisRider.rotationYaw - this.rotationYaw) * 0.5F;
	        
	        if (var3 > 5.0F)
	        {
	            var3 = 5.0F;
	        }

	        if (var3 < -5.0F)
	        {
	            var3 = -5.0F;
	        }

	        this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw + var3);
		}
		
		float speed = 0.0F;
		
		int mode = speedMode;
		
		switch(mode){
		case 0:
			return;
		case 1:
			speed = isReversing()? -0.15F: 0.15F;
			break;
		case 2:
			speed = 0.25F;
			
			if (isInWater()){
				speed = 0.2F;
			}
			break;
		case 3:
			speed = 0.4F;
			break;
		case 4:
			speed = getGallopBonus();
			break;
		default:
			speedMode = 0;
			return;
		}
		
		if (isSprinting() && isInWater()){
    		speedMode = 3;
    		handleSpeedDown();
    	}
		
		float f1 = (rotationYaw * (float)Math.PI) / 180F;
        float f2 = MathHelper.sin(f1);
        float f3 = MathHelper.cos(f1);
        
        //this.getNavigator().clearPathEntity();
		super.moveEntity(speed * -f2, 0.1, speed * f3);
	}
	
	public boolean shouldRiderFaceForward(EntityPlayer player)
    {
        return true;
    }
	
	/*public void moveEntity(double d, double d1, double d2)
    {
		if (speedMode > 0 && !worldObj.isRemote){
			//if (isCollidedHorizontally && speedMode > 1) handleHardStop(1);
			
			if (turningL && !turningR){
				rotationYaw -= isReversing()? 3: isWalking()? 5: 8;
			} else if (turningR && !turningL){
				rotationYaw += isReversing()? 3: isWalking()? 5: 8;
			} else if (turningL && turningR){
				handleHardStop(0);
			}
			
			float f1 = (rotationYaw * (float)Math.PI) / 180F;
	        float f2 = MathHelper.sin(f1);
	        float f3 = MathHelper.cos(f1);
	        
	        //Adjust for terrain friction
        	int j = worldObj.getBlockId(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY) - 1, MathHelper.floor_double(posZ));
        	float f = 1;
        	if (j == Block.ice.blockID){
        		f = Block.blocksList[j].slipperiness * 0.3F;
        	}
	        
        	//Determine movement and speed
        	if (isSprinting() && onGround){
        		if (isSpeeding()){
        			motionX += getGallopBonus() * f * -f2;
        			motionZ += getGallopBonus() * f * f3;
        		}
        		else{
        			motionX += 0.3D * f * -f2;
        			motionZ += 0.3D * f * f3;
        		}
        	}
        	else if(isSprinting() && !isInWater()){
        		if (isSpeeding()){
        			motionX += 0.05 * -f2;
        			motionZ += 0.05 * f3;
        		}
        		else{
        			motionX += 0.05 * -f2;
        			motionZ += 0.05 * f3;
        		}
        	}
        	else if (isWalking() && onGround){
        		if (isReversing()){
        			motionX += -0.03D * f * -f2;
    	        	motionZ += -0.03D * f * f3;
        		}
        		else{
        			motionX += 0.05D * f * -f2;
    	        	motionZ += 0.05D * f * f3;
        		}
        	}
        	else if (isWalking() && !isInWater()){
    			motionX += 0.01 * -f2;
    			motionZ += 0.01 * f3;
        	}
        	else if (onGround){
        		motionX += 0.1D * f * -f2;
    	        motionZ += 0.1D * f * f3;
        	}
        	else if (!isInWater()){
        		motionX += 0.05D * f * -f2;
    	        motionZ += 0.05D * f * f3;
        	}
        	
        	//Adjust speed for water
        	if (isInWater()){
        		motionX += 0.01 * f * -f2;
        		motionZ += 0.01 * f * f3;
        	}
        	
        	//Switch speedmodes in material that should slow movement
        	if (isSprinting() && isInWater()){
        		speedMode = 3;
        		handleSpeedDown();
        	}
        	
        	//ditto ^
        	if (isInWeb){
        		if (speedMode > 1){
        			speedMode = 2;
            		handleSpeedDown();
        		}
        		isInWeb = false;
        		motionX *= 0.7F;
        		motionZ *= 0.7F;
        	}
        	
        	//Submit!
            super.moveEntity(motionX, motionY, motionZ);
		}
		else{
			super.moveEntity(d, d1, d2);
		}
    }*/
	
	/**Called from packetHandler. Determines what speed the horse should move up to*/
	public void handleSpeedUp(){
		switch(speedMode){
		case 0:
			setWalking(true);
			speedMode++; //speedMode = 1: horse is now walking
			break;
		case 1:
			setWalking(false);
			setSprinting(false);
			speedMode++; //speedMode = 2: horse is now trotting
			break;
		case 2:
			setSprinting(true);
			speedMode++; //speedMode = 3: horse is now galloping
			break;
		case 3:
			setSpeeding(true);
			speedMode++; //speedMode = 4; horse is now galloping, but faster
			break;
		default:
			break;
		}
	}
	
	/**Called from packetHandler. Determines if the horse should slow down or reverse*/
	public void handleSpeedDown(){
		switch(speedMode){
		case 4:
			setSpeeding(false);
			speedMode--; //speedMode = 3; horse is now galloping more slowly
			break;
		case 3:
			setSprinting(false);
			setWalking(false);
			speedMode--; //speedMode = 2; horse is now trotting
			break;
		case 2:
			setWalking(true);
			speedMode--; //speedMode = 1; horse is now walking
			break;
		case 1:
			speedMode--; //speedMode = 0; horse has stopped
			break;
		case 0:
			setReversing(true);
			speedMode++; //speedMode = 1; horse is walking backwards
			break;
		default:
			break;
		}
	}
	
	public void handleReversing(){
		if (speedMode == 1 && isReversing()){
			setWalking(true);
			setReversing(false);
			setSprinting(false);
			speedMode = 0;
		}
	}
	
	public void handleHardStop(){};
	
	public void handleHardStop(int par1){
		if (!(par1 == 0 || par1 == 1)) par1 = 0;
		
		if (speedMode > 0){
			setWalking(true);
			setReversing(false);
			setSprinting(false);
			speedMode = par1;
		}
	}
	
	public void handleTurnLeft(Boolean flag){
		if (flag){
			if (speedMode == 0){
				wasStopped = true;
				handleSpeedUp();
			}
			
			turningL = true;
		}
		else{
			turningL = false;
			if (wasStopped){
				wasStopped = false;
				handleSpeedDown();
			}
		}
	}
	
	public void handleTurnRight(Boolean flag){		
		if (flag){
			if (speedMode == 0){
				wasStopped = true;
				handleSpeedUp();
			}
			
			turningR = true;
		}
		else{
			turningR = false;
			if (wasStopped){
				wasStopped = false;
				handleSpeedDown();
			}
		}
	}
	
	public void handleDropping(Boolean flag){}
	
	public void handleJumping(Boolean flag){
		if (!worldObj.isRemote){
			jump(true);
		}
	}
	
	/**Handles auto-jumping and manual jumping*/
	public void jump(Boolean flag){ //boolean true = 2.5-high jump. false = normal jump.
		if (!worldObj.isRemote){
	    	if (onGround && jumpTicks == 0){
	    		super.jump();
	    		if (flag){
	    			motionY += 0.2;
	    		}
	    		
	    		jumpTicks = 10;
	    	}
		}
	}

	private boolean isSpeeding() {
		return speeding;
	}

	private void setSpeeding(boolean flag) {
		speeding = flag;
	}

	private boolean isReversing() {
		return reversing;
	}

	private void setReversing(boolean flag) {
		reversing = flag;
	}
	
	public int getSpeedMode(){
		return speedMode;
	}

	/**when lockHeading is true, the horse is steering by keys instead of by the player's rotation
	 * a boolean value of 'true' is only used if this method is called from the packet handler,
	 * when a user wants to toggle between steering modes.*/
	public void LockHeading(boolean flag) {
		if (flag && lockHeading){
			unLockHeading();
			return;
		}
		
		lockHeading = true;
	}
	
	public void unLockHeading(){
		lockHeading = false;
	}
	
	
}
