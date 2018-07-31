package ch.swisssmp.adventuredungeons;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import ch.swisssmp.adventuredungeons.command.AdventureDungeonsCommand;
import ch.swisssmp.adventuredungeons.command.DungeonCommand;
import ch.swisssmp.adventuredungeons.command.PlayerCommand;
import ch.swisssmp.adventuredungeons.world.Dungeon;
import ch.swisssmp.adventuredungeons.world.DungeonInstance;

public class AdventureDungeons extends JavaPlugin{
	private static Logger logger;
	public static PluginDescriptionFile pdfFile;
	public static AdventureDungeons plugin;
	public static boolean debug = false;
	
	public static WorldGuardPlugin worldGuardPlugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		
		AdventureDungeonsCommand mmoCommand = new AdventureDungeonsCommand();
		PlayerCommand mmoPlayerCommand = new PlayerCommand();
		DungeonCommand dungeonCommand = new DungeonCommand();
		this.getCommand("AdventureDungeons").setExecutor(mmoCommand);
		this.getCommand("rename").setExecutor(mmoCommand);
		this.getCommand("join").setExecutor(mmoPlayerCommand);
		this.getCommand("leave").setExecutor(mmoPlayerCommand);
		this.getCommand("refuse").setExecutor(mmoPlayerCommand);
		this.getCommand("choose").setExecutor(mmoPlayerCommand);
		this.getCommand("invite").setExecutor(mmoPlayerCommand);
		this.getCommand("dungeon").setExecutor(dungeonCommand);
		this.getCommand("ready").setExecutor(dungeonCommand);
		
		Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if(worldGuard instanceof WorldGuardPlugin){
			worldGuardPlugin = (WorldGuardPlugin) worldGuard;
		}
		else{
			throw new NullPointerException("[AdventureDungeons] WorldGuard missing");
		}

		Dungeon.loadDungeons();
		
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		for(DungeonInstance dungeonInstance : Dungeon.instances.values()){
			dungeonInstance.delete(false);
		}
		for(Dungeon dungeon : Dungeon.dungeons.values()){
			dungeon.saveTemplate();
		}
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
    public static void info(String info){
    	if(debug){
        	logger.info(info);
    	}
    }
    public static void debug(String info){
    	logger.info(info);
    }
}
