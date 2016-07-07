package simplyhorses.common.items;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import simplyhorses.common.SimplyHorses;
import simplyhorses.common.entities.horses.EntityArabianSH;
import simplyhorses.common.entities.horses.EntityClydesdaleSH;
import simplyhorses.common.entities.horses.EntityHorseSH;
import simplyhorses.common.entities.horses.EntityHorseTameSH;
import simplyhorses.common.entities.horses.EntityMustangSH;

public class ItemBreedifierSH extends ItemSH{

	Class alterToBreed;
	
	public ItemBreedifierSH(int par1, Class par2Class) {
		super(par1);
        maxStackSize = 1;
        
		alterToBreed = par2Class;
	}
	
	@Override
	//TODO Figure out how to initialized from the class
	public boolean itemInteractionForEntity(ItemStack par1ItemStack, EntityLiving par2EntityLiving){
		if (!par2EntityLiving.worldObj.isRemote){
			if (par2EntityLiving instanceof EntityMustangSH && !((EntityMustangSH) par2EntityLiving).isLocked()){
				EntityHorseSH oldHorse = (EntityHorseSH) par2EntityLiving;
				EntityHorseSH newHorse = null;
				
				if (alterToBreed != EntityMustangSH.class){
					if (alterToBreed == EntityArabianSH.class){
						newHorse = new EntityArabianSH(oldHorse.worldObj);
					}
					else if (alterToBreed == EntityClydesdaleSH.class){
						newHorse = new EntityClydesdaleSH(oldHorse.worldObj);
					}
					
					oldHorse.showExplosionParticle();
					oldHorse.setDead();
		            oldHorse.transferStatsTo(newHorse);
		            oldHorse.worldObj.spawnEntityInWorld(newHorse);
				}
				else if (alterToBreed == EntityMustangSH.class){
					((EntityMustangSH) oldHorse).setLocked(true);
				}
				else return false;
				
                --par1ItemStack.stackSize;
	            
	            return true;
			}
		}
		
		return false;
	}

}
