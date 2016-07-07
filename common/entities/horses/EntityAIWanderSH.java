package simplyhorses.common.entities.horses;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIWander;

public class EntityAIWanderSH extends EntityAIWander
{
    private EntityHorseSH entity;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private float speed;

    public EntityAIWanderSH(EntityCreature par1EntityCreature, float par2)
    {
        super(par1EntityCreature, par2);
        this.entity = (EntityHorseSH) par1EntityCreature;
        this.speed = par2;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	if (entity.worldObj.isRemote) return false;
    	
    	if (!entity.isFree()){
    		return false;
    	}
    	
    	if (entity.getGrazeTimer() > 0){
    		return false;
    	}
    	
    	if (entity instanceof EntityHorseWildSH && ((EntityHorseWildSH) entity).isMatriarch() && entity.getRNG().nextInt((int) (2D)) != 0)
        {
            return false;
        }
    	
        return super.shouldExecute();
    }
}
