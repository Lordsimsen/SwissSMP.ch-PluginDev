package ch.swisssmp.city.guides;

import ch.swisssmp.city.Addon;
import ch.swisssmp.city.AddonType;
import ch.swisssmp.city.Techtree;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import ch.swisssmp.city.City;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class AddonGuides {
	public static void updateAll(){
		for(Chunk chunk : Bukkit.getWorlds().get(0).getLoadedChunks()){
			updateAll(chunk);
		}
	}
	
	public static void updateAll(Chunk chunk){
		for(Entity entity : chunk.getEntities()){
			update(entity);
		}
	}

	public static Optional<AddonGuide> findGuide(Addon addon){
		String worldName = addon.getWorldName();
		Vector vector = addon.getOrigin();
		if(worldName==null || vector==null) return Optional.empty();
		World world = Bukkit.getWorld(worldName);
		if(world==null) return Optional.empty();
		Collection<Entity> entities = world.getNearbyEntities(new Location(world,vector.getX(),vector.getY(),vector.getZ()), 1, 1, 1, (entity)->entity.getType()==EntityType.ARMOR_STAND);
		AddonGuide result = null;
		for(Entity entity : entities){
			AddonGuide guide = AddonGuide.get(entity).orElse(null);
			if(guide==null) continue;
			if(guide.getAddon()!=addon) continue;
			result = guide;
			break;
		}

		return result!=null ? Optional.of(result) : Optional.empty();
	}

	private static void update(Entity entity){
		if(entity.getType()!=EntityType.ARMOR_STAND) return;
		NPCInstance npc = NPCInstance.get(entity);
		if(npc==null) return;
		AddonGuide guide = AddonGuide.get(npc).orElse(null);
		Addon addon = guide!=null ? guide.getAddon() : null;
		if(addon==null){
			if(guide!=null) npc.remove();
			return;
		}
		Location location = npc.getEntity().getLocation();
		Vector addonOrigin = addon.getOrigin()!=null ? addon.getOrigin() : null;
		if(addonOrigin==null || !location.getWorld().getName().equals(addon.getWorldName()) || location.toVector().distanceSquared(addonOrigin)>1){
			npc.remove();
			return;
		}
		City city = addon.getCity();
		if(city==null) return;
		Techtree techtree = city.getTechtree();
		AddonType type = techtree.getAddonType(addon.getAddonId()).orElse(null);
		if(type==null) return;
		techtree.updateAddonState(addon);
		addon.save();
		guide.update();
	}
}
