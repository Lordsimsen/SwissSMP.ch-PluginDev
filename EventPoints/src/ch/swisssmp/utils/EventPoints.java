package ch.swisssmp.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;

public class EventPoints extends JavaPlugin{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static EventPoints plugin;
	protected static boolean debug;
	
	private static String signature = "§6Schoggi Taler";
	
	private static CustomItemBuilder eventPointBuilder;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		this.getCommand("eventpoints").setExecutor(new EventPointCommand());
		loadEventPointBuilder();
	}
	
	private static void loadEventPointBuilder(){
		CustomItemBuilder eventPointBuilder = CustomItems.getCustomItemBuilder("EVENT_POINT");
		if(eventPointBuilder==null){
			logger.info("[EventPoints] Eventpunkt-Item konnte nicht geladen werden.");
			return;
		}
		List<String> lore = new ArrayList<String>();
		lore.add("§7Schoggi Taler erhälst");
		lore.add("§7du an SwissSMP.ch");
		lore.add("§7Minecraft-Events.");
		eventPointBuilder.setLore(lore);
		eventPointBuilder.setDisplayName(signature);
		EventPoints.eventPointBuilder = eventPointBuilder;
	}
	
	public static ItemStack getItem(int amount){
		eventPointBuilder.setAmount(amount);
		return eventPointBuilder.build();
	}
	
	public static String getSignature(){
		return signature;
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}
