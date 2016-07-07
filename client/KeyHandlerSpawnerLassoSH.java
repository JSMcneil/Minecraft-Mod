package simplyhorses.client;

import java.util.EnumSet;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import simplyhorses.common.PacketHandlerSH;
import simplyhorses.common.SimplyHorses;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class KeyHandlerSpawnerLassoSH extends KeyHandler{
	
	static KeyBinding mode = new KeyBinding("Spawner Lasso: Mode", Keyboard.KEY_M);
	static KeyBinding toggleFoal = new KeyBinding("Spawner Lasso: Toggle Entity Action", Keyboard.KEY_K);
	
	public KeyHandlerSpawnerLassoSH() {
		super(new KeyBinding[]{mode, toggleFoal}, new boolean[]{false, false});
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		if (tickEnd && FMLClientHandler.instance().getClient().inGameHasFocus && FMLClientHandler.instance().getClient().currentScreen == null){
			
			EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;
			
			if (player.getCurrentEquippedItem() == null || player.getCurrentEquippedItem().itemID != SimplyHorses.spawnLasso.itemID) return;
			
			int keyType = 0;
			
			if(kb.keyDescription == mode.keyDescription){
				keyType = 0;
			} else if(kb.keyDescription == toggleFoal.keyDescription){
				keyType = 1;
			}
			
			if (player.worldObj.isRemote){
				PacketHandlerSH.sendPacketKeyPressSpawnerLasso(keyType);
			}
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.WORLD, TickType.WORLDLOAD, TickType.CLIENT, TickType.PLAYER);
	}
}
