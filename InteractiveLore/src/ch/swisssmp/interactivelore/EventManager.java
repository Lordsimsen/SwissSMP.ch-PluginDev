package ch.swisssmp.interactivelore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;


public class EventManager implements Listener{
	public final JavaPlugin plugin;
	public EventManager(JavaPlugin plugin){
		this.plugin = plugin;
	}

	//@EventHandler(priority=EventPriority.LOWEST)
	public void onCreatureSpawn(CreatureSpawnEvent event){
		SpawnReason spawnReason = event.getSpawnReason();
		if(spawnReason != SpawnReason.SPAWNER_EGG || event.getEntity().getType()!=EntityType.VILLAGER)
			return;
		event.getEntity().remove();
		World world = event.getEntity().getWorld();
		Character character = new Character(world);
		
		Main.logger.info(character.getCustomName());
		EntityTypes.spawnEntity(character, event.getLocation());
	}
	
}
