package ch.swisssmp.stalker.listeners;

import org.bukkit.Bukkit;

import ch.swisssmp.stalker.Stalker;
import ch.swisssmp.stalker.listeners.block.BlockEventListener;
import ch.swisssmp.stalker.listeners.enchantment.EnchantmentEventListener;
import ch.swisssmp.stalker.listeners.entity.EntityEventListener;
import ch.swisssmp.stalker.listeners.hanging.HangingEventListener;
import ch.swisssmp.stalker.listeners.inventory.InventoryEventListener;
import ch.swisssmp.stalker.listeners.player.PlayerEventListener;
import ch.swisssmp.stalker.listeners.server.ServerEventListener;
import ch.swisssmp.stalker.listeners.vehicle.VehicleEventListener;
import ch.swisssmp.stalker.listeners.weather.WeatherEventListener;
import ch.swisssmp.stalker.listeners.world.WorldEventListener;

public class EventListeners {
	public static void registerAll(){
		Bukkit.getPluginManager().registerEvents(new BlockEventListener(), Stalker.getInstance());
		Bukkit.getPluginManager().registerEvents(new EnchantmentEventListener(), Stalker.getInstance());
		Bukkit.getPluginManager().registerEvents(new EntityEventListener(), Stalker.getInstance());
		Bukkit.getPluginManager().registerEvents(new HangingEventListener(), Stalker.getInstance());
		Bukkit.getPluginManager().registerEvents(new InventoryEventListener(), Stalker.getInstance());
		Bukkit.getPluginManager().registerEvents(new PlayerEventListener(), Stalker.getInstance());
		Bukkit.getPluginManager().registerEvents(new ServerEventListener(), Stalker.getInstance());
		Bukkit.getPluginManager().registerEvents(new VehicleEventListener(), Stalker.getInstance());
		Bukkit.getPluginManager().registerEvents(new WeatherEventListener(), Stalker.getInstance());
		Bukkit.getPluginManager().registerEvents(new WorldEventListener(), Stalker.getInstance());
	}
}
