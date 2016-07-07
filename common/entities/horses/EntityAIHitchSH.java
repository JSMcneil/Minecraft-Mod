package simplyhorses.common.entities.horses;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class EntityAIHitchSH extends EntityAIBase {

	private EntityHorseSH entityhorse;
    private World worldObjBlock;
    private PathNavigate path;
    private ChunkCoordinates hitchingBlock;

    public EntityAIHitchSH(EntityHorseSH entity)
    {
        entityhorse = entity;
        worldObjBlock = entity.worldObj;
        path = entity.getNavigator();
        
    }

    @Override
    public boolean shouldExecute()
    {
    	if (!entityhorse.isHitched()){
    		return false;
    	}
    	
    	if (entityhorse.getHitchCoordinates() == null){
    		return false;
    	}
    	else{
    		hitchingBlock = entityhorse.getHitchCoordinates();
    	}
    	
    	return true;
    }

    @Override
    public boolean continueExecuting()
    {
    	int i = hitchingBlock.posX;
    	int j = hitchingBlock.posY;
    	int k = hitchingBlock.posZ;
    	
    	int blockId = worldObjBlock.getBlockId(i, j, k);
    	
    	switch(blockId){
    	case 0:
    		return false;
    	case 5:
    		return shouldExecute();
    	case 17:
    		return shouldExecute();
    	case 85:
    		return shouldExecute();
    	case 113:
    		return shouldExecute();
		default:
			return false;
    	}
    }

    @Override
    public void resetTask()
    {
    	hitchingBlock = null;
    	entityhorse.setHitchCoordinates(null);
    }

    @Override
    public void updateTask()
    {
    	int i = hitchingBlock.posX;
    	int j = hitchingBlock.posY;
    	int k = hitchingBlock.posZ;
    	
    	if (entityhorse.getDistance(i, j, k) > 5){
    		entityhorse.showHeartsOrSmokeFX(false, 3);
    		entityhorse.getNavigator().clearPathEntity();
    		entityhorse.getNavigator().tryMoveToXYZ(i, j, k, 0.3F);
    	}
    }
    
    public void setHitched(EntityHorseSH entityhorse, ChunkCoordinates hitchingBlock){
    	int i = hitchingBlock.posX;
    	int j = hitchingBlock.posY;
    	int k = hitchingBlock.posZ;
    	
    	entityhorse.showHeartsOrSmokeFX(false, 3);
		entityhorse.getNavigator().clearPathEntity();
		entityhorse.getNavigator().tryMoveToXYZ(i, j, k, 0.3F);
    }

}
