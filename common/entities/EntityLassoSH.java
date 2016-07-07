package simplyhorses.common.entities;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

import simplyhorses.common.PacketHandlerSH;
import simplyhorses.common.SimplyHorses;
import simplyhorses.common.entities.horses.EntityHorseSH;
import simplyhorses.common.items.ItemLassoSH;
import simplyhorses.common.items.LassoHelperSH;

public class EntityLassoSH extends Entity implements IEntityAdditionalSpawnData{
	private Entity caughtEntity;
	private Entity tiedToEntity;
	private ChunkCoordinates hitchCoords;
	private boolean shouldActAsAI;
	
	private UUID[] loadList;

	public EntityLassoSH(World par1World) {
		this(par1World, null, null, null);
	}

	public EntityLassoSH(World par1world, Entity caughtEntity2, Entity tiedToEntity2, ChunkCoordinates hitchBlock){
		super(par1world);
		ignoreFrustumCheck = true;
		
		this.caughtEntity = caughtEntity2;
		this.tiedToEntity = tiedToEntity2;
		hitchCoords = hitchBlock;
		shouldActAsAI = caughtEntity2 instanceof ILassoableSH? false: true;
		loadList = new UUID[1];
	}
	
	@Override
	public void onEntityUpdate(){
		super.onEntityUpdate();
		
		if (SimplyHorses.doLassoRespawning && !worldObj.isRemote && loadList.length == 2){
			caughtEntity = findEntity(loadList[0]);
			tiedToEntity = findEntity(loadList[1]);
			
			if (caughtEntity == null){
				setDead();
				return;
			}
			
			setDead();
			EntityLassoSH lasso = new EntityLassoSH(worldObj, caughtEntity, tiedToEntity, hitchCoords);
			lasso.setPositionAndRotation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
			LassoHelperSH.addLassoTrio(null, lasso, caughtEntity);
			worldObj.spawnEntityInWorld(lasso);
		}

		if (caughtEntity == null){
			if (!worldObj.isRemote){
				setDead();
			}
			return;
		}
		
		this.posX = caughtEntity.posX;
		this.posY = caughtEntity.boundingBox.minY + caughtEntity.height * 0.8D;
		this.posZ = caughtEntity.posZ;
		//System.out.println("me: " + this + "\nentity: " + caughtEntity+ "\n");
		
		if (caughtEntity == null || caughtEntity.isDead || shouldLassoDie() || (tiedToEntity != null && tiedToEntity.isDead)){
			if (!worldObj.isRemote){
				setDead();
			}

			return;
		}
		
		/*if (SimplyHorses.showLeadRopes){
			double[] hookcoords = getHookCoords();
			
			if (hookcoords == null){
				setDead();
			}
			
			setPosition(hookcoords[0], hookcoords[1], hookcoords[2]);
		}
		else{
			setPosition(caughtEntity.posX, caughtEntity.boundingBox.minY + caughtEntity.height * 0.8D, caughtEntity.posZ);
		}
		*/
		
		if (worldObj.isRemote || !shouldActAsAI || !(caughtEntity instanceof EntityLiving)){
			return;
		}
		
		EntityLiving caughtLiving = (EntityLiving) caughtEntity;
		
		if (tiedToEntity != null){
			if (caughtLiving.getDistanceToEntity(tiedToEntity) > 3){
				caughtLiving.stepHeight = 1.0F;
				
		        if (((EntityLiving) caughtEntity).getNavigator().tryMoveToXYZ(tiedToEntity.posX, tiedToEntity.posY, tiedToEntity.posZ, 0.3F)){
		        }

			}
		} else if (hitchCoords != null){
			if (caughtLiving.getDistance(hitchCoords.posX, hitchCoords.posY, hitchCoords.posZ) > 3){
				caughtLiving.getNavigator().tryMoveToXYZ(hitchCoords.posX, hitchCoords.posY, hitchCoords.posZ, 0.3F);
			}
		} else {
			setDead();
		}
	}
	
	private double[] getHookCoords() {
		if (caughtEntity == null || caughtEntity.isDead){
			return null;
		}
		
		if (caughtEntity instanceof ILassoableSH){
			return ((ILassoableSH) caughtEntity).getLassoHookPosition();
		}
		
		return new double[]{caughtEntity.posX, caughtEntity.posY, caughtEntity.posZ};
	}

	private boolean shouldLassoDie(){
		for (Entity entity: LassoHelperSH.getLassodEntityList()){
			if (entity == caughtEntity){
				if (entity instanceof EntityHorseSH && ((EntityHorseSH) entity).isFree()){
					return true;
				}
				
				if (entity instanceof ILassoableSH && !(entity instanceof EntityHorseSH) && ((ILassoableSH) entity).getTiedToEntity() == null){
					return true;
				}
				
				return false;
			}
		}
		
		return true;
	}
	
	public void setDead(){
		LassoHelperSH.removeLassoTrio(this);
		if (shouldActAsAI && caughtEntity != null){
			caughtEntity.stepHeight = 0.5F;
		}
		
		super.setDead();
	}
	
	/**Finds an entity based on the UUID passed in*/
	public Entity findEntity(UUID id)
    {
        for (int i = 0; i < worldObj.loadedEntityList.size(); i++)
        {
            Entity entity = (Entity)worldObj.loadedEntityList.get(i);
            
            if (entity == null || entity.getPersistentID() == null){
            	continue;
            }

            if (entity.getDistanceToEntity(this) <= 50F && entity.getPersistentID() != null && entity.getPersistentID().equals(id)){
            	return entity;
            }
        }
        
        return null;
    }
        
       

	@Override
	protected void  writeEntityToNBT(NBTTagCompound nbttagcompound) {
		if (caughtEntity != null){
	        if (caughtEntity.getPersistentID() != null)
	        {
	        	nbttagcompound.setLong("Caught PersistentIDMSB", caughtEntity.getPersistentID().getMostSignificantBits());
	        	nbttagcompound.setLong("Caught PersistentIDLSB", caughtEntity.getPersistentID().getLeastSignificantBits());
	        }
		}
		
		if (tiedToEntity != null){
	        if (tiedToEntity.getPersistentID() != null)
	        {
	        	nbttagcompound.setLong("Tied PersistentIDMSB", tiedToEntity.getPersistentID().getMostSignificantBits());
	        	nbttagcompound.setLong("Tied PersistentIDLSB", tiedToEntity.getPersistentID().getLeastSignificantBits());
	        }
		}
        
        if (hitchCoords != null){
        	nbttagcompound.setInteger("Hitch X", (int) (hitchCoords.posX));
			nbttagcompound.setInteger("Hitch Y", (int) (hitchCoords.posY));
			nbttagcompound.setInteger("Hitch Z", (int) (hitchCoords.posZ));
        }
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		loadList = new UUID[2];
		
		if (nbttagcompound.hasKey("Caught PersistentIDMSB") && nbttagcompound.hasKey("Caught PersistentIDLSB")){
			loadList[0] = new UUID(nbttagcompound.getLong("Caught PersistentIDMSB"), nbttagcompound.getLong("Caught PersistentIDLSB"));
		}
		
		if (nbttagcompound.hasKey("Tied PersistentIDMSB") && nbttagcompound.hasKey("Tied PersistentIDLSB")){
			loadList[1] = new UUID(nbttagcompound.getLong("Tied PersistentIDMSB"), nbttagcompound.getLong("Tied PersistentIDLSB"));
		}
		
		if (nbttagcompound.hasKey("Hitch X") && nbttagcompound.hasKey("Hitch Y") && nbttagcompound.hasKey("Hitch Z")){
			hitchCoords = new ChunkCoordinates(nbttagcompound.getInteger("Hitch X"), nbttagcompound.getInteger("Hitch Y"), nbttagcompound.getInteger("Hitch Z"));
		}
	}

	@Override
	protected void entityInit() {
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		data.writeInt(caughtEntity == null? 0: caughtEntity.entityId);
		data.writeInt(tiedToEntity == null? 0: tiedToEntity.entityId);
		if (hitchCoords != null){
			data.writeBoolean(true);
			data.writeInt(hitchCoords.posX);
			data.writeInt(hitchCoords.posY);
			data.writeInt(hitchCoords.posZ);
		}else{
			data.writeBoolean(false);
		}
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		int caughtEntityID = data.readInt();
		int tiedToEntityID = data.readInt();
		
		for (int i = 0; i < worldObj.loadedEntityList.size(); i++){
			Entity entity1 = (Entity)worldObj.loadedEntityList.get(i);
			
			if (entity1.entityId == caughtEntityID){
				caughtEntity = (EntityLiving) entity1;
				break;
			}
		}
		
		for (int i = 0; i < worldObj.loadedEntityList.size(); i++){
			Entity entity1 = (Entity)worldObj.loadedEntityList.get(i);
			
			if (entity1.entityId == tiedToEntityID){
				tiedToEntity = entity1;
				break;
			}
		}
		
		boolean isHitched = data.readBoolean();
		if (isHitched){
			int x = data.readInt();
			int y = data.readInt();
			int z = data.readInt();
			
			hitchCoords = new ChunkCoordinates(x, y, z);
		}
		
		if (caughtEntity != null && !(caughtEntity instanceof ILassoableSH)){
			caughtEntity.stepHeight = 1;
		}
	}

	public Entity getCaughtEntity() {
		return caughtEntity;
	}
	
	public Entity getTiedToEntity() {
		return tiedToEntity;
	}

	public ChunkCoordinates getHitchCoords() {
		return hitchCoords;
	}
	
}
