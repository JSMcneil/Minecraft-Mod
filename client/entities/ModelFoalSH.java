package simplyhorses.client.entities;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import simplyhorses.common.entities.horses.EntityHorseSH;

public class ModelFoalSH extends ModelBase
{
  //fields
    ModelRenderer head;
    ModelRenderer body;
    ModelRenderer leg1;
    ModelRenderer leg2;
    ModelRenderer leg3;
    ModelRenderer leg4;
    ModelRenderer neckbottom;
    ModelRenderer necktop;
    ModelRenderer tail;
    private float nbSwing;
    private float ntSwing;
    private float headSwing;
    private boolean isSprinting;
  
  public ModelFoalSH()
  {
    textureWidth = 64;
    textureHeight = 32;
    setTextureOffset("head.main", 0, 0);
	setTextureOffset("head.ear1", 0, 27);
	setTextureOffset("head.ear2", 0, 27);
	setTextureOffset("head.muzzle", 0, 8);
	setTextureOffset("body.body", 24, 0);
	setTextureOffset("tail.main", 24, 16);
	setTextureOffset("leg1.leg", 16, 0);
	setTextureOffset("leg2.leg", 16, 0);
	setTextureOffset("leg3.leg", 16, 0);
	setTextureOffset("leg4.leg", 16, 0);
	setTextureOffset("neckbottom.main", 0, 15);
	
      body = new ModelRenderer(this, "body");
	  body.setRotationPoint(0F, 6F, 0F);  //This is not a child. It's .setRotationPoint is relative to the entire model's origin in Techne.
      body.addBox("body", -3F, -3F, -5F, 6, 6, 10); //The first three values are the Offset values. The last three are dimensions.
	  
	  tail = new ModelRenderer(this, "tail");
	  tail.setRotationPoint(0F, -3F, 4F); //since this is a child, this is relative to the parent's .setRotationPoint. It can be calculated by subtracted its X value from the Parent's X value, etc. You may need to play with signs afterwards.
	  tail.addBox("main", -1F, 0F, 0F, 2, 6, 7); //these Offset values can be taken straight from Techne, as can the dimensions. All addbox's must have their "Position" values the same as .setRotationPoint.
	  body.addChild(tail);
      
      leg1 = new ModelRenderer(this, "leg1");
	  leg1.setRotationPoint(-2F, 9F, 5F);
      leg1.addBox("leg", -1F, 0F, -1F, 2, 15, 2);
      
      leg2 = new ModelRenderer(this, "leg2");
      leg2.setRotationPoint(2F, 9F, 5F);
	  leg2.addBox("leg", -1F, 0F, -1F, 2, 15, 2);
      
      leg3 = new ModelRenderer(this, "leg3");
      leg3.setRotationPoint(-2F, 9F, -2.8F);
      leg3.addBox("leg", -1F, 0F, -1F, 2, 15, 2);
      
      leg4 = new ModelRenderer(this, "leg4");
      leg4.setRotationPoint(2F, 9F, -2.8F);
      leg4.addBox("leg", -1F, 0F, -1F, 2, 15, 2);
      
      neckbottom = new ModelRenderer(this, "neckbottom");
	  neckbottom.setRotationPoint(0F, 6F, -2F);
	  neckbottom.addBox("main", -1F, -8F, -3F, 2, 8, 4);

	  head = new ModelRenderer(this, "head");
	  head.setRotationPoint(0F, -8F, 1F);
	  head.addBox("main", -2F, -4F, -4F, 4, 4, 4);
	  head.addBox("muzzle", -2F, -4F, -7F, 4, 4, 3);
	  head.addBox("ear1", 1F, -6F, -1F, 1, 2, 1);
	  head.addBox("ear2", -2F, -6F, -1F, 1, 2, 1);
	  neckbottom.addChild(head);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    body.render(f5);
    leg1.render(f5);
    leg2.render(f5);
    leg3.render(f5);
    leg4.render(f5);
    neckbottom.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  public void setLivingAnimations(EntityLiving entityliving, float f, float f1, float f2)
  {
      super.setLivingAnimations(entityliving, f, f1, f2);
      nbSwing = (((EntityHorseSH)entityliving).nbSwing(f2));
      headSwing = (((EntityHorseSH)entityliving).headSwing(f2));
      isSprinting = ((EntityHorseSH)entityliving).isSprinting();
      
  }
  
  public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    head.rotateAngleX = headSwing;
    head.rotateAngleY = f3 / 120.0F;
    
    body.rotateAngleX = 0F;
    
    if (isSprinting){
    	leg1.rotateAngleX = MathHelper.cos(f * 0.6662F) * 0.7F * f1;
    	leg2.rotateAngleX = MathHelper.cos(f * 0.6662F + 0.7F) * 0.7F * f1;
    	leg3.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI + 0.7F) * 0.7F * f1;
    	leg4.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 0.7F * f1;
    }else{
	    leg1.rotateAngleX = MathHelper.cos(f * 0.6662F) * 0.5F * f1;
	    leg2.rotateAngleX = MathHelper.cos(f * 0.6662F + 3.141593F) * 0.5F * f1;
	    leg3.rotateAngleX = MathHelper.cos(f * 0.6662F + 3.141593F) * 0.5F * f1;
	    leg4.rotateAngleX = MathHelper.cos(f * 0.6662F) * 0.5F * f1;
    }
    
    tail.rotateAngleX = 0.4642F;
    
    neckbottom.rotateAngleX = nbSwing;
  }

}