package ch.swisssmp.taxcollector;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.WebCore;

@SuppressWarnings("unused")
public class TaxCollector extends JavaPlugin{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static TaxCollector plugin;
	protected static boolean debug;
	protected static HashMap<Integer,TaxChest> taxChests = new HashMap<Integer, TaxChest>();
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		this.getCommand("tax").setExecutor(new ConsoleCommand());
		this.getCommand("abgaben").setExecutor(new PlayerCommand());
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		reloadChests();
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	protected static void collect(){
		for(Integer city_id : taxChests.keySet()){
			collect(city_id);
		}
	}
	
	protected static void collect(int city_id){
		if(city_id<1){
			return;
		}
		TaxInventory.closeAll();
		DataSource.getResponse("taxes/collect_tribute.php", new String[]{
				"city="+city_id
		});
	}
	
	//player_uuid can also be the player name
	protected static void inform_player(String[] arguments, CommandSender sender){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("taxes/info.php", arguments);
		if(yamlConfiguration.contains("message")){
			for(String line : yamlConfiguration.getStringList("message")){
				sender.sendMessage(line);
			}
		}
	}
	
	protected static void reloadChests(){
		taxChests.clear();
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("taxes/chests.php");
		if(!yamlConfiguration.contains("chests"))return;
		ConfigurationSection chestsSection = yamlConfiguration.getConfigurationSection("chests");
		for(String key : chestsSection.getKeys(false)){
			ConfigurationSection taxChestSection = chestsSection.getConfigurationSection(key);
			Integer city_id = taxChestSection.getInt("city_id");
			String world = taxChestSection.getString("world");
			int x = taxChestSection.getInt("x");
			int y = taxChestSection.getInt("y");
			int z = taxChestSection.getInt("z");
			TaxChest taxChest = new TaxChest(city_id, world, x, y, z);
			Chest chest = taxChest.getChest();
			if(chest==null) continue;
			chest.setCustomName("§dAkroma Kiste");
			
			BlockFace[] neighbourFaces = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
			Block neighbourBlock;
			for(BlockFace neighbourFace : neighbourFaces){
				neighbourBlock = chest.getBlock().getRelative(neighbourFace);
				if(neighbourBlock==null || neighbourBlock.getType()!=chest.getBlock().getType()) continue;
				Chest neighbourChest = (Chest) neighbourBlock.getState();
				neighbourChest.setCustomName("§dAkroma Kiste");
			}
			taxChests.put(city_id, taxChest);
		}
		WebCore.info("[TaxCollector] Successfully loaded tax chests");
	}
}
