package simplyhorses.common.items;

import java.util.ArrayList;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import simplyhorses.common.SimplyHorses;
import simplyhorses.common.entities.horses.EntityArabianSH;
import simplyhorses.common.entities.horses.EntityClydesdaleSH;
import simplyhorses.common.entities.horses.EntityFoalSH;
import simplyhorses.common.entities.horses.EntityHorseSH;
import simplyhorses.common.entities.horses.EntityMustangSH;
import simplyhorses.common.entities.horses.EntityHorseWildSH;

public class ItemSpawnerLassoSH extends ItemSH {

	/**Decides what breed of horse is spawned when the lasso is used. corresponds to the breed field in EntityHorseSH*/
	//@Invariant Should be between 1 and 4 inclusive: Wild Horse, Mustang, Arabian, Clydesdale
	private static ArrayList<String> playerList;
	private static ArrayList<Integer> playerBreedMode;
	private static ArrayList<Boolean> playerFoalMode;
	
	public ItemSpawnerLassoSH(int par1) {
		super(par1);
		playerList = new ArrayList<String>();
		playerBreedMode = new ArrayList<Integer>();
		playerFoalMode = new ArrayList<Boolean>();
		
		maxStackSize = 1;
	}
	
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
		if (par3World.isRemote){
			return false;
		}
		
		if (!playerList.contains(par2EntityPlayer.username)){
			addPlayer(par2EntityPlayer, 1, false);
		}
		
		int breedmode = playerBreedMode.get(playerList.indexOf(par2EntityPlayer.username));
		boolean foalmode = playerFoalMode.get(playerList.indexOf(par2EntityPlayer.username));
		EntityHorseSH horse = null;
		
		switch(breedmode){
		case 1:
			horse = new EntityHorseWildSH(par3World);
			if (par2EntityPlayer.isSneaking()) ((EntityHorseWildSH) horse).setPriority(1);
			else ((EntityHorseWildSH) horse).setPriority(3);
			break;
		case 2:
			horse = new EntityMustangSH(par3World);
			((EntityMustangSH) horse).setLocked(true);
			break;
		case 3:
			horse = new EntityArabianSH(par3World);
			break;
		case 4:
			horse = new EntityClydesdaleSH(par3World);
			break;
		}
		
		if (foalmode){
			horse = new EntityFoalSH(par3World);
			horse.setBreed(breedmode);
			horse.setGrowingAge(-24000);
		}
		
		if (horse == null){
			return false;
		}
		
		horse.setLocationAndAngles(par4, par5 + 1, par6, par2EntityPlayer.rotationYaw, 0);
        horse.renderYawOffset = 0;
		par3World.spawnEntityInWorld(horse);
		
		return true;
    }
	
	private static void addPlayer(EntityPlayer player, int i, boolean b) {
		playerList.add(player.username);
		playerBreedMode.add(i);
		playerFoalMode.add(b);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack par1ItemStack, EntityLiving par2EntityLiving)
	{
		return false;
	}
	
	public static void handleModeChange(EntityPlayer player){
		if (!playerList.contains(player.username)){
			addPlayer(player, 1, false);
		}
		
		int breedmode = playerBreedMode.get(playerList.indexOf(player.username));
		boolean foalmode = playerFoalMode.get(playerList.indexOf(player.username));
		
		breedmode++;
		if (breedmode == 5){
			breedmode = 1;
		}
		
		playerBreedMode.set(playerList.indexOf(player.username), breedmode);
		
		if (player != null){
			player.addChatMessage("Mode: " + getBreedFromMode(breedmode) + (foalmode? " Foal": ""));
		}
	}
	
	public static void toggleSpawnFoal(EntityPlayer player){
		if (!playerList.contains(player.username)){
			addPlayer(player, 1, false);
		}
		
		int breedmode = playerBreedMode.get(playerList.indexOf(player.username));
		boolean foalmode = !playerFoalMode.get(playerList.indexOf(player.username));
		
		playerFoalMode.set(playerList.indexOf(player.username), foalmode);
		
		if (player != null) player.addChatMessage("Mode: " + getBreedFromMode(breedmode) + (foalmode? " Foal": ""));
	}
	
	public static String getBreedFromMode(int mode){
		switch(mode){
		case 1:
			return "Wild Horse";
		case 2:
			return "Mustang";
		case 3:
			return "Arabian";
		case 4:
			return "Clydesdale";
		default:
			return "";
		}
	}
}
