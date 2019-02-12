package ch.swisssmp.stairchairs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class StairChairs extends JavaPlugin{
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static StairChairs plugin;

	protected static HashMap<Player,Location> locationMap = new HashMap<Player,Location>();
	protected static HashMap<Block,Player> playerMap = new HashMap<Block,Player>();
	protected static HashMap<Entity,Block> entityMap = new HashMap<Entity,Block>();
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	protected static void removeUnusedArmorStands(List<Entity> entities){
		List<ArmorStand> toRemove = new ArrayList<ArmorStand>();
		ArmorStand armorStand;
		for(Entity entity : entities){
			if(!(entity instanceof ArmorStand)){
				continue;
			}
			armorStand = (ArmorStand) entity;
			if(armorStand.getCustomName()==null){
				continue;
			}
			if(!armorStand.getCustomName().equals("§cStairChair")){
				return;
			}
			if(armorStand.getPassengers().size()==0)
			{
				toRemove.add(armorStand);
			}
		}
		for(ArmorStand armorStandToRemove : toRemove){
			armorStandToRemove.remove();
		}
	}

	@Override
	public void onDisable() {
		for(Entity entity : entityMap.keySet().toArray(new Entity[entityMap.size()])){
			entity.eject();
			entity.remove();
		}
		playerMap.clear();
		entityMap.clear();
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}
