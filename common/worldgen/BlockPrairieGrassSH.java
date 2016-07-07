package simplyhorses.common.worldgen;

import java.util.ArrayList;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import simplyhorses.common.SimplyHorses;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPrairieGrassSH extends BlockCrops {

	public Icon[] metaIcons;
	
	public BlockPrairieGrassSH(int par1) {
		super(par1);
        setBlockBounds(0.0F, 0.0F, 0.0F, 0.9F, 0.9F, 0.9F);
	}
        
	@Override
    public int getRenderType() {
        return 1;
    }
    
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    public Icon getIcon (int side, int metadata) {
        if (metadata < 9){
        	return metaIcons[metadata];
        } else {
        	return metaIcons[metadata - 9];
        }
    }
    
    @Override
    public void registerIcons(IconRegister par1IconRegister)
    {
    	metaIcons = new Icon[9];
    	
    	for (int i = 0; i < metaIcons.length; i++){
    		metaIcons[i] = par1IconRegister.registerIcon("SimplyHorses:prairiegrass_" + i);
    	}
    }
    
    @Override
    protected boolean canThisPlantGrowOnThisBlockID(int par1)
    {
    	boolean flag = par1 == Block.dirt.blockID || par1 == Block.grass.blockID || par1 == SimplyHorses.prairieGrass.blockID;
        if (!flag){
        }
    	return flag;
    }
    
    @Override
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        int meta = par1World.getBlockMetadata(par2, par3, par4);
        
        if (meta == 8 || meta == 15){
        	return;
        }
        
        int growthRate = par1World.isRaining() ? 5: 10;
        
        if (par1World.getBlockLightValue(par2, par3 + 1, par4) >= 9 && par5Random.nextInt(growthRate) == 0)
        {
        	par1World.setBlockMetadataWithNotify(par2, par3, par4, ++meta, 2);
        	if (meta == 8){
        		par1World.setBlock(par2, par3 + 1, par4, SimplyHorses.prairieGrass.blockID, 9, 2);
        	}
        }
    }
    
    @Override
    public void onNeighborBlockChange (World world, int x, int y, int z, int neighborId) {
        if (!canBlockStay(world, x, y, z)) {
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.destroyBlock(x, y, z, false);
            return;
        }
        
        if (world.getBlockMetadata(x, y, z) == 8 && world.getBlockId(x, y + 1, z) != SimplyHorses.prairieGrass.blockID){
        	world.setBlockMetadataWithNotify(x, y, z, 7, 2);
        	return;
        }
    }
    
    @Override
    public boolean canBlockStay(World world, int x, int y, int z){
    	if (world.getBlockId(x, y - 1, z) == SimplyHorses.prairieGrass.blockID && world.getBlockMetadata(x, y - 1, z) != 8){
    		return false;
    	}
    	
    	return canThisPlantGrowOnThisBlockID(world.getBlockId(x, y - 1, z));
    }
    
    @Override
    public int idDropped (int metadata, Random random, int par2) {
    	if (metadata == 15) {
    		return SimplyHorses.prairieSeeds.itemID;
    	} else if (metadata >= 9) {
    		metadata -= 9;
    	}
    	
    	if (metadata > 4){
    		//return hay;
    	}
    	
    	return -1;
    }
    
    @Override 
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
    {
    	ArrayList<ItemStack> ret = super.getBlockDropped(world, x, y, z, metadata, fortune);
    	
    	ret.clear();
    	
    	if (metadata == 15){
    		for (int i = 0; i < 1 + world.rand.nextInt(3); i++){
    			ret.add(new ItemStack(SimplyHorses.prairieSeeds, 1, 0));
    		}
    	}
    	
    	if (metadata >= 9){
    		metadata -= 9;
    	}
    	
    	if (metadata > 4)
        {
            for (int n = 0; n < 3 + fortune; n++)
            {
                if (world.rand.nextInt(15) <= metadata)
                {
                    //ret.add(new ItemStack(hay, 1, 0));
                }
            }
        }

        return ret;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
    	this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
    	
        setBlockBounds(0.0F, 0.0F, 0.0F, 0.9F, 0.9F, 0.9F);
    	
        int metadata = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
        
        if (metadata >= 9){
            setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getBlockColor()
    {
        double var1 = 0.5D;
        double var3 = 1.0D;
        return ColorizerGrass.getGrassColor(var1, var3);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderColor(int par1)
    {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
        return par1IBlockAccess.getBiomeGenForCoords(par2, par4).getBiomeGrassColor();
    }
}
