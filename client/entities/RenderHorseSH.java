package simplyhorses.client.entities;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.EntityLiving;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import simplyhorses.common.entities.EntityLassoSH;
import simplyhorses.common.entities.horses.EntityArabianSH;
import simplyhorses.common.entities.horses.EntityClydesdaleSH;
import simplyhorses.common.entities.horses.EntityFoalSH;
import simplyhorses.common.entities.horses.EntityHorseSH;
import simplyhorses.common.entities.horses.EntityHorseTameSH;
import simplyhorses.common.entities.horses.EntityMustangSH;

@SideOnly(Side.CLIENT)
public class RenderHorseSH extends RenderLiving
{
	ModelHorseSH passModelCoat;
	ModelHorseSH passModelMask;
	ModelHorseArmorSH passModelGear;
	ModelHorseArmorSH passModelTack;
	
    public RenderHorseSH(ModelBase modelbase, float f)
    {
        super(modelbase, f);
        
        /*passModelCoat = new ModelHorseSH(0.05F);
        passModelMask = new ModelHorseSH(0.06F);*/
        passModelGear = new ModelHorseArmorSH(0.2F); //Displays colorable halter and saddle blankets
        passModelTack = new ModelHorseArmorSH(0.3F); //Displays the saddle or harness
    }
    
    protected int renderHorseArmor(EntityHorseSH horseSH, int par2, float par3)
    {
    	
    	GL11.glEnable(GL11.GL_NORMALIZE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
    	switch (par2){
    	/*case 0:
    		loadTexture("/mods/SimplyHorses/textures/horses/horseGeneric_coat.png");
    		setRenderPassModel(passModelCoat);
    		return 1;
    	case 1:
    		loadTexture("/mods/SimplyHorses/textures/horses/horseGeneric_mask.png");
    		setRenderPassModel(passModelMask);
    		return 1;*/
    	case 2:
    		if (horseSH instanceof EntityHorseTameSH){
    			if (horseSH instanceof EntityMustangSH) loadTexture("/mods/SimplyHorses/textures/horses/mGear.png");
    			else if (horseSH instanceof EntityArabianSH) loadTexture("/mods/SimplyHorses/textures/horses/aGear.png");
    			else if (horseSH instanceof EntityClydesdaleSH) loadTexture("/mods/SimplyHorses/textures/horses/cGear.png");
    			passModelGear.head.showModel = horseSH instanceof EntityMustangSH? ((EntityMustangSH) horseSH).isLocked()? true: false: true;
    			passModelGear.body.showModel = ((EntityHorseTameSH) horseSH).isTacked()? true: false;
    			
    			float var4 = 1.0F;
                int var5 = ((EntityHorseTameSH) horseSH).getGearColor();
                if (var5 != 0){
                	GL11.glColor3f(var4 * EntityHorseTameSH.gearColorTable[var5][0], var4 * EntityHorseTameSH.gearColorTable[var5][1], var4 * EntityHorseTameSH.gearColorTable[var5][2]);
                }
                else {
                	GL11.glColor3f(0.76F, 0.51F, 0.18F);
                }
                setRenderPassModel(passModelGear);
    			return 1;
    		}
    		break;
    	case 3:
    		if (horseSH instanceof EntityHorseTameSH && ((EntityHorseTameSH) horseSH).isTacked()){
    			if (horseSH instanceof EntityMustangSH) loadTexture("/mods/SimplyHorses/textures/horses/mSaddle.png");
    			else if (horseSH instanceof EntityArabianSH) loadTexture("/mods/SimplyHorses/textures/horses/aSaddle.png");
    			else if (horseSH instanceof EntityClydesdaleSH) loadTexture("/mods/SimplyHorses/textures/horses/cHarness.png");
    			GL11.glColor3f(1, 1, 1);
                setRenderPassModel(passModelTack);
    			return 1;
    		}
    		break;
		default:
    		break;	
    	}
    	
    	return -1;
    }
    
    protected int shouldRenderPass(EntityLiving par1EntityLiving, int par2, float par3)
    {
        return this.renderHorseArmor((EntityHorseSH)par1EntityLiving, par2, par3);
    }
    
    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        this.mainModel.onGround = this.renderSwingProgress(par1EntityLiving, par9);

        if (this.renderPassModel != null)
        {
            this.renderPassModel.onGround = this.mainModel.onGround;
        }

        this.mainModel.isRiding = par1EntityLiving.isRiding();

        if (this.renderPassModel != null)
        {
            this.renderPassModel.isRiding = this.mainModel.isRiding;
        }

        this.mainModel.isChild = par1EntityLiving.isChild();

        if (this.renderPassModel != null)
        {
            this.renderPassModel.isChild = this.mainModel.isChild;
        }

        try
        {
            float var10 = this.func_77034_a(par1EntityLiving.prevRenderYawOffset, par1EntityLiving.renderYawOffset, par9);
            float var11 = this.func_77034_a(par1EntityLiving.prevRotationYawHead, par1EntityLiving.rotationYawHead, par9);
            float var12 = par1EntityLiving.prevRotationPitch + (par1EntityLiving.rotationPitch - par1EntityLiving.prevRotationPitch) * par9;
            this.renderLivingAt(par1EntityLiving, par2, par4, par6);
            float var13 = this.handleRotationFloat(par1EntityLiving, par9);
            this.rotateCorpse(par1EntityLiving, var13, var10, par9);
            float var14 = 0.0625F;
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glScalef(-1.0F, -1.0F, 1.0F);
            this.preRenderCallback(par1EntityLiving, par9);
            GL11.glTranslatef(0.0F, -24.0F * var14 - 0.0078125F, 0.0F);
            float var15 = par1EntityLiving.prevLimbYaw + (par1EntityLiving.limbYaw - par1EntityLiving.prevLimbYaw) * par9;
            float var16 = par1EntityLiving.limbSwing - par1EntityLiving.limbYaw * (1.0F - par9);

            if (par1EntityLiving.isChild())
            {
                var16 *= 3.0F;
            }

            if (var15 > 1.0F)
            {
                var15 = 1.0F;
            }

            GL11.glEnable(GL11.GL_ALPHA_TEST);
            this.mainModel.setLivingAnimations(par1EntityLiving, var16, var15, par9);
            this.renderModel(par1EntityLiving, var16, var15, var13, var11 - var10, var12, var14);
            float var19;
            int var18;
            float var20;
            float var22;

            for (int var17 = 0; var17 < 7; ++var17)
            {
                var18 = this.shouldRenderPass(par1EntityLiving, var17, par9);

                if (var18 > 0)
                {
                    this.renderPassModel.setLivingAnimations(par1EntityLiving, var16, var15, par9);
                    this.renderPassModel.render(par1EntityLiving, var16, var15, var13, var11 - var10, var12, var14);

                    if (var18 == 15)
                    {
                        var19 = (float)par1EntityLiving.ticksExisted + par9;
                        this.loadTexture("%blur%/misc/glint.png");
                        GL11.glEnable(GL11.GL_BLEND);
                        var20 = 0.5F;
                        GL11.glColor4f(var20, var20, var20, 1.0F);
                        GL11.glDepthFunc(GL11.GL_EQUAL);
                        GL11.glDepthMask(false);

                        for (int var21 = 0; var21 < 2; ++var21)
                        {
                            GL11.glDisable(GL11.GL_LIGHTING);
                            var22 = 0.76F;
                            GL11.glColor4f(0.5F * var22, 0.25F * var22, 0.8F * var22, 1.0F);
                            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                            GL11.glMatrixMode(GL11.GL_TEXTURE);
                            GL11.glLoadIdentity();
                            float var23 = var19 * (0.001F + (float)var21 * 0.003F) * 20.0F;
                            float var24 = 0.33333334F;
                            GL11.glScalef(var24, var24, var24);
                            GL11.glRotatef(30.0F - (float)var21 * 60.0F, 0.0F, 0.0F, 1.0F);
                            GL11.glTranslatef(0.0F, var23, 0.0F);
                            GL11.glMatrixMode(GL11.GL_MODELVIEW);
                            this.renderPassModel.render(par1EntityLiving, var16, var15, var13, var11 - var10, var12, var14);
                        }

                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                        GL11.glMatrixMode(GL11.GL_TEXTURE);
                        GL11.glDepthMask(true);
                        GL11.glLoadIdentity();
                        GL11.glMatrixMode(GL11.GL_MODELVIEW);
                        GL11.glEnable(GL11.GL_LIGHTING);
                        GL11.glDisable(GL11.GL_BLEND);
                        GL11.glDepthFunc(GL11.GL_LEQUAL);
                    }

                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                }
            }

            this.renderEquippedItems(par1EntityLiving, par9);
            float var26 = par1EntityLiving.getBrightness(par9);
            var18 = this.getColorMultiplier(par1EntityLiving, var26, par9);
            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

            if ((var18 >> 24 & 255) > 0 || par1EntityLiving.hurtTime > 0 || par1EntityLiving.deathTime > 0)
            {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDepthFunc(GL11.GL_EQUAL);

                if (par1EntityLiving.hurtTime > 0 || par1EntityLiving.deathTime > 0)
                {
                    GL11.glColor4f(var26, 0.0F, 0.0F, 0.4F);
                    this.mainModel.render(par1EntityLiving, var16, var15, var13, var11 - var10, var12, var14);

                    for (int var27 = 0; var27 < 4; ++var27)
                    {
                        if (this.inheritRenderPass(par1EntityLiving, var27, par9) >= 0)
                        {
                            GL11.glColor4f(var26, 0.0F, 0.0F, 0.4F);
                            this.renderPassModel.render(par1EntityLiving, var16, var15, var13, var11 - var10, var12, var14);
                        }
                    }
                }

                if ((var18 >> 24 & 255) > 0)
                {
                    var19 = (float)(var18 >> 16 & 255) / 255.0F;
                    var20 = (float)(var18 >> 8 & 255) / 255.0F;
                    float var29 = (float)(var18 & 255) / 255.0F;
                    var22 = (float)(var18 >> 24 & 255) / 255.0F;
                    GL11.glColor4f(var19, var20, var29, var22);
                    this.mainModel.render(par1EntityLiving, var16, var15, var13, var11 - var10, var12, var14);

                    for (int var28 = 0; var28 < 4; ++var28)
                    {
                        if (this.inheritRenderPass(par1EntityLiving, var28, par9) >= 0)
                        {
                            GL11.glColor4f(var19, var20, var29, var22);
                            this.renderPassModel.render(par1EntityLiving, var16, var15, var13, var11 - var10, var12, var14);
                        }
                    }
                }

                GL11.glDepthFunc(GL11.GL_LEQUAL);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }
        catch (Exception var25)
        {
            var25.printStackTrace();
        }

        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
        
        this.passSpecialRender(par1EntityLiving, par2, par4, par6);
    }
    
    private float func_77034_a(float par1, float par2, float par3)
    {
        float var4;

        for (var4 = par2 - par1; var4 < -180.0F; var4 += 360.0F)
        {
            ;
        }

        while (var4 >= 180.0F)
        {
            var4 -= 360.0F;
        }

        return par1 + par3 * var4;
    }
}
