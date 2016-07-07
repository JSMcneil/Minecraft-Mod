package simplyhorses.common.entities.horses;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import simplyhorses.common.entities.vehicles.EntityDraughtTechSH;
import simplyhorses.common.entities.vehicles.EntityVehicleSH;
import simplyhorses.common.entities.vehicles.EntityWhippletreeSH;

public class EntityFoalSH extends EntityHorseSH{

	private EntityHorseSH mother;
	private String motherID;
	private int searchCooldown;
	private boolean lettingMomWork;
	
	public EntityFoalSH(World world) {
		super(world);
		
		breed = 1;
        mother = null;
        motherID = "noID";
        searchCooldown = 0;
        lettingMomWork = false;
        setSize(0.9F, 2.5F);
        
        tasks.addTask(10, new EntityAIFollowParentSH(this, 0.30F));
	}
    
	@Override
    public int getTextureSlots(){
    	return 8;
    }
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		if ((mother == null || mother.isDead) && searchCooldown <= 0){
			findMother();
		}
		
		if (mother != null){
			if (mother instanceof EntityHorseDraughtSH && mother.isWorking()){
				if (!lettingMomWork){
					lettingMomWork = true;
					setTiedToEntity(((EntityHorseDraughtSH)mother).getActiveWhippletree().getDraughtHelper().getAuxilliary());
				} else if (getTiedToEntity() != null && getTiedToEntity() instanceof EntityWhippletreeSH){
				
					EntityDraughtTechSH tech = ((EntityWhippletreeSH) getTiedToEntity()).getDraughtHelper().getAuxilliary();
					if (tech instanceof EntityVehicleSH){
						setTiedToEntity(tech);
					}
				}
			} else if (lettingMomWork){
				lettingMomWork = false;
				setTiedToEntity(null);
				setFree();
			}
		}
		
		if(!isChild()){
			growUp();
		}
	}
	
	public void findMother(){
		
		EntityHorseSH motherBreed = null;
		EntityHorseSH motherFree = null;
		EntityHorseSH motherLast = null;
		
		for (int i = 0; i < worldObj.loadedEntityList.size(); i++)
        {
            Entity entity1 = (Entity)worldObj.loadedEntityList.get(i);
            if(entity1 instanceof EntityHorseSH && entity1.getDistanceToEntity(this) <= 10)
            {
        		if (((EntityHorseSH)entity1).getSimplyID().equals(motherID) && ((EntityHorseSH)entity1).getItsFoal() == null){
                	setMother((EntityHorseSH)entity1);
                	((EntityHorseSH)entity1).setItsFoal(this);
                	return;
        		} else if (((EntityHorseSH)entity1).getBreed() == getBreed() && ((EntityHorseSH)entity1).getItsFoal() == null && motherBreed == null){
        			motherBreed = ((EntityHorseSH)entity1);
        		} else if (((EntityHorseSH)entity1).getItsFoal() == null){
        			motherFree = ((EntityHorseSH)entity1);
        		} else if (((EntityHorseSH)entity1) instanceof EntityHorseSH){
        			motherLast = ((EntityHorseSH)entity1);
        		}
            }
        }
		
        if (motherBreed != null){
        	setMother(motherBreed);
        	motherBreed.setItsFoal(this);
        } else if (motherFree != null){
        	setMother(motherFree);
        	motherFree.setItsFoal(this);
        } else if (motherLast != null){
        	setMother(motherLast);
        } else {
        	searchCooldown = 1200;
        }
	}
	
	private void growUp(){
		if (worldObj.isRemote){
			return;
		}
		
		setDead();
		switch(breed){
		case 1:
			EntityHorseWildSH entitywildhorse = new EntityHorseWildSH(worldObj);
			entitywildhorse.setPriority(3);
			transferStatsTo(entitywildhorse);
			
			if(mother != null && mother instanceof EntityHorseWildSH){
				EntityHorseWildSH wildMom = (EntityHorseWildSH) mother;
				
				if (wildMom.getHerd() != null && !wildMom.getHerd().hasStallion()){
					wildMom.getHerd().setStallion(entitywildhorse);
					//System.out.println(this.getSimplyID() + ": becoming mom's herd's stallion!");
				}
				else if(wildMom.getHerd() != null){
					wildMom.getHerd().addHorse(entitywildhorse);
					//System.out.println(this.getSimplyID() + ": joining mom's herd!");
				}
				else if (wildMom.getHerd() == null){
					entitywildhorse.setPriority(1);
					entitywildhorse.setHerdLoaded(true);
					entitywildhorse.setNewHerd(false);
					//System.out.println(this.getSimplyID() + ": creating own herd cuz Mom doesn't have one!");
				}
			}
			else{
				entitywildhorse.setPriority(1);
				entitywildhorse.setHerdLoaded(true);
				entitywildhorse.setNewHerd(false);
				//System.out.println(this.getSimplyID() + ": creating own herd!");
			}
			
	        worldObj.spawnEntityInWorld(entitywildhorse);
	        break;
		case 2:
			EntityMustangSH entitymustang = new EntityMustangSH(worldObj);
			transferStatsTo(entitymustang);
			entitymustang.setLocked(true);
	        worldObj.spawnEntityInWorld(entitymustang);
	        break;
		case 3:
			EntityArabianSH entityarabian = new EntityArabianSH(worldObj);
			transferStatsTo(entityarabian);
	        worldObj.spawnEntityInWorld(entityarabian);
	        break;
		case 4:
			EntityClydesdaleSH entityclydesdale = new EntityClydesdaleSH(worldObj);
			transferStatsTo(entityclydesdale);
	        worldObj.spawnEntityInWorld(entityclydesdale);
	        break;
		}
	}
	
	@Override
	public boolean interact(EntityPlayer entityplayer){
		ItemStack itemstack = entityplayer.getCurrentEquippedItem();
    	if (super.interact(entityplayer)){
			if (didHeal){
				showHeartsOrSmokeFX(true, 3);
				if (!entityplayer.capabilities.isCreativeMode) --itemstack.stackSize;
				return true;
			}
    	}
    	
    	if (entityplayer.capabilities.isCreativeMode && !worldObj.isRemote){
			if (itemstack != null && itemstack.itemID == Item.bucketMilk.itemID){
				growUp();
				return true;
	    	}
		}
    	
    	return false;
	}
	
	public float headSwing(float f) //you get the picture
    {
        if (grazeTimer <= 0) 
        {
            return 0.3944F + rotationPitch / 57.29578F; //45 degrees + "looking-around" angle
        }
        if (grazeTimer >= 4 && grazeTimer <= 296)
        {
        	float f1 = ((float)(grazeTimer - 4) - f) / 32F;
            return -0.7854F + 0.2199115F * MathHelper.sin(f1 * 28.7F); //grazing animation taken from EntitySheep
        }
        else
        {
        	return 0.3944F + rotationPitch / 57.29578F; //45 degrees + "looking-around" angle
        }
    }
	
	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        if (mother != null){
        	par1NBTTagCompound.setString("MotherID", mother.getSimplyID());
        }
        else{
        	par1NBTTagCompound.setString("MotherID", "noID");
        }
        par1NBTTagCompound.setInteger("Breed", breed);
    }

	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        motherID = par1NBTTagCompound.getString("MotherID");
		breed = par1NBTTagCompound.getInteger("Breed");
    }

	public EntityHorseSH getMother() {
		return mother;
	}

	public void setMother(EntityHorseSH mother) {
		this.mother = mother;
	}

	public void setBreed(int breed) {
		this.breed = breed;
	}
	
}
