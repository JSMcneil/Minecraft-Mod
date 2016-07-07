package simplyhorses.client.entities;

public class ModelArabianArmorSH extends ModelHorseArmorSH {

	public ModelArabianArmorSH(float par1) {
		super(par1);
		
		textureWidth = 128;
	    textureHeight = 64;
	    setTextureOffset("head.main", 0, 0);
		setTextureOffset("head.ear1", 18, 14);
		setTextureOffset("head.ear2", 18, 14);
		setTextureOffset("head.muzzle", 0, 14);
		setTextureOffset("body.body", 42, 0);
		setTextureOffset("necktop.main", 0, 24);
		setTextureOffset("neckbottom.main", 0, 38);
		
		body = new ModelRendererSH(this, "body");
		body.setRotationPoint(0F, 1F, 2F);
		body.addBox("body", -5F, -3F, -10F, 10, 10, 26, par1);
		
		neckbottom = new ModelRendererSH(this, "neckbottom");
		neckbottom.setRotationPoint(0F, 3F, -3F);
		neckbottom.addBox("main", -2F, -15F, -4F, 4, 16, 8, par1);
		
		necktop = new ModelRendererSH(this, "necktop");
		necktop.setRotationPoint(0F, -15F, 4F);
		necktop.addBox("main", -1F, 0F, -6F, 2, 8, 6, par1);
		neckbottom.addChild(necktop);
		
		head = new ModelRendererSH(this, "head");
		head.setRotationPoint(0F, 2F, -4F);
		head.addBox("main", -3F, -3F, -7F, 6, 7, 7, par1);
		head.addBox("muzzle", -3F, -1F, -10F, 6, 4, 3, par1);
		head.addBox("ear1", 1F, -5F, -1F, 2, 2, 1, par1);
		head.addBox("ear2", -3F, -5F, -1F, 2, 2, 1, par1);
		necktop.addChild(head);
	}

}
