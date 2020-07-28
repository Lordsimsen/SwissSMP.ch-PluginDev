package ch.swisssmp.city.guides;

import ch.swisssmp.city.Addon;
import ch.swisssmp.city.AddonType;
import ch.swisssmp.city.Techtree;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import ch.swisssmp.city.City;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.webcore.HTTPRequest;

import java.util.UUID;

public class AddonGuides {
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
			AddonGuide guide = AddonGuide.get(npc).orElse(null);
			Addon addon = guide!=null ? guide.getAddon() : null;
			if(addon==null){
				npc.remove();
				continue;
			}
			City city = addon.getCity();
			if(city==null) continue;
			Techtree techtree = city.getTechtree();
			AddonType type = techtree.getAddonType(addon.getAddonId()).orElse(null);
			if(type==null) continue;
			techtree.updateAddonState(addon);
			addon.save();
			guide.update();
		}
	}
}
