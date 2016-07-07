package simplyhorses.common.entities.horses;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import simplyhorses.common.SimplyHorses;


public class EntityHorseWildSH extends EntityHorseSH
{
    /**Priority determines the heirarchy of the horse in the herd.
     * 0 = Maverick, must become a stallion or is deleted from the world.
     * 1 = Stallion, protects and loads the herd.
     * 2 = Matriarch, leads the herd, loads the herd if stallion is null.
     * 3 = Mare, regular member of the herd.
     * 4 = Caught Stallion.
     * 5 = Caught Mare. */
	private int priority;
    
    private EntityHorseWildSH[] herdList;
    private String[] loadedHerdList;
    private HorseHerdSH herd;
    private EntityAIMateWildHorsesSH entityaimate;
    public EntityAIDefendHerdSH entityaidefend;
    private boolean herdLoaded;
    private boolean fleeing;
    
    private int bCountdown; //NBT - written
    
    public EntityHorseWildSH(World world)
    {
        super(world);
        breed = 1;
        priority = 0;
        bCountdown = 48000;
        herdLoaded = false;
        setFleeing(false);
        herd = null;
        
        entityaimate = new EntityAIMateWildHorsesSH(this);
        entityaidefend = new EntityAIDefendHerdSH(this);
        
        /**Priority*/
        dataWatcher.addObject(17, new Integer(0));
        tasks.addTask(10, new EntityAIFindStallionSH(this));
        tasks.addTask(11, new EntityAIHerdSH(this, 0.3F, 5F, 15F));
        tasks.addTask(12, entityaidefend);
        tasks.addTask(13, entityaimate);
        
    }
	
    @SideOnly(Side.CLIENT)
	public String getTexture()
	{
		/*if (isStallion() || getPriority() == 4){
			if (textureID() > 7 || textureID() == 1){
				int i = rand.nextInt(8);
				if (i == 1){
					i = 2;
				}
				
				textureIDSet(i);
			}
			
			String id = textureID() == 0? "": "" + textureID();
			
			return "/mods/SimplyHorses/textures/horses/stallion" + id + ".png";
	    }
		
		else */return super.getTexture();
	}
	
    @Override
	public int getMaxSpawnedInChunk()
    {
        return 1;
    }
	
	/**Removes this horse from its current herd (if possible) and prepares a new herd for this horse
	 * flag: true if this horse should attempt to form a new herd, false if it should populate the herd from a list 
	 * (as with stallions and mares on reload or grown wild foals).*/
	public void setNewHerd(Boolean flag){
		if (herd != null ){
			herd.removeHorse(this);
		}
		
		herdList = new EntityHorseWildSH[SimplyHorses.maxHerdLimit + 5];
		
		if(this.isStallion()){
			herdList[0] = this;
		}else{
			herdList[0] = null;
			herdList[1] = this;
		}

		if (flag){
    		loadedHerdList = null;
    	}
    	herd = new HorseHerdSH(this, herdList);
    	SimplyHorses.registerNewHerd(this, herd);
    	
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		/*Stallions and Matriarchs should attempt to load a herd into the game. If they have no herd saved in NBT
		 * The Stallion (if present) should try to form a heard
		 * Matriarchs should only try and load a herd if their stallion isn't present,
		 * as the Matriarch's priority is only written to NBT if this was the case at the end of the last session.*/
		if ((getPriority() == 1 || getPriority() == 2) && !herdLoaded && !worldObj.isRemote){
			
			if (loadedHerdList != null){
				if (loadedHerdList.length < 1){
					setNewHerd(false);
					setHerdLoaded(true);
					return;
				}
				
				if (getPriority() == 2 && ((EntityHorseWildSH) findEntity(loadedHerdList[0])) != null && ((EntityHorseWildSH) findEntity(loadedHerdList[0])).isStallion()){
					return;
				}

				setNewHerd(false);
				herd.loadHerd(loadedHerdList, this, isStallion());
				setHerdLoaded(true);
			}
			else if (getPriority() == 1){
				setNewHerd(false);
				herd.formHerd();
				setHerdLoaded(true);
			}
		}
		
		//Handle fleeing behavior. Not used currently. TODO use currently.
		if (isFleeing() & herd != null){
			if (getNavigator().noPath()){
				if (isMatriarch()){
					herd.herdStartled = false;
					setFleeing(false);
				}else if(herd.getMatriarch() != null && herd.getMatriarch().isFleeing()){
					herd.herdStartled = false;
					//getNavigator().tryMoveToEntityLiving(herd.getMatriarch(), 0.5F);
				}else {
					setFleeing(false);
				}
			}
		}

		//Things todo if not free, like decriment break counter
		if (!isFree())
		{
			setFleeing(false); 
			
			bCountdown--;
			
			if(bCountdown % 10 == 0){
				showHeartsOrSmokeFX(false, 1);
			}
			if(bCountdown <= 0 && !worldObj.isRemote){
				breakHorse();
			}
			if(getTiedToEntity() instanceof EntityPlayer){
				ItemStack itemstack = ((EntityPlayer)getTiedToEntity()).getCurrentEquippedItem();
				if((itemstack == null || (itemstack != null && (itemstack.itemID != SimplyHorses.lasso.itemID && !isFeed(itemstack)))) && !isHitched()){
					setFree();
				}
			}
		}
		
		//Mares should check their surroundings for players, and alert the stallion if one is found
		if (!worldObj.isRemote && !isStallion() && isFree() && herd != null && !herd.herdStartled){
			checkSurroundings();
		}
	}
	
	@Override
	public boolean interact(EntityPlayer entityplayer){
		ItemStack itemstack = entityplayer.getCurrentEquippedItem();
		
		if (super.interact(entityplayer) && !isFree()){
			if (itemstack != null && isFeed(itemstack)){
				if (getFoodWeight2(itemstack)){
					setBreak(foodWeight);
					if (!worldObj.isRemote) System.out.println(bCountdown);
					didHeal = true;
					if (didHeal){
						if (rand.nextInt(10) == 0 && !entityplayer.capabilities.isCreativeMode){
							showHeartsOrSmokeFX(false, 3);
							attackEntityAsMob(entityplayer);
							entityplayer.addVelocity(-MathHelper.sin((rotationYaw * (float)Math.PI) / 180F) * 2F * 0.5F, 0.3D, MathHelper.cos((rotationYaw * (float)Math.PI) / 180F) * 2F * 0.5F);
							entityplayer.motionX *= 1.5D;
							entityplayer.motionZ *= 1.5D;
						}
						else{
							showHeartsOrSmokeFX(true, 3);
						}
						if (!entityplayer.capabilities.isCreativeMode){
							--itemstack.stackSize;

			                if (itemstack.stackSize <= 0)
			                {
			                	entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, (ItemStack)null);
			                }
						}
						didHeal = false;
						return true;
					}
				}
			}
		}
		
		//Delete herd using the spawner lasso
		if (!worldObj.isRemote && itemstack != null && itemstack.itemID == SimplyHorses.spawnLasso.itemID){
			if (entityplayer.isSneaking() && (this.isStallion() || this.isMatriarch()) && this.getHerd() != null){
			getHerd().deleteAll();
			
			return true;
			}
		}
		
		//Creative abilities
		if (entityplayer.capabilities.isCreativeMode){
			if (itemstack != null){
				if (itemstack.itemID == Item.bucketMilk.itemID && !worldObj.isRemote){
					entityaimate.pushAI(this);
		    	}
				
				if (itemstack.itemID == SimplyHorses.spawnLasso.itemID){
					if (getHerd() == null){
						entityplayer.addChatMessage(getSimplyID() + ": No Herd!");
						
						return true;
					} else{
						EntityHorseWildSH[] herd = getHerd().herdList;
						entityplayer.addChatMessage(getSimplyID() + ": Herd Found!");
						for (int i = 0; i < herd.length; i++){
							if (herd[i] != null){
								entityplayer.addChatMessage(herd[i].getSimplyID());
							}
						}
						
						return true;
					}
				}
			}
		}
		
		return false;
	}

	private void breakHorse() {
		if (!worldObj.isRemote){
			setDead();
			
			if (herd != null){
				herd.removeHorse(this);
			}
	        EntityMustangSH entitymustang = new EntityMustangSH(worldObj);
	        transferStatsTo(entitymustang);
	        worldObj.spawnEntityInWorld(entitymustang);
		}
		
        showHeartsOrSmokeFX(true, 7);
	}

	public void checkSurroundings(){
		
		EntityPlayer targetPlayer = worldObj.getClosestPlayerToEntity(this, 10);
        
		if (targetPlayer == null || (targetPlayer != null && (targetPlayer.capabilities.isCreativeMode || (targetPlayer.getCurrentEquippedItem() != null && targetPlayer.getCurrentEquippedItem().itemID == SimplyHorses.spawnLasso.itemID))))
        {
            return;
        }
        
        if (targetPlayer.isSneaking() && targetPlayer.getDistanceToEntity(this) >= 6F){
        	return;
        }

        if (!getEntitySenses().canSee(targetPlayer)) 
        {
        	return;
        }
        
        if (herd != null && !herd.herdStartled && rand.nextInt(5) == 0){
        	getNavigator().clearPathEntity();
        	faceEntity(targetPlayer, 30.0F, 30.0F);
        	showHeartsOrSmokeFX(false, 3);

			//System.out.println(getSimplyID() + ": Startling the herd!");
        	herd.onHerdStartle(targetPlayer, this);
        }
	}
	
	/**Currently unused. Escaped Wild Horses should flee back to their herd if possible.*/
	public void fleeTo(EntityHorseWildSH targetMatr){
		PathNavigate path = getNavigator();
		
		path.clearPathEntity();
		if (path.tryMoveToEntityLiving(targetMatr, 0.4F)){
			setFleeing(true);
		}
	}
	
	/**Decrease the breaking countdown by a certain value.*/
	public void setBreak(int i){
		bCountdown -= foodWeight;
	}
	
	@Override
	public float getBlockPathWeight(int par1, int par2, int par3)
    {
		if (worldObj.getBlockId(par1, par2 - 1, par3) == Block.sand.blockID){
			return 0F;
		}
		
        if (worldObj.getBlockId(par1, par2 - 1, par3) == Block.grass.blockID)
        {
        	if (worldObj.getBiomeGenForCoords(par1, par3).biomeName.equals("plains")){
        		return 10F;
        	}
        	
        	return 9F;
        }
        else
        {
            return worldObj.getLightBrightness(par1, par2, par3) - 0.5F;
        }
    }
	
	@Override
	public void transferStatsTo(EntityHorseSH entityhorse)
    {
		super.transferStatsTo(entityhorse);
		
		if (entityhorse instanceof EntityMustangSH){
			entityhorse.textureIDSet(this.textureID());
		}
    }
	
	@Override
	public void setDead()
    {
		if (herd != null){
			herd.removeHorse(this);
		}
		
		super.setDead();
    }
	
	protected boolean getFoodWeight2(ItemStack itemstack){
		if (itemstack.itemID == SimplyHorses.sCubes.itemID){
			foodWeight *= 200; //foodWeight == 200
			return true;
		}
		if (itemstack.itemID == Item.wheat.itemID){
			foodWeight *= 200; //foodWeight == 400
			return true;
		}
		if (itemstack.itemID == Item.carrot.itemID){
			foodWeight *= 200; //foodWeight == 800
			return true;
		}
		if (itemstack.itemID == Item.appleRed.itemID){
			foodWeight *= 200; //foodWeight == 1200
			return true; 
		}
		if (itemstack.itemID == Item.bread.itemID){
			foodWeight *= 200; //foodWeight == 2000
			return true; 
		}
		if (itemstack.itemID == Item.cake.itemID){
			foodWeight *= 300; //foodWeight == 6000
			return true;
		}
		return false;
	}
	
	public boolean handleLasso(Entity entity){
		if (entity instanceof EntityHorseWildSH || entity == this){
			return false;
		}
		
		if (!isStallion() && herd != null && herd.getStallion() != null && entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.isCreativeMode){
			herd.onHerdStartle((EntityPlayer) entity, this);
		}
		
		return super.handleLasso(entity);
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("Priority", getPriority());
		par1NBTTagCompound.setInteger("Countdown", bCountdown);
		
		if ((getPriority() == 1 || getPriority() == 2) && herd != null){
			par1NBTTagCompound.setInteger("HerdSize", herd.getHerdSize());
			NBTTagList nbttaglist = new NBTTagList();
	        for(int i = 0; i < herd.herdList.length; i++)
	        {
	            if(herd.herdList[i] != null)
	            {
	                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
	                nbttagcompound1.setString("Horse", herd.herdList[i].getSimplyID());
	                nbttaglist.appendTag(nbttagcompound1);
	            }
	        }
	
	        par1NBTTagCompound.setTag("Herd", nbttaglist);
		}
    }

	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        setPriority(par1NBTTagCompound.getInteger("Priority"));
		bCountdown = par1NBTTagCompound.getInteger("Countdown");
		
		if (par1NBTTagCompound.getInteger("Priority") == 1 || par1NBTTagCompound.getInteger("Priority") == 2){
			
			NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Herd");
			loadedHerdList = new String[nbttaglist.tagCount()];
	        for(int i = 0; i < nbttaglist.tagCount(); i++)
	        {
	            NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
	            String sID = nbttagcompound1.getString("Horse");
	            
	            loadedHerdList[i] = sID;
	        }
		}
    }

	public void setPriority(int i) {
		priority = i;
		dataWatcher.updateObject(17, i);		
	}
	
	public int getPriority(){
		return dataWatcher.getWatchableObjectInt(17);
	}

    public boolean isStallion() {
		return dataWatcher.getWatchableObjectInt(17) == 1;
	}

	public boolean isMatriarch() {
		return dataWatcher.getWatchableObjectInt(17) == 2;
	}
	
	public boolean isFleeing(){
		return fleeing;
	}
	
	public boolean isMaverick() {
		return dataWatcher.getWatchableObjectInt(17) == 0;
	}
	
	public EntityHorseWildSH[] getHerdList(){
		return herdList;
	}
	
	public HorseHerdSH getHerd(){
		return herd;
	}
	
	public void setHerd(HorseHerdSH horseherd){
		herd = horseherd;
	}
	
	public int getbCountdown() {
		return bCountdown;
	}

	public void setbCountdown(int bCountdown) {
		this.bCountdown = bCountdown;
	}
	
	public void setHerdLoaded(boolean flag){
		herdLoaded = flag;
	}

	public void setFleeing(boolean fleeing) {
		this.fleeing = fleeing;
	}
	
	public EntityAIMateWildHorsesSH getMateAI(){
		return entityaimate;
	}
}