package ch.swisssmp.mobcamps;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class MobCamps extends JavaPlugin{
	protected static PluginDescriptionFile pdfFile;
	protected static MobCamps plugin;
	
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		Bukkit.getPluginCommand("camp").setExecutor(new CampCommand());
		Bukkit.getPluginCommand("camps").setExecutor(new CampsCommand());
		MobCampInstance.loadAll();
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static EntityType getEntityType(Material material){
		String typeName = material.toString().toUpperCase();
		if(!typeName.contains("_SPAWN_EGG")) return null;
		return EntityType.valueOf(typeName.replace("_SPAWN_EGG", ""));
	}
	
	protected static EntityType getFirstEggType(Inventory inventory){
		for(int i = 0; i < inventory.getSize(); i++){
			ItemStack itemStack = inventory.getItem(i);
			if(itemStack==null) continue;
			String typeName = itemStack.getType().toString().toUpperCase();
			if(!typeName.contains("_SPAWN_EGG")) continue;
			String entityTypeName = typeName.replace("_SPAWN_EGG", "");
			return EntityType.valueOf(entityTypeName);
		}
		
		return null;
	}
	
	public static MobCamps getInstance(){
		return plugin;
	}
}
