package ch.swisssmp.city;

import java.util.*;
import java.util.function.Consumer;

import ch.swisssmp.city.npcs.NpcType;
import ch.swisssmp.city.npcs.guides.AddonGuide;
import ch.swisssmp.npc.NPCInstance;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CitySystem {
	public static void createCity(String name, Player mayor, Collection<Player> founders, SigilRingType ringType, Block origin, long time, Consumer<City> callback){
		City.create(name, mayor, founders, ringType, origin, time, (city)->{
			if(city!=null) Cities.add(city);
			if(callback!=null) callback.accept(city);
		});
	}

	public static Optional<City> findCity(String key){
		return Cities.findCity(key);
	}

	public static Optional<City> getCity(UUID uid){
		return Cities.getCity(uid);
	}

	@Deprecated
	public static Optional<City> getCity(int legacyId){
		return Cities.getCity(legacyId);
	}

	public static Optional<Addon> getAddon(UUID cityId, String addonId){
		return Addons.getAddon(cityId, addonId);
	}

	public static Collection<Addon> getAddons(UUID cityId){
		return Addons.getAll(cityId);
	}

	public static void reloadAddons(){reloadAddons(null);}

	public static void reloadAddons(Consumer<Boolean> callback){
		Addons.loadAll(callback);
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

	public static void reloadTechtrees(Consumer<Boolean> callback){
		Techtrees.loadAll(callback);
	}

	public static void reloadCities(){reloadCities(null);}

	public static void reloadCities(Consumer<Boolean> callback){
		Cities.loadAll(callback);
	}

	public static void reloadCityPromotions(){reloadCityPromotions(null);}

	public static void reloadCityPromotions(Consumer<Boolean> callback){
		CityPromotions.loadAll(callback);
	}

	public static boolean checkCityLevel(City city, String levelId){
		return checkCityLevel(city.getUniqueId(), city.getTechtreeId(), levelId);
	}

	public static boolean checkCityLevel(City city, CityLevel level){
		return checkCityLevel(city.getUniqueId(), level.getTechtree().getId(), level.getId());
	}

	public static boolean checkCityLevel(City city, Techtree techtree, String levelId){
		return checkCityLevel(city.getUniqueId(), techtree.getId(), levelId);
	}

	public static boolean checkCityLevel(UUID cityId, String techtreeId, String levelId){
		return CityPromotions.check(cityId,techtreeId,levelId);
	}

	public static void unlockCityLevel(UUID cityId, String techtreeId, String levelId, Consumer<Boolean> callback){
		CityPromotions.add(cityId, techtreeId, levelId, callback);
	}

	public static void lockCityLevel(UUID cityId, String techtreeId, String levelId, Consumer<Boolean> callback){
		CityPromotions.remove(cityId, techtreeId, levelId, callback);
	}

	public static Optional<CityLevel> getCityLevel(ItemStack tokenStack){
		return CityLevel.get(tokenStack);
	}

	public static Optional<CityPromotion> getCityPromotion(ItemStack keyStack){
		return CityPromotion.get(keyStack);
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

	public static Optional<Addon> findAddon(String[] lines, boolean createIfMissing) {
		return findAddon(lines[1],lines[2], createIfMissing);
	}

	public static Optional<Addon> findAddon(Block block, boolean createIfMissing) {
		return AddonUtility.findAddon(block, createIfMissing);
	}

	public static Optional<Addon> findAddon(String cityKey, String addonKey, boolean createIfMissing) {
		return AddonUtility.findAddon(cityKey, addonKey, createIfMissing);
	}

	public static NamespacedKey getNpcTypeKey(){
		return NpcType.getKey();
	}
}
