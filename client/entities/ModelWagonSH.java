package simplyhorses.client.entities;

import net.minecraft.client.model.ModelRenderer;


public class ModelWagonSH extends ModelVehicleSH {

	public ModelWagonSH() {
		textureWidth = 256;
		textureHeight = 256;
		setTextureOffset("body.main", 0, 54);
		setTextureOffset("body.basin", 0, 0);
		setTextureOffset("body.undercarriage", 0, 104);
		setTextureOffset("body.seat", 0, 180);
		setTextureOffset("body.bar", 0, 141);
		setTextureOffset("body.axelF", 0, 167);
		setTextureOffset("body.axelB", 0, 167);
		setTextureOffset("body.chestF", 96, 176);
		setTextureOffset("body.chestB", 96, 176);
		setTextureOffset("wheelFL.spoke", 0, 169);
		setTextureOffset("wheelFL.rim", 6, 169);
		setTextureOffset("wheelFR.spoke", 0, 169);
		setTextureOffset("wheelFR.rim", 6, 169);
		setTextureOffset("wheelBL.spoke", 0, 169);
		setTextureOffset("wheelBL.rim", 6, 169);
		setTextureOffset("wheelBR.spoke", 0, 169);
		setTextureOffset("wheelBR.rim", 6, 169);

		body = new ModelRenderer(this, "body");
		body.setRotationPoint(0F, 13F, 0F);
		body.addBox("main", -13F, -14F, -20F, 26, 10, 40);
		body.addBox("basin", -12F, -14F, -19F, 24, 9, 38);
		body.addBox("undercarriage", -10F, -4F, -16F, 20, 5, 32);
		body.addBox("seat", -13F, -9F, -30F, 26, 1, 10);
		body.addBox("bar", -1F, -6F, -44F, 2, 2, 24);
		body.addBox("axelF", -16F, -0.5F, -15F, 32, 1, 1);
		body.addBox("axelB", -16F, -0.5F, 14F, 32, 1, 1);
		body.addBox("chestF", -8F, -21F, -18F, 16, 16, 16);
		body.addBox("chestB", -8F, -21F, 1F, 16, 16, 16);

		wheelFL = new ModelRenderer(this, "wheelFL");
		wheelFL.addBox("spoke", 0F, 0F, -1F, 1, 9, 2);
		wheelFL.addBox("rim", -1F, 9F, -4.5F, 3, 2, 9);
		wheelFL.setRotationPoint(14F, 13F, -14.5F);

		wheelFR = new ModelRenderer(this, "wheelFR");
		wheelFR.addBox("spoke", 0F, 0F, -1F, 1, 9, 2);
		wheelFR.addBox("rim", -1F, 9F, -4.5F, 3, 2, 9);
		wheelFR.setRotationPoint(-15F, 13F, -14.5F);

		wheelBL = new ModelRenderer(this, "wheelBL");
		wheelBL.addBox("spoke", 0F, 0F, -1F, 1, 9, 2);
		wheelBL.addBox("rim", -1F, 9F, -4.5F, 3, 2, 9);
		wheelBL.setRotationPoint(14F, 13F, 14.5F);

		wheelBR = new ModelRenderer(this, "wheelBR");
		wheelBR.addBox("spoke", 0F, 0F, -1F, 1, 9, 2);
		wheelBR.addBox("rim", -1F, 9F, -4.5F, 3, 2, 9);
		wheelBR.setRotationPoint(-15F, 13F, 14.5F);
	}
}
