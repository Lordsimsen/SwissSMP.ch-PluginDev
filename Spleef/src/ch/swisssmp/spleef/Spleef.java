package ch.swisssmp.spleef;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class Spleef extends JavaPlugin{
	private static Spleef plugin;
	
	public static WorldEditPlugin worldEditPlugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
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
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
    
    public static Spleef getInstance(){
    	return plugin;
    }
}
