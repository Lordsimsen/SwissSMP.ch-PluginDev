package ch.swisssmp.city;

import java.util.ArrayList;
import java.util.List;

import ch.swisssmp.city.guides.AddonGuide;
import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;

import ch.swisssmp.npc.NPCInstance;

public class AddonUnlockView implements Listener {

	private final Player player;
	private final Addon addon;
	private final Villager villager;
	
	
	private AddonUnlockView(Player player, Addon addon, Villager villager){
		this.player = player;
		this.addon = addon;
		this.villager = villager;
	}
	
	@EventHandler
	private void onInventoryClose(InventoryCloseEvent event){
		if(event.getView().getTopInventory().getHolder()!=this.villager) return;
		HandlerList.unregisterAll(this);
	}
	
	@EventHandler
	private void onInventoryClick(InventoryClickEvent event){
		if(event.getView().getTopInventory().getHolder()!=this.villager) return;
		if(event.getSlotType()!=SlotType.RESULT) return;
		if(event.getInventory().getItem(event.getSlot())==null) return;
		event.setCancelled(true);
		AddonState oldState = addon.getState();
		AddonStateReason oldReason = addon.getStateReason();
		if(!addon.unlock()){
			return;
		}
		MerchantInventory merchantInventory = (MerchantInventory) event.getInventory();
		for(ItemStack price : merchantInventory.getSelectedRecipe().getIngredients()){
			merchantInventory.removeItem(price);
		}
		HumanEntity human = event.getView().getPlayer();
		World world = villager.getWorld();
		ItemStack a = merchantInventory.getItem(0);
		ItemStack b = merchantInventory.getItem(1);
		if(a!=null) world.dropItem(human.getEyeLocation(), a);
		if(b!=null) world.dropItem(human.getEyeLocation(), b);
		merchantInventory.clear();
		Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(),()->{
			event.getView().close();
		},3L);
		addon.save((success)->{
			if(!success){
				addon.setAddonState(oldState, oldReason);
				for(ItemStack price : merchantInventory.getSelectedRecipe().getIngredients()){
					world.dropItem(player.getEyeLocation(),price);
				}
				SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Etwas ist schiefgelaufen.");
				return;
			}

			City city = addon.getCity();
			if(city!=null){
				city.updateAddonStates();
				city.reloadPermissions();
			}
			addon.announceUnlock(player);
		});
	}
	
	private static List<MerchantRecipe> getTrades(AddonUnlockTrade[] trades, ItemStack result){
		List<MerchantRecipe> recipes = new ArrayList<MerchantRecipe>();
		for(AddonUnlockTrade trade : trades){
			MerchantRecipe recipe = trade.getRecipe(result);
			if(recipe==null) continue;
			recipes.add(recipe);
		}
		return recipes;
	}

	public static AddonUnlockView open(Player player, AddonGuide guide){
		Addon addon = guide.getAddon();
		AddonType type = addon.getType();
		NPCInstance npc = guide.getNPC();
		if(type==null || type.getUnlockTrades().length==0) return null;
		if(!(npc.getEntity() instanceof Villager)) return null;
		Villager villager = (Villager) npc.getEntity();
		if(villager.isTrading()) return null;
		villager.setRecipes(getTrades(type.getUnlockTrades(), type.getItemStack()));
		AddonUnlockView view = new AddonUnlockView(player, addon, villager);
		Bukkit.getPluginManager().registerEvents(view, CitySystemPlugin.getInstance());
		player.openMerchant(villager, true);
		return view;
	}
}
