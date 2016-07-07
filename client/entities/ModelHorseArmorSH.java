package simplyhorses.client.entities;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;
import simplyhorses.common.entities.horses.EntityHorseSH;

@SideOnly(Side.CLIENT)
public class ModelHorseArmorSH extends ModelBase {
	// fields
	ModelRendererSH head;
	ModelRendererSH body;
	ModelRendererSH neckbottom;
	ModelRendererSH necktop;
	private float nbSwing;
	private float ntSwing;
	private float headSwing;
	private boolean isSprinting;
	private boolean isRidden;

	public ModelHorseArmorSH(float par1) {
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
		body.addBox("body", -6F, -3F, -10F, 12, 10, 26, par1);

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
		head.addBox("muzzle", -3F, -3F, -10F, 6, 7, 3, par1);
		head.addBox("ear1", 1F, -5F, -1F, 2, 2, 1, par1);
		head.addBox("ear2", -3F, -5F, -1F, 2, 2, 1, par1);
		necktop.addChild(head);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		body.render(f5);
		neckbottom.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setLivingAnimations(EntityLiving entityliving, float f,
			float f1, float f2) {
		super.setLivingAnimations(entityliving, f, f1, f2);
		nbSwing = (((EntityHorseSH) entityliving).nbSwing(f2));
		ntSwing = (((EntityHorseSH) entityliving).ntSwing(f2));
		headSwing = (((EntityHorseSH) entityliving).headSwing(f2));
		isSprinting = ((EntityHorseSH) entityliving).isSprinting();
		isRidden = ((EntityHorseSH) entityliving).riddenByEntity != null && ((EntityHorseSH) entityliving).riddenByEntity instanceof EntityPlayer;

	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		body.rotateAngleX = 0F;

		// Controlling head and neck animations
		head.rotateAngleX = headSwing;

		if (isSprinting) { // larger head movements while galloping.
			neckbottom.rotateAngleX = nbSwing + swingPlus(f1) * 0.2F
					+ MathHelper.cos(f * 0.6F * 0.6662F) * 0.5F * (f1 * 0.5F);
			necktop.rotateAngleX = ntSwing - MathHelper.cos(f * 0.6F * 0.6662F)
					* 0.5F * (f1 * 0.5F);
		} else { // Smaller head movements while trotting.
			neckbottom.rotateAngleX = nbSwing + swingPlus(f1) * 0.2F
					+ MathHelper.cos(f * 0.6F * 0.6662F) * 0.5F * (f1 * 0.2F);
			necktop.rotateAngleX = ntSwing - MathHelper.cos(f * 0.6F * 0.6662F)
					* 0.5F * (f1 * 0.2F);
		}

		if (!isRidden) {
			head.rotateAngleY = f3 / 57.29578F; // horse should only look around
												// if not being ridden
		} else {
			head.rotateAngleY = 0;
		}
	}

	public float swingPlus(float f1) {
		if (f1 <= 0.1) {
			return 0;
		}
		return 1;
	}
}