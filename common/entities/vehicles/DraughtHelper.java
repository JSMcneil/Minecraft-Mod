package simplyhorses.common.entities.vehicles;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import simplyhorses.common.SimplyHorses;
import simplyhorses.common.entities.ISteerableSH;
import simplyhorses.common.entities.horses.EntityHorseDraughtSH;

public class DraughtHelper implements ISteerableSH{
	
	private Random rand;
	
	private UUID[] loadList;
	
	/**Holds all draughtTech part of this unit*/
	private ArrayList<EntityDraughtTechSH> draughtAssembly;
	
	/**Holds all horses currently pulling this train*/
	private ArrayList<EntityHorseDraughtSH> horseList;
	
	/**The draughttech entity that acts as the "primary" entity in this assembly. Updates this assembly*/
	private EntityDraughtTechSH auxilliary;
	
	/**The draughttech entity at the front of the assembly, used to change direction.*/
	private EntityDraughtTechSH lead;
	
	/**The entity (technically, a player) driving this assembly*/
	private Entity driver;
	
	/**0 = stop, 1 = walk, 2 = trot, 3 = gallop*/
	private int speedMode;
	
	/**Same as speedMode. The actually speed are the assembly is determined by the smaller of these values.*/
	private int speedAllowance;
	
	private boolean draughtHalted;
	
	private boolean turningLeft;
	private boolean turningRight;
	
	private int stopCountdown;
	
	public DraughtHelper(){
		this(null);
	}
	
	public DraughtHelper(EntityDraughtTechSH draughtTech){
		//SimplyHorses.registerNewDraughtHelper(this);
		
		rand = new Random();
		
		loadList = null;
		
		auxilliary = draughtTech;
		lead = null;
		
		draughtAssembly = new ArrayList<EntityDraughtTechSH>();
		horseList = new ArrayList<EntityHorseDraughtSH>();
		
		if (draughtTech != null) {
			draughtAssembly.add(draughtTech);
		}
		
		driver = null;
		speedMode = 0;
		speedAllowance = 0;
		draughtHalted = false;
		
		stopCountdown = -1;
	}
	
	/**Called every tick by the DraughtTech entity that initialized it*/
	public void onDraughtUpdate(){
		//System.out.println(this + " updated!");
		
		//load assembly if needed
		if (auxilliary != null && loadList != null){
			ArrayList<EntityDraughtTechSH> list = new ArrayList<EntityDraughtTechSH>();
			World world = auxilliary.worldObj;
			
			for (int i = 0; i < loadList.length; i++){
				list.add((EntityDraughtTechSH) auxilliary.findEntity(loadList[i]));
			}
			
			addUnits(list);
			loadList = null;
		}
		
		//Check the driver
		if (driver != null){
			if (driver.isDead){
				driver = null;
			}
			else if (driver.ridingEntity == null){
				driver = null;
			}
			else if (!(driver.ridingEntity instanceof EntityVehicleSeatSH)){
				driver = null;
			}
			else if (draughtAssembly.indexOf(((EntityVehicleSeatSH) driver.ridingEntity).getVehicle()) == -1){
				driver = null;
			}
		}
		
		//Clean up assembly before continuing
		ArrayList<EntityDraughtTechSH> removallist = new ArrayList<EntityDraughtTechSH>();
		
		for(EntityDraughtTechSH unit: draughtAssembly){
			if (unit != null && unit.isDead){
				removallist.add(unit);
			}
		}
		
		if (!removallist.isEmpty()){
			removeUnits(removallist);
		}
		
		//Check for auxilliary and lead units
		if (auxilliary == null || draughtAssembly.indexOf(auxilliary) == -1){
			auxilliary = draughtAssembly.get(0);
		}
		
		if (lead == null || draughtAssembly.indexOf(lead) == -1 || draughtAssembly.indexOf(lead) != draughtAssembly.size() - 1){
			lead = draughtAssembly.get(draughtAssembly.size() - 1);
		}
		
		//Coordinate movements
		float speed = 0.0F;
		
		int mode = Math.min(speedMode, speedAllowance);
		
		switch(mode){
		case 0:
			return;
		case 1:
			speed = 0.15F;
			break;
		case 2:
			speed = 0.25F;
			break;
		case 3:
			speed = 0.40F;
			break;
		default:
			System.out.println("non-valid speedmode [" + speedMode + "]! Stopping!");
			speedMode = 0;
			return;
		}
		
		boolean waiting = false;
		
		for (int i = 0, j = 1, k = -1; i < draughtAssembly.size(); i++, j++, k++){
			EntityDraughtTechSH unit = draughtAssembly.get(i);
			EntityDraughtTechSH nextUnit = j < draughtAssembly.size()? draughtAssembly.get(j): null;
			EntityDraughtTechSH prevUnit = k >= 0? draughtAssembly.get(k): null;
			
			if (nextUnit != null && nextUnit != unit){
				
				unit.faceEntity(nextUnit, 10.0F, unit.getVerticalFaceSpeed());
				
				if (unit.getDistanceToEntity(nextUnit) >= 4.5){
					unit.getNavigator().tryMoveToEntityLiving(nextUnit, 0.3F);
					waiting = true;
					continue;
				}
				
				if (unit.getDistanceToEntity(nextUnit) <= 4){
					unit.getNavigator().clearPathEntity();
				}
			} else if (unit == lead){ 
				if (turningLeft && !turningRight){
					lead.rotationYaw -= 2;
				} else if (turningRight && !turningLeft){
					lead.rotationYaw += 2;
				} else if (turningLeft && turningRight){
					handleHardStop(0);
				}
			}
			
			if (!waiting && !unit.worldObj.isRemote){
				float f1 = (unit.rotationYaw * (float)Math.PI) / 180F;
		        float f2 = MathHelper.sin(f1);
		        float f3 = MathHelper.cos(f1);
		        
				unit.moveEntity(speed * -f2, 0.1, speed * f3);
			}
		}
		
		if (driver == null){
			if (stopCountdown == -1){
				stopCountdown = 60 + 20 * rand.nextInt(speedMode + 1);
			}
			else if (stopCountdown == 0){
				handleSpeedDown();
				if (speedMode > 0){
					stopCountdown = 60 + 20 * rand.nextInt(speedMode + 1);
				}
			}
			
			stopCountdown--;
		}
	}
	
	public void findSpeedAllowance(){
		float load = 0.0F;
		float bonus = 0.0F;
		float power = 0.0F;
		draughtHalted = false;
		
		for (EntityDraughtTechSH tech: draughtAssembly){
			if (tech instanceof EntityVehicleSH){
				load += ((EntityVehicleSH) tech).getVehicleWeight();
				bonus += ((EntityVehicleSH) tech).getSpeedBonus();
			} else if (tech instanceof EntityWhippletreeSH){
				power += ((EntityWhippletreeSH) tech).getHorsePower();
			}
			
			if (tech.draughtHalted()){
				draughtHalted = true;
			}
		}
		
		float allowance = power - load;
		float allowance2 = allowance - bonus;
		
		if (allowance < 0){
			speedAllowance = 0;
		} else if (allowance == 0){
			speedAllowance = 1;
		} else if (allowance > 0){
			speedAllowance = 2;
		}
		
		if (allowance2 > 0){
			speedAllowance = 3;
		}
		
		if (driver != null && driver instanceof EntityPlayer && ((EntityPlayer) driver).capabilities.isCreativeMode){
			speedAllowance = 3;
		}
		
		if (draughtHalted){
			speedAllowance = 0;
		}
	}
	
	public void setDriver(Entity entity){
		if (driver == null){
			driver = entity;
			if (entity instanceof EntityPlayer){
				((EntityPlayer) entity).addChatMessage("You are now driving!");
			}
		} else{
			if (entity instanceof EntityPlayer){
				if (driver instanceof EntityPlayer && ((EntityPlayer) driver).username.equals(((EntityPlayer) entity).username)){
					((EntityPlayer) entity).addChatMessage("You were already driving!");
				}
				else{
					((EntityPlayer) entity).addChatMessage(((EntityPlayer) entity).username + " is already driving!");
				}
			}
		}
		
		findSpeedAllowance();
	}

	public void tryRemoveDriver(EntityPlayer entityplayer) {
		if (driver == entityplayer){
			driver = null;
		}
	}
	
	public void toggleSpeedmode(){
		speedMode = speedMode == 0? 1: 0;
	}
	
	/**Obligatory comment...
	 * Straightens the assembly based on the position and rotation of auxiliary, placing every unit (whippletree or vehicle) in its correct spot*/
	private void refreshAssembly(){
		//System.out.println(this + " Refreshing Assembly!");
		
		for (EntityDraughtTechSH tech: draughtAssembly){
			System.out.println("Draught Assembly: " + tech);
		}
		
		if (auxilliary == null && draughtAssembly.size() > 0){
			auxilliary = draughtAssembly.get(0);
		}
		
		if (auxilliary == null){
			return;
		}
		
		float f1 = (auxilliary.rotationYaw * (float)Math.PI) / 180F;
        float f2 = MathHelper.sin(f1);
        float f3 = MathHelper.cos(f1);
        
        int baseOffset = 0;
        
		for (EntityDraughtTechSH tech: draughtAssembly){
			if (tech == auxilliary){
				continue;
			}
			
			int techindex = draughtAssembly.indexOf(tech);
			tech.setPositionAndRotation(auxilliary.posX - (double)(f2 * (baseOffset + techindex * 3F)), auxilliary.posY, auxilliary.posZ + (double)(f3 * (baseOffset + techindex * 2.5F)), auxilliary.rotationYaw, auxilliary.rotationPitch);
			tech.moveEntityWithHeading(0.0F, 5.0F);
		}
		
		//System.out.println(this + " Assembly refreshed!");
		
		findSpeedAllowance();
	}
	
	/**Finds all whippletrees and singletrees in the trains and places them at the front*/
	public void orderWhippletrees(){
		ArrayList<EntityWhippletreeSH> trees = new ArrayList<EntityWhippletreeSH>();
		
		for (EntityDraughtTechSH tech: draughtAssembly){
			if (tech instanceof EntityWhippletreeSH){
				trees.add((EntityWhippletreeSH) tech);
			}
		}
		
		for (EntityWhippletreeSH tree: trees){
			draughtAssembly.remove(tree);
			draughtAssembly.add(tree);
		}
		
	}
	
	/**Public accessor to this class to add units*/
	public void incorporate(EntityDraughtTechSH tech){
		if (tech.getDraughtHelper() == this){
			refreshAssembly();
		} else {
			addUnits(tech.getDraughtHelper().draughtAssembly);
		}
	}
	
	/**Public accessor to this class to add units*/
	public void incorporate(ArrayList<EntityDraughtTechSH> tech){
		addUnits(tech);
	}
	
	/**Public accessor to replace tech*/
	public void replaceTech(EntityDraughtTechSH tech, EntityDraughtTechSH tech2){
		//If the original tech isn't in this assembly
		if (draughtAssembly.indexOf(tech) == -1){
			incorporate(tech2);
			return;
		}
		
		//if the new tech is alread in this assembly
		if (draughtAssembly.indexOf(tech2) != -1){
			refreshAssembly();
			return;
		}
		
		//replace tech with tech2
		draughtAssembly.set(draughtAssembly.indexOf(tech), tech2);

		tech.setDraughtHelper(new DraughtHelper(tech));
		tech2.setDraughtHelper(this);
		
		if (tech2 instanceof EntityWhippletreeSH){
			for (int i = 0; i < ((EntityWhippletreeSH) tech2).getHorses().length; i++){
				horseList.add(((EntityWhippletreeSH) tech2).getHorses()[i]);
			}
		}
		
		refreshAssembly();
	}
	
	/**Splits this DraughtHelper's assembly into a separate DraughtHelper starting after the passed-in EntityDraughtTechSH*/
	public void splitAfter(EntityDraughtTechSH tech){
		int auxindex = draughtAssembly.indexOf(tech) + 1;
		if (auxindex >= draughtAssembly.size()){
			return;
		}
		
		//Prepare an ArrayList of the tech to be moved
		ArrayList<EntityDraughtTechSH> newAssembly = new ArrayList<EntityDraughtTechSH>();
		
		//Populate that list...
		for (int i = auxindex; i < draughtAssembly.size(); i++){
			newAssembly.add(draughtAssembly.get(i));
		}
		
		//Prepare a new DraughtHelper for these new tech
		DraughtHelper newHelper = new DraughtHelper();
		
		//Move units around
		this.removeUnits(newAssembly); //Remove from here
		newHelper.addUnits(newAssembly); //Add to there
	}
	
	/**Incorporates an old DraughtHelper into this one. The units in that DraughtHelper's
	 * assembly are added in the same order to this assembly*/
	private void addUnits(DraughtHelper dhelper){
		addUnits(dhelper.draughtAssembly);
		/*for (EntityDraughtTechSH tech: dhelper.draughtAssembly){
			draughtAssembly.add(tech);
			
			if (tech instanceof EntityWhippletreeSH){
				for (int i = 0; i < ((EntityWhippletreeSH) tech).getHorsePair().length; i++){
					horseList.add(((EntityWhippletreeSH) tech).getHorsePair()[i]);
				}
			}
			
			tech.setDraughtHelper(this);
		}
		
		dhelper.closeOut();
		refreshAssembly();*/
	}
	
	/**Incorporates the units in the passed-in ArrayList into this DH. The units are added in the same 
	 * order to this assembly*/
	private void addUnits(ArrayList<EntityDraughtTechSH> newAssembly) {
		for (EntityDraughtTechSH tech: newAssembly){
			if (tech == null){
				System.out.println(this + " Tried to load null tech!");
				continue;
			}
			
			if (draughtAssembly.indexOf(tech) == -1){
				draughtAssembly.add(tech);
			}else{
				continue;
			}
			
			if (tech.getDraughtHelper() != null){
				tech.getDraughtHelper().closeOut();
			}
			
			if (tech instanceof EntityWhippletreeSH){
				for (int i = 0; i < ((EntityWhippletreeSH) tech).getHorses().length; i++){
					horseList.add(((EntityWhippletreeSH) tech).getHorses()[i]);
				}
			}
			
			tech.setDraughtHelper(this);
		}
		
		orderWhippletrees();
		
		refreshAssembly();
	}
	
	/**Removes all entities in the passed-in ArrayList<EntityDraughtTechSH>*/
	private void removeUnits(ArrayList<EntityDraughtTechSH> removallist) {
		for (EntityDraughtTechSH tech: removallist){
			System.out.println("removing " + tech);
			draughtAssembly.remove(tech);
			
			if (tech instanceof EntityWhippletreeSH){
				for (int i = 0; i < ((EntityWhippletreeSH) tech).getHorses().length; i++){
					horseList.remove(((EntityWhippletreeSH) tech).getHorses()[i]);
				}
			}
			
			if (tech != null && !tech.isDead){
				tech.setDraughtHelper(null);
			}
		}
		
		refreshAssembly();
	}
	
	/**Called if this DH's assembly has been incorporated into a different DH*/
	private void closeOut() {
		auxilliary = null;
		draughtAssembly = new ArrayList<EntityDraughtTechSH>();
		setDriver(null);
		speedMode = 0;
	}
	
	public boolean isMoving() {
		return speedMode > 0;
	}
	
	public EntityDraughtTechSH getAuxilliary(){
		return auxilliary;
	}
	
	public ArrayList<EntityDraughtTechSH> getAssembly(){
		return draughtAssembly;
	}
	
	public Entity getDriver() {
		return driver;
	}
	
	public int getSpeedMode(){
		return speedMode;
	}
	
	/**Returns true only if the tech passed in is this DH's auxilliary and if the assembly has two or more units or a driver*/
	public boolean shouldBeSavedBy(EntityDraughtTechSH tech){
		if (tech != auxilliary){
			return false;
		}
		
		else if (draughtAssembly.size() < 1 && loadList == null){
			return false;
		}
		
		else if (driver == null){
			return false;
		}
		
		else {
			return true;
		}
	}

	public boolean isHorseInDraughtTeam(EntityHorseDraughtSH horse) {
		return horseList.indexOf(horse) != -1;
	}
	
	//ISteerableSH stuff
	@Override
	public void handleSpeedUp() {
		if (speedMode == 0){
			findSpeedAllowance();
		}
		
		if (speedMode != 3){
			speedMode++;
		}
	}

	@Override
	public void handleSpeedDown() {
		if (speedMode != 0){
			speedMode--;
		}
	}

	@Override
	public void handleHardStop(int i) {
		speedMode = 0;
	}

	@Override
	public void handleTurnLeft(Boolean flag) {
		turningLeft = flag;
	}

	@Override
	public void handleTurnRight(Boolean flag) {
		turningRight = flag;
	}

	@Override
	public void handleReversing() {}

	@Override
	public void handleJumping(Boolean flag) {}

	@Override
	public void handleDropping(Boolean Flag) {}
	
	public NBTTagList writeToNBT(NBTTagList par1NBTTagList)
    {
		for (EntityDraughtTechSH tech: draughtAssembly){
			int i = draughtAssembly.indexOf(tech);
			
			NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            if (tech.getPersistentID() != null)
            {
            	nbttagcompound1.setLong("tech" + i + "PersistentIDMSB", tech.getPersistentID().getMostSignificantBits());
            	nbttagcompound1.setLong("tech" + i + "PersistentIDLSB", tech.getPersistentID().getLeastSignificantBits());
            }
            par1NBTTagList.appendTag(nbttagcompound1);
		}
		
		return par1NBTTagList;
    }
	
	public void readFromNBT(NBTTagList par1NBTTagList)
    {
		if (par1NBTTagList.tagCount() > 0){
			loadList = new UUID[par1NBTTagList.tagCount()];
			
	        for(int i = 0; i < par1NBTTagList.tagCount(); i++)
	        {
	            NBTTagCompound nbttagcompound1 = (NBTTagCompound)par1NBTTagList.tagAt(i);
	            
	            if (nbttagcompound1.hasKey("tech" + i + "PersistentIDMSB") && nbttagcompound1.hasKey("tech" + i + "PersistentIDLSB")){
	            	loadList[i] = new UUID(nbttagcompound1.getLong("tech" + i + "PersistentIDMSB"), nbttagcompound1.getLong("tech" + i + "PersistentIDLSB"));
	            }
	        }
	        
		}
    }
	
	public UUID[] getLoadList(){
		return loadList;
	}

}
