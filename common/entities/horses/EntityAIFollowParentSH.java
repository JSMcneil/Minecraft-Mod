package simplyhorses.common.entities.horses;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIFollowParentSH extends EntityAIBase {

	EntityFoalSH foal;
    EntityHorseSH mom;
    World worldObjBlock;
    private float followSpeed;
    private PathNavigate path;
    float mindist;
    float maxdist;
    private boolean field_48311_i;
    
	public EntityAIFollowParentSH(EntityFoalSH entityFoalSH, float f) {
		foal = entityFoalSH;
		followSpeed = f;
		path = entityFoalSH.getNavigator();
        maxdist = 10;
        mindist = 3;
        worldObjBlock = entityFoalSH.worldObj;
	}

	@Override
	public boolean shouldExecute() {
		
		mom = foal.getMother();
    	
    	if (mom == null){
    		return false;
    	}
    	
        if (mom != null && mom.isDead)
        {
            return false;
        }
		
		if (mom != null && foal.getDistanceSqToEntity(mom) < maxdist){
			return false;
		}
		
		if (!foal.isFree()){
			return false;
		}
		
		if (mom instanceof EntityHorseDraughtSH && mom.isWorking()){
			return false;
		}
        
		return true;
	}
	
	@Override
	public boolean continueExecuting()
    {
        return !path.noPath() && shouldExecute() && foal.getDistanceToEntity(mom) > mindist;
    }
	
	@Override
	public void startExecuting()
    {
        field_48311_i = foal.getNavigator().getAvoidsWater();
        foal.getNavigator().setAvoidsWater(false);
    }
	
	@Override
	public void resetTask()
    {
        mom = null;
        path.clearPathEntity();
        foal.getNavigator().setAvoidsWater(field_48311_i);
    }

	@Override
	public void updateTask()
    {
    	checkMom();
    	if (mom == null){
    		return;
    	}

        if (foal.getDistanceToEntity(mom) >= 12D)
        {
	        int i = MathHelper.floor_double(mom.posX) - 2;
	        int j = MathHelper.floor_double(mom.posZ) - 2;
	        int k = MathHelper.floor_double(mom.boundingBox.minY);
	
	        for (int l = 0; l <= 4; l++)
	        {
	            for (int i1 = 0; i1 <= 4; i1++)
	            {
	                if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && worldObjBlock.isBlockNormalCube(i + l, k - 1, j + i1) && !worldObjBlock.isBlockNormalCube(i + l, k, j + i1) && !worldObjBlock.isBlockNormalCube(i + l, k + 1, j + i1))
	                {
	                    foal.setLocationAndAngles((float)(i + l) + 0.5F, k, (float)(j + i1) + 0.5F, foal.rotationYaw, foal.rotationPitch);
	                    path.clearPathEntity();
	                    return;
	                }
	            }
	        }
        }
        
    	if (foal.getDistanceToEntity(mom) >= 5D && path.tryMoveToEntityLiving(mom, 0.35F)){
    		return;
    	}
    	
    	followSpeed = EntityAIFollowLassoSH.getTargetMoveSpeed(mom);
    	
        if (path.tryMoveToEntityLiving(mom, followSpeed))
        {
            return;
        }
    }
	
	protected void checkMom(){
    	mom = foal.getMother();
    }
}
