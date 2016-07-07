package simplyhorses.common.entities.vehicles;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import simplyhorses.common.PacketHandlerSH;
import simplyhorses.common.SimplyHorses;

public class EntityVardoSH extends EntityVehicleSH {

	private boolean bedDeployed;
	private ChunkCoordinates bedFootCoordinates;
	private ChunkCoordinates bedHeadCoordinates;
	
	public EntityVardoSH(World par1World) {
		super(par1World);
        ignoreFrustumCheck = true;

        bedDeployed = false;
        bedFootCoordinates = null;
        bedHeadCoordinates = null;
	}

	@Override
	public String getTexture(){
		return "/mods/SimplyHorses/textures/vehicles/vardo.png";
	}
	
	@Override
	public void onLivingUpdate(){
    	if (bedDeployed){
    		int i = worldObj.getBlockMetadata(bedHeadCoordinates.posX, bedHeadCoordinates.posY, bedHeadCoordinates.posZ);
    		
    		if (!BlockBed.isBedOccupied(i)){
        		destroyBed();
    		}
    	}
    	
    	super.onLivingUpdate();
    }
	
	public void destroyBed() {
		if (bedFootCoordinates == null){
			return;
		}
		
		int i = bedFootCoordinates.posX;
		int j = bedFootCoordinates.posY;
		int k = bedFootCoordinates.posZ;
		worldObj.destroyBlock(i, j, k, false);
		
		int i1 = bedHeadCoordinates.posX;
		int j1 = bedHeadCoordinates.posY;
		int k1 = bedHeadCoordinates.posZ;
		worldObj.destroyBlock(i, j, k, false);
		
		bedHeadCoordinates = null;
		bedFootCoordinates = null;
		bedDeployed = false;
	}
	
	public boolean setPlayerSleeping(EntityPlayer entityplayer){
		
		if (worldObj.isRemote){
			PacketHandlerSH.sendPacketVardoSleepReq(this);
			return true;
		}
		
		if (getDraughtHelper() != null && getDraughtHelper().isMoving()){
			entityplayer.addChatMessage("You can only sleep while the vardo is still.");
			return false;
		}
		
		if (!worldObj.provider.canRespawnHere()){
			entityplayer.addChatMessage("It's a bad idea to sleep here. Try the Overworld.");
			return false;
        }
		
		if (!worldObj.isRemote && worldObj.isDaytime()){
			entityplayer.addChatMessage("tile.bed.noSleep");
			return false;
		}
		
		if (bedDeployed){
			entityplayer.addChatMessage("A player is already sleeping in here!");
			return false;
		}
		
		BlockBed blockbed = (BlockBed)Block.bed;
        int i = MathHelper.floor_double((double)((rotationYaw * 4F) / 360F) + 0.5D) & 3;
        byte byte0 = 0;
        byte byte1 = 0;

        if (i == 0)
        {
            byte1 = 1;
        }

        if (i == 1)
        {
            byte0 = -1;
        }

        if (i == 2)
        {
            byte1 = -1;
        }

        if (i == 3)
        {
            byte0 = 1;
        }
        
        int i1 = (int)posX;
        int j1 = (int)posY + 1;
        int k1 = (int)posZ;
        
        boolean airTest = !bedDeployed && worldObj.isAirBlock(i1, j1, k1) && worldObj.isAirBlock(i1 + byte0, j1, k1 + byte1);
        boolean bedTest = bedDeployed && worldObj.getBlockId(i1, j1, k1) == Block.bed.blockID && worldObj.getBlockId(i1 + byte0, j1, k1 + byte1) == Block.bed.blockID;

        if (airTest || bedTest)
        {
        	worldObj.setBlock(i1, j1, k1, blockbed.blockID, i, 3);

            if (worldObj.getBlockId(i1, j1, k1) == blockbed.blockID)
            {
            	worldObj.setBlock(i1 + byte0, j1, k1 + byte1, blockbed.blockID, i + 8, 3);
            }
            
            int i3 = worldObj.getBlockMetadata(i1, j1, k1);
            int j3 = BlockBed.getDirection(i3);
            
            float bedRotation = 0;
            int m;
            int n;

            switch (j3)
            {
                case 0:
                	bedRotation = 0F;
                	m = 1;
                	n = 0;
                	break;

                case 1:
                	bedRotation = 90F;
                	m = 1;
                	n = 1;
                	break;

                case 2:
                	bedRotation = 180F;
                	m = 1;
                	n = 0;
                	break;

                case 3:
                	bedRotation = 270F;
                	m = 1;
                	n = 1;
                	break;
                default:
                	m = 0;
                	n = 1;
            }
            
            double i2 = (i1 + i1 + byte0)/2 + m * 0.5;
            double j2 = j1 - 1;
            double k2 = (k1 + k1 + byte1)/2 + n * 0.5;
            
            bedDeployed = true;
            setPositionAndRotation(i2, j2, k2, bedRotation, rotationPitch);
            addVelocity(0.01, 0.0D, 0.01);
            
            entityplayer.setPosition(i2, j2 + 1, k2);
            blockbed.onBlockActivated(worldObj, i1, j1, k1, entityplayer, 0, 0, 0, 0);
            
        	bedFootCoordinates = new ChunkCoordinates(i1, j1, k1);
        	bedHeadCoordinates = new ChunkCoordinates(i1 + byte0, j1, k1 + byte1);
            
            return true;
        }
        
        return false;
	}

	@Override
	protected void spawnSeats() {
		EntityVehicleSeatSH seat = new EntityVehicleSeatSH(worldObj, this, true, 0, -1.5F, 0.85F, 1.5F, 0.5F);
		seat.setPositionAndRotation(posX, posY, posZ, rotationYaw, rotationPitch);
		
		EntityVehicleSeatSH seat1 = new EntityVehicleSeatSH(worldObj, this, true, 1, -1.5F, 0.85F, 1.5F, -0.5F);
		seat1.setPositionAndRotation(posX, posY, posZ, rotationYaw, rotationPitch);
		
		seats = new EntityVehicleSeatSH[]{seat, seat1};
		
		for (int i = 0; i < seats.length; i++){
			worldObj.spawnEntityInWorld(seats[i]);
		}
	}

	@Override
	public void addSeat(EntityVehicleSeatSH seat, int position) {
		if (seats == null){
			seats = new EntityVehicleSeatSH[2];
		}
		
		seats[position] = seat;
	}
	
	@Override
	public boolean canHoldPets(){
		return false;
	}

	@Override
	public void updateRiderPosition(){
		float f1 = (rotationYaw * (float)Math.PI) / 180F;
        float f2 = MathHelper.sin(f1);
        float f3 = MathHelper.cos(f1);
        
        riddenByEntity.setPosition(posX - (f2 * 1.5F), posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), posZ + (f3 * 1.5F));
	}

	@Override
	public double getMountedYOffset(){
		return (double)this.height * 0.9D;
	}

	@Override
	public float getVehicleWeight(){
		return 2.0F;
	}

	@Override
	public float getSpeedBonus(){
		return 2.0F;
	}
	
	@Override
	public boolean draughtHalted(){
		return bedDeployed;
	}

	@Override
	public Item getVehicleItem() {
		return SimplyHorses.vardo;
	}

}
