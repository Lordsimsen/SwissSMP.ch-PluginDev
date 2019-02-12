package ch.swisssmp.transformations;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class AreaTransformations extends JavaPlugin implements Listener{
	private static PluginDescriptionFile pdfFile;
	private static AreaTransformations plugin;
	public static WorldEditPlugin worldEditPlugin;

	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		
		this.getCommand("transformation").setExecutor(new PlayerCommand());
		Bukkit.getPluginManager().registerEvents(this, this);
		
		Plugin worldEdit = Bukkit.getPluginManager().getPlugin("WorldEdit");
		if(worldEdit instanceof WorldEditPlugin){
			worldEditPlugin = (WorldEditPlugin) worldEdit;
		}
		else{
			new NullPointerException("WorldEdit missing");
		}
		
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		
		for(World world : Bukkit.getWorlds()){
			TransformationWorld transformationWorld = TransformationWorld.loadWorld(world);
			transformationWorld.loadTransformations();
		}
		
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll((JavaPlugin)this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
    public static void info(String info){
    	if(debug){
    		Bukkit.getLogger().info(info);
    	}
    }
    
    public static AreaTransformations getInstance(){
    	return plugin;
    }
}
