package simplyhorses.client.entities;


import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

public class ModelCartSH extends ModelVehicleSH {
	ModelRenderer wheelL;
	ModelRenderer wheelR;

	public ModelCartSH() {

		textureWidth = 128;
		textureHeight = 256;
		setTextureOffset("body.main", 0, 33);
		setTextureOffset("body.basin", 0, 0);
		setTextureOffset("body.undercarriage", 0, 69);
		setTextureOffset("body.seat", 0, 133);
		setTextureOffset("body.barL", 0, 94);
		setTextureOffset("body.barR", 0, 94);
		setTextureOffset("body.axel", 0, 120);
		setTextureOffset("wheelL.spoke", 0, 122);
		setTextureOffset("wheelL.rim", 6, 122);
		setTextureOffset("wheelR.spoke", 0, 122);
		setTextureOffset("wheelR.rim", 6, 122);

		body = new ModelRenderer(this, "body");
		body.setRotationPoint(0F, 13F, 0F);
		body.addBox("main", -13F, -14F, -13F, 26, 10, 26);
		body.addBox("basin", -12F, -14F, -12F, 24, 9, 24);
		body.addBox("undercarriage", -10F, -4F, -10F, 20, 5, 20);
		body.addBox("seat", -13F, -9F, -23F, 26, 1, 10);
		body.addBox("barL", 10F, -6F, -37F, 2, 2, 24);
		body.addBox("barR", -12F, -6F, -37F, 2, 2, 24);
		body.addBox("axel", -16F, -0.5F, -1F, 32, 1, 1);

		wheelL = new ModelRenderer(this, "wheelL");
		wheelL.addBox("spoke", 0F, 0F, -1F, 1, 9, 2);
		wheelL.addBox("rim", -1F, 9F, -4.5F, 3, 2, 9);
		wheelL.setRotationPoint(14F, 13F, -0.5F);

		wheelR = new ModelRenderer(this, "wheelR");
		wheelR.addBox("spoke", 0F, 0F, -1F, 1, 9, 2);
		wheelR.addBox("rim", -1F, 9F, -4.5F, 3, 2, 9);
		wheelR.setRotationPoint(-15F, 13F, -0.5F);
	}

	@Override
	public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {
		setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);

		body.render(par7);
		for (int i = 0; i < 8; i++) {
			wheelL.rotateAngleX = par2 * 0.4F + i * 0.785F;
			wheelL.render(par7);

			wheelR.rotateAngleX = par2 * 0.4F + i * 0.785F;
			wheelR.render(par7);
		}
	}
}
