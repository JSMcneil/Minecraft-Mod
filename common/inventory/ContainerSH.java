package simplyhorses.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import simplyhorses.common.entities.IInventoryEntitySH;

public class ContainerSH extends Container {
	
	IInventoryEntitySH container;

    public ContainerSH(InventoryPlayer inventoryplayer, IInventoryEntitySH iie)
    {
    	container = iie;
        inventoryItemStacks.clear();
        inventorySlots.clear();
        int i = 2 * 18;
        
        for (int j = 0; j < 6; j++)
        {
            for (int i1 = 0; i1 < 9; i1++)
            {
                addSlotToContainer(new Slot(container.getInventories()[0], i1 + j * 9, 8 + i1 * 18, 18 + j * 18));
            }
        }

        for (int k = 0; k < 3; k++)
        {
            for (int j1 = 0; j1 < 9; j1++)
            {
            	addSlotToContainer(new Slot(inventoryplayer, j1 + k * 9 + 9, 8 + j1 * 18, 104 + k * 18 + i));
            }
        }

        for (int l = 0; l < 9; l++)
        {
        	addSlotToContainer(new Slot(inventoryplayer, l, 8 + l * 18, 162 + i));
        }
    }


    /**
     * Called to transfer a stack from one inventory to the other eg. when shift clicking.
     */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack stack = null;
		Slot slotObject = (Slot) inventorySlots.get(slot);

		// null checks and checks if the item can be stacked (maxStackSize > 1)
		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();

			// merges the item into player inventory since its in the tileEntity
			if (slot < 54) {
				if (!this.mergeItemStack(stackInSlot, 54, inventorySlots.size(), true)) {
					return null;
				}
			}
			// places it into the tileEntity is possible since its in the player
			// inventory
			else if (!this.mergeItemStack(stackInSlot, 0, 54, false)) {
				return null;
			}

			if (stackInSlot.stackSize == 0) {
				slotObject.putStack(null);
			} else {
				slotObject.onSlotChanged();
			}

			if (stackInSlot.stackSize == stack.stackSize) {
				return null;
			}
			slotObject.onPickupFromSlot(player, stackInSlot);
		}
		return stack;
	}

    public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
