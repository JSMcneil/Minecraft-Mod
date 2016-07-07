package simplyhorses.client.entities;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelVehicleSH extends ModelBase {

	ModelRenderer body;
    ModelRenderer wheelFL;
    ModelRenderer wheelFR;
    ModelRenderer wheelBL;
    ModelRenderer wheelBR;
  
  public ModelVehicleSH()
  {
    textureWidth = 256;
    textureHeight = 256;
	setTextureOffset("body.main", 0, 60);
	setTextureOffset("body.roof", 0, 0);
	setTextureOffset("body.undercarriage", 0, 128);
	setTextureOffset("body.seat", 0, 198);
	setTextureOffset("body.bar", 0, 168);
	setTextureOffset("body.door", 52, 168);
	setTextureOffset("body.axelF", 0, 195);
	setTextureOffset("body.axelB", 0, 195);
	setTextureOffset("wheelFL.spoke", 0, 210);
	setTextureOffset("wheelFL.rim", 6, 210);
	setTextureOffset("wheelFR.spoke", 0, 210);
	setTextureOffset("wheelFR.rim", 6, 210);
	setTextureOffset("wheelBL.spoke", 30, 210);
	setTextureOffset("wheelBL.rim", 36, 210);
	setTextureOffset("wheelBR.spoke", 30, 210);
	setTextureOffset("wheelBR.rim", 36, 210);
    
	body = new ModelRenderer(this, "body");
	body.setRotationPoint(0F, -7F, 0F);
	body.addBox("main", -13F, -14F, -20F, 26, 28, 40);
	body.addBox("roof", -14F, -15F, -21F, 28, 18, 42);
	body.addBox("undercarriage", -10F, 14F, -16F, 20, 8, 32);
	body.addBox("seat", -13F, 5F, -31F, 26, 1, 11);
	body.addBox("bar", -1F, 12F, -44F, 2, 2, 24);
	body.addBox("door", -7F, -12F, 20F, 14, 24, 1);
	body.addBox("axelF", -16F, 19.5F, -15F, 32, 1, 1);
	body.addBox("axelB", -16F, 14.5F, 14F, 32, 1, 1);
	
	wheelFL = new ModelRenderer(this, "wheelFL");
	wheelFL.addBox("spoke", 0F, 0F, -1F, 1, 9, 2);
	wheelFL.addBox("rim", -1F, 9F, -4.5F, 3, 2, 9);
	wheelFL.setRotationPoint(15F, 13F, -14.5F);
	
	wheelFR = new ModelRenderer(this, "wheelFR");
	wheelFR.addBox("spoke", 0F, 0F, -1F, 1, 9, 2);
	wheelFR.addBox("rim", -1F, 9F, -4.5F, 3, 2, 9);
	wheelFR.setRotationPoint(-16F, 13F, -14.5F);
    
    wheelBL = new ModelRenderer(this, "wheelBL");
    wheelBL.addBox("spoke", 0F, 0F, -1F, 1, 14, 2);
    wheelBL.addBox("rim", -1F, 14F, -6.5F, 3, 2, 13);
    wheelBL.setRotationPoint(15F, 8F, 14.5F);
    
    wheelBR = new ModelRenderer(this, "wheelBR");
    wheelBR.addBox("spoke", 0F, 0F, -1F, 1, 14, 2);
    wheelBR.addBox("rim", -1F, 14F, -6.5F, 3, 2, 13);
    wheelBR.setRotationPoint(-16F, 8F, 14.5F);
    
  }
  
  @Override
  public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
  {
	  setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
	  
	  body.render(par7);
	  for (int i = 0; i < 8; i++){
		  wheelFL.rotateAngleX = par2 * 0.4F + i * 0.785F;
		  wheelFL.render(par7);
		  wheelFR.rotateAngleX = par2 * 0.4F + i * 0.785F;
		  wheelFR.render(par7);
		  
		  wheelBL.rotateAngleX = par2 * 0.4F + i * 0.785F;
		  wheelBL.render(par7);
		  wheelBR.rotateAngleX = par2 * 0.4F + i * 0.785F;
		  wheelBR.render(par7);
	  }
  }
  
  @Override
  public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    
    body.rotateAngleZ = MathHelper.cos(f * 0.6662F) * 0.03F * f1;
  }
}
