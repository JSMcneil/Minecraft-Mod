package simplyhorses.common.entities.vehicles;

import simplyhorses.common.SimplyHorses;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityCartSH extends EntityVehicleSH {

	public EntityCartSH(World par1World) {
		super(par1World);
        setSize(1.8F, 1.6F);
	}

	@Override
	public String getTexture() {
		return "/mods/SimplyHorses/textures/vehicles/cart.png";
	}
	
	@Override
	protected void spawnSeats(){
		EntityVehicleSeatSH seat = new EntityVehicleSeatSH(worldObj, this, true, 0, -1F, 0.5F, 1F, 0.5F);
		seat.setPositionAndRotation(posX, posY, posZ, rotationYaw, rotationPitch);
		
		EntityVehicleSeatSH seat1 = new EntityVehicleSeatSH(worldObj, this, true, 1, -1F, 0.5F, 1F, -0.5F);
		seat1.setPositionAndRotation(posX, posY, posZ, rotationYaw, rotationPitch);
		
		seats = new EntityVehicleSeatSH[]{seat, seat1};
		
		for (int i = 0; i < seats.length; i++){
			worldObj.spawnEntityInWorld(seats[i]);
		}
	}
	
	@Override
	public void addSeat(EntityVehicleSeatSH seat, int position){
		if (seats == null){
			seats = new EntityVehicleSeatSH[2];
		}
		
		seats[position] = seat;
	}

	@Override
	public void updateRiderPosition(){
		float f1 = (rotationYaw * (float)Math.PI) / 180F;
        float f2 = MathHelper.sin(f1);
        float f3 = MathHelper.cos(f1);
        
        riddenByEntity.setPosition(posX, posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), posZ);
	}

	@Override
	public double getMountedYOffset() {
		return (double)this.height * 0.65D;
	}

	@Override
	public float getVehicleWeight() {
		return 1.0F;
	}

	@Override
	public float getSpeedBonus() {
		return 0.0F;
	}

	@Override
	public Item getVehicleItem() {
		return SimplyHorses.cart;
	}
}
