package simplyhorses.common.entities.vehicles;

import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import simplyhorses.common.SimplyHorses;
import simplyhorses.common.inventory.InventorySH;

public class EntityWagonSH extends EntityVehicleSH {

	private InventorySH chest2;
	private EntityWagonChestSH[] chests;
	
	public EntityWagonSH(World par1World) {
		super(par1World);
        setSize(1.8F, 1.5F);
		
		chest2 = new InventorySH(this, 54);
		chests = null;
	}

	@Override
	public String getTexture() {
		return "/mods/SimplyHorses/textures/vehicles/wagon.png";
	}
	
	@Override
	public void onLivingUpdate() {
		
		if (!worldObj.isRemote && chests == null){
			setUpChests();
		} else if (chests != null){
			updateChestPositions();
		}
		
		super.onLivingUpdate();
	}
	
	public void updateChestPositions() {
		float f1 = (rotationYaw * (float)Math.PI) / 180F;
        float f2 = MathHelper.sin(f1);
        float f3 = MathHelper.cos(f1);
        
        if (chests[0] != null){
        	chests[0].setPosition(posX - (f2 * 0.6F), posY + 1, posZ + (f3 * 0.6F));
        }
        
        if (chests[1] != null){
        	chests[1].setPosition(posX + (f2 * 0.6F), posY + 1, posZ - (f3 * 0.6F));
        }
	}
	
	public void setUpChests(){
		if (worldObj.isRemote){
			return;
		}
		
		chests = new EntityWagonChestSH[2];
		
		for (int i = 0; i < chests.length; i++){
			EntityWagonChestSH chestsh = new EntityWagonChestSH(worldObj);
			chestsh.setWagon(this);
			chestsh.setPlace(i);
			chestsh.setPositionAndRotation(posX, posY, posZ, rotationYaw, rotationPitch);
			chests[i] = chestsh;
			worldObj.spawnEntityInWorld(chestsh);
		}
	}
	
	public void setChest(EntityWagonChestSH newchest, int i){
		if (chests == null){
			chests = new EntityWagonChestSH[2];
			chests[i] = newchest;
		} else{
			chests[i] = newchest;
		}
	}

	@Override
	protected void spawnSeats() {
		EntityVehicleSeatSH seat = new EntityVehicleSeatSH(worldObj, this, true, 0, -1.5F, 0.5F, 1.5F, 0.5F);
		seat.setPositionAndRotation(posX, posY, posZ, rotationYaw, rotationPitch);
		
		EntityVehicleSeatSH seat1 = new EntityVehicleSeatSH(worldObj, this, true, 1, -1.5F, 0.5F, 1.5F, -0.5F);
		seat1.setPositionAndRotation(posX, posY, posZ, rotationYaw, rotationPitch);
		
		seats = new EntityVehicleSeatSH[]{seat, seat1};
		
		for (int i = 0; i < seats.length; i++){
			worldObj.spawnEntityInWorld(seats[i]);
		}
	}

	@Override
	public void addSeat(EntityVehicleSeatSH seat, int position) {
		if (seats == null){
			seats = new EntityVehicleSeatSH[2];
		}
		
		seats[position] = seat;
	}

	@Override
	public void updateRiderPosition() {
		float f1 = (rotationYaw * (float)Math.PI) / 180F;
        float f2 = MathHelper.sin(f1);
        float f3 = MathHelper.cos(f1);
        
        riddenByEntity.setPosition(posX - (f2 * 0.5F), posY + 1 + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), posZ + (f3 * 0.5F));
	}

	@Override
	public double getMountedYOffset() {
		return (double)this.height * 0.65D;
	}
	
	@Override
	public boolean hasGui(){
		return false;
	}

	@Override
	public float getVehicleWeight() {
		return 2.0F;
	}

	@Override
	public float getSpeedBonus() {
		return 1.0F;
	}
	
	@Override
	public InventorySH[] getInventories(){
		return new InventorySH[]{chest, chest2};
	}

	@Override
	public Item getVehicleItem() {
		return SimplyHorses.wagon;
	}

}
