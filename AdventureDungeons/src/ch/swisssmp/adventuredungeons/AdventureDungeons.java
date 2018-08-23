package ch.swisssmp.adventuredungeons;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.adventuredungeons.command.DungeonCommand;
import ch.swisssmp.adventuredungeons.command.PlayerCommand;
import ch.swisssmp.adventuredungeons.world.Dungeon;
import ch.swisssmp.adventuredungeons.world.DungeonInstance;

public class AdventureDungeons extends JavaPlugin{
	private static Logger logger;
	private static PluginDescriptionFile pdfFile;
	private static AdventureDungeons plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		
		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("join").setExecutor(playerCommand);
		this.getCommand("leave").setExecutor(playerCommand);
		this.getCommand("refuse").setExecutor(playerCommand);
		this.getCommand("invite").setExecutor(playerCommand);
		DungeonCommand dungeonCommand = new DungeonCommand();
		this.getCommand("dungeon").setExecutor(dungeonCommand);
		this.getCommand("ready").setExecutor(dungeonCommand);

		Dungeon.loadDungeons();
		
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		for(DungeonInstance dungeonInstance : Dungeon.instances.values()){
			dungeonInstance.delete(false);
		}
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}

    public static AdventureDungeons getInstance(){
    	return plugin;
    }
}
