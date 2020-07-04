package ch.swisssmp.transformations;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class AreaTransformations implements Listener{

	protected static boolean debug = false;
	
    public static void info(String info){
    	if(debug){
    		Bukkit.getLogger().info(info);
    	}
    }
}
