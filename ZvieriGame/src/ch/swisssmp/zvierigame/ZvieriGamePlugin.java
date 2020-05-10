package ch.swisssmp.zvierigame;


import ch.swisssmp.zvierigame.game.CraftingRecipes;
import ch.swisssmp.zvierigame.game.StopGameCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ZvieriGamePlugin extends JavaPlugin{
	
	private static ZvieriGamePlugin plugin;
	private static Listener eventListener;
	
	@Override
	public void onEnable() {
		plugin = this;	

		EventListener eventListener = new EventListener();
		setEventListener(eventListener);
		Bukkit.getPluginManager().registerEvents(eventListener, plugin);
		
		Bukkit.getPluginCommand("zvieriarena").setExecutor(new ZvieriArenaCommand());
		Bukkit.getPluginCommand("zvieriarenen").setExecutor(new ZvieriArenenCommand());
		Bukkit.getPluginCommand("zvierigame").setExecutor(new StopGameCommand());

		CraftingRecipes.registerCraftingRecipes();
		CraftingRecipes.registerFurnaceRecipes();
		CraftingRecipes.registerBrewingRecipes();
		
		for (World world : Bukkit.getWorlds()) {
			ZvieriArenen.load(world);
		}
		
	}
	
	@Override
	public void onDisable() {

		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);		
	}
	
	public static String getPrefix() {
		return "[" + ChatColor.GOLD + "ZvieriGame" + ChatColor.RESET + "]";
	}
	
	public static ZvieriGamePlugin getInstance() {
		return plugin;
	}

	public static Listener getEventListener(){
		return eventListener;
	}

	public static void setEventListener(Listener eventListener){
		ZvieriGamePlugin.eventListener = eventListener;
	}

}
