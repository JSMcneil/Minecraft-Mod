package simplyhorses.common.entities.vehicles;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import simplyhorses.common.SimplyHorses;

public class EntityNonLiving extends EntityLiving {

	public EntityNonLiving(World par1World) {
		super(par1World);
	}
	
	@Override
	public boolean isAIEnabled(){
		return true;
	}
	
	@Override
	public void onLivingUpdate(){
		rotationYawHead = rotationYaw;
		super.onLivingUpdate();
	}

	@Override
	public int getMaxHealth() {
		return 20 * (SimplyHorses.tfcON? 50: 1);
	}
	
	@Override
	public boolean canBreatheUnderwater(){
		return true;
	}

}
