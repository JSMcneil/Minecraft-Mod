package simplyhorses.common.entities.vehicles;

import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import simplyhorses.common.PacketHandlerSH;
import simplyhorses.common.SimplyHorses;
import simplyhorses.common.entities.IInventoryEntitySH;
import simplyhorses.common.entities.horses.EntityHorseSH;
import simplyhorses.common.inventory.InventorySH;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public abstract class EntityVehicleSH extends EntityDraughtTechSH implements IInventoryEntitySH {
	
	protected EntityVehicleSeatSH[] seats;
	protected InventorySH chest;
	protected UUID riddenByUUID;
	
	public EntityVehicleSH(World par1World) {
		super(par1World);
        setSize(1.8F, 1.8F);
        
        chest = new InventorySH(this, 54);
	}
	
	@Override
	public abstract String getTexture();
	
	@Override
	public void onLivingUpdate(){
		if (seats == null){
			if (!worldObj.isRemote){
				spawnSeats();
			} else{
				PacketHandlerSH.sendPacketRespawnSeats(this);
			}
		}
		
		if (seats != null){
			for (int i = 0; i < seats.length; i++){
				if (seats[i] == null || seats[i].isDead){
					if (!worldObj.isRemote){
						respawnSeats();
					} else{
						PacketHandlerSH.sendPacketRespawnSeats(this);
						break;
					}
				}
			}
		}
		
		if (riddenByUUID != null){
			Entity entity = findEntity(riddenByUUID);
			if (entity != null && !worldObj.isRemote){
				entity.mountEntity(this);
				riddenByUUID = null;
			}
		}
		
		super.onLivingUpdate();
	}
	
	public void tryNewDriver(EntityPlayer player) {
		if (getDraughtHelper() != null){
			getDraughtHelper().setDriver(player);
		}
	}
	
	protected abstract void spawnSeats();
	
	public abstract void addSeat(EntityVehicleSeatSH seat, int position);
	
	public void respawnSeats(){
		for (int i = 0; i < seats.length; i++){
			if (seats[i] != null){
				seats[i].setDead();
			}
		}
		
		spawnSeats();
	}
	
	public void switchSeat(EntityPlayer player) {
		int seatIndex = -1;
		
		for (int i = 0; i < seats.length; i++){
			if (seats[i].riddenByEntity != null && seats[i].riddenByEntity == player){
				seatIndex = i;
			}
			else if (seatIndex != -1 && i > seatIndex){
				if (seats[i].riddenByEntity == null){
					addRider(player, i);
					return;
				}
			}
		}
		
		for (int i = 0; i < seats.length; i++){
			if (seats[i].riddenByEntity == null){
				addRider(player, i);
				return;
			}
		}
	}
	
	public void addRider(EntityPlayer player, int i){
		player.mountEntity(seats[i]);
		
		if (getDraughtHelper() != null){
			if (seats[i].riddenByEntity != player){
				getDraughtHelper().tryRemoveDriver(player);
			}
		}
	}

	@Override
	public boolean interact(EntityPlayer entityplayer){
		ItemStack item = entityplayer.getHeldItem();
		
		if (entityplayer.isSneaking()){
			if (!worldObj.isRemote){
				for (int i = 0; i < seats.length; i++){
					if (seats[i] != null && (seats[i].riddenByEntity == null || seats[i].riddenByEntity == entityplayer)){
						addRider(entityplayer, i);
						break;
					}
				}
			}
			
			return true;
		}
		
		/*if (entityplayer.isSneaking()){
			if (!worldObj.isRemote){
				entityplayer.mountEntity(this);
				
				if (getDraughtHelper() != null){
					getDraughtHelper().tryRemoveDriver(entityplayer);
				}
			}
			
			return true;
		}*/
		
		else if (item != null && item.itemID == SimplyHorses.lasso.itemID){
			return false;
		}
		
		else if (hasGui()){
			entityplayer.openGui(SimplyHorses.instance, entityId, worldObj, (int) posX, (int) posY, (int) posZ);
            return true;
		}
		
		return false;
	}
	
	public abstract Item getVehicleItem();
	
	@Override
	public abstract void updateRiderPosition();
	
	@Override
	public abstract double getMountedYOffset();
	
	@Override
	public void applyEntityCollision(Entity entity){
		if ((entity.ridingEntity != null && entity.ridingEntity instanceof EntityVehicleSeatSH) || draughtHalted()){
			return;
		}
		
		super.applyEntityCollision(entity);
	}
	
	@Override
	protected void collideWithEntity(Entity par1Entity)
    {
		if ((par1Entity.ridingEntity != null && par1Entity.ridingEntity instanceof EntityVehicleSeatSH) || draughtHalted()){
			return;
		}
		
		super.collideWithEntity(par1Entity);
    }
	
	//IInventoryEntitySH stuff
	@Override
	public InventorySH[] getInventories(){
		return new InventorySH[]{chest};
	}
	
	@Override
	public void dropItems(World world, int x, int y, int z){
		if (world.isRemote){
			return;
		}
		
        IInventory inventory = chest;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
        	
            ItemStack item = inventory.getStackInSlot(i);

            if (item != null && item.stackSize > 0) {
                float rx = rand.nextFloat() * 0.8F + 0.1F;
                float ry = rand.nextFloat() * 0.8F + 0.1F;
                float rz = rand.nextFloat() * 0.8F + 0.1F;

                EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z + rz, new ItemStack(item.itemID, item.stackSize, item.getItemDamage()));

                //TODO Go back to the tut and get the snippet cut out here

                float factor = 0.05F;
                entityItem.motionX = rand.nextGaussian() * factor;
                entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
                entityItem.motionZ = rand.nextGaussian() * factor;
                world.spawnEntityInWorld(entityItem);
                item.stackSize = 0;
            }
        }
	}
	
	//ILassoableEntity stuff
	public boolean handleLasso(Entity par1Entity){
		if (canHoldPets() && par1Entity instanceof EntityAnimal && !(par1Entity instanceof EntityHorseSH)){
			releaseFromLasso();
			par1Entity.mountEntity(this);
			return true;
		}
		
		return super.handleLasso(par1Entity);
	}
	
	public boolean canHoldPets(){
		return true;
	}
	
	@Override
	public void setDead(){
		dropItems(worldObj, (int) posX, (int) posY, (int) posZ);
		if (!worldObj.isRemote){
			dropItemWithOffset(getVehicleItem().itemID, 1, this.height);
		}
		super.setDead();
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		
		for (int i = 0; i < getInventories().length; i++){
			nbttagcompound.setTag("Inventory" + i, this.getInventories()[i].writeToNBT(new NBTTagList()));
		}
		
		if (riddenByEntity != null){
			if (riddenByEntity.getPersistentID() != null)
            {
				nbttagcompound.setLong("rBEPersistentIDMSB", riddenByEntity.getPersistentID().getMostSignificantBits());
				nbttagcompound.setLong("rBEPersistentIDLSB", riddenByEntity.getPersistentID().getLeastSignificantBits());
            }
		}
		
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		
		for (int i = 0; i < getInventories().length; i++){
			getInventories()[i].readFromNBT(nbttagcompound.getTagList("Inventory" + i));
		}
		
		if (nbttagcompound.hasKey("rBEPersistentIDMSB") && nbttagcompound.hasKey("rBEPersistentIDLSB")){
			riddenByUUID = new UUID(nbttagcompound.getLong("rBEPersistentIDMSB"), nbttagcompound.getLong("rBEPersistentIDLSB"));
		}
	}
	
	/**Should return true if this vehicle's GUI is accessed via right-clicking this entity*/
	public boolean hasGui(){
		return true;
	}

	public abstract float getVehicleWeight();
	
	public abstract float getSpeedBonus();
	
}
