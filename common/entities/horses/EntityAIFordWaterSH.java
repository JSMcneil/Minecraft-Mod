package simplyhorses.common.entities.horses;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

/**Used by horses and vehicles to walk in 1-block-high water and float (per usual) in deeper water*/
public class EntityAIFordWaterSH extends EntityAISwimming{

	EntityLiving target;
	
	public EntityAIFordWaterSH(EntityLiving par1target) {
		super(par1target);
		target = par1target;
	}
	
	public void updateTask()
    {
    	int i = MathHelper.floor_double(target.posX);
        int j = MathHelper.floor_double(target.posY);
        int k = MathHelper.floor_double(target.posZ);
        
        if (!target.worldObj.isBlockNormalCube(i, j-1, k) || target.worldObj.getBlockId(i, j + 1, k) == Block.waterStill.blockID || target instanceof EntityFoalSH)
        {
        	if (target.getRNG().nextFloat() < 0.8F)
	        {
	            target.getJumpHelper().setJumping();
	        }
        }
        else {
        	if (target instanceof EntityHorseSH && target.riddenByEntity != null && target.riddenByEntity instanceof EntityPlayer){
	        	target.motionX *= 0.3D;
	        	target.motionZ *= 0.3D;
        	}
        	else {
        		target.motionX *= 1.1D;
	        	target.motionZ *= 1.1D;
        	}
        	
        	if (target.isCollidedHorizontally){
        		target.getJumpHelper().setJumping();
        	}
        }
    }

}
