package ch.swisssmp.knightstournament;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.nbt.NBTTagCompound;

public class KnightsTournamentPlugin extends JavaPlugin{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static KnightsTournamentPlugin plugin;
	
	protected static CustomItemBuilder tournamentLanceBuilder = null;
	protected static Recipe tournamentLanceRecipe;
	
	protected static String prefix = "[§4Ritterspiele§r]";
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		
		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("knightstournament").setExecutor(playerCommand);
		
		KnightsTournamentPlugin.loadTournamentLance();
		
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
	}
	
	private static void loadTournamentLance(){
		CustomItemBuilder lanceBuilder = CustomItems.getCustomItemBuilder("TOURNAMENT_LANCE");
		if(lanceBuilder==null){
			logger.info("[KnightsTournament] Turnierlanze konnte nicht geladen werden.");
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
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static KnightsTournamentPlugin getInstance(){
		return plugin;
	}
}
