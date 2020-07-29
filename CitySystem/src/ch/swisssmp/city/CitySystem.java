package ch.swisssmp.city;

import java.util.*;
import java.util.function.Consumer;

import ch.swisssmp.city.guides.AddonGuide;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.utils.SwissSMPler;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public class CitySystem {
	public static void createCity(String name, Player mayor, Collection<Player> founders, String ringType, Block origin, long time, Consumer<City> callback){
		City.create(name, mayor, founders, ringType, origin, time, callback);
	}

	public static Optional<City> findCity(String key){
		return Cities.findCity(key);
	}

	public static Optional<City> getCity(UUID uid){
		return Cities.getCity(uid);
	}

	public static Optional<Addon> getAddon(UUID cityId, String addonId){
		City city = getCity(cityId).orElse(null);
		return city!=null ? city.getAddon(addonId) : Optional.empty();
	}

	public static Collection<City> getCities(){
		return Cities.getAll();
	}

	public static Optional<Techtree> getTechtree(String id){
		return Techtrees.getTechtree(id);
	}

	public static Collection<Techtree> getTechtrees(){return Techtrees.getAll();}

	public static void reloadTechtrees(){
		reloadTechtrees(null);
	}

	public static void reloadTechtrees(Runnable callback){
		Techtrees.loadAll(callback);
	}

	public static Optional<Citizenship> getCitizenship(UUID cityId, String playerName){
		return Citizenships.getCitizenship(cityId, playerName);
	}

	public static Optional<Citizenship> getCitizenship(UUID cityId, Player player){
		return getCitizenship(cityId, player.getUniqueId());
	}

	public static Optional<Citizenship> getCitizenship(UUID cityId, UUID playerUid){
		return Citizenships.getCitizenship(cityId, playerUid);
	}

	public static AddonGuide createAddonGuide(Player player, Sign sign, Addon addon){
		if(addon.hasGuideActive()){
			SwissSMPler.get(player).sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.RED+"Dieses Addon hat bereits einen Addon Guide.");
			SwissSMPler.get(player).sendMessage(CitySystemPlugin.getPrefix()+ChatColor.RED+"Entferne zuerst diesen und versuche es dann nochmals.");
			return null;
		}
		Location location = sign.getLocation();
		location.setYaw(player.getLocation().getYaw()+180);
		sign.getBlock().setType(Material.AIR);
		return AddonGuide.create(location.add(0.5, 0, 0.5), addon);
	}

	public static Optional<Addon> getAddon(NPCInstance npc) {
		String identifier = npc.getIdentifier();
		if (identifier == null || !identifier.equals("addon_instance_guide")) return Optional.empty();
		JsonObject json = npc.getJsonData();
		if (json == null || !json.has("city_id") || !json.has("addon_id")) return Optional.empty();
		UUID cityId = JsonUtil.getUUID("city_id", json);
		String addonId = json.get("addon_id").getAsString();
		return CitySystem.getAddon(cityId, addonId);
	}
}
