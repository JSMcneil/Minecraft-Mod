package simplyhorses.client;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import simplyhorses.common.PacketHandlerSH;
import simplyhorses.common.SimplyHorses;
import simplyhorses.common.items.ItemLassoSH;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class KeyHandlerLassoSH extends KeyHandler {

	static KeyBinding mode = new KeyBinding("Lasso: Mode", Keyboard.KEY_M);
	
	public KeyHandlerLassoSH(){
		super(new KeyBinding[]{mode}, new boolean[]{false});
	}
	
	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		if (tickEnd && FMLClientHandler.instance().getClient().inGameHasFocus && FMLClientHandler.instance().getClient().currentScreen == null){
			
			EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;
			
			if (player.getCurrentEquippedItem() == null || player.getCurrentEquippedItem().itemID != SimplyHorses.lasso.itemID) return;
			
			int lassoMode = 0;
			
			if(kb.keyDescription == mode.keyDescription){
				lassoMode = ItemLassoSH.getLassoMode(player) == 0? 1: 0;
			}
			
			if (player.worldObj.isRemote){
				PacketHandlerSH.sendPacketKeyPressLasso(lassoMode);
				ItemLassoSH.handleModeChange(player, lassoMode);
			}
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.WORLD, TickType.WORLDLOAD, TickType.CLIENT, TickType.PLAYER);
	}

}
