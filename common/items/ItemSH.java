package simplyhorses.common.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemSH extends Item {

	protected String name;
	
	public ItemSH(int par1) {
		super(par1);
		setCreativeTab(CreativeTabs.tabMisc);
	}
	
	public Item setName(String name){
		this.name = name;
		return this;
	}
	
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		itemIcon = iconRegister.registerIcon("SimplyHorses:" + name);
	}
}
