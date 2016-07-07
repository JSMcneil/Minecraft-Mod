package simplyhorses.client.entities;


import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import simplyhorses.common.entities.horses.EntityHorseWildSH;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelMustangSH extends ModelHorseSH
{
  public ModelMustangSH()
  {
    textureWidth = 128;
    textureHeight = 64;
    setTextureOffset("head.main", 0, 0);
	setTextureOffset("head.ear1", 18, 14);
	setTextureOffset("head.ear2", 18, 14);
	setTextureOffset("head.muzzle", 0, 14);
	setTextureOffset("body.body", 42, 0);
	setTextureOffset("tail.main", 24, 38);
	setTextureOffset("legReRi.leg", 26, 0);
	setTextureOffset("legReLe.leg", 26, 0);
	setTextureOffset("legFrRi.leg", 26, 0);
	setTextureOffset("legFrLe.leg", 26, 0);
	setTextureOffset("necktop.main", 0, 24);
	setTextureOffset("neckbottom.main", 0, 38);
	
      body = new ModelRendererSH(this, "body");
	  body.setRotationPoint(0F, 1F, 2F);  //This is not a child. It's .setRotationPoint is relative to the entire model's origin in Techne.
      body.addBox("body", -6F, -3F, -10F, 12, 10, 26); //The first three values are the Offset values. The last three are dimensions.
	  
	  tail = new ModelRendererSH(this, "tail");
	  tail.setRotationPoint(1F, -2F, 14F); //since this is a child, this is relative to the parent's .setRotationPoint. It can be calculated by subtracted its X value from the Parent's X value, etc. You may need to play with signs afterwards.
	  tail.addBox("main", -2F, -6F, 0F, 2, 6, 17); //these Offset values can be taken straight from Techne, as can the dimensions. All addbox's must have their "Position" values the same as .setRotationPoint.
	  body.addChild(tail);
      
      legReRi = new ModelRendererSH(this, "legReRi");
	  legReRi.setRotationPoint(-3F, 8F, 18F);
      legReRi.addBox("leg", -3F, 0F, -2F, 4, 16, 4);
      
      legReLe = new ModelRendererSH(this, "legReLe");
      legReLe.setRotationPoint(3F, 8F, 18F);
	  legReLe.addBox("leg", -1F, 0F, -2F, 4, 16, 4);
      
      legFrRi = new ModelRendererSH(this, "legFrRi");
      legFrRi.setRotationPoint(-3F, 8F, -1.8F);
      legFrRi.addBox("leg", -3F, 0F, -3F, 4, 16, 4);
      
      legFrLe = new ModelRendererSH(this, "legFrLe");
      legFrLe.setRotationPoint(3F, 8F, -1.8F);
      legFrLe.addBox("leg", -1F, 0F, -3F, 4, 16, 4);
      
      neckbottom = new ModelRendererSH(this, "neckbottom");
	  neckbottom.setRotationPoint(0F, 3F, -3F);
	  neckbottom.addBox("main", -2F, -15F, -4F, 4, 16, 8);
	  
	  necktop = new ModelRendererSH(this, "necktop");
	  necktop.setRotationPoint(0F, -15F, 4F);
	  necktop.addBox("main", -1F, 0F, -6F, 2, 8, 6);
	  neckbottom.addChild(necktop);

	  head = new ModelRendererSH(this, "head");
	  head.setRotationPoint(0F, 2F, -4F);
	  head.addBox("main", -3F, -3F, -7F, 6, 7, 7);
	  head.addBox("muzzle", -3F, -3F, -10F, 6, 7, 3);
	  head.addBox("ear1", 1F, -5F, -1F, 2, 2, 1);
	  head.addBox("ear2", -3F, -5F, -1F, 2, 2, 1);
	  necktop.addChild(head);
  }
  


	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		if (entity instanceof EntityHorseWildSH && ((EntityHorseWildSH) entity).isStallion()){
			float s = 1.1F;
			
			setRotationAngles(f, f1, f2, f3, f4, f5, entity);
			GL11.glPushMatrix();
	        GL11.glScalef(s, s, s);
	        GL11.glTranslatef(0.0F, -0.125F, 0.0F);
			body.render(f5);
			legReRi.render(f5);
			legReLe.render(f5);
			legFrRi.render(f5);
			legFrLe.render(f5);
			neckbottom.render(f5);
			GL11.glPopMatrix();
		} else{
			super.render(entity, f, f1, f2, f3, f4, f5);
		}
	}
}