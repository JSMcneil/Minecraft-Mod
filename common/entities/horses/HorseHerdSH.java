package simplyhorses.common.entities.horses;

import java.util.Random;

import simplyhorses.common.SimplyHorses;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class HorseHerdSH {
	
	/**The list of up to 30 horses that can exist as one herd*/
	public EntityHorseWildSH[] herdList;
	
	/**In technical terms, the stallion is the horse that orbits and defends the herd.*/
	public EntityHorseWildSH stallion;
	
	/**In technical terms, the matriarch is the horse the rest of the herd stays near and flees with.
	 * If lasso'd, the matriarch switches places in herdList with the next available mare, which becomes the new matriarch*/
	public EntityHorseWildSH matriarch;
	
	public World worldObj;
	public Random rand;
	
	/**Controls the activation of onHerdStartle, and thus the fleeing behavior of the herd. 
	 * Set to true when the herd is startled, can only be reset once the matriarch (who sets the 
	 * fleeing path) has reached the end of her path. As all mares will constantly call onHerdStartle
	 * as long as a player is nearby, this prevents onHerdStartle() from being called repeatedly.*/
	public boolean herdStartled;
	
	/**Whether or not the herd has a stallion*/
	public boolean hasStallion;
	
	public boolean herdFirstUpdate;
	
	public String herdID;
	
	public int stallionCooldown;
	
	public int maxSize;
	
	HorseHerdSH(EntityHorseWildSH EntityWildHorseSH, EntityHorseWildSH[] herd){
		stallion = EntityWildHorseSH;
		herdList = herd;
		matriarch = null;
		worldObj = EntityWildHorseSH.worldObj;
		rand = new Random();
		herdStartled = false;
		herdFirstUpdate = true;
		herdID = "Herd of " + EntityWildHorseSH.getSimplyID();
		stallionCooldown = 0;
		maxSize = SimplyHorses.maxHerdLimit;
	}
	
	/**called from SimplyHorses.class every player tick. This updates the class every tick to check the horses.*/	
	public void onHerdUpdate(){
		//if (herdStartled) System.out.println("Herd Startled!");
		
		if (stallion == null && herdList[0] != null && herdList[0].isStallion()) stallion = herdList[0];
		
		if (stallion != null){
			if (stallion.isDead){
				stallion = null;
			}
			else{
				if (herdStartled && !stallion.entityaidefend.isAttackingPlayer()) herdStartled = false;
				
				if (!herdStartled && stallion.entityaidefend.isAttackingPlayer()) herdStartled = true;
				
				if (stallionCooldown > 0){
					stallionCooldown--;
					
					if (stallionCooldown == 0){
						stallion.entityaidefend.resetTask();
					}
				}
			}
		}
		else{
			herdStartled = false;
		}
		
		for (int i = 0; i < herdList.length; i++){
			if (herdList[i] != null){
				EntityHorseWildSH horse = herdList[i];
				
				//check that this horse is in the correct herd.
				if (horse.getHerd() != this){
					horse.setHerd(this);
				}
				
				//check that this horse is still alive.
				if (horse.isDead){
					removeHorse(horse);
				}
				
				//check if this horse has been caught.
				if (horse.getPriority() < 4 && !horse.isFree()){
					if(horse.isStallion()){
						horse.setPriority(4);
					}else{
						horse.setPriority(5);
					}
				}
				
				/*check if this horse was caught but is now free.
				 * Horses should return to their herd if it currently has a matriarch.
				 * If there is no Matriarch, that means there are no free mares (if any mares at all) in the herd
				 * to assume to roll of Matriarch. Thus, if this horse was a caught Stallion,
				 * it should resume being the stallion. If this horse is a caught mare, it should become
				 * the new Matriarch.*/
				if (horse.getPriority() > 3 && horse.isFree()){

					if (horse.getPriority() == 4){
						if (getStallion() == null || stallion == horse){
							horse.setPriority(1);
							//horse.fleeTo(matriarch);
						}else{
							horse.setPriority(1);
							removeHorse(horse);
							horse.setNewHerd(true);
						}
						
					}else{
						horse.setPriority(3);
						//horse.fleeTo(matriarch);
					}
					
					/*if (matriarch != null){
							if (matriarch.getDistanceToEntity(horse) <= 150){
								if (horse.priority == 4){
									if (stallion == null || (stallion != null && stallion == horse)){
										horse.priority = 1;
										horse.fleeTo(matriarch);
									}else{
										horse.priority = 1;
										removeHorse(horse);
										horse.setNewHerd();
									}
									
								}else{
									horse.priority = 3;
									horse.fleeTo(matriarch);
								}
							}
							else{
								if (horse.getPriority() == 4){
									removeHorse(horse);
									horse.setNewHerd();
									//sets this stallion as the stallion of a new herd
								}
								else{
									removeHorse(horse);
									horse.setNewHerd();
									horse.getHerd().herdList[0] = null;
									horse.getHerd().herdList[1] = horse;
									horse.setPriority(2);
									horse.getHerd().refreshHerd();
									//wow, that is convoluted...
									//sets this mare as the matriarch of her own herd...
									//... instead of as the stallion.
								}
							}
					}else{
						if (horse.priority == 4 && (stallion == null || stallion == horse)){
							horse.priority = 1;
						}else if (horse.priority == 4){
							horse.priority = 1;
							horse.setNewHerd();
							removeHorse(horse);
						}else {
							horse.priority = 3;
						}
					}*/
				}
			}
			
			if (herdFirstUpdate){
				herdFirstUpdate = false;
				return;
			}
			
			refreshHerd();
		}
	}
	
	/**Called from the FindStallion or MateWildHorses EntityAIs as well as a wild foal's growUp(). Adds a new horse if possible. 
	 * If not (a return of false), the horse is deleted from the world. This prevents duplicate horses.
	 * Horses should not be able to add themselves to a herd otherwise.*/
	public boolean addHorse(EntityHorseWildSH EntityWildHorseSH){
		if (EntityWildHorseSH == null){
			return false;
		}
		
		for(int i = 0; i < herdList.length; i++){
			if (herdList[i] != null && herdList[i] == EntityWildHorseSH){
				return true;
			}
		}
		
		for(int i = 0; i < herdList.length; i++){
			if (herdList[i] == null){
				herdList[i] = EntityWildHorseSH;
				if (EntityWildHorseSH.getHerd() != null){
					EntityWildHorseSH.getHerd().removeHorse(EntityWildHorseSH);
				}
				EntityWildHorseSH.setHerd(this);
				EntityWildHorseSH.setPriority(3);
				
				return true;
			}
		}
		
		return false;
	}

	/**Finds this horse in the herd's list and removes it*/
	public void removeHorse(EntityHorseSH entityhorse){
		
		for(int i = 0; i < herdList.length; i++){
			if (herdList[i] == entityhorse){
				herdList[i] = null;
				System.out.println("Herd removing horse: " + entityhorse.getSimplyID());
				break;
			}
		}
		
	}
	
	/**As the "matriarch" always inhabits herdList[1], she is swapped with herdList[2] if captured.*/
	public void switchMatriarch(){
		for (int i = 1; i < herdList.length; i++){
			if (herdList[i] != null && herdList[i].getPriority() == 3){
				
				EntityHorseWildSH holder = herdList[1];
				herdList[1] = herdList[i];
				herdList[i] = holder;
				
				break;
			}
		}
		
		if (getMatriarch() != null && !getMatriarch().isFree()){
			getMatriarch().setPriority(5);
		}
	}
	
	/**Temporary Startle Behavior*/
	public void onHerdStartle(EntityPlayer entityplayer, EntityHorseWildSH EntityWildHorseSH){
		if (!herdStartled && stallion != null){

			stallion.getNavigator().clearPathEntity();
			stallion.getNavigator().tryMoveToEntityLiving(EntityWildHorseSH, 0.4F);
			
			if (stallion.getNavigator().tryMoveToEntityLiving(entityplayer, 0.4F)){
				stallion.entityaidefend.isRushingPlayer = true;
				stallionCooldown = 100;
				stallion.getNavigator().clearPathEntity();
				stallion.getNavigator().tryMoveToEntityLiving(entityplayer, 0.4F);
				herdStartled = true;
			}
		}
	}
	
	/**Handles updating the herd on fleeing from the player. The Stallion should move towards the mare that was startled (parameter 2).
	The Matriarch should flee, and the other mares should follow her.*/
	/*public void onHerdStartle2(EntityPlayer entityplayer, EntityWildHorseSH EntityWildHorseSH){
		if (herdStartled){
			return;
		}
		
		for (int i = 0; i < herdList.length; i++){
			if (herdList[i] != null){
				herdList[i].getNavigator().clearPathEntity();
			}
		}
		
		//Have Stallion path to player.
		if (stallion != null){
			stallion.getNavigator().tryMoveToEntityLiving(entityplayer, 0.5F);
		}
		
		//Have Matriarch set path to flee
		if (getMatriarch() == null){
			return;
		}
		
		if (!matriarch.getEntitySenses().canSee(entityplayer))
        {
            return;
        }

        Vec3 vec = RandomPositionGenerator.findRandomTargetBlockAwayFrom(matriarch, 32, 7, Vec3.getVec3Pool().getVecFromPool(entityplayer.posX, entityplayer.posY, entityplayer.posZ));

        if (vec == null)
        {
            return;
        }

        if (entityplayer.getDistanceSq(vec.xCoord, vec.yCoord, vec.zCoord) < entityplayer.getDistanceSqToEntity(matriarch))
        {
            return;
        }

        PathNavigate pathnavigate = matriarch.getNavigator();
        PathEntity pathentity = pathnavigate.getPathToXYZ(vec.xCoord, vec.yCoord, vec.zCoord);

        if (pathentity == null)
        {
            return;
        }

        if (pathentity.isDestinationSame(vec) && pathnavigate.noPath())
        {
            pathnavigate.setPath(pathentity, 0.3F);

            if ((double)matriarch.getDistanceToEntity(entityplayer) < 40D)
            {
            	matriarch.getNavigator().setSpeed(0.5F);
            }
            else
            {
            	matriarch.getNavigator().setSpeed(0.4F);
            }
        }
        
        herdStartled = true;
        
        //have Mares follow Matriarch.
        
        PathPoint pathpoint = pathentity.getFinalPathPoint();
		
		for (int i = 0; i < herdList.length; i++){
			if (herdList[i] != null && herdList[i].getPriority() == 3){
				PathNavigate pathnavigate2 = herdList[i].getNavigator();
				
				Boolean b = rand.nextBoolean();
				Boolean b1 = rand.nextBoolean();
				
				int i1 = pathpoint.xCoord + rand.nextInt(5) * (b == true? 1:-1);
				int j1 = pathpoint.yCoord;
				int k1 = pathpoint.zCoord + rand.nextInt(5) * (b1 == true? 1:-1);
				
				PathEntity pathentity2 = pathnavigate2.getPathToXYZ(i1, j1, k1);
				
				if (pathnavigate2.noPath()){
					pathnavigate2.setPath(pathentity2, 0.3F);
					
					if ((double)herdList[i].getDistanceToEntity(entityplayer) < 40D)
			        {
						pathnavigate2.setSpeed(0.5F);
			        }
			        else
			        {
			        	pathnavigate2.setSpeed(0.4F);
			        }
				}
			}
		}
		
		for (int i = 0; i < herdList.length; i ++){
			if (herdList[i] != null){
				herdList[i].setGrazeTimer(0);
				herdList[i].setFleeing(true);
				herdList[i].setSprinting(true);
			}
		}
	}*/
	
	/**Compacts the herd list after adding or removing a horse. Does not refresh position 0 (Stallion)*/
	private void refreshHerd() {
		for(int i = 1; i < herdList.length; i++){
			if (i != herdList.length - 1 && herdList[i] == null){
				herdList[i] = herdList[i+1];
				herdList[i+1] = null;
			}
		}
		
		if (matriarch == null || matriarch.isDead || matriarch.getPriority() != 2){
			getNewMatriarch();
		}
		
		/*System.out.println("Updating herdList");
		for (int i = 0; i < herdList.length; i++){
			if (herdList[i] != null){
				System.out.println(i + ". " + herdList[i].getSimplyID());
			}
			else {
				System.out.println(i + ". Null");
			}
		}*/
	}
	
	/**Called only by wild foal if it reaches adulthood in a herd that has no stallion.*/
	public void setStallion(EntityHorseWildSH wildhorse){
		if (herdList != null){
			herdList[0] = wildhorse;
			stallion = wildhorse;
			wildhorse.setHerd(this);
			wildhorse.setHerdLoaded(true);
			wildhorse.setPriority(1);
		}
	}
	
	/**Sets the herd's matriarch.*/
	public void setMatriarch(EntityHorseWildSH wildhorse){
		matriarch = wildhorse;
		wildhorse.setPriority(2);
		wildhorse.setHerd(this);
		wildhorse.setHerdLoaded(true);
	}
	
	/**ONLY Returns the horse at position 1 in herdList (reassigns the Stallion). Called from refreshHerd()*/
	public EntityHorseWildSH getNewStallion(){
		if (herdList != null && herdList[0] != null){
			herdList[0].setPriority(1);
			herdList[0].setHerd(this);
			herdList[0].setHerdLoaded(true);
			
			return herdList[0];
		}
		
		return null;
	}
	
	/**ONLY Returns the horse at position 1 in herdList (reassigns the Matriarch). Called from Constructor and refreshHerd()*/
	public EntityHorseWildSH getNewMatriarch(){
		for(int i = 0; i < herdList.length; i++){
			if (herdList[i] != null && herdList[i].getPriority() == 2){
				setMatriarch(herdList[i]);
				return herdList[i];
			}
		}
		
		for(int i = 0; i < herdList.length; i++){
			if (herdList[i] != null && herdList[i].getPriority() == 3){
				setMatriarch(herdList[i]);
				return herdList[i];
			}
		}
		
		return null;
	}

	/**Stallions form herds upon spawning. Called from onLivingUpdate*/
	public boolean formHerd() {
		if (!worldObj.isRemote){
			for (int s = 0; s < 4 + rand.nextInt(5); s++){
				boolean shouldSpawn = false;
				
				EntityHorseWildSH mare = new EntityHorseWildSH(worldObj);
				
				int y = rand.nextBoolean()? 0: 1;
				int z = rand.nextBoolean()? 0: 1;
				
				int i = (int)stallion.posX + rand.nextInt(9) * y;
				int j = (int)stallion.posY;
				int k = (int)stallion.posZ + rand.nextInt(9) * z;
				
				for (int s1 = j; s1 < j + 5; s1++){
					if (!worldObj.isBlockNormalCube(i, s1, k)){
						j = s1;
						shouldSpawn = true;
						break;
					}
					
					if (s1 == j + 4){
						s--;
						break;
					}
				}
				
				if (shouldSpawn){
					mare.setLocationAndAngles(i, j, k, rand.nextInt(360), stallion.rotationPitch);
					mare.setPriority(3);
					addHorse(mare);
					worldObj.spawnEntityInWorld(mare);
				}
			}
		}
		
		if (matriarch != null){
			getStallion().setHerdLoaded(true);
		}
		
		if (matriarch != null){
			getMatriarch().setHerdLoaded(true);
		}
		
		refreshHerd();
		
		return true;
	}
	
	/**Called from EntityWildHorseSH if that entity is a stallion or stallion-less mare
	 * Loads the herd into HerdList*/
	public boolean loadHerd(String[] par1StringList, EntityHorseWildSH leaderHorse, boolean flag){
		for (int i = 0, j = 0; i < par1StringList.length && j < herdList.length; i++, j++){
			/*if the Matriarch is loading the herd, position0 (the stallion) should be skipped.
			 * the Matriarch should only load the herd if there is no stallion*/
			if (par1StringList[i] != null){
				if (i == 0 && !flag){
					j++;
				}
				
				herdList[i] = (EntityHorseWildSH) leaderHorse.findEntity(par1StringList[i]);
				
				if (herdList[i] != null){
					herdList[i].setHerd(this);
				}
			}
		}
		
		if (stallion != null){
			stallion.setHerdLoaded(true);
		}
		
		if (matriarch != null){
			matriarch.setHerdLoaded(true);
		}
		
		refreshHerd();
		
		return true;
	}

	/**Called by stallions upon losing a fight with another stallion. The loser calls this method using his own herd.*/
	public void incorporateHerd(HorseHerdSH herd) {
		System.out.println(herdID + ": Attempting to incorporate!");
		
		if (herd == null || herd == this){
			System.out.println(herdID + ": Problem incorperating herd.");
			return;
		}
		
		for (int i = 0, j = 0; i < herdList.length && j < herdList.length; i++){
			if (herdList[i] == null){
				if (herd.herdList[j] != null){
					herd.herdList[j].setPriority(3);
					
					if (addHorse(herd.herdList[j])){
					}
					else{
						herd.herdList[j].setDead();
						if (herdList[j].getItsFoal() != null){
							herdList[j].getItsFoal().setDead();
						}
					}
					
					j++;
				}
			}
		}
		
		herd.deleteAll();
		
		System.out.println(herdID + ": Done incorporating!");
	}
	
	/**deletes all horses in the herd*/
	public void deleteAll(){
		for (int i = 0; i < herdList.length; i++){
			if (herdList[i] != null){
				if (herdList[i].getItsFoal() != null){
					herdList[i].getItsFoal().setDead();
				}
				
				herdList[i].setDead();
			}
		}
	}
	
	/**Counts all horses (and foals) in the herd. Foals are not included in herdList[].*/
	public int getHerdSize(){
		int size = 0;
		for (int i = 0; i < herdList.length; i++){
			if (herdList[i] != null){
				size++;
				if (herdList[i].getItsFoal() != null){
					size++;
				}
			}
		}
		
		return size;
	}
	
	
	/**Only returns the assigned stallion*/
	public EntityHorseWildSH getStallion(){
		return stallion;
	}
	
	/**Only returns the assigned matriarch*/
	public EntityHorseWildSH getMatriarch(){
		return matriarch;
	}
	
	/**Checks if this herd has a stallion*/
	public boolean hasStallion(){
		if (getStallion() == null){
			System.out.println("Null stallion");
			return false;
		}
		else if (!getStallion().isStallion()){
			System.out.println("Stallion not stallion");
			return false;
		}
		
		System.out.println(stallion.getSimplyID());
		return true;
	}
	
}
