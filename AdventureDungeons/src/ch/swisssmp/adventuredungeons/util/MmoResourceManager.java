package ch.swisssmp.adventuredungeons.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.adventuredungeons.Main;
import ch.swisssmp.adventuredungeons.mmoitem.MmoItemManager;
import ch.swisssmp.adventuredungeons.mmoitem.MmoLootInventory;
import ch.swisssmp.adventuredungeons.mmoplayer.MmoPlayer;
import net.md_5.bungee.api.ChatColor;

public class MmoResourceManager {
	
	public static String rootURL;
	public static String pluginToken;
	private static Random random = new Random();
	
	public static String getResponse(String relativeURL){
		return getResponse(relativeURL, null);
	}
	
	public static String getResponse(String relativeURL, String[] params){
		String resultString = "";
		try{
			String urlString = rootURL+relativeURL+"?token="+pluginToken+"&random="+random.nextInt(1000);
			if(params!=null && params.length>0){
				urlString+="&"+String.join("&", params);
			}
			Main.info("Connecting to: "+urlString);
			URL url = new URL(urlString);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String tempString = "";
			while(null!=(tempString = br.readLine())){
				resultString+= tempString;
			}
			if(resultString.isEmpty()){
				Main.info("Returning empty result");
				return "";
			}
			return resultString;
		}
		catch(Exception e){
			e.printStackTrace();
			Main.info("Causing the error: "+resultString);
			return "";
		}
	}
	
	public static YamlConfiguration getYamlResponse(String relativeURL){
		return getYamlResponse(relativeURL, null);
	}
	
	public static YamlConfiguration getYamlResponse(String relativeURL, String[] params){
		String resultString = convertWebYamlString(getResponse(relativeURL, params));
		if(resultString.isEmpty()){
			Main.info("Returning empty result");
			return new YamlConfiguration();
		}
		try{
			Main.info("Result: "+resultString);
			YamlConfiguration yamlConfiguration = new YamlConfiguration();
			yamlConfiguration.loadFromString(resultString);
			return yamlConfiguration;
		}
		catch(Exception e){
			e.printStackTrace();
			Main.info("Causing the error: "+resultString);
			return new YamlConfiguration();
		}
	}
    private static String convertWebYamlString(String webYamlString){
    	webYamlString = webYamlString.replace("<br>", "\r\n");
    	webYamlString = webYamlString.replace("&nbsp;", " ");
    	return webYamlString;
    }
    public static void processYamlResponse(UUID player_uuid, String relativeURL){
    	processYamlResponse(player_uuid, relativeURL, null);
    }
	public static void processYamlResponse(UUID player_uuid, String relativeURL, String[] params){
    	YamlConfiguration serverResponse = getYamlResponse(relativeURL, params);
    	processYamlData(player_uuid, serverResponse);
    }
	public static void processYamlData(UUID player_uuid, YamlConfiguration yamlConfiguration){
		Player player = Bukkit.getPlayer(player_uuid);
		if(yamlConfiguration.contains("items") && player!=null){
			ConfigurationSection rewardsSection = yamlConfiguration.getConfigurationSection("items");
			for(String stringIndex : rewardsSection.getKeys(false)){
				//get item data
				ConfigurationSection rewardSection = rewardsSection.getConfigurationSection(stringIndex);
				ItemStack itemStack = MmoItemManager.getItemFromHybridID(rewardSection);
				if(itemStack==null)
					continue;
				//drop the actual item
				Location location = player.getLocation();
				World world = location.getWorld();
				world.dropItem(location, itemStack);
				String name = itemStack.getType().name();
				if(itemStack.getItemMeta().hasDisplayName())
					name = itemStack.getItemMeta().getDisplayName();
				MmoPlayer.sendMessage(player, ChatColor.YELLOW+""+itemStack.getAmount()+" "+name+ChatColor.YELLOW+" erhalten!");
			}
		}
		if(yamlConfiguration.contains("loot") && player!=null){
			ConfigurationSection lootInfoSection = yamlConfiguration.getConfigurationSection("loot_info");
			String inventory_name = lootInfoSection.getString("name");
			int reset_time = lootInfoSection.getInt("time");
			String type = lootInfoSection.getString("type");
			boolean global = (lootInfoSection.getInt("global")==1);
			String action = lootInfoSection.getString("action");
			int x = lootInfoSection.getInt("x");
			int y = lootInfoSection.getInt("y");
			int z = lootInfoSection.getInt("z");
			String worldName = lootInfoSection.getString("world_instance");
			World world = Bukkit.getWorld(worldName);
			Location location = new Location(world, x, y, z);
			ConfigurationSection lootSection = yamlConfiguration.getConfigurationSection("loot");
			ArrayList<ItemStack> loot = new ArrayList<ItemStack>();
			for(String key : lootSection.getKeys(false)){
				ConfigurationSection itemSection = lootSection.getConfigurationSection(key);
				ItemStack itemStack = MmoItemManager.getItemFromHybridID(itemSection);
				if(itemStack!=null)
					loot.add(itemStack);
			}
			ItemStack[] items = new ItemStack[loot.size()];
			items = loot.toArray(items);
			Inventory inventory = Bukkit.createInventory(null, InventoryType.valueOf(type), inventory_name);
			inventory.setStorageContents(items);
			MmoLootInventory lootInventory = MmoLootInventory.create(player, action, global, location.getBlock(), inventory);
			BukkitTask task = Bukkit.getScheduler().runTaskLater(Main.plugin, lootInventory, reset_time*20);
			lootInventory.task_id = task.getTaskId();
			player.openInventory(lootInventory.inventory);
		}
		if(yamlConfiguration.contains("commands") && player!=null){
			List<String> commandsList = yamlConfiguration.getStringList("commands");
			for(String command : commandsList){
				if(player!=null){
					command = command.replace("[player]", player.getName());
					command = command.replace("[world]", player.getWorld().getName());
				}
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command); 
			}
		}
    }
}
