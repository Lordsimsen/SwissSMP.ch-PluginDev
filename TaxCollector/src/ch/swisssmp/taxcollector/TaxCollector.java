package ch.swisssmp.taxcollector;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
	protected static ArrayList<Chest> taxChests = new ArrayList<Chest>();
	
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
	private void onInventoryOpen(InventoryOpenEvent event){
		Inventory inventory = event.getInventory();
		InventoryHolder inventoryHolder = inventory.getHolder();
		if(!(inventoryHolder instanceof Chest)) 
			return;
		Chest chest = (Chest) inventoryHolder;
		if(!taxChests.contains(chest))
			return;
		chest.setCustomName("§dAkroma Kiste");
		HumanEntity humanEntity = event.getPlayer();
		if(!(humanEntity instanceof Player)) 
			return;
		((Player)humanEntity).playSound(humanEntity.getLocation(), Sound.ENTITY_LIGHTNING_THUNDER, 5f, 1f);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled=true)
	private void onItemMove(InventoryClickEvent event){
		InventoryView inventoryView = event.getView();
		Inventory bottomInventory = inventoryView.getBottomInventory();
		Inventory topInventory = inventoryView.getTopInventory();
		if(event.getClickedInventory()!=bottomInventory) return;
		if(event.getCurrentItem()==null) return;
		if(event.getCurrentItem().getType()==Material.AIR) return;
		InventoryHolder targetHolder = topInventory.getHolder();
		if(!(targetHolder instanceof Chest)) 
			return;
		Chest chest = (Chest) targetHolder;
		if(!taxChests.contains(chest)) 
			return;
		InventoryHolder bottomHolder = bottomInventory.getHolder();
		if(!(bottomHolder instanceof Player)){
			return;
		}
		Player player = (Player) bottomHolder;
		ItemStack itemStack = event.getCurrentItem();
		if(itemStack.getItemMeta().hasLore()) return;
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("taxes/transfer.php", new String[]{
			"player="+player.getUniqueId().toString(),
			"mc_enum="+itemStack.getType().toString(),
			"mc_id="+itemStack.getData().getData(),
			"amount="+itemStack.getAmount()
		});
		if(yamlConfiguration.getInt("allow")==0){
			event.setCancelled(true);
		}
		if(yamlConfiguration.contains("remaining")){
			itemStack.setAmount(yamlConfiguration.getInt("remaining"));
		}
		if(yamlConfiguration.contains("sound")){
			player.playSound(player.getLocation(), yamlConfiguration.getString("sound"), 5f, 1f);
		}
		if(yamlConfiguration.contains("message")){
			for(String line : yamlConfiguration.getStringList("message")){
				player.sendMessage(line);
			}
		}
		if(yamlConfiguration.contains("commands")){
			for(String line : yamlConfiguration.getStringList("commands")){
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), line);
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onInventoryClose(InventoryCloseEvent event){
		Inventory inventory = event.getInventory();
		InventoryHolder inventoryHolder = inventory.getHolder();
		if(!(inventoryHolder instanceof Chest)) return;
		Chest chest = (Chest) inventoryHolder;
		if(!taxChests.contains(chest)) return;
		HumanEntity player = event.getPlayer();
		inform_player(new String[]{
				"player="+player.getUniqueId().toString()	
			}, player);
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
			Location location = chestsSection.getLocation(key);
			if(location==null){
				continue;
			}
			Block block = location.getBlock();
			if(block.getType()!=Material.CHEST && block.getType()!=Material.TRAPPED_CHEST){
				continue;
			}
			taxChests.add((Chest)block.getState());
		}
		WebCore.info("Successfully loaded tax chests");
	}
	
	/*public static void collect(){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("taxes/pending.php");
		if(yamlConfiguration==null) return;
		for(String key : yamlConfiguration.getKeys(false)){
			ConfigurationSection taxSection = yamlConfiguration.getConfigurationSection(key);
			int city_id = taxSection.getInt("city_id");
			int tax_id = taxSection.getInt("tax_id");
			HashMap<Material, Integer> resources = new HashMap<Material, Integer>();
			ConfigurationSection resourcesSection = taxSection.getConfigurationSection("resources");
			for(String resourceKey : resourcesSection.getKeys(false)){
				ConfigurationSection resourceSection = resourcesSection.getConfigurationSection(resourceKey);
				Material material = resourceSection.getMaterial("material");
				int amount = resourceSection.getInt("amount");
				resources.put(material, amount);
			}
			Location chestLocation = taxSection.getLocation("chest");
			if(chestLocation==null){
				continue;
			}
			Block block = chestLocation.getBlock();
			BlockState blockState = block.getState();
			if(blockState instanceof Chest){
				Inventory inventory = ((Chest)blockState).getInventory();
				for(ItemStack itemStack : inventory){
					if(itemStack==null) continue;
					if(resources.containsKey(itemStack.getType())){
						int required = resources.get(itemStack.getType());
						int take = Math.min(required, itemStack.getAmount());
						itemStack.setAmount(itemStack.getAmount()-take);
						resources.put(itemStack.getType(), required-take);
						Bukkit.getLogger().info("Removed "+take+" "+itemStack.getType().toString()+" from "+block.getX()+","+block.getY()+","+block.getZ()+" for the tax collection in city_"+city_id+".");
					}
				}
			}
			else{
				continue;
			}
			List<String> arguments = new ArrayList<String>();
			arguments.add("city_id="+city_id);
			arguments.add("tax_id="+tax_id);
			for(Entry<Material,Integer> entry : resources.entrySet()){
				arguments.add("resources["+entry.getKey().toString()+"]="+entry.getValue());
			}
			String[] argumentsArray = new String[arguments.size()];
			DataSource.getResponse("taxes/update.php", arguments.toArray(argumentsArray));
		}
	}
	
	public static void info(){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("taxes/chests.php");
		if(yamlConfiguration==null) return;
		for(String key : yamlConfiguration.getKeys(false)){
			ConfigurationSection taxSection = yamlConfiguration.getConfigurationSection(key);
			int addon_id = taxSection.getInt("addon_id");
			HashMap<String, Integer> resources = new HashMap<String, Integer>();
			Location chestLocation = taxSection.getLocation("chest");
			if(chestLocation==null){
				continue;
			}
			Block block = chestLocation.getBlock();
			BlockState blockState = block.getState();
			if(blockState instanceof Chest){
				Inventory inventory = ((Chest)blockState).getInventory();
				for(ItemStack itemStack : inventory){
					if(itemStack==null) continue;
					@SuppressWarnings("deprecation")
					String displayName = itemStack.getType().toString()+"-"+itemStack.getData().getData();
					int amount = itemStack.getAmount();
					if(!resources.containsKey(displayName)){
						resources.put(displayName, amount);
					}
					else{
						resources.put(displayName, resources.get(displayName)+amount);
					}
				}
			}
			else{
				continue;
			}
			List<String> arguments = new ArrayList<String>();
			arguments.add("addon="+addon_id);
			for(Entry<String,Integer> entry : resources.entrySet()){
				arguments.add("resources["+entry.getKey()+"]="+entry.getValue());
			}
			String[] argumentsArray = new String[arguments.size()];
			DataSource.getResponse("taxes/info.php", arguments.toArray(argumentsArray));
		}
	}*/
}
