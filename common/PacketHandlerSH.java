package simplyhorses.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import simplyhorses.common.entities.EntityLassoSH;
import simplyhorses.common.entities.ILassoableSH;
import simplyhorses.common.entities.ISteerableSH;
import simplyhorses.common.entities.horses.EntityHorseDraughtSH;
import simplyhorses.common.entities.horses.EntityHorseRideableSH;
import simplyhorses.common.entities.horses.EntityHorseSH;
import simplyhorses.common.entities.vehicles.DraughtHelper;
import simplyhorses.common.entities.vehicles.EntityVardoSH;
import simplyhorses.common.entities.vehicles.EntityVehicleSH;
import simplyhorses.common.entities.vehicles.EntityVehicleSeatSH;
import simplyhorses.common.entities.vehicles.EntityWhippletreeSH;
import simplyhorses.common.items.ItemLassoSH;
import simplyhorses.common.items.ItemSpawnerLassoSH;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.src.ModLoader;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class PacketHandlerSH implements IPacketHandler {

	public static final byte customParticleSpawn = 0;
	public static final byte grazingUpdate = 1;
	public static final byte keyPressSteering = 2;
	public static final byte tiedToData = 3;
	public static final byte lassoSpawn = 4;
	public static final byte keyPressSpawnerLasso = 5;
	public static final byte whippletreeUpdate = 6;
	public static final byte whippletreeRequest = 7;
	public static final byte vardoSleepRequest = 8;
	public static final byte seatRespawn = 9;
	public static final byte lassoMode = 10;
	
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player playerIn) {
		DataInputStream newIS = new DataInputStream(new ByteArrayInputStream(packet.data));
		
		if(packet.channel.equals("SimplyHorses")){
			Byte subChannel = 0;
			int horseID = 0;
			try{
				subChannel = newIS.readByte();
				
				EntityPlayer player = (EntityPlayer)playerIn;
				World world = player.worldObj;
				
				if (subChannel == customParticleSpawn){
					horseID = newIS.readInt();
					int spawnType = newIS.readInt();
					int particleCount = newIS.readInt();
					
					for (int i = 0; i < world.loadedEntityList.size(); i++){
						Entity entity1 = (Entity)world.loadedEntityList.get(i);
						
						if (entity1 instanceof EntityHorseSH && entity1.entityId == horseID){
							if (spawnType == 3){
								((EntityHorseSH) entity1).showExplosionParticle();
								break;
							}
							
							((EntityHorseSH) entity1).showHeartsOrSmokeFX(spawnType == 0? false: true, particleCount);
							break;
						}
					}
				}
				else if (subChannel == grazingUpdate){
					horseID = newIS.readInt();
					int grazeTime = newIS.readInt();
					
					for (int i = 0; i < world.loadedEntityList.size(); i++){
						Entity entity1 = (Entity)world.loadedEntityList.get(i);
						
						if (entity1 instanceof EntityHorseSH && entity1.entityId == horseID){
							((EntityHorseSH) entity1).setGrazeTimer(grazeTime);
							break;
						}
					}
				}
				else if (subChannel == keyPressSteering){
					ISteerableSH steeringClass = null;
					
					if (player.ridingEntity != null){
						if (player.ridingEntity instanceof EntityHorseRideableSH){
							steeringClass = (EntityHorseRideableSH) player.ridingEntity;
						} 
						else if (player.ridingEntity instanceof EntityVehicleSH){
							DraughtHelper draughtHelper = ((EntityVehicleSH) player.ridingEntity).getDraughtHelper();
							if (draughtHelper.getDriver() == player){
								steeringClass = ((EntityVehicleSH) player.ridingEntity).getDraughtHelper();
							}
						}
						else if (player.ridingEntity instanceof EntityVehicleSeatSH){
							DraughtHelper draughtHelper = ((EntityVehicleSeatSH) player.ridingEntity).getVehicle().getDraughtHelper();
							if (draughtHelper.getDriver() == player){
								steeringClass = ((EntityVehicleSeatSH) player.ridingEntity).getVehicle().getDraughtHelper();
							}
						}
					}
					
					int keyID = newIS.readInt();
					
					switch(keyID){
					case 9:
						if (player.ridingEntity instanceof EntityVehicleSH){
							((EntityVehicleSH) player.ridingEntity).tryNewDriver(player);
						}
						else if (player.ridingEntity instanceof EntityVehicleSeatSH){
							((EntityVehicleSeatSH) player.ridingEntity).getVehicle().tryNewDriver(player);
						}
						break;
					case 10:
						if (player.ridingEntity instanceof EntityVehicleSeatSH){
							((EntityVehicleSeatSH) player.ridingEntity).getVehicle().switchSeat(player);
						}
						else if (player.ridingEntity instanceof EntityHorseRideableSH){
							((EntityHorseRideableSH) player.ridingEntity).LockHeading(true);
						}
						break;
					}
					
					if (steeringClass == null){
						return;
					}
					
					switch(keyID){
					case 0:
						steeringClass.handleSpeedUp();
						break;
					case 1:
						steeringClass.handleSpeedDown();
						break;
					case 2:
						steeringClass.handleReversing();
						break;
					case 3:
						steeringClass.handleTurnLeft(true);
						break;
					case 4:
						steeringClass.handleTurnLeft(false);
						break;
					case 5:
						steeringClass.handleTurnRight(true);
						break;
					case 6:
						steeringClass.handleTurnRight(false);
						break;
					case 7:
						steeringClass.handleHardStop(0);
						break;
					case 8:
						steeringClass.handleJumping(true); //boolean value does nothing u mad?
						break;
					default:
						break;
					}
				}
				else if (subChannel == keyPressSpawnerLasso){
					ItemStack itemstack = player.getCurrentEquippedItem();
					if (itemstack.itemID != SimplyHorses.spawnLasso.itemID) return;
					
					int keyType = newIS.readInt();
					
					if (keyType == 0) ((ItemSpawnerLassoSH) itemstack.getItem()).handleModeChange(player);
					else ((ItemSpawnerLassoSH) itemstack.getItem()).toggleSpawnFoal(player);
				}
				else if (subChannel == whippletreeUpdate){
					//Client side!
					if (!world.isRemote){
						return;
					}
					
					Entity entity1 = world.getEntityByID(newIS.readInt());
					EntityWhippletreeSH whippletree = null;
					
					if (entity1 == null){
						return;
					}
					else if (entity1 instanceof EntityWhippletreeSH){
						whippletree = (EntityWhippletreeSH) entity1;
					}
					
					if (whippletree == null){
						return;
					}
					
					whippletree.createNewHorseArray();
					
					int[] idList = new int[whippletree.getHorses().length];
					
					for (int i = 0; i < idList.length; i++){
						idList[i] = newIS.readInt();
					}
					
					Entity[] entityList = new Entity[idList.length];
					
					for (int i = 0; i < entityList.length; i++){
						Entity entity = world.getEntityByID(idList[i]);
						
						if (entity == null){
						}
						else if (entity instanceof EntityHorseDraughtSH){
							entityList[i] = entity;
						}
					}
					
					whippletree.setHorses((EntityHorseDraughtSH[]) entityList);
					
				}
				else if (subChannel == whippletreeRequest){
					//Server side!
					if (world.isRemote){
						return;
					}
					
					int id = newIS.readInt();
					EntityWhippletreeSH whippletree = null;
					
					Entity entity = world.getEntityByID(id);
					
					if (entity instanceof EntityWhippletreeSH){
						whippletree = (EntityWhippletreeSH) entity;
					}
					
					if (whippletree == null){
						return;
					}
					
					if (whippletree.isLoaded()){
						whippletree.respawn();
					}
				}
				else if (subChannel == vardoSleepRequest){
					//Server side!
					if (world.isRemote){
						return;
					}
					
					int vardoid = newIS.readInt();
					
					EntityVardoSH vardo = (EntityVardoSH) world.getEntityByID(vardoid);
					if (vardo != null){
						vardo.setPlayerSleeping(player);
					}
				}
				else if (subChannel == seatRespawn){
					int vehicleid = newIS.readInt();
					
					EntityVehicleSH vehicle = (EntityVehicleSH) world.getEntityByID(vehicleid);
					
					if (vehicle != null){
						vehicle.respawnSeats();
					}
				}
				else if (subChannel == lassoMode){
					int lassoMode = newIS.readInt();
					
					ItemLassoSH.handleModeChange(player, lassoMode);
				}
			}catch(IOException e){
				return;
			}
		}
	}
	
	public static void sendPacketParticleSpawn(Entity entity, int par1, int par2){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		
		try {
			dos.writeByte(customParticleSpawn);
			dos.writeInt(entity.entityId);
			dos.writeInt(par1);
			dos.writeInt(par2);
			dos.close();
			
			pkt.channel = "SimplyHorses";
			pkt.data = bos.toByteArray();
			pkt.length = bos.size();
			pkt.isChunkDataPacket = false;
			
			SimplyHorses.proxy.sendCustomPacket(pkt);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return;
	}
	
	public static void sendPacketGrazingUpdate(EntityHorseSH horse, int par1){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		
		try {
			dos.writeByte(grazingUpdate);
			dos.writeInt(horse.entityId);
			dos.writeInt(par1);
			dos.close();
			
			pkt.channel = "SimplyHorses";
			pkt.data = bos.toByteArray();
			pkt.length = bos.size();
			pkt.isChunkDataPacket = false;
			
			SimplyHorses.proxy.sendCustomPacket(pkt);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void sendPacketKeyPressSteering(int par1){
		if (!ModLoader.getMinecraftInstance().theWorld.isRemote){
			return;
		} else{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			Packet250CustomPayload pkt = new Packet250CustomPayload();
			
			try {
				dos.writeByte(keyPressSteering);
				dos.writeInt(par1);
				dos.close();
				
				pkt.channel = "SimplyHorses";
				pkt.data = bos.toByteArray();
				pkt.length = bos.size();
				pkt.isChunkDataPacket = false;
				
				PacketDispatcher.sendPacketToServer(pkt);
			}catch(IOException e){
			}
		}
	}

	public static void sendPacketTiedToData(EntityHorseSH entityhorseSH, Entity tiedToEntity, ChunkCoordinates hitchCoordinates) {
		if (entityhorseSH.worldObj.isRemote){
			return;
		}
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		
		try {
			dos.writeByte(tiedToData);
			dos.writeInt(entityhorseSH.entityId);
			dos.writeInt(tiedToEntity == null? -1: tiedToEntity.entityId);
			if (hitchCoordinates == null){
				dos.writeInt(0);
			}
			else{
				dos.writeInt(1);
				dos.writeInt(hitchCoordinates.posX);
				dos.writeInt(hitchCoordinates.posY);
				dos.writeInt(hitchCoordinates.posZ);
			}
			
			dos.close();
			
			pkt.channel = "SimplyHorses";
			pkt.data = bos.toByteArray();
			pkt.length = bos.size();
			pkt.isChunkDataPacket = false;

			SimplyHorses.proxy.sendCustomPacket(pkt);
		}catch(IOException e){
		}
	}

	public static void sendPacketLassoSpawn(Entity caughtEntity, Entity tiedToEntity) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		
		try {
			dos.writeByte(lassoSpawn);
			dos.writeInt(caughtEntity == null? -1: caughtEntity.entityId);
			dos.writeInt(tiedToEntity == null? -1: tiedToEntity.entityId);
			dos.close();
			
			pkt.channel = "SimplyHorses";
			pkt.data = bos.toByteArray();
			pkt.length = bos.size();
			pkt.isChunkDataPacket = false;
			
			SimplyHorses.proxy.sendCustomPacket(pkt);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return;
		
	}

	@SideOnly(Side.CLIENT)
	public static void sendPacketKeyPressSpawnerLasso(int keyType) {
		if (!ModLoader.getMinecraftInstance().theWorld.isRemote){
			return;
		} else{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			Packet250CustomPayload pkt = new Packet250CustomPayload();
			
			try {
				dos.writeByte(keyPressSpawnerLasso);
				dos.writeInt(keyType);
				dos.close();
				
				pkt.channel = "SimplyHorses";
				pkt.data = bos.toByteArray();
				pkt.length = bos.size();
				pkt.isChunkDataPacket = false;
				
				PacketDispatcher.sendPacketToServer(pkt);
			}catch(IOException e){
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void sendPacketKeyPressLasso(int keyType) {
		if (!ModLoader.getMinecraftInstance().theWorld.isRemote){
			return;
		} else{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			Packet250CustomPayload pkt = new Packet250CustomPayload();
			
			try {
				dos.writeByte(lassoMode);
				dos.writeInt(keyType);
				dos.close();
				
				pkt.channel = "SimplyHorses";
				pkt.data = bos.toByteArray();
				pkt.length = bos.size();
				pkt.isChunkDataPacket = false;
				
				PacketDispatcher.sendPacketToServer(pkt);
			}catch(IOException e){
			}
		}
	}

	/*public static void sendPacketWhippletreeUpdate(EntityWhippletreeSH entityWhippletreeSH, EntityHorseDraughtSH entityHorseDraughtSH, EntityHorseDraughtSH entityHorseDraughtSH2) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		
		try {
			dos.writeByte(whippletreeUpdate);
			dos.writeInt(entityWhippletreeSH == null? -1: entityWhippletreeSH.entityId);
			dos.writeInt(entityHorseDraughtSH == null? -1: entityHorseDraughtSH.entityId);
			dos.writeInt(entityHorseDraughtSH2 == null? -1: entityHorseDraughtSH2.entityId);
			dos.close();
			
			pkt.channel = "SimplyHorses";
			pkt.data = bos.toByteArray();
			pkt.length = bos.size();
			pkt.isChunkDataPacket = false;
			
			SimplyHorses.proxy.sendCustomPacket(pkt);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}*/
	
	public static void sendPacketWhippletreeUpdate(EntityWhippletreeSH entityWhippletreeSH) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		
		try {
			dos.writeByte(whippletreeUpdate);
			dos.writeInt(entityWhippletreeSH.entityId);
			EntityHorseDraughtSH[] horses = entityWhippletreeSH.getHorses();
			for (int i = 0; i < horses.length; i++){
				dos.writeInt(horses[i].entityId);
			}
			dos.close();
			
			pkt.channel = "SimplyHorses";
			pkt.data = bos.toByteArray();
			pkt.length = bos.size();
			pkt.isChunkDataPacket = false;
			
			SimplyHorses.proxy.sendCustomPacket(pkt);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	@SideOnly(Side.CLIENT)
	public static void sendPacketWhippletreeRequest(EntityWhippletreeSH entityWhippletreeSH) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		
		try {
			dos.writeByte(whippletreeRequest);
			dos.writeInt(entityWhippletreeSH.entityId);
			dos.close();
			
			pkt.channel = "SimplyHorses";
			pkt.data = bos.toByteArray();
			pkt.length = bos.size();
			pkt.isChunkDataPacket = false;
			
			PacketDispatcher.sendPacketToServer(pkt);
		}catch(IOException e){
		}
	}

	//Clientside
	public static void sendPacketVardoSleepReq(EntityVardoSH entityvardoSH) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		
		try {
			dos.writeByte(vardoSleepRequest);
			dos.writeInt(entityvardoSH.entityId);
			dos.close();
			
			pkt.channel = "SimplyHorses";
			pkt.data = bos.toByteArray();
			pkt.length = bos.size();
			pkt.isChunkDataPacket = false;
			
			PacketDispatcher.sendPacketToServer(pkt);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	@SideOnly(Side.CLIENT)
	public static void sendPacketRespawnSeats(EntityVehicleSH entityVehicleSH) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		
		try {
			dos.writeByte(seatRespawn);
			dos.writeInt(entityVehicleSH.entityId);
			dos.close();
			
			pkt.channel = "SimplyHorses";
			pkt.data = bos.toByteArray();
			pkt.length = bos.size();
			pkt.isChunkDataPacket = false;
			
			PacketDispatcher.sendPacketToServer(pkt);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
	
}
