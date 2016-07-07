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
public class ModelHorseSH extends ModelBase {
	// fields
	ModelRendererSH head;
	ModelRendererSH body;
	ModelRendererSH legReRi;
	ModelRendererSH legReLe;
	ModelRendererSH legFrRi;
	ModelRendererSH legFrLe;
	ModelRendererSH neckbottom;
	ModelRendererSH necktop;
	ModelRendererSH tail;
	private float nbSwing;
	private float ntSwing;
	private float headSwing;
	private boolean isSprinting;
	private boolean isWalking;
	private boolean isRidden;

	public ModelHorseSH() {
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

		body = (ModelRendererSH) new ModelRendererSH(this, "body");
		body.setRotationPoint(0F, 1F, 2F);
		body.addBox("body", -6F, -3F, -10F, 12, 10, 26);

		tail = (ModelRendererSH) new ModelRendererSH(this, "tail");
		tail.setRotationPoint(0F, -2F, 14F);
		tail.addBox("main", -2F, -6F, 0F, 2, 6, 17);
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
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		body.render(f5);
		legReRi.render(f5);
		legReLe.render(f5);
		legFrRi.render(f5);
		legFrLe.render(f5);
		neckbottom.render(f5);
	}

	public void setLivingAnimations(EntityLiving entityliving, float f, float f1, float f2) {
		super.setLivingAnimations(entityliving, f, f1, f2);
		nbSwing = (((EntityHorseSH) entityliving).nbSwing(f2));
		ntSwing = (((EntityHorseSH) entityliving).ntSwing(f2));
		headSwing = (((EntityHorseSH) entityliving).headSwing(f2));
		isSprinting = ((EntityHorseSH) entityliving).isSprinting();
		isWalking = ((EntityHorseSH) entityliving).isWalking();
		isRidden = ((EntityHorseSH) entityliving).riddenByEntity != null && ((EntityHorseSH) entityliving).riddenByEntity instanceof EntityPlayer;

	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		body.rotateAngleX = 0F;

		// Controlling leg and tail animations
		if (isSprinting) {
			legReRi.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.0F * f1;
			legReLe.rotateAngleX = MathHelper.cos(f * 0.6662F + 0.7F) * 1.0F * f1;
			legFrRi.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI + 0.7F) * 1.0F * f1;
			legFrLe.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.0F * f1;

			tail.rotateAngleX = -0.2F + MathHelper.cos(f * 0.6662F + 3.141593F) * 0.2F * f1;
		} else if (isWalking) {
			legReRi.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
			legReLe.rotateAngleX = MathHelper.cos(f * 0.6662F + 3.141593F) * 1.4F * f1;
			legFrRi.rotateAngleX = MathHelper.cos(f * 0.6662F + 1.570796F) * 1.4F * f1;
			legFrLe.rotateAngleX = MathHelper.cos(f * 0.6662F + 3.141593F + 1.570796F) * 1.4F * f1;

			tail.rotateAngleX = -1.1345F + swingPlus(f1) * 0.2F + MathHelper.cos(f * 0.6662F + 3.141593F) * 0.5F * f1;
		} else {
			legReRi.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
			legReLe.rotateAngleX = MathHelper.cos(f * 0.6662F + 3.141593F) * 1.4F * f1;
			legFrRi.rotateAngleX = MathHelper.cos(f * 0.6662F + 3.141593F) * 1.4F * f1;
			legFrLe.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;

			tail.rotateAngleX = -0.5345F + MathHelper.cos(f * 0.6662F + 3.141593F) * 0.2F * f1;
		}

		// Controlling head and neck animations
		head.rotateAngleX = headSwing;

		if (isSprinting) { // larger head movements while galloping.
			neckbottom.rotateAngleX = nbSwing + swingPlus(f1) * 0.2F + MathHelper.cos(f * 0.6F * 0.6662F) * 0.5F * (f1 * 0.5F);
			necktop.rotateAngleX = ntSwing - MathHelper.cos(f * 0.6F * 0.6662F) * 0.5F * (f1 * 0.5F);
		} else { // Smaller head movements while trotting.
			neckbottom.rotateAngleX = nbSwing + swingPlus(f1) * 0.2F + MathHelper.cos(f * 0.6F * 0.6662F) * 0.5F * (f1 * 0.2F);
			necktop.rotateAngleX = ntSwing - MathHelper.cos(f * 0.6F * 0.6662F) * 0.5F * (f1 * 0.2F);
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