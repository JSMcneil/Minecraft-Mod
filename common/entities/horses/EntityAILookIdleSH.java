package simplyhorses.common.entities.horses;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAILookIdle;

public class EntityAILookIdleSH extends EntityAILookIdle{

	EntityHorseSH target;
	
	public EntityAILookIdleSH(EntityLiving par1EntityLiving) {
		super(par1EntityLiving);
		target = (EntityHorseSH) par1EntityLiving;
	}

	@Override
	public boolean shouldExecute(){
		
		if (target.isWorking() || target.getGrazeTimer() > 0){
			return false;
		}
		
		return super.shouldExecute();
	}
}
