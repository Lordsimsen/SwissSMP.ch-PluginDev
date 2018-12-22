package ch.swisssmp.utils;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.webcore.DataSource;

public class EventPoints extends JavaPlugin{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static EventPoints plugin;
	protected static boolean debug;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		this.getCommand("eventpoints").setExecutor(new EventPointCommand());
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
	}
	
	public static CurrencyInfo getInfo(String currencyType){
		return CurrencyInfo.get(currencyType);
	}
	
	public static ItemStack getItem(int amount){
		return EventPoints.getItem(amount, "EVENT_POINT");
	}
	
	public static ItemStack getItem(int amount, String currencyType){
		CustomItemBuilder eventPointBuilder = CustomItems.getCustomItemBuilder(currencyType);
		if(eventPointBuilder==null) return new ItemStack(Material.AIR);
		eventPointBuilder.setAmount(amount);
		return eventPointBuilder.build();
	}
	
	public static void give(CommandSender sender, String playerName, int amount, String currencyType, String reason){
		sender.sendMessage(DataSource.getResponse("players/change_wallet.php", new String[]{
				"sender="+URLEncoder.encode(sender instanceof Player ? ((Player)sender).getName() : "Server"),
				"player="+URLEncoder.encode(playerName),
				"amount="+(amount),
				"currency="+URLEncoder.encode(currencyType),
				"reason="+URLEncoder.encode(reason)
		}));
	}
	
	public static void take(CommandSender sender, String playerName, int amount, String currencyType, String reason){
		sender.sendMessage(DataSource.getResponse("players/change_wallet.php", new String[]{
				"sender="+URLEncoder.encode(sender instanceof Player ? ((Player)sender).getName() : "Server"),
				"player="+URLEncoder.encode(playerName),
				"amount="+(-amount),
				"currency="+URLEncoder.encode(currencyType),
				"reason="+URLEncoder.encode(reason)
		}));
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}
