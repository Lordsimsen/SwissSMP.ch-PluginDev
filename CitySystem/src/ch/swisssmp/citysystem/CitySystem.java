package ch.swisssmp.citysystem;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.Wool;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;

public class CitySystem extends JavaPlugin{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static CitySystem plugin;
	
	protected static String prefix = "[§4Städtesystem§r]";
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		
		//PlayerCommand playerCommand = new PlayerCommand();
		//this.getCommand("citysystem").setExecutor(playerCommand);
		
		CitySystem.loadItems();
		
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
	}
	
	private static void loadItems(){
		CustomItemBuilder lanceBuilder = CustomItems.getCustomItemBuilder("TOURNAMENT_LANCE");
		if(lanceBuilder==null){
			logger.info("[KnightsTournament] Turnierlanze konnte nicht geladen werden.");
			return;
		}
		lanceBuilder.setAmount(1);
		lanceBuilder.setAttackDamage(1);
		lanceBuilder.setAttackSpeed(0.1f);
		lanceBuilder.setDisplayName("§bTurnierlanze");
		lanceBuilder.setLocalizedName("§bTournament Lance");
		tournamentLanceBuilder = lanceBuilder;
		ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(KnightsTournament.plugin, "Turnierlanze"), lanceBuilder.build());
		recipe.shape(
				"  i",
				"wi ",
				"bw "
				);
		recipe.setIngredient('i', Material.IRON_INGOT);
		recipe.setIngredient('w', new Wool(DyeColor.RED));
		recipe.setIngredient('b', Material.IRON_BLOCK);
		tournamentLanceRecipe = recipe;
		Bukkit.getServer().addRecipe(recipe);
	}
	

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}
