package simplyhorses.common.entities.horses;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockCloth;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import simplyhorses.common.SimplyHorses;
import simplyhorses.common.entities.vehicles.EntityNonLiving;

public class EntityHorseTameSH extends EntityHorseSH
{
	/** Yep, totally took this from the vanilla sheep source u mad bro?*/
	public static final float[][] gearColorTable = new float[][] {{1.0F, 1.0F, 1.0F}, {0.95F, 0.7F, 0.2F}, {0.9F, 0.5F, 0.85F}, {0.6F, 0.7F, 0.95F}, {0.9F, 0.9F, 0.2F}, {0.5F, 0.8F, 0.1F}, {0.95F, 0.7F, 0.8F}, {0.3F, 0.3F, 0.3F}, {0.6F, 0.6F, 0.6F}, {0.3F, 0.6F, 0.7F}, {0.7F, 0.4F, 0.9F}, {0.2F, 0.4F, 0.8F}, {0.5F, 0.4F, 0.3F}, {0.4F, 0.5F, 0.2F}, {0.8F, 0.3F, 0.3F}, {0.1F, 0.1F, 0.1F}};
	
	private int timeSinceExhaust;
    private EntityCreeper startler;
    protected float gallopBonus;

    public EntityHorseTameSH(World world)
    {
        super(world);
        timeSinceExhaust = 0;
        gallopBonus = 0.4F;
        startler = null;
        
        /*tacked. 0 = false, 1 = true*/
        dataWatcher.addObject(17, new Integer(0));
        /*Gear color. Corresponds directly to the color index in ItemDye*/
        dataWatcher.addObject(19, new Integer(1));
        
        tasks.addTask(9, new EntityAIPanicSH(this));
    }

    @Override
    public int getMaxHealth()
    {
        return 20;
    }

    public int getMaxEnergy()
    {
        return 20;
    }
    
    @Override
    public double getMountedYOffset()
	{
	    return (double)height * 0.9D;
	}

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (!worldObj.isRemote && isWorkingActive())
        {
            timeSinceExhaust++;

            if (timeSinceExhaust == 20)
            {
                addExhaustion();
                timeSinceExhaust = 0;
            }
        }

        if (energy <= 0.0D && energy > -0.04D)
        {
            quitWorking();
        }
        
        if (isSprinting()){ //initiates the Mustang Charge
			List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
	        if(list != null && list.size() > 0)
	        {
	            for(int i = 0; i < list.size(); i++)
	            {
	                Entity entity = (Entity)list.get(i);
	                if(entity.canBePushed() && entity != riddenByEntity)
	                {
	                    entity.applyEntityCollision(this);
	                    if(isSprinting())
	                    {
	                        doTrample(entity);
	                    }
	                }
	            }
	        }
		}
    }
    
    @Override
    public void setDead(){
    	if (isTacked() && !worldObj.isRemote){
    		dropItemWithOffset(getBreedTack().itemID, 1, 3);
    	}
    	
		super.setDead();
    }

    @Override
    public boolean interact(EntityPlayer entityplayer)
    {
    	ItemStack itemstack = entityplayer.getCurrentEquippedItem();
    	
		if (itemstack != null && isFeed(itemstack)){
			super.interact(entityplayer);
			
			getFoodWeight2(itemstack);
			if (energy < getMaxEnergy()){
				addEnergy(foodWeight);
				didHeal = true;
			}
			
			if (didHeal){
				showHeartsOrSmokeFX(true, 3);
				if (!entityplayer.capabilities.isCreativeMode){
					--itemstack.stackSize;

	                if (itemstack.stackSize <= 0)
	                {
	                	entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, (ItemStack)null);
	                }
				}
				didHeal = false;
				return true;
			}
		}
		
		if (itemstack != null && isBreedTack(itemstack) && !isTacked() && !worldObj.isRemote){
			setTacked(true);
			if (!entityplayer.capabilities.isCreativeMode){
				--itemstack.stackSize;

                if (itemstack.stackSize <= 0)
                {
                	entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, (ItemStack)null);
                }
			}
			return true;
		}
		
		if(itemstack != null && itemstack.itemID == Item.dyePowder.itemID && !worldObj.isRemote){
			int dyeID = BlockCloth.getBlockFromDye(itemstack.getItemDamage());

            if (getGearColor() != dyeID)
            {
                setGearColor(dyeID);
                if (!entityplayer.capabilities.isCreativeMode){
                	--itemstack.stackSize;

	                if (itemstack.stackSize <= 0)
	                {
	                	entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, (ItemStack)null);
	                }
                }
            }

            return true;
		}
		
		if (itemstack == null && isTacked() && !worldObj.isRemote){
			if (!entityplayer.capabilities.isCreativeMode){
				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, new ItemStack(getBreedTack()));
			}
			
	        setTacked(false);
    		return true;
    	}
    	
    	return super.interact(entityplayer);
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource damage, int i){
    	if (damage.getDamageType().equals(DamageSource.inWall.damageType) && isWorking()){
    		return false;
    	}
    	
    	try{
    		if (riddenByEntity != null){
	    		if (damage.getSourceOfDamage() == riddenByEntity){
	    			return false;
	    		}
	    		
	    		if (damage.getDamageType().equals("arrow")){
	    			Entity entity = ((EntityArrow) damage.getSourceOfDamage()).shootingEntity;
	    			
	    			if (entity != null && entity == riddenByEntity){
	        			return false;
	    			}
	    		}
	    		
	    		if (damage.getDamageType().equals("thrown")){
	    			Entity entity = ((EntityThrowable) damage.getSourceOfDamage()).getThrower();
	    			
	    			if (entity != null && entity == riddenByEntity){
	        			return false;
	    			}
	    		}
    		}
    		}catch(Exception e){
    		//something odd here...
    	}
    	
    	return super.attackEntityFrom(damage, i);
    }

	public boolean isBreedTack(ItemStack itemstack) {
		return false;
	}
	
	public Item getBreedTack(){
		return Item.saddle;
	}

	public void transferStatsTo(EntityHorseSH entityhorse)
    {
        super.transferStatsTo(entityhorse);
        
        if (entityhorse instanceof EntityHorseTameSH){
        	((EntityHorseTameSH)entityhorse).setTacked(isTacked());
        }
    }

	public void doTrample(Entity entity){
		if (!(entity instanceof EntityLiving) || entity instanceof EntityNonLiving){
			return;
		}
		
		entity.addVelocity(-MathHelper.sin((rotationYaw * (float)Math.PI) / 180F) * 2F * 0.5F, 0.3D, MathHelper.cos((rotationYaw * (float)Math.PI) / 180F) * 2F * 0.5F);
	}
	
    public boolean onStartle()
    {
        return true;
    }

    protected void quitWorking()
    {
    }

    protected void addExhaustion()
    {

        energy -= 0.05D;
        energy = (double) ((int) (energy * 100))/100;

        if (energy < -0.5D)
        {
            energy = -0.5;
            quitWorking();
            attackEntityFrom(DamageSource.generic, 1 * (SimplyHorses.tfcON? 50: 1));
        }
    }

    protected void addEnergy(int i)
    {
        energy += i;

        if (energy > (double)getMaxEnergy())
        {
            energy = getMaxEnergy();
        }
    }

    protected void gUpdate()
    {
        addEnergy(5);
        super.gUpdate();
    }

    protected boolean getFoodWeight2(ItemStack itemstack)
    {
        if (itemstack.itemID == Item.cake.itemID)
        {
            foodWeight *= 1.5D;
        }

        return true;
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setBoolean("Tacked", isTacked());
        nbttagcompound.setInteger("Gear Color", getGearColor());
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readEntityFromNBT(nbttagcompound);
        setTacked(nbttagcompound.getBoolean("Tacked"));
        setGearColor(nbttagcompound.getInteger("Gear Color"));
    }
    
    /**returns true if the horse is tacked for work (ie wearing a saddle or harness)*/
    public boolean isTacked(){
		return dataWatcher.getWatchableObjectInt(17) == 1;
	}
    
    public boolean isTired(){
    	return energy > 0;
    }

	public void setTacked(boolean tacked) {
		dataWatcher.updateObject(17, tacked? 1: 0);
	}
	
	public int getGearColor(){
		return dataWatcher.getWatchableObjectInt(19);
	}
	
	public void setGearColor(int i){
		dataWatcher.updateObject(19, i);
	}

	public float getGallopBonus() {
		return gallopBonus;
	}

	public void setGallopBonus(float bonus) {
		gallopBonus = bonus;
	}

}
