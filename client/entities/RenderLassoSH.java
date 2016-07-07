package simplyhorses.client.entities;

import org.lwjgl.opengl.GL11;

import simplyhorses.common.SimplyHorses;
import simplyhorses.common.entities.EntityLassoSH;
import simplyhorses.common.entities.horses.EntityHorseDraughtSH;
import simplyhorses.common.entities.horses.EntityHorseSH;
import simplyhorses.common.entities.horses.EntityHorseTameSH;
import simplyhorses.common.entities.vehicles.EntityDraughtTechSH;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RenderLassoSH extends Render {

	public void doRenderLasso(EntityLassoSH lasso, double d, double d1, double d2, float f, float f1)
    {
        if (lasso.getTiedToEntity() != null && lasso.getTiedToEntity() instanceof EntityLiving){
        	GL11.glPushMatrix();
    		Tessellator tessellator = Tessellator.instance;
            GL11.glPopMatrix();
            
    		EntityLiving tiedtoentity = (EntityLiving) lasso.getTiedToEntity();
    		 double tiedToTargetX = 0;
             double tiedToTargetY = 0;
             double tiedToTargetZ = 0;
             double tiedToEyeHeight = 0;
    		
    		if (tiedtoentity instanceof EntityPlayer){
    			Vec3 vec3 = lasso.worldObj.getWorldVec3Pool().getVecFromPool(-0.5D, 0.03D, 0.8D);
        		vec3.rotateAroundX(-(tiedtoentity.prevRotationPitch + (tiedtoentity.rotationPitch - tiedtoentity.prevRotationPitch) * f1) * (float)Math.PI / 180.0F);
        		vec3.rotateAroundY(-(tiedtoentity.prevRotationYaw + (tiedtoentity.rotationYaw - tiedtoentity.prevRotationYaw) * f1) * (float)Math.PI / 180.0F);
                
                tiedToTargetX = tiedtoentity.prevPosX + (tiedtoentity.posX - tiedtoentity.prevPosX) * (double)f1 + vec3.xCoord;
                tiedToTargetY = tiedtoentity.prevPosY + (tiedtoentity.posY - tiedtoentity.prevPosY) * (double)f1 + vec3.yCoord;
                tiedToTargetZ = tiedtoentity.prevPosZ + (tiedtoentity.posZ - tiedtoentity.prevPosZ) * (double)f1 + vec3.zCoord;
                tiedToEyeHeight = tiedtoentity != Minecraft.getMinecraft().thePlayer ? (double)tiedtoentity.getEyeHeight() : 0.0D;
                
                if (this.renderManager.options.thirdPersonView > 0 || tiedtoentity != Minecraft.getMinecraft().thePlayer)
                {
                    float var31 = (tiedtoentity.prevRenderYawOffset + (tiedtoentity.renderYawOffset - tiedtoentity.prevRenderYawOffset) * f1) * (float)Math.PI / 180.0F;
                    double var32 = (double)MathHelper.sin(var31);
                    double var34 = (double)MathHelper.cos(var31);
                    tiedToTargetX = tiedtoentity.prevPosX + (tiedtoentity.posX - tiedtoentity.prevPosX) * (double)f1 - var34 * 0.35D - var32 * 0.85D;
                    tiedToTargetY = tiedtoentity.prevPosY + tiedToEyeHeight + (tiedtoentity.posY - tiedtoentity.prevPosY) * (double)f1 - 0.45D;
                    tiedToTargetZ = tiedtoentity.prevPosZ + (tiedtoentity.posZ - tiedtoentity.prevPosZ) * (double)f1 - var32 * 0.35D + var34 * 0.85D;
                }
    		}
    		else{
    			tiedToTargetX = tiedtoentity.prevPosX + (tiedtoentity.posX - tiedtoentity.prevPosX);
                tiedToTargetY = tiedtoentity.prevPosY +  tiedtoentity.height * 0.8D + (tiedtoentity.posY - tiedtoentity.prevPosY);
                tiedToTargetZ = tiedtoentity.prevPosZ + (tiedtoentity.posZ - tiedtoentity.prevPosZ);
    		}

            double lassoTargetX = lasso.prevPosX + (lasso.posX - lasso.prevPosX) * (double)f1;
            double lassoTargetY = lasso.prevPosY + (lasso.posY - lasso.prevPosY) * (double)f1 + 0.25D;
            double lassoTargetZ = lasso.prevPosZ + (lasso.posZ - lasso.prevPosZ) * (double)f1;
            
            double deltaTargetX = (double)((float)(tiedToTargetX - lassoTargetX));
            double deltaTargetY = (double)((float)(tiedToTargetY - lassoTargetY));
            double deltaTargetZ = (double)((float)(tiedToTargetZ - lassoTargetZ));
            
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            tessellator.startDrawing(3);
            
            if (lasso.getCaughtEntity() instanceof EntityHorseTameSH){
            	if (SimplyHorses.showLeadRopes){
            		float var4 = 1.0F;
                    int var5 = ((EntityHorseTameSH) lasso.getCaughtEntity()).getGearColor();
                    tessellator.setColorOpaque_F(var4 * EntityHorseTameSH.gearColorTable[var5][0], var4 * EntityHorseTameSH.gearColorTable[var5][1], var4 * EntityHorseTameSH.gearColorTable[var5][2]);
            	}
            	else if (lasso.getCaughtEntity() instanceof EntityHorseDraughtSH && ((EntityHorseDraughtSH) lasso.getCaughtEntity()).isWorking()){
            		tessellator.setColorOpaque(0, 0, 0);
            	}
            	else{
                	tessellator.setColorOpaque(172, 158, 90);
            	}
            	
            }
            else if (lasso.getCaughtEntity() instanceof EntityDraughtTechSH){
            	tessellator.setColorOpaque(0, 0, 0);
            }
            else {
            	tessellator.setColorOpaque(172, 158, 90);
            }
            byte var43 = 16;

            for (int var44 = 0; var44 <= var43; ++var44)
            {
                float var45 = (float)var44 / (float)var43;
                tessellator.addVertex(d + deltaTargetX * (double)var45, d1 + deltaTargetY * (double)(var45 * var45 + var45) * 0.5D + 0.25D, d2 + deltaTargetZ * (double)var45);
            }

            
            tessellator.draw();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
        else if (lasso.getHitchCoords() != null){
        	GL11.glPushMatrix();
    		Tessellator tessellator = Tessellator.instance;
            GL11.glPopMatrix();
            
            double tiedToTargetX = lasso.getHitchCoords().posX + 0.5;
            double tiedToTargetY = lasso.getHitchCoords().posY + 0.5;
            double tiedToTargetZ = lasso.getHitchCoords().posZ + 0.5;
            
            double lassoTargetX = lasso.prevPosX + (lasso.posX - lasso.prevPosX) * (double)f1;
            double lassoTargetY = lasso.prevPosY + (lasso.posY - lasso.prevPosY) * (double)f1 + 0.25D;
            double lassoTargetZ = lasso.prevPosZ + (lasso.posZ - lasso.prevPosZ) * (double)f1;
            
            double deltaTargetX = (double)((float)(tiedToTargetX - lassoTargetX));
            double deltaTargetY = (double)((float)(tiedToTargetY - lassoTargetY));
            double deltaTargetZ = (double)((float)(tiedToTargetZ - lassoTargetZ));
            
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            tessellator.startDrawing(3);
            tessellator.setColorOpaque(172, 158, 90);
            byte var43 = 16;

            for (int var44 = 0; var44 <= var43; ++var44)
            {
                float var45 = (float)var44 / (float)var43;
                tessellator.addVertex(d + deltaTargetX * (double)var45, d1 + deltaTargetY * (double)(var45 * var45 + var45) * 0.5D + 0.25D, d2 + deltaTargetZ * (double)var45);
            }

            
            tessellator.draw();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
    }
	
	@Override
	public void doRender(Entity var1, double var2, double var4, double var6,
			float var8, float var9) {
		this.doRenderLasso((EntityLassoSH)var1, var2, var4, var6, var8, var9);
	}

}
