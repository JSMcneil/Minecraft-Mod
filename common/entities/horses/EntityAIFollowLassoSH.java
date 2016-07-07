package simplyhorses.common.entities.horses;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import simplyhorses.common.entities.vehicles.EntityDraughtTechSH;

public class EntityAIFollowLassoSH extends EntityAIBase {

	private EntityHorseSH lassodentity;
    private Entity targetentity;
    private World worldObjBlock;
    private float moveSpeed;
    private PathNavigate path;
    private int delay;
    float mindist;
    float maxdist;
    private boolean avoidsWater;
    
	public EntityAIFollowLassoSH(EntityHorseSH entity, float par2, float minDistance, float maxDistance) {
		lassodentity = entity;
        worldObjBlock = entity.worldObj;
        moveSpeed = par2;
        path = entity.getNavigator();
        maxdist = maxDistance;
        mindist = minDistance;
	}

	@Override
	public boolean shouldExecute() {
		Entity lassoentity = lassodentity.getTiedToEntity();

        if (lassoentity == null || lassodentity.isFree() || lassodentity.isHitched() || lassodentity.isWorking())
        {
            return false;
        }
        
        if (lassodentity instanceof EntityHorseTameSH && ((EntityHorseTameSH)lassodentity).isWorking()){
        	return false;
        }
		
        if (lassodentity.getDistanceToEntity(lassoentity) < maxdist)
        {
            return false;
        }
        else
        {
            targetentity = lassoentity;
            return true;
        }
	}
	
	@Override
	public boolean continueExecuting()
    {
        return !path.noPath() && lassodentity.getDistanceToEntity(targetentity) > mindist;
    }

	@Override
	public void startExecuting()
    {
        avoidsWater = lassodentity.getNavigator().getAvoidsWater();
        lassodentity.getNavigator().setAvoidsWater(false);
    }

	@Override
	public void resetTask()
    {
        targetentity = null;
        moveSpeed = 0.325F;
        path.clearPathEntity();
        lassodentity.getNavigator().setAvoidsWater(avoidsWater);
    }

	@Override
	public void updateTask()
    {
    	if (targetentity.isSprinting()){
    		moveSpeed = 0.5F;
    	}
    	
        if (path.tryMoveToEntityLiving((EntityLiving) targetentity, moveSpeed))
        {
            return;
        }

        if (lassodentity.getDistanceToEntity(targetentity) >= maxdist + 2)
        {
	        int i = MathHelper.floor_double(targetentity.posX) - 2;
	        int j = MathHelper.floor_double(targetentity.posZ) - 2;
	        int k = MathHelper.floor_double(targetentity.boundingBox.minY);
	
	        for (int l = 0; l <= 4; l++)
	        {
	            for (int i1 = 0; i1 <= 4; i1++)
	            {
	                if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && worldObjBlock.isBlockNormalCube(i + l, k - 1, j + i1) && !worldObjBlock.isBlockNormalCube(i + l, k, j + i1) && !worldObjBlock.isBlockNormalCube(i + l, k + 1, j + i1))
	                {
	                    lassodentity.setLocationAndAngles((float)(i + l) + 0.5F, k, (float)(j + i1) + 0.5F, lassodentity.rotationYaw, lassodentity.rotationPitch);
	                    path.clearPathEntity();
	                    return;
	                }
	            }
	        }
        }
    }

	/**Tries its damnedest to find the best approximation of the passed-in entity's speed of movement.
	 * Doesn't work so well with custom mob entities (outside of SH).*/
	public static float getTargetMoveSpeed(Entity entity){
		if (entity instanceof EntityHorseSH && ((EntityHorseSH) entity).isWorking()){
			if (entity instanceof EntityHorseRideableSH){
				if (entity.isSprinting()){
					return 0.5F;
				} else if (entity.isSneaking()){
					return 0.2F;
				} else {
					return 0.35F;
				}
			}
		}
		
		if (entity instanceof EntityDraughtTechSH){
			int i = ((EntityDraughtTechSH) entity).getDraughtHelper().getSpeedMode();
			
			switch (i){
			case 0:
				return 0.15F;
			case 1:
				return 0.15F;
			case 2:
				return 0.25F;
			case 3:
				return 0.4F;
			default:
				return 0.15F;
			}
		}
		
		if (entity instanceof EntityLiving){
			EntityLiving living = (EntityLiving) entity;
			if (!(living instanceof EntityPlayer) && !living.getNavigator().noPath()){
				return living.getMoveHelper().getSpeed();
			}
			
			return Math.max(living.landMovementFactor *= living.getSpeedModifier(), 0.15F);
		}
		
		return 0.35F;
	}
}
