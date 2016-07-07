package simplyhorses.common.entities.horses;

import java.util.Random;

import simplyhorses.common.SimplyHorses;

import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIMateWildHorsesSH extends EntityAIBase{

	EntityHorseWildSH stallion;
	EntityHorseWildSH mare;
	int foalLimit;
	int herdLimit = SimplyHorses.maxHerdLimit;
	
	public EntityAIMateWildHorsesSH(EntityHorseWildSH entityWildHorseSH) {
		stallion = entityWildHorseSH;
	}
	
	/**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	if(stallion.getRNG().nextInt(1000) != 0){
    		return false;
    	}
    	
    	if(!stallion.isStallion()){
    		return false;
    	}
    	
    	if(!stallion.isFree()){
    		return false;
    	}
    	
    	if(stallion.getHerd() == null){
    		return false;
    	}
    	
    	if(stallion.getHerd().getHerdSize() >= herdLimit){
    		System.out.println("Herd too big!/n"+ stallion + "\nHerd Size: " + stallion.getHerd().getHerdSize() + "\nMax Size: " + herdLimit);
    		return false;
    	}
    	
    	return true;
    }

    @Override
    public boolean continueExecuting()
    {
    	/*if(!stallion.isStallion()){
    		return false;
    	}
    	
    	if(!stallion.isFree()){
    		return false;
    	}
    	
    	if(!stallion.isStallion() || !stallion.isFree()){
    		return false;
    	}
    	if(stallion.getHerd().getHerdSize() >= herdLimit){
    		return false;
    	}
    	
    	System.out.println("Continuing wild horse mate ai");
    	return true;*/
    	
    	return false; 
    }

    @Override
    public void resetTask()
    {
    	mare = null;
    }

    @Override
    public void updateTask()
    {
    	System.out.println("Mating Wild Horses!");
    	if (stallion.getHerd() == null){
    		System.out.println("Null Herd problem in AIMateWild!");
    		return;
    	}
    	
    	int herd = stallion.getHerd().getHerdSize();
		if(herd < herdLimit){
			foalLimit = Math.max(1, Math.min(herdLimit - herd, herd / 2));
		}
		else {
			foalLimit = 0;
			return;
		}
    	for(int l = 0; l < stallion.worldObj.rand.nextInt(foalLimit); l++){
    		mare = findMare();
    		if (mare != null){
				mate();
    		}
    		else {
    			break;
    		}
    	}
    }
    
    public void pushAI(EntityHorseWildSH wildHorse){
    	if (wildHorse.isStallion()){
    		stallion = wildHorse;
    		updateTask();
    	}
    }
    
    public EntityHorseWildSH findMare(){
    	for (int i = 0; i < stallion.getHerd().getHerdSize(); i++)
        {
    		EntityHorseWildSH newMare = stallion.getHerd().herdList[i];
    		if (newMare == null || newMare.isStallion()){
    			continue;
    		}
    		
    		if (newMare.getGrowingAge() == 0 && newMare.getItsFoal() == null && newMare.isFree()){
    			return newMare;
    		}
        }
    	
    	return null;
    }
    
    public void mate(){
    	EntityFoalSH entityfoal = new EntityFoalSH(mare.worldObj);
    	entityfoal.setLocationAndAngles(mare.posX, mare.posY, mare.posZ, mare.rotationYaw, mare.rotationPitch);
    	entityfoal.renderYawOffset = mare.renderYawOffset;
    	mare.setItsFoal(entityfoal);
    	entityfoal.setMother(mare);
    	entityfoal.breed = 1;
    	mare.setGrowingAge(6000);
    	entityfoal.setGrowingAge(-24000);
    	mare.worldObj.spawnEntityInWorld(entityfoal);
    	Random random = mare.getRNG();
    	mare.showHeartsOrSmokeFX(true, 7);
    }

}
