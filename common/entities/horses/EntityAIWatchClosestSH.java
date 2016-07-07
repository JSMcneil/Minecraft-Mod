package simplyhorses.common.entities.horses;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIWatchClosest;

public class EntityAIWatchClosestSH extends EntityAIWatchClosest{

	EntityHorseSH target;
	
	public EntityAIWatchClosestSH(EntityLiving par1EntityLiving, Class par2Class, float par3) {
		super(par1EntityLiving, par2Class, par3);
		
		target = (EntityHorseSH) par1EntityLiving;
	}
	
	@Override
	public boolean shouldExecute()
    {
    	if (target.isWorking() || target.getGrazeTimer() > 0){
    		return false;
    	}
    	
    	return super.shouldExecute();
    }

}
