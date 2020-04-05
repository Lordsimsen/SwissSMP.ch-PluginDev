package ch.swisssmp.utils;

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
import ch.swisssmp.webcore.HTTPRequest;
import ch.swisssmp.webcore.RequestMethod;

public class EventPoints extends JavaPlugin{
	private static PluginDescriptionFile pdfFile;
	private static EventPoints plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		this.getCommand("eventpoints").setExecutor(new EventPointCommand());
		this.getCommand("balance").setExecutor(new BalanceCommand());
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
	}
	
	public static CurrencyInfo getInfo(String currencyType){
		return CurrencyInfo.get(currencyType);
	}
	
	public static CurrencyInfo getInfo(ItemStack itemStack){
		return CurrencyInfo.get(itemStack);
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
		HTTPRequest request = DataSource.getResponse(plugin, "change_wallet.php", new String[]{
				"sender="+URLEncoder.encode(sender instanceof Player ? ((Player)sender).getName() : "Server"),
				"player="+URLEncoder.encode(playerName),
				"amount="+(amount),
				"currency="+URLEncoder.encode(currencyType),
				"reason="+URLEncoder.encode(reason)
		});
		request.onFinish(()->{
			sender.sendMessage(request.getResponse());
		});
	}
	
	public static void take(CommandSender sender, String playerName, int amount, String currencyType, String reason){
		HTTPRequest request = DataSource.getResponse(plugin, "change_wallet.php", new String[]{
				"sender="+URLEncoder.encode(sender instanceof Player ? ((Player)sender).getName() : "Server"),
				"player="+URLEncoder.encode(playerName),
				"amount="+(-amount),
				"currency="+URLEncoder.encode(currencyType),
				"reason="+URLEncoder.encode(reason)
		});
		request.onFinish(()->{
			sender.sendMessage(request.getResponse());
		});
	}

	public static int getBalance(String player_identifier, String currency){
		HTTPRequest request = DataSource.getResponse(EventPoints.getInstance(), "balance.php", new String[]{
			"player="+URLEncoder.encode(player_identifier),
			"currency="+URLEncoder.encode(currency)
		}, RequestMethod.POST_SYNC);
		YamlConfiguration yamlConfiguration = request.getYamlResponse();
		if(yamlConfiguration==null || !yamlConfiguration.contains("balance")) return 0;
		return yamlConfiguration.getInt("balance");
	}
	
	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static EventPoints getInstance(){
		return plugin;
	}
	
	public static String getPrefix(){
		return "[Eventpunkte] ";
	}
}
