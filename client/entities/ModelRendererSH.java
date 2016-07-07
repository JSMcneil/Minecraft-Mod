package simplyhorses.client.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.TextureOffset;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRendererSH extends ModelRenderer
{
    private Object baseModel;
	private int textureOffsetX;
	private int textureOffsetY;
	boolean compiled;
	int displayList;
	
	public ModelRendererSH(ModelRendererSH modelrenderer){
		this((ModelBase) modelrenderer.baseModel, (String)null);
		setTextureOffset(textureOffsetX, textureOffsetY);
		setRotationPoint(modelrenderer.rotationPointX, modelrenderer.rotationPointY, modelrenderer.rotationPointZ);
		cubeList = modelrenderer.cubeList;
		childModels = modelrenderer.childModels;
	}

	public ModelRendererSH(ModelBase par1ModelBase, String par2Str)
    {
    	super(par1ModelBase, par2Str);
    	
    	baseModel = par1ModelBase;
    }

    public ModelRendererSH(ModelBase par1ModelBase)
    {
        this(par1ModelBase, (String)null);
    }

    public ModelRendererSH(ModelBase par1ModelBase, int par2, int par3)
    {
        this(par1ModelBase);
        this.setTextureOffset(par2, par3);
    }

	public void addBox(String par0Str, float par1, float par2, float par3, int par4, int par5, int par6, float par7)
    {
    	par0Str = this.boxName + "." + par0Str;
        TextureOffset var8 = ((ModelBase) this.baseModel).getTextureOffset(par0Str);
        this.setTextureOffset(var8.textureOffsetX, var8.textureOffsetY);
        this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, par1, par2, par3, par4, par5, par6, par7));
    }
    
    @Override
    public ModelRenderer setTextureOffset(int par1, int par2)
    {
        this.textureOffsetX = par1;
        this.textureOffsetY = par2;
        
        super.setTextureOffset(par1, par2);
        return this;
    }
    
    @SideOnly(Side.CLIENT)
    public void render(float par1)
    {
        if (!this.isHidden)
        {
            if (this.showModel)
            {
                if (!this.compiled)
                {
                    this.compileDisplayList(par1);
                }

                GL11.glTranslatef(this.field_82906_o, this.field_82908_p, this.field_82907_q);
                int i;

                if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F)
                {
                    if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F)
                    {
                        GL11.glCallList(this.displayList);

                        if (this.childModels != null){
                        	renderChildModels(par1);
                        }
                    }
                    else
                    {
                        GL11.glTranslatef(this.rotationPointX * par1, this.rotationPointY * par1, this.rotationPointZ * par1);
                        GL11.glCallList(this.displayList);

                        if (this.childModels != null)
                        {
                        	renderChildModels(par1);
                        }

                        GL11.glTranslatef(-this.rotationPointX * par1, -this.rotationPointY * par1, -this.rotationPointZ * par1);
                    }
                }
                else
                {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(this.rotationPointX * par1, this.rotationPointY * par1, this.rotationPointZ * par1);

                    if (this.rotateAngleZ != 0.0F)
                    {
                        GL11.glRotatef(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
                    }

                    if (this.rotateAngleY != 0.0F)
                    {
                        GL11.glRotatef(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
                    }

                    if (this.rotateAngleX != 0.0F)
                    {
                        GL11.glRotatef(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
                    }

                    GL11.glCallList(this.displayList);

                    if (this.childModels != null){
                    	renderChildModels(par1);
                    }

                    GL11.glPopMatrix();
                }

                GL11.glTranslatef(-this.field_82906_o, -this.field_82908_p, -this.field_82907_q);
            }
        }
    }
    
    public void renderChildModels(float par1){
        for (int i = 0; i < this.childModels.size(); ++i)
        {
            ((ModelRenderer)this.childModels.get(i)).render(par1);
        }
    }
    
    @SideOnly(Side.CLIENT)
    private void compileDisplayList(float par1)
    {
        this.displayList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(this.displayList, GL11.GL_COMPILE);
        Tessellator tessellator = Tessellator.instance;

        for (int i = 0; i < this.cubeList.size(); ++i)
        {
            ((ModelBox)this.cubeList.get(i)).render(tessellator, par1);
        }

        GL11.glEndList();
        this.compiled = true;
    }
}
