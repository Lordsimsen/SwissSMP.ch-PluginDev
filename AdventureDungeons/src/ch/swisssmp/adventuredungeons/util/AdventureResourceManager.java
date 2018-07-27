package ch.swisssmp.adventuredungeons.util;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
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
