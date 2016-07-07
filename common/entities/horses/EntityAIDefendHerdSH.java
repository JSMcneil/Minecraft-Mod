package simplyhorses.common.entities.horses;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class EntityAIDefendHerdSH extends EntityAIBase {

	EntityHorseWildSH thisstallion;
	EntityPlayer targetplayer;
	EntityHorseWildSH targetstallion;
	int attackCooldown;
	boolean isRushingPlayer;
	boolean isAttackingPlayer;
	boolean isAttacking;
	
	public EntityAIDefendHerdSH(EntityHorseWildSH entityWildHorseSH) {
		thisstallion = entityWildHorseSH;
		targetplayer = findTargetPlayer();
		targetstallion = findTargetStallion();
		attackCooldown = 0;
		isRushingPlayer = false;
		isAttackingPlayer = false;
		isAttacking = false;
	}

	@Override
	public boolean shouldExecute() {
		/*if (thisstallion.getSimplyID().equals("WildHorse8")){
			if (isAttackingPlayer()) System.out.println(thisstallion.getSimplyID() + " is attacking? " + isAttackingPlayer());
			if (isRushingPlayer) System.out.println("Rush: " + isRushingPlayer);
			if (isAttackingPlayer) System.out.println("Attack: " + isAttackingPlayer);
		}*/
		
		if (!thisstallion.isStallion() || !thisstallion.isFree() || thisstallion.worldObj.isRemote) return false;
		
		checkTargets();
		
		if (targetplayer == null && targetstallion == null) return false;

		if (thisstallion.getHerd() == null || thisstallion.getHerd().getMatriarch() == null) return false;
		
		if (targetplayer != null && thisstallion.getHerd().getMatriarch().getDistanceToEntity(targetplayer) > 20) return false;
		
		if (thisstallion.getDistanceToEntity(thisstallion.getHerd().getMatriarch()) > 45) return false;
		
		isAttacking = true;
		return true;
	}
	
	@Override
	public boolean continueExecuting() {
		return shouldExecute();
	}

	@Override
	public void resetTask() {
		targetplayer = null;
		targetstallion = null;
		isRushingPlayer = false;
		isAttackingPlayer = false;
		isAttacking = false;
		thisstallion.getNavigator().clearPathEntity();
	}

	@Override
	public void updateTask() {
		checkTargets();
		
		if(targetplayer != null && targetplayer.getDistanceToEntity(thisstallion) <= 10 && (!targetplayer.isSneaking() && targetplayer.ridingEntity == null)){
			isAttackingPlayer = true;
			isRushingPlayer = false;
			
			float f = targetplayer.getDistanceToEntity(thisstallion);
			attackEntity(thisstallion, targetplayer, f);
		}
		else if(targetstallion != null && targetstallion.getDistanceToEntity(thisstallion) <= 20 && targetstallion.isFree()){
			float f = targetstallion.getDistanceToEntity(thisstallion);
			attackEntity(thisstallion, targetstallion, f);
			if(thisstallion.getHealth() < 3 && !thisstallion.worldObj.isRemote){
				thisstallion.setPriority(3);
				targetstallion.getHerd().incorporateHerd(thisstallion.getHerd());
			}
		}
	}
	
	protected EntityPlayer findTargetPlayer(){
		EntityPlayer thisplayer = null;
		EntityPlayer entityplayer = thisstallion.worldObj.getClosestPlayerToEntity(thisstallion, 20D);
		if (entityplayer != null && !entityplayer.capabilities.isCreativeMode){
			thisplayer = entityplayer;
		}
		return thisplayer;
	}
	
	protected EntityHorseWildSH findTargetStallion(){
		EntityHorseWildSH entitywildhorse = null;
		for (int i = 0; i < thisstallion.worldObj.loadedEntityList.size(); i++)
        {
            Entity entity1 = (Entity)thisstallion.worldObj.loadedEntityList.get(i);
            if(entity1 instanceof EntityHorseWildSH && ((EntityHorseWildSH)entity1).isStallion() && entity1 != thisstallion && entity1.getDistanceToEntity(thisstallion) < 20  && ((EntityHorseWildSH) entity1).isFree()){
            	entitywildhorse = (EntityHorseWildSH)entity1;
            	break;
            }
        }
		return entitywildhorse;
	}
	
	protected void checkTargets(){
		if(targetplayer == null){
			targetplayer = findTargetPlayer();
		}
		if(targetstallion == null){
			targetstallion = findTargetStallion();
		}
		if(targetplayer != null && targetplayer.isDead){
			targetplayer = null;
		}
		if(targetstallion != null && (!targetstallion.isStallion() || targetstallion.isDead)){
			thisstallion.setHealth(thisstallion.getMaxHealth());
			targetstallion = null;
		}
	}
	
	protected void attackEntity(EntityHorseWildSH par1Entity, EntityLiving par2Entity, float par2) {
		EntityHorseWildSH attacker = par1Entity;
        EntityLiving target = par2Entity;
		if(attacker.isWithinHomeDistance(MathHelper.floor_double(target.posX), MathHelper.floor_double(target.posY), MathHelper.floor_double(target.posZ))){
			if (attacker.getEntitySenses().canSee(target))
	        {
	            attacker.getNavigator().tryMoveToEntityLiving(target, 0.4F);
	        }
			
			attacker.getLookHelper().setLookPositionWithEntity(target, 30F, 30F);
			attacker.getNavigator().tryMoveToEntityLiving(target, 0.4F);
			attackCooldown = Math.max(attackCooldown - 1, 0);
			double d = attacker.width * 2.0F * (attacker.width * 2.0F);
			if(attacker.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ) > d){
	            return;
	        }
			if(attackCooldown > 0){
				return;
			}
			else{
				if(target instanceof EntityPlayer){
					attackCooldown = 20;
					attacker.attackEntityAsMob(target);
					
					target.addVelocity(-MathHelper.sin((attacker.rotationYaw * (float)Math.PI) / 180F) * 2F * 0.5F, 0.3D, MathHelper.cos((attacker.rotationYaw * (float)Math.PI) / 180F) * 2F * 0.5F);

					thisstallion.showHeartsOrSmokeFX(false, 3);
					isRushingPlayer = false;
					isAttackingPlayer = false;
		            return;
				}
				else{
					attackCooldown = 20;
					attacker.attackEntityAsMob(target);
					
					target.addVelocity(-MathHelper.sin((attacker.rotationYaw * (float)Math.PI) / 180F) * 2F * 0.5F, 0.1D, MathHelper.cos((attacker.rotationYaw * (float)Math.PI) / 180F) * 2F * 0.5F);
					attacker.showExplosionParticle();
					
		            return;
				}
			}
		}
	}

	public boolean isAttackingPlayer() {
		return isAttackingPlayer || isRushingPlayer;
	}

	public void targetPlayer(EntityPlayer entityplayer) {
		
	}

}
