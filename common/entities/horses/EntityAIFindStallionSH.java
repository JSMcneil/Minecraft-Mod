package simplyhorses.common.entities.horses;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIFindStallionSH extends EntityAIBase {

	private EntityHorseWildSH target;
	
	EntityAIFindStallionSH(EntityHorseWildSH wildhorse){
		target = wildhorse;
	}
	
	@Override
	public boolean shouldExecute() {
		if (!target.isMaverick()){
    		return false;
    	}
    	
    	return true;
	}
	
	@Override
	public boolean continueExecuting()
    {
        return shouldExecute();
    }
	
	@Override
	public void updateTask()
    {
        for (int i = 0; i < target.worldObj.loadedEntityList.size(); i++)
        {
            Entity entity1 = (Entity)target.worldObj.loadedEntityList.get(i);
            //Nearby entity is a Horse and is within 50 blocks of this Horse
            if(entity1 instanceof EntityHorseWildSH && entity1.getDistanceToEntity(target) < 50)
            {
                //Nearby Horse is a leader, make it this horse's leader and break from method
                if(((EntityHorseWildSH)entity1).isStallion())
                {
                	System.out.println(target.getSimplyID() + " found stallion.");
                	target.setDead();
                	return;
                }
            }
        }
        
        //No leader found, make this one leader
        System.out.println(target.getSimplyID() + " becoming stallion.");
        target.setPriority(1);
    }

}
