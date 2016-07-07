package simplyhorses.common.inventory;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import simplyhorses.common.entities.IInventoryEntitySH;

public class ContainerWagonSH extends Container {

	IInventoryEntitySH wagon;
	InventoryPlayer invP;

	public ContainerWagonSH(InventoryPlayer inventoryplayer, IInventoryEntitySH iie){
		this(inventoryplayer, iie, 0);
	}
    public ContainerWagonSH(InventoryPlayer inventoryplayer, IInventoryEntitySH iie, int i){
    	wagon = iie;
        invP = inventoryplayer;
        
        createSlots(i);
    }


    public void createSlots(int par1) {
    	inventoryItemStacks.clear();
        inventorySlots.clear();
        
        int i = 2 * 18;
        
        for (int j = 0; j < 6; j++)
        {
            for (int i1 = 0; i1 < 9; i1++)
            {
            	addSlotToContainer(new Slot(wagon.getInventories()[par1], i1 + j * 9, 8 + i1 * 18, 18 + j * 18));
            }
        }

        for (int k = 0; k < 3; k++)
        {
            for (int j1 = 0; j1 < 9; j1++)
            {
            	addSlotToContainer(new Slot(invP, j1 + k * 9 + 9, 8 + j1 * 18, 104 + k * 18 + i));
            }
        }

        for (int l = 0; l < 9; l++)
        {
        	addSlotToContainer(new Slot(invP, l, 8 + l * 18, 162 + i));
        }
	}
    
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
			// places it into the tileEntity if possible since its in the player
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


	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return true;
	}

}
