package simplyhorses.common.entities.vehicles;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityVehicleSeatSH extends Entity implements IEntityAdditionalSpawnData{
	private EntityVehicleSH vehicle;
	private int position;
	private float[] offset;
	private boolean seatedPlayer;
	private int vehicleID;

	public EntityVehicleSeatSH(World par1World) {
		this(par1World, null, true, -1, -1, -1, -1, -1);
	}

	public EntityVehicleSeatSH(World worldObj, EntityVehicleSH vehiclesh, boolean sits, int h, float i, float j, float k, float l) {
		super(worldObj);
		setSize(0.75F, 0.75F);
		
		vehicle = vehiclesh;
		position = h;
		offset = new float[]{i, j, k, l};
		seatedPlayer = sits;
		vehicleID = -1;
	}

	@Override
	protected void entityInit() {}
	
	@Override
	public void onUpdate(){
		if (vehicle == null || vehicle.isDead){
			setDead();
			return;
		} else if (offset != null){
			float f1 = (vehicle.rotationYaw * (float)Math.PI) / 180F;
	        float f2 = MathHelper.sin(f1);
	        float f3 = MathHelper.cos(f1);
	        
			this.setPositionAndRotation(vehicle.posX + (f2 * offset[0]) + (f3 * offset[3]), vehicle.posY + offset[1], vehicle.posZ + (f3 * offset[2]) + (f2 * offset[3]), vehicle.rotationYaw, vehicle.rotationPitch);
		}
		
	}
	
	@Override
	public void setDead(){
		super.setDead();
	}
	
	public void setVehicle(EntityVehicleSH entityvehicle){
		vehicle = entityvehicle;
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound var1) {	
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound var1) {
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		if (vehicle == null){
			data.writeInt(-1);
			return;
		}
		
		data.writeInt(vehicle.entityId);
		data.writeBoolean(seatedPlayer);
		data.writeInt(position);
		for (int i = 0; i < offset.length; i++){
			data.writeFloat(offset[i]);
		}
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		int i = data.readInt();
		
		if (i == -1){
			return;
		}
		
		vehicleID = i;
		vehicle = (EntityVehicleSH) worldObj.getEntityByID(vehicleID);
		
		seatedPlayer = data.readBoolean();
		position = data.readInt();
		
		if (vehicle != null){
			vehicle.addSeat(this, position);
		}
		
		offset = new float[]{data.readFloat(), data.readFloat(), data.readFloat(), data.readFloat()};
	}
	
	public EntityVehicleSH getVehicle(){
		return vehicle;
	}
	
	@Override
	public boolean shouldRiderSit()
    {
		return seatedPlayer;
    }

}
