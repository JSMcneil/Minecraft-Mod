package simplyhorses.common.items;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.SparrowAPI;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import simplyhorses.common.entities.EntityLassoSH;
import simplyhorses.common.entities.IDraughtEntitySH;
import simplyhorses.common.entities.ILassoableSH;
import simplyhorses.common.entities.horses.EntityFoalSH;
import simplyhorses.common.entities.horses.EntityHorseSH;
import simplyhorses.common.entities.vehicles.EntityWagonChestSH;

/**This class handles managing all lassos, and is called from ItemLasso when that item is used by the player.
 * It contains two 'linked' ArrayLists which record any active lasso and its caught entity, as well as a third
 * ArrayList that records which players are actively using a lasso (have an entity tied to them via a lasso). When
 * entityInteractionByPlayer is called in ItemLasso, that class calls manageNewLasso(), which acts accordingly*/
public class LassoHelperSH {
	public static ArrayList<String> playerList = new ArrayList<String>();
	public static ArrayList<EntityLassoSH> lassoList = new ArrayList<EntityLassoSH>();
	public static ArrayList<Entity> lassodEntityList = new ArrayList<Entity>();
	
	public static boolean manageNewLasso(EntityPlayer player, Entity entity){
		if(!isPlayerUsingLasso(player)){
			if (entity instanceof ILassoableSH || entity instanceof EntityAnimal){
				if (entity instanceof EntityFoalSH && ((EntityFoalSH) entity).getBreed() == 0){
					return false;
				}
				
				lassoEntityToPlayer(player.worldObj, entity, player);
				return true;
			}
			
			if (entity instanceof EntityWagonChestSH && ((EntityWagonChestSH) entity).getWagon() != null){
				lassoEntityToPlayer(player.worldObj, ((EntityWagonChestSH) entity).getWagon(), player);
				return true;
			}
			
			if (entity instanceof SparrowAPI){
				SparrowAPI sparrow = (SparrowAPI) entity;
				
				if (sparrow.customStringAndResponse("SH_isLassoable").equals("true")){
					//useLasso(par2EntityLiving);
					return true;
				}
			}
			
			return false;
		}
		else{
			Entity listEntity = lassodEntityList.get(playerList.indexOf(player.username));
			
			if (listEntity == null){
				return false;
			}
			
			/*if (entity == listEntity){
				unUseLasso((Entity) entity);
				return true;
			}*/
			
			else{
				unUseLasso(listEntity, entity);
				return true;
			}
			
		}
	}
	
	public static boolean manageNewTrainLead(EntityPlayer player, Entity entity){
		if(!isPlayerUsingLasso(player)){
			return manageNewLasso(player, entity);
		}
		else {
			Entity listEntity = lassodEntityList.get(playerList.indexOf(player.username));
			
			if (listEntity == null || !(listEntity instanceof IDraughtEntitySH)){
				return false;
			}
			
			unUseTrainLead(listEntity, entity);
			return true;
		}
	}
	
	public static boolean manageNewHitch(EntityPlayer player, World world, int par4, int par5, int par6){
		int blockId = world.getBlockId(par4, par5, par6);
		
		switch(blockId){
    	case 5:
    		break;
    	case 17:
    		break;
    	case 85:
    		break;
    	case 113:
    		break;
    	case 131:
    		break;
		default:
			if (isPlayerUsingLasso(player)){
				unUseLasso(player);
			}
			return false;
    	}
		
		if (isPlayerUsingLasso(player)){
			
			int i = playerList.indexOf(player.username);
			
			Entity entity = lassodEntityList.get(i);
			
			if (entity == null){
				removeLassoTrio(i);
				return false;
			}
			
			unUseLasso(entity, new ChunkCoordinates(par4, par5, par6));
			
			return true;
		}
		
		return false;
	}
	
	/**Returns true if the passed in player is in playerList*/
	public static boolean isPlayerUsingLasso(EntityPlayer player){
		return player != null && playerList.indexOf(player.username) >= 0;
	}
	
	/**Assigns the entity appropriately to the Player.
	 * All other 'uses' of the lasso (to tie entities to other entities or to a post) is handled in one of the
	 * unUseLasso methods.*/
	public static void lassoEntityToPlayer(World worldObj, Entity entity, EntityPlayer player){
		if (entity instanceof ILassoableSH){
			((ILassoableSH) entity).handleLasso(player);
		}
		
		if (lassodEntityList.indexOf(entity) >= 0){
			EntityLassoSH oldLasso = lassoList.get(lassodEntityList.indexOf(entity));
			if (oldLasso != null){
				oldLasso.setDead();
			}
		}
		
		EntityLassoSH lasso = new EntityLassoSH(worldObj, entity, player, null);
		addLassoTrio(player.username, lasso, entity);

		lasso.setLocationAndAngles(entity.posX, entity.boundingBox.minY + entity.height * 0.8D, entity.posZ, entity.rotationYaw, entity.rotationPitch);
		worldObj.spawnEntityInWorld(lasso);
		
		//printArrays();
	}
	
	/**The base unUseLasso. Deletes this lasso trio without calling handleLasso or other functions*/
	public static void unUseLasso(Entity par1entity){
		//Clear old lasso on par1entity if present
		int i = lassodEntityList.indexOf(par1entity);
		
		if (i < 0){
			System.out.println("Entity not found! [unUseLasso(E)]");
			return;
		}
		
		removeLassoTrio(i);
		
		if (par1entity instanceof ILassoableSH){
			((ILassoableSH) par1entity).releaseFromLasso();
		}
		
		//printArrays();
	}
	
	/**Find the lasso the player has active, frees the entity attached, and removes the lasso group*/
	public static void unUseLasso(EntityPlayer player){
		//Clear old lasso on par1entity if present
		int i = playerList.indexOf(player.username);
		
		if (i < 0){
			System.out.println("Player not found! [unUseLasso(EP)]");
			return;
		}
		
		Entity entity = lassodEntityList.get(i);
		
		if (entity != null && entity instanceof ILassoableSH){
			((ILassoableSH) entity).releaseFromLasso();
		}
		
		removeLassoTrio(i);
		
		//printArrays();
	}
	
	/**Removes any lassoCatch entities. Spawns new lasso between entities in the parameters*/
	public static void unUseLasso(Entity par1Entity, Entity par2Entity){
		//Clear old lasso on par1entity if present
		unUseLasso(par1Entity);
		
		//handle a lasso'd LassoableEntity
		if (par1Entity instanceof ILassoableSH){
			((ILassoableSH) par1Entity).handleLasso(par2Entity);
		}
		
		//Check for lassoing an entity to itself before proceeding
		if (par1Entity == par2Entity){
			return;
		}
		
		//Generate new lasso
		EntityLassoSH lasso = new EntityLassoSH(par1Entity.worldObj, par1Entity, par2Entity, null);
		
		addLassoTrio(null, lasso, par1Entity);
		lasso.setLocationAndAngles(par1Entity.posX, par1Entity.boundingBox.minY + par1Entity.height * 0.8D, par1Entity.posZ, par1Entity.rotationYaw, par1Entity.rotationPitch);
		par1Entity.worldObj.spawnEntityInWorld(lasso);
		
		//printArrays();
	}
	
	public static void unUseTrainLead(Entity par1Entity, Entity par2Entity){
		unUseLasso(par1Entity);
		
		((IDraughtEntitySH) par1Entity).handleTrainHook(par2Entity);
		
		if (par1Entity == par2Entity){
			return;
		}
		
		EntityLassoSH lasso = new EntityLassoSH(par1Entity.worldObj, par1Entity, par2Entity, null);
		
		addLassoTrio(null, lasso, par1Entity);
		lasso.setLocationAndAngles(par1Entity.posX, par1Entity.boundingBox.minY + par1Entity.height * 0.8D, par1Entity.posZ, par1Entity.rotationYaw, par1Entity.rotationPitch);
		par1Entity.worldObj.spawnEntityInWorld(lasso);
		
		//printArrays();
	}
	
	/**Removes any lassoCatch entities. Spawns new lasso from par1entity to par2coords*/
	public static void unUseLasso(Entity par1entity, ChunkCoordinates par2coords){
		//Clear old lasso on par1entity if present
		unUseLasso(par1entity);
		
		//handle a lasso'd Horse
		if (par1entity instanceof EntityHorseSH){
			((EntityHorseSH) par1entity).setHitchCoordinates(par2coords);
		}
		
		//Generate new lasso
		EntityLassoSH lasso = new EntityLassoSH(par1entity.worldObj, par1entity, null, par2coords);
		
		addLassoTrio(null, lasso, par1entity);
		lasso.setLocationAndAngles(par1entity.posX, par1entity.boundingBox.minY + par1entity.height * 0.8D, par1entity.posZ, par1entity.rotationYaw, par1entity.rotationPitch);
		par1entity.worldObj.spawnEntityInWorld(lasso);
		
		//printArrays();
	}
	
	public static boolean addLassoTrio(String username, EntityLassoSH par1lasso, Entity par2entity){
		if (lassodEntityList.contains(par2entity)){
			removeLassoTrio(lassodEntityList.indexOf(par2entity));
		}
		
		playerList.add(username == null? "noPlayer" : username);
		lassoList.add(par1lasso);
		lassodEntityList.add(par2entity);
		
		return true;
	}
	
	/**Removes a lasso and its caught entity from the paired lists via the lasso paramater passed in*/
	public static void removeLassoTrio(EntityLassoSH par1lasso){
		if (par1lasso == null) return;
		
		removeLassoTrio(lassoList.indexOf(par1lasso));
	}
	
	/**Removes a lasso and its caught entity from the paired lists via their index in the lists*/
	public static void removeLassoTrio(int indexOf) {
		if (indexOf < 0){
			return;
		}

		EntityLassoSH lasso = lassoList.get(indexOf);
		
		lassoList.remove(indexOf);
		lassodEntityList.remove(indexOf);
		playerList.remove(indexOf);
		
		if (lasso != null && !lasso.isDead){
			lasso.setDead();
		}
		
	}

	public static ArrayList<Entity> getLassodEntityList() {
		return lassodEntityList;
	}
	
	/**Used for Debugging only. Prints the contents of playerList, lassoList, and lassodEntityList*/
	public static void printArrays(){
		System.out.println("Printing playerList");
		
		for (String username: playerList){
			System.out.println(username);
		}
		
		System.out.println("Printing lassoList");
		for (EntityLassoSH player: lassoList){
			System.out.println(player);
		}
		
		System.out.println("Printing entityList");
		for (Entity player: lassodEntityList){
			System.out.println(player);
		}
		
		System.out.println("Done\n ");
	}
}
