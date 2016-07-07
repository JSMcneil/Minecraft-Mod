package simplyhorses.client.entities;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;


public class ModelWhippletreeSH extends ModelBase {

	// fields
	ModelRenderer whippletree;

	public ModelWhippletreeSH() {
		textureWidth = 128;
		textureHeight = 64;
		setTextureOffset("whippletree.leftHarness", 0, 0);
		setTextureOffset("whippletree.rightHarness", 0, 0);
		setTextureOffset("whippletree.leftBar", 0, 55);
		setTextureOffset("whippletree.rightBar", 0, 55);
		setTextureOffset("whippletree.leftHook", 0, 57);
		setTextureOffset("whippletree.rightHook", 0, 57);
		setTextureOffset("whippletree.mainBar", 0, 53);
		setTextureOffset("whippletree.mainHook", 0, 57);

		whippletree = new ModelRenderer(this, "whippletree");
		whippletree.setRotationPoint(0F, 0F, 0F);
		whippletree.addBox("leftHarness", 3F, 0F, -14F, 14, 24, 29);
		whippletree.addBox("rightHarness", -17F, 0F, -14F, 14, 24, 29);
		whippletree.addBox("leftBar", 3F, 6F, 15F, 14, 1, 1);
		whippletree.addBox("rightBar", -17F, 6F, 15F, 14, 1, 1);
		whippletree.addBox("leftHook", 9F, 6F, 16F, 1, 1, 3);
		whippletree.addBox("rightHook", -10F, 6F, 16F, 1, 1, 3);
		whippletree.addBox("mainBar", -11F, 6F, 19F, 22, 1, 1);
		whippletree.addBox("mainHook", -1F, 6F, 20F, 2, 1, 3);

	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		whippletree.render(f5);
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}

}
