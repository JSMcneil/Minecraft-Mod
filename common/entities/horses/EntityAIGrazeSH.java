package simplyhorses.common.entities.horses;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import simplyhorses.common.PacketHandlerSH;
import simplyhorses.common.SimplyHorses;

public class EntityAIGrazeSH extends EntityAIBase
{
    private EntityHorseSH entityhorse;
    private World worldObj;
    int gCountdown;
    int cooldown;
    PathNavigate path;

    public EntityAIGrazeSH(EntityHorseSH par1EntityHorseSH)
    {
        gCountdown = 0;
        cooldown = 0;
        entityhorse = par1EntityHorseSH;
        worldObj = par1EntityHorseSH.worldObj;
        path = par1EntityHorseSH.getNavigator();
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	if (cooldown < 0){
    		cooldown = 0;
    	}
    	
    	if (cooldown > 0 && cooldown-- != 0){
    		return false;
    	}
    	
    	if (entityhorse.isWorkingActive()){
    		return false;
    	}
    	
    	if (entityhorse instanceof EntityFoalSH){
    		return false;
    	}
    	
        if (!path.noPath() || entityhorse.getRNG().nextInt((entityhorse.isChild() || entityhorse.getHealth() < entityhorse.getMaxHealth() || entityhorse.energy < entityhorse.getMaxEnergy()) ? 50 : 5) != 0)
        {
            return false;
        }
        
        float f1 = (entityhorse.rotationYaw * (float)Math.PI) / 180F;
        float f2 = MathHelper.sin(f1);
        float f3 = MathHelper.cos(f1);
        
        int i = MathHelper.floor_double(entityhorse.posX - f2 * 1);
        int j = MathHelper.floor_double(entityhorse.posY);
        int k = MathHelper.floor_double(entityhorse.posZ + f3 * 1);

        if (worldObj.getBlockId(i, j, k) == Block.tallGrass.blockID && worldObj.getBlockMetadata(i, j, k) == 1){
        	return true;
        }
        
        if (worldObj.getBlockId(i, j, k) == SimplyHorses.prairieGrass.blockID && worldObj.getBlockMetadata(i, j, k) > 4){
        	return true;
        }

        return worldObj.getBlockId(i, j - 1, k) == Block.grass.blockID;
    }

    public void startExecuting()
    {
        gCountdown = 300;
        PacketHandlerSH.sendPacketGrazingUpdate(entityhorse, 300);
        entityhorse.getNavigator().clearPathEntity();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        gCountdown = 0;
        PacketHandlerSH.sendPacketGrazingUpdate(entityhorse, 0);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
    	return entityhorse.getNavigator().noPath() && !entityhorse.isWorkingActive() && gCountdown > 0;
    }

    public int getgCountdown()
    {
        return gCountdown;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
    	gCountdown = Math.max(0, gCountdown - 1);
        
        if (gCountdown != 4)
        {
            return;
        }

        float f1 = (entityhorse.rotationYaw * (float)Math.PI) / 180F;
        float f2 = MathHelper.sin(f1);
        float f3 = MathHelper.cos(f1);
        
        int i = MathHelper.floor_double(entityhorse.posX - f2 * 1);
        int j = MathHelper.floor_double(entityhorse.posY);
        int k = MathHelper.floor_double(entityhorse.posZ + f3 * 1);

        if (worldObj.getBlockId(i, j, k) == Block.tallGrass.blockID)
        {
            worldObj.playAuxSFX(2001, i, j, k, Block.tallGrass.blockID + 4096);
            worldObj.destroyBlock(i, j, k, false);
            entityhorse.gUpdate();
        }
        else if (worldObj.getBlockId(i, j, k) == SimplyHorses.prairieGrass.blockID && worldObj.getBlockMetadata(i, j, k) > 4)
        {
            worldObj.playAuxSFX(2001, i, j, k, Block.grass.blockID);
            worldObj.setBlockMetadataWithNotify(i, j, k, 0, 2);
            entityhorse.gUpdate();
        }
        else if (worldObj.getBlockId(i, j - 1, k) == Block.grass.blockID)
        {
            worldObj.playAuxSFX(2001, i, j - 1, k, Block.grass.blockID);
            worldObj.setBlock(i, j - 1, k, Block.dirt.blockID, 0, 2);
            entityhorse.gUpdate();
        }
        
        cooldown = 20 + worldObj.rand.nextInt(5) * 20;
    }
}
