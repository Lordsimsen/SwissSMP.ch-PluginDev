package ch.swisssmp.craftmmo.mmoevent;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.craftmmo.mmoitem.MmoItem;
import ch.swisssmp.craftmmo.mmoitem.MmoItemFood;
import ch.swisssmp.craftmmo.mmoplayer.MmoPlayer;
import ch.swisssmp.craftmmo.mmoplayer.MmoQuestbook;

public class MmoEventListenerItem extends MmoEventListener{
	public MmoEventListenerItem(JavaPlugin plugin) {
		super(plugin);
	}
	@EventHandler(ignoreCancelled=true)
	private void onItemHeld(PlayerItemHeldEvent event){
		Player player = event.getPlayer();
		ItemStack itemStack = player.getInventory().getItem(event.getNewSlot());
		MmoPlayer.neutralizeAttackSpeed(player, itemStack);
	}
	@EventHandler 
	private void onItemDrop(PlayerDropItemEvent event){
		if(event.isCancelled()){
			return;
		}
		Item item = event.getItemDrop();
		ItemStack itemStack = item.getItemStack();
		MmoItem mmoItem = MmoItem.get(itemStack);
		if(mmoItem==null){
			MmoQuestbook mmoQuestbook = MmoQuestbook.get(event.getPlayer(), itemStack);
			if(mmoQuestbook!=null){
				mmoQuestbook.hide();
				item.remove();
			}
		}
	}
	@EventHandler(ignoreCancelled=true)
	private void onItemConsume(PlayerItemConsumeEvent event){
		ItemStack itemStack = event.getItem();
		MmoItem mmoItem = MmoItem.get(itemStack);
		if(mmoItem==null) return;
		if(mmoItem instanceof MmoItemFood){
			Player player = event.getPlayer();
			MmoItemFood food = (MmoItemFood) mmoItem;
			int hunger = food.nutrition;
			float saturation = food.saturation;
			hunger -= MmoItemFood.getVanillaHunger(itemStack);
			saturation -= MmoItemFood.getVanillaSaturation(itemStack);
			player.setFoodLevel(player.getFoodLevel()+hunger);
			player.setSaturation(player.getSaturation()+saturation);
			
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onItemCraft(PrepareItemCraftEvent event){
		CraftingInventory inventory = event.getInventory();
		ItemStack result = inventory.getResult();
		MmoItem resultItem = MmoItem.get(result);
		//this function is here to block item repairing with mmo items
		if(resultItem!=null){
			//and if it reaches this section the result is an mmo item and therefore not the result of reparation
			return;
		}
		ItemStack[] itemStacks = inventory.getContents();
		for(ItemStack ingredient : itemStacks){
			MmoItem ingredientItem = MmoItem.get(ingredient);
			if(ingredientItem!=null){
				//MmoItems cannot be an ingredient for a vanilla Item
				inventory.setResult(null);
			}
		}
	}
}
