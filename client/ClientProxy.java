package simplyhorses.client;

import java.util.EnumSet;

import simplyhorses.common.CommonProxy;
import simplyhorses.common.SimplyHorses;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import simplyhorses.client.entities.*;
import simplyhorses.common.entities.*;
import simplyhorses.common.entities.horses.*;
import simplyhorses.common.entities.vehicles.*;

public class ClientProxy extends CommonProxy{

	@Override
	public void registerRenderInformation() {
		
		Minecraft minecraft = FMLClientHandler.instance().getClient();
		//CoatHelper.loadCoatColors();
		//CoatHelper.setColorizer(FMLClientHandler.instance().getClient().renderEngine.getTextureContents("/mods/SimplyHorses/textures/horses/testing/coatcolor.png"));
		
		KeyBindingRegistry.registerKeyBinding(new KeyHandlerSteeringSH());
		KeyBindingRegistry.registerKeyBinding(new KeyHandlerSpawnerLassoSH());
		KeyBindingRegistry.registerKeyBinding(new KeyHandlerLassoSH());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityHorseWildSH.class, new RenderHorseSH(new ModelMustangSH(), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntityMustangSH.class, new RenderHorseSH(new ModelMustangSH(), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntityArabianSH.class, new RenderArabianSH(new ModelArabianSH(), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntityClydesdaleSH.class, new RenderClydesdaleSH(new ModelClydesdaleSH(), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntityFoalSH.class, new RenderHorseSH(new ModelFoalSH(), 0.7F));
		
		/*RenderingRegistry.registerEntityRenderingHandler(EntityHorseWildSH.class, new RenderHorseSH(new ModelMustangSH(0F), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntityMustangSH.class, new RenderHorseSH(new ModelMustangSH(0F), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntityArabianSH.class, new RenderArabianSH(new ModelArabianSH(0F), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntityClydesdaleSH.class, new RenderClydesdaleSH(new ModelClydesdaleSH(0F), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntityFoalSH.class, new RenderFoalSH(new ModelFoalSH(0F), 0.7F));*/
		
		RenderingRegistry.registerEntityRenderingHandler(EntityCartSH.class, new RenderDraughtTechSH(new ModelCartSH(), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntityWagonSH.class, new RenderDraughtTechSH(new ModelWagonSH(), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntityVardoSH.class, new RenderDraughtTechSH(new ModelVardoSH(), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntityWhippletreeSH.class, new RenderDraughtTechSH(new ModelWhippletreeSH(), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntitySwingletreeSH.class, new RenderDraughtTechSH(new ModelSwingletreeSH(), 0.7F));
		
		RenderingRegistry.registerEntityRenderingHandler(EntityLassoSH.class, new RenderLassoSH());
		RenderingRegistry.registerEntityRenderingHandler(EntityWagonChestSH.class, new Render(){
			@Override
			public void doRender(Entity var1, double var2, double var4,
					double var6, float var8, float var9) {
				
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityVehicleSeatSH.class, new Render(){
			@Override
			public void doRender(Entity var1, double var2, double var4,
					double var6, float var8, float var9) {
				
			}
		});
	}
	
	public void registerTickHandler() {
		TickRegistry.registerTickHandler(this, Side.CLIENT);
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		SimplyHorses.tickStart(type, tickData);
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel() {
		return "SimplyHorses";
	}
}
