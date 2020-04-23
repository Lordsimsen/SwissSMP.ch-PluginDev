package ch.swisssmp.knightstournament;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;

public class KnightsTournamentPlugin extends JavaPlugin{
	protected static KnightsTournamentPlugin plugin;
	
	protected static CustomItemBuilder tournamentLanceBuilder = null;
	protected static Recipe tournamentLanceRecipe;
	
	protected static String prefix = "[§4Ritterspiele§r]";
	
	@Override
	public void onEnable() {
		plugin = this;
		
		KnightsTournamentCommand knightsTournamentCommand = new KnightsTournamentCommand();
		this.getCommand("knightstournament").setExecutor(knightsTournamentCommand);
		
		this.getCommand("knightsarena").setExecutor(new KnightsArenaCommand());
		
		for(World world : Bukkit.getWorlds()) {
			KnightsArena.load(world);
		}
		
		KnightsTournamentPlugin.loadTournamentLance();
		
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		
		Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
		
	}
	
	private static void loadTournamentLance(){
		CustomItemBuilder lanceBuilder = CustomItems.getCustomItemBuilder("TOURNAMENT_LANCE");
		if(lanceBuilder==null){
			Bukkit.getLogger().info("[KnightsTournament] Turnierlanze konnte nicht geladen werden.");
			return;
		}
		lanceBuilder.setAmount(1);
		lanceBuilder.setAttackDamage(1);
		lanceBuilder.setAttackSpeed(0.1f);
		tournamentLanceBuilder = lanceBuilder;
		ItemStack lance = lanceBuilder.build();
		ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(KnightsTournamentPlugin.plugin, "tournament_lance"), lance);
		recipe.shape(
				"  i",
				"wi ",
				"bw "
				);
		recipe.setIngredient('i', Material.IRON_INGOT);
		recipe.setIngredient('w', Material.RED_WOOL);
		recipe.setIngredient('b', Material.IRON_BLOCK);
		tournamentLanceRecipe = recipe;
		Bukkit.getServer().addRecipe(recipe);
	}
	

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static KnightsTournamentPlugin getInstance(){
		return plugin;
	}
}
