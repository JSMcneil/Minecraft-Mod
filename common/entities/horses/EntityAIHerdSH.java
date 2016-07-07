package simplyhorses.common.entities.horses;

import java.util.Random;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIHerdSH extends EntityAIBase {

	private EntityHorseWildSH entityhorse;
    private EntityHorseWildSH matriarch;
    World worldObjBlock;
    private float moveSpeed;
    private PathNavigate path;
    float mindist;
    float maxdist;
    private boolean avoidsWaterReset;
    private Random rand = new Random();
    
	public EntityAIHerdSH(EntityHorseWildSH entityWildHorseSH, float par2, float minDistance, float maxDistance) {
		entityhorse = entityWildHorseSH;
        worldObjBlock = entityWildHorseSH.worldObj;
        moveSpeed = par2;
        path = entityWildHorseSH.getNavigator();
        maxdist = maxDistance;
        mindist = minDistance;
	}

	@Override
	public boolean shouldExecute() {
		if (entityhorse.getHerd() == null){
			return false;
		}
		
		matriarch = entityhorse.getHerd().getMatriarch();
	        
        if (matriarch == null){
        	return false;
        }

        if (!entityhorse.isFree() || entityhorse.ridingEntity != null || entityhorse.entityaidefend.isAttacking)
        {
            return false;
        }
		
        if (entityhorse.getDistanceToEntity(matriarch) < maxdist)
        {
            return false;
        }
        
    	return true;
	}
	
	@Override
	public boolean continueExecuting()
    {
        return entityhorse.getDistanceToEntity(matriarch) > mindist && matriarch.isFree();
    }

	@Override
    public void startExecuting()
    {
        avoidsWaterReset = entityhorse.getNavigator().getAvoidsWater();
        entityhorse.getNavigator().setAvoidsWater(false);
    }
	
	@Override
	public void resetTask()
    {
        matriarch = null;
        entityhorse.getNavigator().clearPathEntity();
        entityhorse.getNavigator().setAvoidsWater(avoidsWaterReset);
    }

    @Override
    public void updateTask()
    {
    	if (!matriarch.isFree() || !path.noPath()){
    		return;
    	}
    	
    	int x = (int) matriarch.posX + (rand.nextBoolean() == true? -1: 1) * rand.nextInt(10);
    	int y = (int) matriarch.posY;
    	int z = (int) matriarch.posZ + (rand.nextBoolean() == true? -1: 1) * rand.nextInt(10);
    	
    	
        if (path.tryMoveToXYZ(x, y, z, 0.3F))
        {
            return;
        }

        if (entityhorse.getDistanceToEntity(matriarch) > maxdist + 5 && entityhorse.getDistanceToEntity(matriarch) < maxdist * 2 && !entityhorse.isAirBorne)
        {
	        int i = MathHelper.floor_double(matriarch.posX) - 2;
	        int j = MathHelper.floor_double(matriarch.posZ) - 2;
	        int k = MathHelper.floor_double(matriarch.boundingBox.minY);
	
	        for (int l = 0; l <= 4; l++)
	        {
	            for (int i1 = 0; i1 <= 4; i1++)
	            {
	                if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && worldObjBlock.isBlockNormalCube(i + l, k - 1, j + i1) && !worldObjBlock.isBlockNormalCube(i + l, k, j + i1) && !worldObjBlock.isBlockNormalCube(i + l, k + 1, j + i1))
	                {
	                    entityhorse.setLocationAndAngles((float)(i + l) + 0.5F, k, (float)(j + i1) + 0.5F, entityhorse.rotationYaw, entityhorse.rotationPitch);
	                    path.clearPathEntity();
	                    return;
	                }
	            }
	        }
        }
    }

}
