package simplyhorses.common.entities.horses;

import simplyhorses.common.SimplyHorses;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityClydesdaleSH extends EntityHorseDraughtSH{

	Entity rider;
	
	public EntityClydesdaleSH(World world) {
		super(world);
		
		breed = 4;
		rider = null;
		
	}
    
	@Override
    public int getTextureSlots(){
    	return 33;
    }
	
	@Override
    public int getMaxHealth(){
    	return 30 * (SimplyHorses.tfcON? 50: 1);
    }
	
	public int getMaxEnergy(){
    	return 30;
    }
	
	@Override
    public double getMountedYOffset()
	{
	    return (double)height * 1.1D;
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		if (rider != null && (rider.ridingEntity != this || rider.isDead)) rider = null;
		
		if (getTiedToEntity() == null && rider != null && rider instanceof EntityOcelot && ((EntityOcelot) rider).isTamed() && ((EntityOcelot) rider).getOwner() != null){
			EntityPlayer riderOwner = (EntityPlayer) ((EntityOcelot) rider).getOwner();
			setTiedToEntity(riderOwner);
		}
	}
	
	@Override
	public boolean interact(EntityPlayer entityplayer){
		if (isTacked() && !this.worldObj.isRemote && entityplayer.isSneaking() && (this.riddenByEntity == null || this.riddenByEntity == entityplayer))
        {
        	entityplayer.addChatMessage("Only cats can ride Clydesdales!");
			return true;
        }
        else
        {
            return super.interact(entityplayer);
        }
	}
	public boolean handleLasso(Entity entity){
		if (entity instanceof EntityOcelot && ((EntityOcelot) entity).isTamed()){
			rider = entity;
			entity.mountEntity(this);
			setFree();
			return true;
		}
		
		else return super.handleLasso(entity);
	}

}
