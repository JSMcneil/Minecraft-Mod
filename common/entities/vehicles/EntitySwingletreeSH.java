package simplyhorses.common.entities.vehicles;

import java.util.UUID;

import simplyhorses.common.entities.horses.EntityHorseDraughtSH;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySwingletreeSH extends EntityWhippletreeSH {

	public EntitySwingletreeSH(World par1World) {
		super(par1World);
	}
	
	public EntitySwingletreeSH(World par1World, EntityHorseDraughtSH horse){
		this(par1World);
		
		setHorses(new EntityHorseDraughtSH[]{horse});
		isLoaded = true;
		
	}
	
	@Override
    public String getTexture()
	{
		return "/mods/SimplyHorses/textures/vehicles/swingletree.png";
	}
	
	@Override
	public void loadHorses(){
		if (loadList == null){
			return;
		}
		
		setHorses(new EntityHorseDraughtSH[]{(EntityHorseDraughtSH) findEntity(loadList[0])});
		
		isLoaded = true;
	}
	
	@Override
	public void updateHorsePositions(){
		if (!isLoaded()){
			return;
		}
		
		float f1 = (rotationYaw * (float)Math.PI) / 180F;
        float f2 = MathHelper.sin(f1);
        float f3 = MathHelper.cos(f1);
        
		horses[0].setPositionAndRotation(posX - (double)(f2 * 0.28F), posY, posZ + (double)(f3 * 0.28F), rotationYaw, horses[0].rotationPitch);
		horses[0].rotationYaw = this.rotationYaw;
	}
	
	@Override
	public void respawn() {
		if (!worldObj.isRemote){
			EntitySwingletreeSH newSwingletree = new EntitySwingletreeSH(this.worldObj, this.horses[0]);
			newSwingletree.setPositionAndRotation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
			
			getDraughtHelper().replaceTech(this, newSwingletree);
			setDead();
			worldObj.spawnEntityInWorld(newSwingletree);
		}
	}
	
	@Override
	public void createNewHorseArray(){
		horses = new EntityHorseDraughtSH[1];
	}
	
	@Override
	protected UUID[] getNewLoadList(){
		return new UUID[1];
	}

}
