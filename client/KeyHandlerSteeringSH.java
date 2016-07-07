package simplyhorses.client;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import simplyhorses.common.PacketHandlerSH;
import simplyhorses.common.entities.horses.EntityHorseRideableSH;
import simplyhorses.common.entities.vehicles.EntityVehicleSH;
import simplyhorses.common.entities.vehicles.EntityVehicleSeatSH;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class KeyHandlerSteeringSH extends KeyHandler{

	static KeyBinding walkOn = new KeyBinding("Mounted Walk On", Keyboard.KEY_W);
	static KeyBinding reignIn = new KeyBinding("Mounted Reign-In", Keyboard.KEY_S);
	static KeyBinding steerLeft = new KeyBinding("Mounted Left", Keyboard.KEY_A);
	static KeyBinding steerRight = new KeyBinding("Mounted Right", Keyboard.KEY_D);
	static KeyBinding hardStop = new KeyBinding("Mounted Hard-Stop", Keyboard.KEY_LSHIFT);
	static KeyBinding jump = new KeyBinding("Mounted Jump", Keyboard.KEY_SPACE);
	static KeyBinding drive = new KeyBinding("Take the Reigns!", Keyboard.KEY_R);
	static KeyBinding seat = new KeyBinding("Change Seats", Keyboard.KEY_F);
	
	Minecraft minecraft;
	GameSettings settings;
	
	public KeyHandlerSteeringSH() {
		super(new KeyBinding[]{walkOn, reignIn, steerLeft, steerRight, hardStop, jump, drive, seat}, new boolean[]{false, false, false, false, false, false, false, false});
		minecraft = FMLClientHandler.instance().getClient();
		settings = FMLClientHandler.instance().getClient().gameSettings;
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		EntityClientPlayerMP player = minecraft.thePlayer;
		
		if (tickEnd && minecraft.inGameHasFocus && minecraft.currentScreen == null){
			if (playerIsRiding(player)){
				int keyType = 0;
				
				if(kb.keyDescription == walkOn.keyDescription){
					keyType = 0;
				} 
				else if(kb.keyDescription == reignIn.keyDescription){
					keyType = 1; //2 is KeyUp for this key
				} 
				else if (kb.keyDescription == steerLeft.keyDescription){
					keyType = 3; //4 is KeyUp for this key
				} 
				else if (kb.keyDescription == steerRight.keyDescription){
					keyType = 5; //6 is KeyUp for this key
				} 
				else if (kb.keyDescription == hardStop.keyDescription){
					keyType = 7;
					if(minecraft.gameSettings.keyBindSneak.keyCode == hardStop.keyCode){
						minecraft.gameSettings.keyBindSneak.pressed = true;
					}
				} 
				else if (kb.keyDescription == jump.keyDescription){
					keyType = 8;
				} 
				else if (kb.keyDescription == drive.keyDescription){
					keyType = 9;
				}
				else if (kb.keyDescription == seat.keyDescription){
					keyType = 10;
				}
				
				if (player.worldObj.isRemote){
					PacketHandlerSH.sendPacketKeyPressSteering(keyType);
				}
			}
			else{
				for (KeyBinding key : settings.keyBindings )
				{
					if (kb.keyCode == key.keyCode)
						key.pressed = true;
				}
			}
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		EntityClientPlayerMP player = minecraft.thePlayer;
		
		if (tickEnd && minecraft.inGameHasFocus && minecraft.currentScreen == null){
			if (playerIsRiding(player)){
				//EntityHorseRideableSH steed = (EntityHorseRideableSH) player.ridingEntity;
				int keyType = 0;
				
				if(kb.keyDescription == reignIn.keyDescription){
					//steed.handleReversing();
					keyType = 2;
				} 
				else if (kb.keyDescription == steerLeft.keyDescription){
					//steed.handleTurnLeft(false, player.ridingEntity.rotationYaw);
					keyType = 4;
				} 
				else if (kb.keyDescription == steerRight.keyDescription){
					//steed.handleTurnRight(false, player.ridingEntity.rotationYaw);
					keyType = 6;
				} 
				else if (kb.keyDescription == hardStop.keyDescription){
					if(minecraft.gameSettings.keyBindSneak.keyCode == hardStop.keyCode){
						minecraft.gameSettings.keyBindSneak.pressed = false;
					}
				}
				
				if (player.worldObj.isRemote && keyType != 0){
					PacketHandlerSH.sendPacketKeyPressSteering(keyType);
				}
			}
			else{
				for (KeyBinding key : settings.keyBindings )
				{
					if (kb.keyCode == key.keyCode)
						key.pressed = false;
				}
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}
	
	public boolean playerIsRiding(EntityClientPlayerMP player){
		return player.ridingEntity != null && (player.ridingEntity instanceof EntityHorseRideableSH || player.ridingEntity instanceof EntityVehicleSH || player.ridingEntity instanceof EntityVehicleSeatSH);
	}

}
