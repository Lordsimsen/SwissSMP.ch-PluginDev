package ch.swisssmp.spleef;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class Spleef extends JavaPlugin{
	protected static Spleef plugin;
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static boolean debug;
	
	public static WorldEditPlugin worldEditPlugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("spleef").setExecutor(playerCommand);
		
		Plugin worldEdit = Bukkit.getPluginManager().getPlugin("WorldEdit");
		if(worldEdit instanceof WorldEditPlugin){
			worldEditPlugin = (WorldEditPlugin) worldEdit;
		}
		else{
			new NullPointerException("WorldEdit missing");
		}
                
        Arena.loadArenas();
	}

	@Override
	public void onDisable() {
		for(Arena arena : Arena.arenas.values())
		{
			arena.resetGame();
		}
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
