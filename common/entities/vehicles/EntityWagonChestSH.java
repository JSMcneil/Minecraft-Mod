package simplyhorses.common.entities.vehicles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import simplyhorses.common.SimplyHorses;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityWagonChestSH extends Entity implements IEntityAdditionalSpawnData{

	private EntityWagonSH wagon;
	private int place;
	
	public EntityWagonChestSH(World par1World) {
		super(par1World);
        setSize(1F, 1F);
        
		wagon = null;
		place = 0;
	}
	
	@Override
	protected void entityInit() {
	}
	
	@Override
	public boolean canBeCollidedWith(){
		return true;
	}
	
	@Override
	public void onEntityUpdate(){
		if (wagon == null || wagon.isDead){
			setDead();
		}
		
		super.onEntityUpdate();
	}
	
	@Override
	public boolean interact(EntityPlayer entityplayer){
		ItemStack item = entityplayer.getCurrentEquippedItem();
		
		if (getWagon().interact(entityplayer)){
			return true;
		}
		
		if (item != null && item.itemID == SimplyHorses.lasso.itemID){
			return false;
		}
		
		entityplayer.openGui(SimplyHorses.instance, entityId, worldObj, (int) posX, (int) posY, (int) posZ);
		return true;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource damage, int i){
		return wagon.attackEntityFrom(damage, i);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound var1) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound var1) {
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		data.writeInt(wagon == null? -1: wagon.entityId);
		data.writeInt(place);
		
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		int i = data.readInt();
		
		if (i == -1){
			return;
		}
		
		wagon = (EntityWagonSH) worldObj.getEntityByID(i);
		place = data.readInt();
		
		if (wagon != null){
			wagon.setChest(this, place);
		}
	}
	
	public EntityWagonSH getWagon() {
		return wagon;
	}

	public void setWagon(EntityWagonSH wagon) {
		this.wagon = wagon;
	}
	
	public int getPlace(){
		return place;
	}
	
	public void setPlace(int i){
		this.place = i;
	}
}
