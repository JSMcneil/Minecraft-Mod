package simplyhorses.common;

import simplyhorses.common.entities.ILassoableSH;
import simplyhorses.common.entities.horses.EntityHorseRideableSH;
import simplyhorses.common.items.ItemLassoSH;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

public class EventHooksSH {

	@ForgeSubscribe
	public void entityInteracted(EntityInteractEvent ev){
		EntityPlayer player = ev.entityPlayer;
		Entity target = ev.target;
		
		if (player.getCurrentEquippedItem() == null || player.getCurrentEquippedItem().itemID != SimplyHorses.lasso.itemID){
			return;
		}
		
		if(target.interact(player)){
			ev.setCanceled(true);
			return;
		}
		
		ItemStack lasso = player.getCurrentEquippedItem();
		
		((ItemLassoSH) lasso.getItem()).entityInteractionByPlayer(lasso, target, player);
		player.swingItem();
	}
	
	@ForgeSubscribe
	public void arrowLoosed(ArrowLooseEvent ev){
		EntityPlayer player = ev.entityPlayer;
		if (player.ridingEntity == null || !(player.ridingEntity instanceof EntityHorseRideableSH)){
			return;
		}
		
		EntityHorseRideableSH mount = (EntityHorseRideableSH) player.ridingEntity;
		
		if (mount.givesArrowBonus()){
			ev.charge = ev.charge >= 5? 20: ev.charge;
		}
	}
	
	@ForgeSubscribe
	public void arrowNocked(ArrowNockEvent ev){
		EntityPlayer player = ev.entityPlayer;
		if (player.ridingEntity == null || !(player.ridingEntity instanceof EntityHorseRideableSH)){
			return;
		}
		
		EntityHorseRideableSH mount = (EntityHorseRideableSH) player.ridingEntity;
		
		mount.LockHeading(false);
	}
}
