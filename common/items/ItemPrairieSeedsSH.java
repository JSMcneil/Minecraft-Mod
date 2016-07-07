package simplyhorses.common.items;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

public class ItemPrairieSeedsSH extends ItemSeeds {

	public ItemPrairieSeedsSH(int par1, int par2, int par3) {
		super(par1, par2, par3);

		setCreativeTab(CreativeTabs.tabMisc);
	}
	
	@Override
    public EnumPlantType getPlantType(World world, int x, int y, int z)
    {
        return EnumPlantType.Plains;
    }
	
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
	         itemIcon = iconRegister.registerIcon("SimplyHorses:prairieseeds");
	}

}
