package simplyhorses.client.entities;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSwingletreeSH extends ModelWhippletreeSH
{
  public ModelSwingletreeSH()
  {
    textureWidth = 128;
    textureHeight = 64;
    setTextureOffset("swingletree.harness", 0, 0);
    setTextureOffset("swingletree.bar", 0, 53);
    setTextureOffset("swingletree.hook", 0, 55);
    
    
    whippletree = new ModelRenderer(this, "swingletree");
    whippletree.setRotationPoint(0F, 0F, 0F);
    whippletree.addBox("harness", -7F, 0F, -14F, 14, 24, 29);
    whippletree.addBox("bar", -7F, 6F, 15F, 14, 1, 1);
    whippletree.addBox("hook", -1F, 6F, 16F, 2, 1, 3);
    
  }
}