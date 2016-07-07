package simplyhorses.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import simplyhorses.common.entities.IInventoryEntitySH;

public class InventorySH implements IInventory {
	
	private IInventoryEntitySH iowner;
	private ItemStack[] inventory;
	
	public InventorySH(IInventoryEntitySH iientity, int i){
		iowner = iientity;
		inventory = new ItemStack[i];
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return inventory[var1];
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2) {
		ItemStack stack = getStackInSlot(var1);
		
        if (stack != null) {
                if (stack.stackSize <= var2) {
                        setInventorySlotContents(var1, null);
                } else {
                        stack = stack.splitStack(var2);
                        if (stack.stackSize == 0) {
                                setInventorySlotContents(var1, null);
                        }
                }
        }
        return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		ItemStack stack = getStackInSlot(var1);
        if (stack != null) {
                setInventorySlotContents(var1, null);
        }
        return stack;
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		inventory[var1] = var2;
		if (var2 != null && var2.stackSize > getInventoryStackLimit()){
			var2.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInvName() {
		return "SHInventory";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return true;
	}

	@Override
	public void onInventoryChanged() {}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}
	
	public NBTTagList writeToNBT(NBTTagList par1NBTTagList)
    {
        int var2;
        NBTTagCompound var3;

        for (var2 = 0; var2 < this.inventory.length; ++var2)
        {
            if (this.inventory[var2] != null)
            {
                var3 = new NBTTagCompound();
                var3.setByte("Slot", (byte)var2);
                this.inventory[var2].writeToNBT(var3);
                par1NBTTagList.appendTag(var3);
            }
        }
        
        return par1NBTTagList;
    }
	
	public void readFromNBT(NBTTagList par1NBTTagList)
    {
        for (int var2 = 0; var2 < par1NBTTagList.tagCount(); ++var2)
        {
            NBTTagCompound var3 = (NBTTagCompound)par1NBTTagList.tagAt(var2);
            int var4 = var3.getByte("Slot") & 255;
            ItemStack var5 = ItemStack.loadItemStackFromNBT(var3);

            this.inventory[var4] = var5;
        }
    }

	@Override
	public boolean isInvNameLocalized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		// TODO Auto-generated method stub
		return false;
	}

}
