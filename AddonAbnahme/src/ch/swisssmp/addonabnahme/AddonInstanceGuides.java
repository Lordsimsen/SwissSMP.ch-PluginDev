package ch.swisssmp.addonabnahme;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import ch.swisssmp.city.City;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.HTTPRequest;

public class AddonInstanceGuides {
	public static void updateAll(){
		for(Chunk chunk : Bukkit.getWorlds().get(0).getLoadedChunks()){
			updateAll(chunk);
		}
	}
	
	public static void updateAll(Chunk chunk){
		for(Entity entity : chunk.getEntities()){
			if(entity.getType()!=EntityType.ARMOR_STAND) continue;
			NPCInstance npc = NPCInstance.get(entity);
			if(npc==null) continue;
			String identifier = npc.getIdentifier();
			if(identifier==null || !identifier.equals("addon_instance_guide")) continue;
			AddonInstanceInfo info = AddonInstanceInfo.get(npc);
			if(info==null){
				npc.remove();
				continue;
			}
			City city = info.getCity();
			if(city==null) continue;
			AddonInfo addon = info.getAddonInfo();
			if(addon==null) continue;
			int city_id = city.getId();
			String techtree_id = city.getTechtreeId();
			String addon_id = addon.getAddonId();
			HTTPRequest request = AddonManager.downloadAddonInstanceInfo(city_id, techtree_id, addon_id);
			if(request==null) continue;
			request.onFinish(()->{
				JsonObject json = request.getJsonResponse();
				if(json==null || !json.has("addon")) return;
				AddonInstanceInfo addonInfo = AddonInstanceInfo.get(json.getAsJsonObject("addon"));
				if(addonInfo==null) return;
				addonInfo.apply(npc);
			});
		}
	}
}
