package simplyhorses.common.entities.horses;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class EntityAIControlledByPlayerSH extends EntityAIBase {

	private final EntityLiving thisEntity;
    private final float maxSpeed;
    private float speed = 0.0F;
    private boolean speedingUp = false;
    private int speedUpTicker = 0;
    private int maxSpeedUpTime = 0;

    public EntityAIControlledByPlayerSH(EntityLiving par1EntityLiving, float par2)
    {
        this.thisEntity = par1EntityLiving;
        this.maxSpeed = par2;
        this.setMutexBits(7);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.speed = 0.0F;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.speedingUp = false;
        this.speed = 0.0F;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return this.thisEntity.isEntityAlive() && this.thisEntity.riddenByEntity != null && this.thisEntity.riddenByEntity instanceof EntityPlayer;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        EntityPlayer thisRider = (EntityPlayer)this.thisEntity.riddenByEntity;
        EntityCreature riddenEntity = (EntityCreature)this.thisEntity;
        
        float var3 = wrapAngleTo180_float(thisRider.rotationYaw - this.thisEntity.rotationYaw) * 0.5F;
        
        if (var3 > 5.0F)
        {
            var3 = 5.0F;
        }

        if (var3 < -5.0F)
        {
            var3 = -5.0F;
        }

        this.thisEntity.rotationYaw = wrapAngleTo180_float(this.thisEntity.rotationYaw + var3);

        if (this.speed < this.maxSpeed)
        {
            this.speed += (this.maxSpeed - this.speed) * 0.01F;
        }

        if (this.speed > this.maxSpeed)
        {
            this.speed = this.maxSpeed;
        }

        int var4 = MathHelper.floor_double(this.thisEntity.posX);
        int var5 = MathHelper.floor_double(this.thisEntity.posY);
        int var6 = MathHelper.floor_double(this.thisEntity.posZ);
        float var7 = this.speed;

        if (this.speedingUp)
        {
            if (this.speedUpTicker++ > this.maxSpeedUpTime)
            {
                this.speedingUp = false;
            }

            var7 += var7 * 1.15F * MathHelper.sin((float)this.speedUpTicker / (float)this.maxSpeedUpTime * (float)Math.PI);
        }

        this.thisEntity.moveEntityWithHeading(0.0F, var7);
    }
    
    public float wrapAngleTo180_float(float par0)
    {
        par0 %= 360.0F;

        if (par0 >= 180.0F)
        {
            par0 -= 360.0F;
        }

        if (par0 < -180.0F)
        {
            par0 += 360.0F;
        }

        return par0;
    }

}
