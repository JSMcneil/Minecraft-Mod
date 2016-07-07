package simplyhorses.common.entities.horses;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityAIPanicSH extends EntityAIBase {

	public EntityHorseTameSH thisHorse;
	public World worldObj;
	public Random rand;
	public EntityCreeper theCreeper;
	public int panicLoops;
	public int panicCooldown;
	
	public EntityAIPanicSH(EntityHorseTameSH entityhorse){
		thisHorse = entityhorse;
		worldObj = entityhorse.worldObj;
		rand = new Random();
		
		theCreeper = null;
		panicLoops = 0;
		panicCooldown = 0;
	}
	
	@Override
	public boolean shouldExecute() {
		if(thisHorse.isHitched() || thisHorse.isLassoed()){
			return false;
		}
		
		List creeperList = worldObj.getEntitiesWithinAABB(EntityCreeper.class, thisHorse.boundingBox.expand((double)5, (double)5, (double)5));
        Iterator iterator = creeperList.iterator();
        EntityCreeper foundCreeper;

        do
        {
            if (!iterator.hasNext())
            {
                return false;
            }

            foundCreeper = (EntityCreeper)iterator.next();
        }
        while (!thisHorse.getEntitySenses().canSee(foundCreeper));
		
        theCreeper = foundCreeper;
        
		return theCreeper != null && thisHorse.onStartle();
	}
	
	@Override
	public void startExecuting(){
		thisHorse.getNavigator().clearPathEntity();
		panicLoops = rand.nextInt(5) + 1;
		panicCooldown = 100;
	}
	
	@Override
	public boolean continueExecuting(){
		if (panicLoops == 0 && panicCooldown == 0){
			return false;
		}
		
		return !thisHorse.isLassoed() && !thisHorse.isHitched();
	}
	
	@Override
	public void updateTask(){
		
		panicCooldown--;
		
		if (!thisHorse.getNavigator().noPath()) return;
		
		Vec3 vec = RandomPositionGenerator.findRandomTargetBlockAwayFrom(thisHorse, 16, 7, thisHorse.worldObj.getWorldVec3Pool().getVecFromPool(theCreeper.posX, theCreeper.posY, theCreeper.posZ));

        if (vec == null)
        {
            return;
        }

        if (theCreeper.getDistanceSq(vec.xCoord, vec.yCoord, vec.zCoord) < theCreeper.getDistanceSqToEntity(thisHorse))
        {
            return;
        }

        PathNavigate pathnavigate = thisHorse.getNavigator();
        PathEntity pathentity = pathnavigate.getPathToXYZ(vec.xCoord, vec.yCoord, vec.zCoord);

        if (pathentity == null)
        {
            return;
        }

        if (pathentity.isDestinationSame(vec))
        {
            pathnavigate.setPath(pathentity, 0.4F);

            if ((double)thisHorse.getDistanceToEntity(theCreeper) < 21D)
            {
            	thisHorse.getNavigator().setSpeed(0.4F);
            }
            else
            {
            	thisHorse.getNavigator().setSpeed(0.3F);
            }
        }
		
		panicLoops--;
	}
	
	@Override
	public void resetTask(){
		theCreeper = null;
		panicLoops = 0;
	}

}
