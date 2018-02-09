package ch.swisssmp.adventuredungeons.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.event.ItemDiscoveredEvent;
import ch.swisssmp.adventuredungeons.sound.AdventureSound;
import ch.swisssmp.adventuredungeons.world.Dungeon;
import ch.swisssmp.adventuredungeons.world.DungeonInstance;
import ch.swisssmp.adventuredungeons.world.LootInventory;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import io.netty.util.internal.ThreadLocalRandom;
import net.md_5.bungee.api.ChatColor;

public class AdventureResourceManager {
	
    public static boolean processYamlResponse(UUID player_uuid, String relativeURL, String... responseContains){
    	return processYamlResponse(player_uuid, relativeURL, null, responseContains);
    }
	public static boolean processYamlResponse(UUID player_uuid, String relativeURL, String[] params, String... responseContains){
    	YamlConfiguration serverResponse = DataSource.getYamlResponse(relativeURL, params);
    	return processYamlData(player_uuid, serverResponse, responseContains);
    }
	public static boolean processYamlData(UUID player_uuid, YamlConfiguration yamlConfiguration, String[] responseContains){
		Player player = Bukkit.getPlayer(player_uuid);
		if(yamlConfiguration.contains("items") && player!=null){
			ConfigurationSection rewardsSection = yamlConfiguration.getConfigurationSection("items");
			for(String stringIndex : rewardsSection.getKeys(false)){
				//get item data
				CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder(rewardsSection.getConfigurationSection(stringIndex));
				ItemStack itemStack = itemBuilder.build();
				if(itemStack==null)
					continue;
				//drop the actual item
				Location location = player.getLocation();
				World world = location.getWorld();
				world.dropItem(location, itemStack);
				String name = itemStack.getType().name();
				if(itemStack.getItemMeta().hasDisplayName())
					name = itemStack.getItemMeta().getDisplayName();
				SwissSMPler swisssmpler = SwissSMPler.get(player_uuid);
				if(swisssmpler!=null) swisssmpler.sendMessage(ChatColor.YELLOW+""+itemStack.getAmount()+" "+name+ChatColor.YELLOW+" erhalten!");
			}
		}
		if(yamlConfiguration.contains("loot") && player!=null){
			DungeonInstance dungeonInstance = Dungeon.getInstance(player);
			if(dungeonInstance==null) return false;
			ConfigurationSection lootInfoSection = yamlConfiguration.getConfigurationSection("loot_info");
			String inventory_name = lootInfoSection.getString("name");
			int reset_time = lootInfoSection.getInt("time");
			String type = lootInfoSection.getString("type");
			boolean global = lootInfoSection.getBoolean("global");
			String action = lootInfoSection.getString("action");
			int x = lootInfoSection.getInt("x");
			int y = lootInfoSection.getInt("y");
			int z = lootInfoSection.getInt("z");
			String worldName = lootInfoSection.getString("world");
			World world = Bukkit.getWorld(worldName);
			Location location = new Location(world, x, y, z);
			ConfigurationSection lootSection = yamlConfiguration.getConfigurationSection("loot");
			ArrayList<ItemStack> loot = new ArrayList<ItemStack>();
			for(String key : lootSection.getKeys(false)){
				ConfigurationSection itemSection = lootSection.getConfigurationSection(key);
				if(itemSection.contains("probability")){
					double probability = itemSection.getDouble("probability");
					switch(dungeonInstance.getDifficulty()){
					case EASY:
						probability *= 0.4;
						if(probability<0.001) probability = 0;
						break;
					case NORMAL:
						probability *= 0.8;
						if(probability<0.001) probability = 0;
						break;
					case HARD:
						probability *= 1.2;
						break;
					default:
						probability = 0;
						break;
					}
					if(ThreadLocalRandom.current().nextDouble()>probability){
						continue;
					}
				}
				CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder(itemSection);
				ItemStack itemStack = itemBuilder.build();
				if(itemStack!=null && itemStack.getAmount()>0){
					if(AdventureDungeons.debug){
						Bukkit.getLogger().info("[AdventureDungeons] ItemStack zu Loot hinzugefügt: "+itemStack.getType().name());
					}
					if(itemSection.contains("announce")){
						String message = itemSection.getString("announce");
						Bukkit.broadcastMessage(message.replace("{Player}", player.getDisplayName()));
					}
					if(itemSection.contains("event") && itemSection.getString("event").equals("trigger")){
						Bukkit.getPluginManager().callEvent(new ItemDiscoveredEvent(dungeonInstance, player, itemStack));
					}
					loot.add(itemStack);
				}
			}
			Collections.shuffle(loot);
			ItemStack[] items = new ItemStack[loot.size()];
			items = loot.toArray(items);
			Inventory inventory = Bukkit.createInventory(null, InventoryType.valueOf(type), inventory_name);
			if(inventory.getSize()<items.length){
				items = Arrays.copyOfRange(items, 0, inventory.getSize()-1);
			}
			inventory.setStorageContents(items);
			if(dungeonInstance!=null && items.length>0){
				if(AdventureDungeons.debug){
					Bukkit.getLogger().info("[AdventureDungeons] Öffne Loot-Inventar für "+player.getName());
				}
				LootInventory lootInventory = dungeonInstance.createLootInventory(player, action, global, location.getBlock(), inventory);
				BukkitTask task = Bukkit.getScheduler().runTaskLater(AdventureDungeons.plugin, lootInventory, reset_time*20);
				lootInventory.setTaskId(task.getTaskId());
				player.openInventory(lootInventory.getInventory());
				AdventureSound.play(player, 8);
			}
			else if(items.length==0){
				return false;
			}
			else{
				//This shouldn't happen
				Bukkit.getLogger().info("[AdventureDungeons] Konnte Loot-Inventar nicht öffnen, Dungeon Instanz nicht gefunden.");
			}
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
		if(responseContains!=null && responseContains.length>0){
			for(String string : responseContains){
				if(yamlConfiguration.contains(string)) return true;
			}
			return false;
		}
		return true;
    }
}
