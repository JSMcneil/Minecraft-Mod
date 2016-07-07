package simplyhorses.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import simplyhorses.client.gui.GuiCartSH;
import simplyhorses.client.gui.GuiVardoSH;
import simplyhorses.client.gui.GuiWagonSH;
import simplyhorses.common.entities.IInventoryEntitySH;
import simplyhorses.common.entities.vehicles.EntityCartSH;
import simplyhorses.common.entities.vehicles.EntityVardoSH;
import simplyhorses.common.entities.vehicles.EntityVehicleSH;
import simplyhorses.common.entities.vehicles.EntityWagonChestSH;
import simplyhorses.common.entities.vehicles.EntityWagonSH;
import simplyhorses.common.inventory.ContainerSH;
import simplyhorses.common.inventory.ContainerWagonSH;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandlerSH implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		Entity entity = world.getEntityByID(ID);
		if (entity == null || !(entity instanceof IInventoryEntitySH) && !(entity instanceof EntityWagonChestSH)){
			return null;
		}
		
		if (entity instanceof EntityCartSH){
			return new ContainerSH(player.inventory, (IInventoryEntitySH) entity);
		} else
			
		if (entity instanceof EntityWagonChestSH){
			EntityWagonSH wagon = ((EntityWagonChestSH) entity).getWagon();
			int place = ((EntityWagonChestSH) entity).getPlace();
			
			return new ContainerWagonSH(player.inventory, (IInventoryEntitySH) wagon, place);
		} else
		
		if (entity instanceof EntityVardoSH){
			return new ContainerSH(player.inventory, (IInventoryEntitySH) entity);
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		Entity entity = world.getEntityByID(ID);
		if (entity == null || !(entity instanceof IInventoryEntitySH) && !(entity instanceof EntityWagonChestSH)){
			return null;
		}
		
		if (entity instanceof EntityCartSH){
			return new GuiCartSH(player.inventory, (IInventoryEntitySH) entity);
		} else
			
		if (entity instanceof EntityWagonChestSH){
			EntityWagonSH wagon = ((EntityWagonChestSH) entity).getWagon();
			int place = ((EntityWagonChestSH) entity).getPlace();
			
			return new GuiWagonSH(player.inventory, (IInventoryEntitySH) wagon, place);
		} else
			
		if (entity instanceof EntityVardoSH){
			return new GuiVardoSH(player, player.inventory, (IInventoryEntitySH) entity);
		}
		
		return null;
	}

}
