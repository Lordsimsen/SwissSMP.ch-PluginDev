package ch.swisssmp.zvieriplausch;


import ch.swisssmp.crafting.CustomRecipeAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class ZvieriGamePlugin extends JavaPlugin{
	
	private static ZvieriGamePlugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		getConfig().options().copyDefaults();
		saveDefaultConfig();

		EventListener eventListener = new EventListener();
		Bukkit.getPluginManager().registerEvents(eventListener, plugin);
		
		Bukkit.getPluginCommand("zvieriarena").setExecutor(new ZvieriArenaCommand());
		Bukkit.getPluginCommand("zvieriarenen").setExecutor(new ZvieriArenenCommand());
		Bukkit.getPluginCommand("zvierigame").setExecutor(new ZvieriGameCommand());
		Bukkit.getPluginCommand("zvierirecipedisplay").setExecutor(new RecipeDisplayCommand());

		RecipePaintings.download();

		CraftingRecipes.registerCraftingRecipes();
		CraftingRecipes.registerFurnaceRecipes();
		CraftingRecipes.registerBrewingRecipes();
		
		for (World world : Bukkit.getWorlds()) {
			ZvieriArenen.load(world);
		}
		Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
	}
	
	@Override
	public void onDisable() {
		CustomRecipeAPI.removeRecipes(this);
		HandlerList.unregisterAll(this);
		for(ZvieriArena arena : ZvieriArenen.getAll()){
			if(arena.getGame() != null){
				arena.getGame().cancel();
			}
		}
		Bukkit.getScheduler().cancelTasks(this);
		Bukkit.getLogger().info(getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
	}
	
	public static String getPrefix() {
		return "[" + ChatColor.GOLD + "ZvieriPlausch" + ChatColor.RESET + "]";
	}

	public static ZvieriGamePlugin getInstance() {
		return plugin;
	}

}
