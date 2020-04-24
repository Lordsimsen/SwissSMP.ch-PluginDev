package ch.swisssmp.auctionhouse;

import java.io.File;
import java.util.Random;
import java.util.logging.Logger;

import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class AuctionHouse extends JavaPlugin{
	protected static Logger logger;
	protected static File configFile;
	protected static YamlConfiguration config;
	protected static PluginDescriptionFile pdfFile;

	protected static File dataFolder;
	protected static AuctionHouse plugin;
	protected static boolean debug;
	
	protected EventListener eventListener;
	
	private static Random random = new Random();
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		this.getCommand("auction").setExecutor(new PlayerCommand());
		
		eventListener = new EventListener();
		Bukkit.getPluginManager().registerEvents(eventListener, this);
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}

	public static AuctionHouse getInstance(){
		return plugin;
	}

	public static void info(Player player, String addon_name){
		HTTPRequest request = DataSource.getResponse(AuctionHouse.getInstance(), "auction/info.php", new String[]{
				"player="+player.getUniqueId(),
				"addon="+URLEncoder.encode(addon_name),
		});
		request.onFinish(()->{
			YamlConfiguration yamlConfiguration = request.getYamlResponse();
			if(yamlConfiguration==null) return;
			if(yamlConfiguration.contains("message")){
				for(String line : yamlConfiguration.getStringList("message")){
					player.sendMessage(line);
				}
			}
			if(yamlConfiguration.contains("actionbar")){
				SwissSMPler swisssmpler = SwissSMPler.get(player);
				swisssmpler.sendActionBar(yamlConfiguration.getString("actionbar"));
			}
		});
	}
	
	public static void bid(Player player, String addon_name, ItemStack itemStack){
		if(player==null) return;
		if(addon_name==null) return;
		if(itemStack==null) return;
		if(itemStack.getAmount()<=0) return;
		HTTPRequest request = DataSource.getResponse(AuctionHouse.getInstance(), "auction/bid.php", new String[]{
				"player="+player.getUniqueId(),
				"addon="+URLEncoder.encode(addon_name),
				"amount="+itemStack.getAmount()
		});
		request.onFinish(()->{
			YamlConfiguration response = request.getYamlResponse();
			if(response==null) return;
			if(response.contains("transferred")){
				itemStack.setAmount(itemStack.getAmount()-response.getInt("transferred"));
			}
			if(response.contains("message")){
				player.sendMessage(response.getString("message"));
			}
			if(response.contains("sound")){
				Sound sound = Sound.valueOf(response.getString("sound"));
				if(sound!=null){
					player.getWorld().playSound(player.getLocation(), sound, 5f, 0.8f+random.nextFloat()*0.4f);
				}
			}
			if(response.contains("actionbar")){
				SwissSMPler swisssmpler = SwissSMPler.get(player);
				swisssmpler.sendActionBar(response.getString("actionbar"));
			}
		});
	}
}
