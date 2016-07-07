package simplyhorses.common.entities.horses;

import simplyhorses.common.SimplyHorses;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.src.SparrowAPI;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityMustangSH extends EntityHorseRideableSH{

	public EntityMustangSH(World world) {
		super(world);
		
		breed = 2;
		
		dataWatcher.addObject(20, new Integer(0));
	}
    
	@Override
    public int getMaxHealth(){
    	return 40 * (SimplyHorses.tfcON? 50: 1);
    }
	
	public int getMaxEnergy(){
    	return 35;
    }
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
	}
	
	public void transferStatsTo(EntityHorseSH entityhorse){
		if (entityhorse instanceof EntityMustangSH){
			((EntityMustangSH) entityhorse).setLocked(isLocked());
		}
		
		super.transferStatsTo(entityhorse);
	}
	
	public void doTrample(Entity entity){
		if (!(entity instanceof EntityLiving)) return;
		
		if (entity instanceof EntityMob || (entity instanceof SparrowAPI && ((SparrowAPI)entity).isHostile())){
			attackEntityAsMob(entity);
	    	entity.addVelocity(-MathHelper.sin((rotationYaw * (float)Math.PI) / 180F) * 2F * 0.4F, 0.5D, MathHelper.cos((rotationYaw * (float)Math.PI) / 180F) * 2F * 0.5F);
    	} else super.doTrample(entity);
	}
	
	public boolean onStartle(){
		return false;
	}
	
	public boolean givesArrowBonus() {
		return true;
	}

	public boolean isLocked() {
		return dataWatcher.getWatchableObjectInt(20) == 1;
	}

	public void setLocked(boolean locked) {
		dataWatcher.updateObject(20, locked? 1: 0);
	}
	
	public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setBoolean("Locked", isLocked());
    }

    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readEntityFromNBT(nbttagcompound);
        setLocked(nbttagcompound.getBoolean("Locked"));
    }

}
