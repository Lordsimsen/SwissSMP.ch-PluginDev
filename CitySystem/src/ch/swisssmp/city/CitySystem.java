package ch.swisssmp.city;

import java.util.*;
import java.util.function.Consumer;

import ch.swisssmp.city.guides.AddonGuide;
import ch.swisssmp.npc.NPCInstance;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

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
		return Addons.getAddon(cityId, addonId);
	}

	public static Collection<Addon> getAddons(UUID cityId){
		return Addons.getAll(cityId);
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
		return AddonUtility.createAddonGuide(player, sign, addon);
	}

	public static Optional<Addon> getAddon(NPCInstance npc) {
		return AddonUtility.getAddon(npc);
	}

	public static Optional<Addon> findAddon(String[] lines) {
		return AddonUtility.findAddon(lines);
	}

	public static Optional<Addon> findAddon(Block block) {
		return AddonUtility.findAddon(block);
	}
}
