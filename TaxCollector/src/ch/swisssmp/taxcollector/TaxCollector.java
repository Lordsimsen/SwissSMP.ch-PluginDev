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
public class TaxCollector extends JavaPlugin implements Listener{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static TaxCollector plugin;
	protected static boolean debug;
	protected static HashMap<Integer,Chest> taxChests = new HashMap<Integer, Chest>();
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		this.getCommand("tax").setExecutor(new ConsoleCommand());
		this.getCommand("abgaben").setExecutor(new PlayerCommand());
		Bukkit.getPluginManager().registerEvents(this, this);
		reloadChests();
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	protected static void collect(){
		for(Integer city_id : taxChests.keySet()){
			collect(city_id);
		}
	}
	
	protected static void collect(int city_id){
		if(city_id==0){
			return;
		}
		if(!taxChests.containsKey(city_id)){
			return;
		}
		Chest chest = taxChests.get(city_id);
		Chest adminChest = taxChests.get(0);
		if(adminChest==null){
			return;
		}
		
		Inventory chestInventory = chest.getInventory();
		Inventory adminInventory = adminChest.getInventory();
		HashMap<Material, Integer> counters = new HashMap<Material, Integer>();
		Material material;
		int amount;
		
		for(ItemStack itemStack : chestInventory){
			if(itemStack==null) continue;
			adminInventory.addItem(itemStack);
			material = itemStack.getType();
			amount = itemStack.getAmount();
			if(material==Material.IRON_BLOCK){
				material = Material.IRON_INGOT;
				amount*=9;
			}
			else if(material==Material.GOLD_BLOCK){
				material = Material.GOLD_INGOT;
				amount*=9;
			}
			else if(material==Material.DIAMOND_BLOCK){
				material = Material.DIAMOND;
				amount*=9;
			}
			if(counters.containsKey(material)){
				amount+=counters.get(material);
				counters.remove(material);
			}
			counters.put(material, amount);
		}
		logger.info("Clearing chest at "+chest.getX()+","+chest.getY()+","+chest.getZ());
		chest.getBlockInventory().clear();
		BlockFace[] neighbourFaces = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
		Block neighbourBlock;
		for(BlockFace neighbourFace : neighbourFaces){
			neighbourBlock = chest.getBlock().getRelative(neighbourFace);
			if(neighbourBlock==null || neighbourBlock.getType()!=chest.getType()) continue;
			Chest neighbourChest = (Chest) neighbourBlock.getState();
			logger.info("Clearing neighbour at "+neighbourChest.getX()+","+neighbourChest.getY()+","+neighbourChest.getZ());
			neighbourChest.getBlockInventory().clear();
			break;
		}
		String[] args = new String[1+counters.size()];
		try {
			args[0] = "city_id="+URLEncoder.encode(String.valueOf(city_id), "utf-8");
			int index = 1;
			for(Entry<Material,Integer> entry : counters.entrySet()){
				args[index] = "items["+entry.getKey().toString()+"]="+entry.getValue();
				index++;
			}
			DataSource.getResponse("taxes/pay.php", args);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onPlayerJoin(PlayerJoinEvent event){
		Bukkit.getScheduler().runTaskLater(this,  new Runnable(){
			public void run(){
				if(event.getPlayer().isOnline()){
					inform_player(new String[]{"player="+event.getPlayer().getUniqueId(), "flags[]=login"}, event.getPlayer());
				}
			}
		}, 600L);
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onBlockBreak(BlockBreakEvent event){
		Block block = event.getBlock();
		if(block.getType()!=Material.CHEST && block.getType()!=Material.TRAPPED_CHEST) return;
		Chest chest = (Chest)block.getState();
		if(chest.getCustomName()==null) return;
		if(!chest.getCustomName().equals("§dAkroma Kiste"))
			return;
		event.setCancelled(true);
		event.getPlayer().sendMessage("[§5AkromaTempel§r] §cDie Opfergabentruhe ist heilig!");
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onBlockExplode(BlockExplodeEvent event){
		for(Chest chest : taxChests.values()){
			event.blockList().remove(chest.getBlock());
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onBlockExplode(EntityExplodeEvent event){
		for(Chest chest : taxChests.values()){
			event.blockList().remove(chest.getBlock());
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onItemMove(InventoryMoveItemEvent event){
		if(event.getSource()==null) return;
		InventoryHolder holder = event.getSource().getHolder();
		if(!(holder instanceof Chest)) return;
		Chest chest = (Chest)holder;
		if(chest==null) return;
		if(chest.getCustomName()==null) return;
		if(!chest.getCustomName().equals("§dAkroma Kiste"))
			return;
		if(event.getInitiator().getHolder() instanceof Player) return;
		event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onInventoryOpen(InventoryOpenEvent event){
		Inventory inventory = event.getInventory();
		InventoryHolder inventoryHolder = inventory.getHolder();
		Chest chest;
		if(inventoryHolder instanceof Chest){
			chest = (Chest) inventoryHolder;
		}
		else if(inventoryHolder instanceof DoubleChest){
			chest = (Chest)((DoubleChest)inventoryHolder).getLeftSide();
		}
		else{
			return;
		}
		
		if(chest.getCustomName()==null){
			return;
		}
		if(!chest.getCustomName().equals("§dAkroma Kiste")){
			return;
		}
		HumanEntity humanEntity = event.getPlayer();
		if(!(humanEntity instanceof Player)){
			return;
		}
		YamlConfiguration response;
		try {
			response = DataSource.getYamlResponse("taxes/info.php", new String[]{
					"player="+event.getPlayer().getUniqueId(),
					"flags[0]=raw",
					"flags[1]=chest",
					"block[world]="+URLEncoder.encode(chest.getWorld().getName(), "utf-8"),
					"block[x]="+chest.getX(),
					"block[y]="+chest.getY(),
					"block[z]="+chest.getZ()
					});
			if(response.contains("data")){
				TaxListener taxListener = new TaxListener((Player)humanEntity, response.getConfigurationSection("data"));
				taxListener.openInventory();
				event.setCancelled(true);
			}
			else if(response.contains("message")){
				humanEntity.sendMessage(response.getString("message"));
				event.setCancelled(true);
			}
			else if(response.contains("access")){
				if(!response.getString("access").equals("granted")){
					humanEntity.sendMessage(response.getString("access"));
					event.setCancelled(true);
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			Integer city_id = chestsSection.getInt(key+".city_id");
			Location location = chestsSection.getLocation(key);
			if(location==null){
				continue;
			}
			Block block = location.getBlock();
			if(block.getType()!=Material.CHEST && block.getType()!=Material.TRAPPED_CHEST){
				continue;
			}
			Chest chest = (Chest)block.getState();
			chest.setCustomName("§dAkroma Kiste");
			
			BlockFace[] neighbourFaces = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
			Block neighbourBlock;
			for(BlockFace neighbourFace : neighbourFaces){
				neighbourBlock = block.getRelative(neighbourFace);
				if(neighbourBlock==null || neighbourBlock.getType()!=block.getType()) continue;
				Chest neighbourChest = (Chest) neighbourBlock.getState();
				neighbourChest.setCustomName("§dAkroma Kiste");
			}
			taxChests.put(city_id, (Chest)block.getState());
		}
		WebCore.info("[TaxCollector] Successfully loaded tax chests");
	}
}
