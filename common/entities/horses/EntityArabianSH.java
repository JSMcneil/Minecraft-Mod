package simplyhorses.common.entities.horses;

import simplyhorses.common.SimplyHorses;
import net.minecraft.world.World;

public class EntityArabianSH extends EntityHorseRideableSH{

	public EntityArabianSH(World worldObj) {
		super(worldObj);
		breed = 3;
		gallopBonus = 0.55F;
	}
    
	@Override
    public int getTextureSlots(){
    	return 53;
    }
    
	@Override
    public int getMaxHealth(){
    	return 20 * (SimplyHorses.tfcON? 50: 1);
    }
	
	public int getMaxEnergy(){
    	return 60;
    }
	
	public boolean onStartle()
    {
		if (isWorking()){
			quitWorking();
		}
		
        return true;
    }
}
