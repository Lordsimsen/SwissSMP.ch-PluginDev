package ch.swisssmp.city;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ch.swisssmp.city.*;
import ch.swisssmp.city.guides.AddonGuides;
import ch.swisssmp.utils.*;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public class AddonUtility {

	public static Optional<Addon> findAddon(String[] lines) {
		String cityName = lines[1];
		City city = CitySystem.findCity(cityName).orElse(null);
		if (city == null) return Optional.empty();
		Techtree techtree = CitySystem.getTechtree(city.getTechtreeId()).orElse(null);
		if (techtree == null) return Optional.empty();
		String addonKey = lines[2];
		AddonType type = techtree.findAddonType(addonKey).orElse(null);
		if (type == null) return Optional.empty();
		Addon result = city.getAddon(type).orElse(city.createAddon(type));
		techtree.updateAddonState(result);
		result.save();
		return Optional.of(result);
	}

	public static Optional<Addon> findAddon(Block block) {
		if (!(block.getState() instanceof Sign)) return Optional.empty();
		Sign sign = (Sign) block.getState();
		return findAddon(sign.getLines());
	}
	
	public static void unlockAddon(Player player, Addon addon, List<ItemStack> items){
		List<String> itemStrings = new ArrayList<String>();
		for(ItemStack item : items){
			itemStrings.add("items[]="+SwissSMPUtils.encodeItemStack(item));
		}
		HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), "unlock_addon.php", new String[]{
				"player="+player.getUniqueId(),
				"city="+addon.getCityId(),
				"addon="+URLEncoder.encode(addon.getAddonId()),
				String.join("&", itemStrings)
		});
		request.onFinish(()->{
			JsonObject json = request.getJsonResponse();
			if(json==null || !json.has("result")){
				for(ItemStack itemStack : items){
					player.getWorld().dropItem(player.getEyeLocation(), itemStack);
				}
				SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Das Addon konnte nicht aktiviert werden.");
				return;
			}
			String result = JsonUtil.getString("result", json);
			if(result.equals("success")){
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "permission reload");
				AddonGuides.updateAll();
				City city = addon.getCity();
				if(city!=null) {
					city.broadcast(CitySystemPlugin.getPrefix()+ChatColor.GREEN+city.getName()+" hat das Addon "+addon.getType().getName()+" aktiviert!");
				}
			}
			else{
				SwissSMPler.get(player).sendActionBar(result);
			}
		});
	}
}
