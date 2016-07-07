package simplyhorses.common.entities;

import net.minecraft.world.World;
import simplyhorses.common.inventory.InventorySH;

public interface IInventoryEntitySH {
	
	InventorySH[] getInventories();
	
	void dropItems(World world, int x, int y, int z);
	
}
