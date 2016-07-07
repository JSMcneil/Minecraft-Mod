package simplyhorses.common.items;

import java.util.ArrayList;
import java.util.HashSet;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import simplyhorses.common.SimplyHorses;
import simplyhorses.common.entities.EntityLassoSH;
import simplyhorses.common.entities.ILassoableSH;
import simplyhorses.common.entities.horses.EntityHorseDraughtSH;
import simplyhorses.common.entities.horses.EntityHorseSH;
import simplyhorses.common.entities.horses.EntityHorseTameSH;
import simplyhorses.common.entities.horses.EntityMustangSH;
import simplyhorses.common.entities.horses.EntityHorseWildSH;
import simplyhorses.common.entities.vehicles.EntityDraughtTechSH;
import simplyhorses.common.entities.vehicles.EntityWagonChestSH;

public class ItemLassoSH extends ItemSH {

	private static ArrayList<String> playerList;
	private static ArrayList<Integer> modeList;
	private Icon[] iconArray;
	
	public ItemLassoSH(int par1) {
		super(par1);
        maxStackSize = 1;
        
        playerList = new ArrayList<String>();
        modeList = new ArrayList<Integer>();
	}
	
	@SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldRotateAroundWhenRendering()
    {
        return true;
    }
    
    /**Called from EventHooksSH. Replaces itemInteractionForEntity by providing the item's player as a formal paramter*/
	public boolean entityInteractionByPlayer(ItemStack par1ItemStack, Entity par2Entity, EntityPlayer par3EntityPlayer){
		initiatePlayerEntry(par3EntityPlayer);
		if (par3EntityPlayer.worldObj.isRemote){
			return false;
		}
		
		if (par2Entity instanceof EntityWagonChestSH){
			par2Entity = ((EntityWagonChestSH) par2Entity).getWagon();
		}
		
		int mode = modeList.get(playerList.indexOf(par3EntityPlayer.username));
		
		switch (mode){
		case 0:
			return LassoHelperSH.manageNewLasso(par3EntityPlayer, par2Entity);
		case 1:
			if (!(par2Entity instanceof EntityHorseDraughtSH) && !(par2Entity instanceof EntityDraughtTechSH)){
				return false;
			}
			
			if (par2Entity instanceof EntityHorseDraughtSH && !((EntityHorseDraughtSH) par2Entity).isTacked()){
				return false;
			}
			
			return LassoHelperSH.manageNewTrainLead(par3EntityPlayer, par2Entity);
		default:
			return false;
		}
		
	}
	
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
		initiatePlayerEntry(par2EntityPlayer);
		return !par3World.isRemote && LassoHelperSH.manageNewHitch(par2EntityPlayer, par3World, par4, par5, par6);
    }
	
	//Mode Stuff
	public static void handleModeChange(EntityPlayer player, int mode){
		initiatePlayerEntry(player);
		
		modeList.set(playerList.indexOf(player.username), mode);
		
		if (player.worldObj.isRemote){
			player.addChatMessage("Lasso Mode is now " + (mode == 0? "\"Lasso\"": "\"Train Hook\""));
		}
	}
	
	public static int getLassoMode(EntityPlayer player){
		initiatePlayerEntry(player);
		return modeList.get(playerList.indexOf(player.username));
	}
	
	public static void initiatePlayerEntry(EntityPlayer player){
		if (!playerList.contains(player.username)){
			playerList.add(player.username);
			modeList.add(0);
		}
	}
	
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		super.registerIcons(iconRegister);
		iconArray = new Icon[]{
				iconRegister.registerIcon("SimplyHorses:" + name),
				iconRegister.registerIcon("SimplyHorses:" + "trainhook")
		};
	}
	
	public Icon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
    {
		initiatePlayerEntry(player);
		int i = modeList.get(playerList.indexOf(player.username));
		
        return iconArray[i];
    }
	
}
