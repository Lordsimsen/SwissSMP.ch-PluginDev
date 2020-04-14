package ch.swisssmp.utils;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;

public class EntityUtil {
	public static void equip(LivingEntity entity, Collection<ItemStack> itemStacks){
		EntityEquipment equipment = entity.getEquipment();
		equipment.clear();
		for(ItemStack itemStack : itemStacks){
			if(ItemUtil.isHelmet(itemStack)) equipment.setHelmet(itemStack);
			else if(ItemUtil.isChestplate(itemStack)) equipment.setChestplate(itemStack);
			else if(ItemUtil.isLeggings(itemStack)) equipment.setLeggings(itemStack);
			else if(ItemUtil.isBoots(itemStack)) equipment.setBoots(itemStack);
			else if(equipment.getItemInMainHand()==null && itemStack.getType()!=Material.SHIELD) equipment.setItemInMainHand(itemStack);
			else if(equipment.getItemInOffHand()==null) equipment.setItemInOffHand(itemStack);
			else return;
		}
	}
	public static JsonObject serialize(Entity entity) {
		return EntitySerializer.serialize(entity);
	}
	public static Entity deserialize(Location location, JsonObject data) {
		return EntityDeserializer.deserialize(location, data);
	}
	public static Entity clone(Entity template, Location location){
		JsonObject data = serialize(template);
		if(data==null) {
			Bukkit.getLogger().warning("[EntityUtil] Kann Entity vom Typ "+template.getType()+" nicht klonen.");
			return null;
		}
		return deserialize(location, data);
	}
}
