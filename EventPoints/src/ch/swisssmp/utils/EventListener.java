package ch.swisssmp.utils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import ch.swisssmp.customitems.CustomItems;

public class EventListener implements Listener {
	@EventHandler
	private void onItemPickup(EntityPickupItemEvent event){
		if(event.getItem()==null) return;
		String customEnum = CustomItems.getCustomEnum(event.getItem().getItemStack());
		if(customEnum==null) return;
		CurrencyInfo currencyInfo = CurrencyInfo.get(customEnum);
		if(currencyInfo==null) return;
		if(!(event.getEntity() instanceof Player)) return;
		Player player = (Player)event.getEntity();
		if(player.getGameMode()!=GameMode.SURVIVAL && player.getGameMode()!=GameMode.ADVENTURE) return;
		event.setCancelled(true);
		Location location = event.getItem().getLocation();
		location.getWorld().playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
		String playerName = player.getName();
		int amount = event.getItem().getItemStack().getAmount();
		String currencyType = currencyInfo.getCurrencyType();
		double x = Math.round(location.getX());
		double y = Math.round(location.getY());
		double z = Math.round(location.getZ());
		String reason = "Item gesammelt in "+location.getWorld().getName()+" ("+x+", "+y+", "+z+")";
		event.getItem().remove();
		EventPoints.give(Bukkit.getConsoleSender(), playerName, amount, currencyType, reason);
		Bukkit.dispatchCommand(player, "balance "+currencyType);
	}
}
