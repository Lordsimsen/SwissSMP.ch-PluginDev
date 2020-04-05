package ch.swisssmp.addonabnahme;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import ch.swisssmp.addonabnahme.AddonUnlockTrade.UnlockType;
import ch.swisssmp.city.City;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.utils.ConfigurationSection;

public class AddonInstanceInfo {
	
	private final City city;
	private final AddonInfo addonInfo;
	private boolean guideActive;
	private AddonState state;
	private String stateReason;
	
	private List<AddonUnlockTrade> unlockTrades = new ArrayList<AddonUnlockTrade>();
	
	public AddonInstanceInfo(City city, AddonInfo addonInfo, boolean guideActive){
		this.city = city;
		this.addonInfo = addonInfo;
		this.guideActive = guideActive;
	}
	
	public void apply(NPCInstance npc){
		npc.setIdentifier("addon_instance_guide");
		npc.setSilent(true);
		npc.setNameVisible(false);
		JsonObject json = npc.getJsonData();
		if(json==null) json = new JsonObject();
		json.add("city_id", new JsonPrimitive(city.getId()));
		json.add("addon_id", new JsonPrimitive(addonInfo.getAddonId()));
		npc.setJsonData(json);
		if(state!=null){
			npc.setName(state.getColor()+addonInfo.getName());
		}
		else{
			npc.setName(addonInfo.getName());
		}
		this.updateVillager(npc.getEntity());
	}
	
	private void updateVillager(Entity entity){
		if(!(entity instanceof Villager)) return;
		Villager villager = (Villager) entity;
		Profession profession;
		switch(addonInfo.getCityLevel()){
		case 0: profession = Profession.FARMER;break;
		case 1: profession = Profession.TOOLSMITH;break;
		case 2: profession = Profession.NITWIT;break;
		case 3: profession = Profession.LIBRARIAN;break;
		case 4: profession = Profession.CLERIC;break;
		default: profession = Profession.FARMER;
		}
		villager.setProfession(profession);
	}
	
	public City getCity(){
		return city;
	}
	
	public AddonInfo getAddonInfo(){
		return addonInfo;
	}
	
	public AddonState getState(){
		return this.state;
	}
	
	public boolean hasActiveGuide(){
		return this.guideActive;
	}
	
	public void setGuideActive(boolean guideActive){
		this.guideActive = guideActive;
	}
	
	public void setAddonState(AddonState state){
		this.state = state;
	}
	
	public void setAddonStateReason(String stateReason){
		this.stateReason = stateReason;
	}
	
	public String getAddonStateReason(){
		return this.stateReason;
	}
	
	public List<AddonUnlockTrade> getUnlockTrades(){
		return this.unlockTrades;
	}
	
	public static AddonInstanceInfo get(ConfigurationSection dataSection){
		int city_id = dataSection.getInt("city_id");
		String addon_id = dataSection.getString("addon_id");
		AddonState state = AddonState.get(dataSection.getString("state"));
		AddonInstanceInfo result = get(city_id, addon_id, dataSection.getInt("guide_active")>0);
		if(result==null){
			return null;
		}
		result.setAddonState(state);
		if(dataSection.contains("reason")){
			result.setAddonStateReason(dataSection.getString("reason"));
		}
		if(state==AddonState.Available && dataSection.contains("cost")){
			AddonUnlockTrade trade = AddonUnlockTrade.get(UnlockType.Perpetual, dataSection.getConfigurationSection("cost"));
			if(trade!=null) result.unlockTrades.add(trade);
		}
		else if(state==AddonState.Accepted && dataSection.contains("licenses")){
			ConfigurationSection licensesSection = dataSection.getConfigurationSection("licenses");
			for(String key : licensesSection.getKeys(false)){
				ConfigurationSection licenseSection = licensesSection.getConfigurationSection(key);
				AddonUnlockTrade trade = AddonUnlockTrade.get(UnlockType.Rental, licenseSection);
				if(trade==null) continue;
				result.unlockTrades.add(trade);
			}
		}
		return result;
	}
	
	public static AddonInstanceInfo get(NPCInstance npc){
		String identifier = npc.getIdentifier();
		if(identifier==null || !identifier.equals("addon_instance_guide")) return null;
		JsonObject json = npc.getJsonData();
		if(json==null || !json.has("city_id") || !json.has("addon_id")) return null;
		int city_id = json.get("city_id").getAsInt();
		String addon_id = json.get("addon_id").getAsString();
		return get(city_id, addon_id, true);
	}
	
	public static AddonInstanceInfo get(String[] lines){
		String cityName = lines[1];
		City city = City.get(cityName);
		if(city==null) return null;
		Techtree techtree = Techtree.get(city.getTechtreeId());
		if(techtree==null) return null;
		String addonKey = lines[2];
		AddonInfo addon = techtree.getAddon(addonKey);
		if(addon==null) return null;
		String stateKey = lines[3];
		AddonInstanceInfo result = new AddonInstanceInfo(city,addon,false);
		if(!stateKey.isEmpty()){
			AddonState state = AddonState.get(stateKey);
			if(state!=null) result.setAddonState(state);
		}
		return result;
	}
	
	public static AddonInstanceInfo get(int city_id, String addon_id, boolean guideActive){
		City city = City.get(city_id);
		if(city==null){
			return null;
		}
		Techtree techtree = Techtree.get(city.getTechtreeId());
		if(techtree==null){
			return null;
		}
		AddonInfo addon = techtree.getAddon(addon_id);
		if(addon==null){
			return null;
		}
		return new AddonInstanceInfo(city,addon,guideActive);
	}
	
	public static AddonInstanceInfo get(Block block){
		if(!(block.getState() instanceof Sign)) return null;
		Sign sign = (Sign) block.getState();
		return get(sign.getLines());
	}
}
