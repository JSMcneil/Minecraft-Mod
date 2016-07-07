package simplyhorses.common.entities.horses;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.world.World;

public class EntityAIMateHorsesSH extends EntityAIBase
{
    private EntityHorseSH theHorse;
    World theWorld;
    private EntityHorseSH targetMate;

    /**
     * Delay preventing a baby from spawning immediately when two mate-able animals find each other.
     */
    int spawnBabyDelay = 0;

    /** The speed the creature moves at during mating behavior. */
    float moveSpeed;

    public EntityAIMateHorsesSH(EntityHorseSH par1EntityAnimal, float par2)
    {
        this.theHorse = par1EntityAnimal;
        this.theWorld = par1EntityAnimal.worldObj;
        this.moveSpeed = par2;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.theHorse.isInLove())
        {
            return false;
        }
        else
        {
            this.targetMate = this.getNearbyMate();
            return this.targetMate != null;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.targetMate.isEntityAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.targetMate = null;
        this.spawnBabyDelay = 0;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.theHorse.getLookHelper().setLookPositionWithEntity(this.targetMate, 10.0F, (float)this.theHorse.getVerticalFaceSpeed());
        this.theHorse.getNavigator().tryMoveToEntityLiving(this.targetMate, this.moveSpeed);
        ++this.spawnBabyDelay;

        if (this.spawnBabyDelay == 60)
        {
            this.spawnBaby();
        }
    }

    /**
     * Loops through nearby animals and finds another animal of the same type that can be mated with. Returns the first
     * valid mate found.
     */
    private EntityHorseSH getNearbyMate()
    {
        float range = 8.0F;
        List mateList = this.theWorld.getEntitiesWithinAABB(EntityHorseSH.class, this.theHorse.boundingBox.expand((double)range, (double)range, (double)range));
        Iterator iterator = mateList.iterator();
        EntityHorseSH horse;

        do
        {
            if (!iterator.hasNext())
            {
                return null;
            }

            horse = (EntityHorseSH)iterator.next();
        }
        while (!this.theHorse.canMateWith(horse));

        return horse;
    }

    /**
     * Spawns a baby animal of the same type.
     */
    private void spawnBaby()
    {
        EntityAnimal var1 = this.theHorse.createChild(this.targetMate);

        if (var1 != null)
        {
            this.theHorse.setGrowingAge(6000);
            this.targetMate.setGrowingAge(6000);
            this.theHorse.resetInLove();
            this.targetMate.resetInLove();
            var1.setGrowingAge(-24000);
            var1.setLocationAndAngles(this.theHorse.posX, this.theHorse.posY, this.theHorse.posZ, 0.0F, 0.0F);
            this.theWorld.spawnEntityInWorld(var1);
            Random var2 = this.theHorse.getRNG();

            for (int var3 = 0; var3 < 7; ++var3)
            {
                double var4 = var2.nextGaussian() * 0.02D;
                double var6 = var2.nextGaussian() * 0.02D;
                double var8 = var2.nextGaussian() * 0.02D;
                this.theWorld.spawnParticle("heart", this.theHorse.posX + (double)(var2.nextFloat() * this.theHorse.width * 2.0F) - (double)this.theHorse.width, this.theHorse.posY + 0.5D + (double)(var2.nextFloat() * this.theHorse.height), this.theHorse.posZ + (double)(var2.nextFloat() * this.theHorse.width * 2.0F) - (double)this.theHorse.width, var4, var6, var8);
            }
        }
    }
}
