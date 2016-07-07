package simplyhorses.common.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import simplyhorses.common.SimplyHorses;
import simplyhorses.common.entities.vehicles.EntityCartSH;
import simplyhorses.common.entities.vehicles.EntityVardoSH;
import simplyhorses.common.entities.vehicles.EntityVehicleSH;
import simplyhorses.common.entities.vehicles.EntityWagonSH;

public class ItemVehicleSH extends ItemSH {
	
	/**1 = cart, 2 = wagon, 3 = vardo*/
	private int targetVehicle;

	public ItemVehicleSH(int par1, int vehicle) {
		super(par1);
		
		targetVehicle = vehicle;
	}
	
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
		if (!par3World.isRemote){
			EntityVehicleSH entityVehicle = null;
			
			switch(targetVehicle){
			case 1:
				entityVehicle = new EntityCartSH(par3World);
				break;
			case 2:
				entityVehicle = new EntityWagonSH(par3World);
				break;
			case 3:
				entityVehicle = new EntityVardoSH(par3World);
				break;
			default:
				break;	
			}
			
			entityVehicle.setLocationAndAngles(par4, par5 + 1, par6, par2EntityPlayer.rotationYaw, 0);
			par3World.spawnEntityInWorld(entityVehicle);
			entityVehicle.moveEntityWithHeading(0.0F, 0.5F);
			
			if (!par2EntityPlayer.capabilities.isCreativeMode){
				par1ItemStack.stackSize--;
			}
			
			return true;
		}
			
        return false;
    }

}
