package ch.swisssmp.addonabnahme;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.SwissSMPUtils;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public class AddonManager {
	
	public static void unlockAddon(Player player, AddonInstanceInfo addonInfo, List<ItemStack> items){
		List<String> itemStrings = new ArrayList<String>();
		for(ItemStack item : items){
			itemStrings.add("items[]="+SwissSMPUtils.encodeItemStack(item));
		}
		HTTPRequest request = DataSource.getResponse(AddonAbnahme.getInstance(), "unlock_addon.php", new String[]{
				"player="+player.getUniqueId(),
				"city="+addonInfo.getCity().getId(),
				"addon="+URLEncoder.encode(addonInfo.getAddonInfo().getAddonId()),
				String.join("&", itemStrings)
		});
		request.onFinish(()->{
			YamlConfiguration yamlConfiguration = request.getYamlResponse();
			if(yamlConfiguration==null || !yamlConfiguration.contains("result")){
				for(ItemStack itemStack : items){
					player.getWorld().dropItem(player.getEyeLocation(), itemStack);
				}
				SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Das Addon konnte nicht aktiviert werden.");
				return;
			}
			String result = yamlConfiguration.getString("result");
			if(result.equals("success")){
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "permission reload");
				AddonInstanceGuides.updateAll();
				addonInfo.getCity().broadcast(CitySystemPlugin.getPrefix()+ChatColor.GREEN+addonInfo.getCity().getName()+" hat das Addon "+addonInfo.getAddonInfo().getName()+" aktiviert!");
			}
			else{
				SwissSMPler.get(player).sendActionBar(result);
			}
		});
	}
	
	public static void createAddonGuide(Player player, Sign sign, AddonInstanceInfo signInfo, JsonObject json){

		if(json==null || !json.has("addon")){
			SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Konnte den Addon Guide nicht platzieren. (Systemfehler)");
			return;
		}
		JsonObject addonSection = json.getAsJsonObject("addon");
		AddonInstanceInfo instanceInfo = AddonInstanceInfo.get(addonSection);
		if(instanceInfo==null){
			SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Konnte den Addon Guide nicht platzieren. (Systemfehler)");
			return;
		}
		if(instanceInfo.hasActiveGuide()){
			SwissSMPler.get(player).sendMessage(AddonAbnahme.getPrefix()+ChatColor.RED+"Dieses Addon hat bereits einen Addon Guide.");
			SwissSMPler.get(player).sendMessage(AddonAbnahme.getPrefix()+ChatColor.RED+"Entferne zuerst diesen und versuche es dann nochmals.");
			return;
		}
		Location location = sign.getLocation();
		location.setYaw(player.getLocation().getYaw()+180);
		sign.getBlock().setType(Material.AIR);
		AddonInstanceGuide.create(location.add(0.5, 0, 0.5), instanceInfo);
	}
	
	public static HTTPRequest downloadAddonInstanceInfo(int city_id, String techtree_id, String addon_id){
		return DataSource.getResponse(AddonAbnahme.getInstance(), "get_addon_instance.php", new String[]{
				"city="+city_id,
				"techtree="+techtree_id,
				"addon_id="+URLEncoder.encode(addon_id)
		});
	}
	
	public static CustomItemBuilder getAddonBuilder(AddonInfo addonInfo){
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder("addon_"+addonInfo.getAddonId());
		if(itemBuilder==null){
			itemBuilder = new CustomItemBuilder();
			itemBuilder.setMaterial(Material.BOOK);
		}
		itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_ENCHANTS);
		itemBuilder.setDisplayName(addonInfo.getName());
		
		return itemBuilder;
		
	}
	
	public static ItemStack getAddonStack(AddonInfo addonInfo){
		return getAddonBuilder(addonInfo).build();
	}
}
