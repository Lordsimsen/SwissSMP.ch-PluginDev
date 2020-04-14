package ch.swisssmp.npc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ch.swisssmp.utils.EntityUtil;
import ch.swisssmp.utils.ItemUtil;

public class NPCPacker {
	
	protected static void pack(World world) {
		for(Entity entity : world.getEntities()) {
			pack(entity);
		}
	}
	
	private static void pack(Entity entity) {
		if(entity==null || entity.getType()!=EntityType.ARMOR_STAND || entity.getVehicle()!=null || !entity.isInvulnerable()) return;
		NPCInstance npc = NPCInstance.get(entity);
		if(npc==null) return;
		pack(npc);
	}
	
	protected static void pack(NPCInstance npc) {
		Entity passenger = npc.getEntity();
		ItemStack spawnEgg = createSpawnEgg(passenger);
		if(spawnEgg==null) {
			Bukkit.getLogger().info(NPCs.getPrefix()+" Konnte NPC "+npc.getName()+" nicht verpacken!");
			return;
		}
		
		passenger.remove();
		npc.getBase().getEquipment().setHelmet(spawnEgg);
	}
	
	protected static void unpack(ArmorStand armorStand, ItemStack spawnEgg) {
		if(spawnEgg==null) {
			return;
		}
		Entity entity = createEntityFromSpawnEgg(armorStand.getLocation(), spawnEgg);
		if(entity==null) {
			String displayString = (spawnEgg.hasItemMeta() ? spawnEgg.getItemMeta().getDisplayName() : spawnEgg.getType().toString());
			Bukkit.getLogger().info(NPCs.getPrefix()+" Konnte NPC "+displayString+" nicht entpacken!");
			return;
		}
		armorStand.addPassenger(entity);
	}
	
	private static ItemStack createSpawnEgg(Entity entity) {
		String prefix = NPCs.getPrefix();
		if(!(entity instanceof LivingEntity)) {
			Bukkit.getLogger().info(prefix+" Kann nur lebende Entities verpacken!");
			return null;
		}
		
		LivingEntity livingEntity = (LivingEntity) entity;
		String serialized = EntityUtil.serialize(livingEntity).toString();
		ItemStack result = new ItemStack(Material.BOOK);
		ItemUtil.setString(result, "serialized_entity", serialized);
		return result;
	}
	
	private static Entity createEntityFromSpawnEgg(Location location, ItemStack itemStack) {
		String serialized = ItemUtil.getString(itemStack, "serialized_entity");
		if(serialized==null) return null;
		JsonObject data;
		try {
			JsonParser parser = new JsonParser();
			JsonElement json = parser.parse(serialized);
			data = json.getAsJsonObject();
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return EntityUtil.deserialize(location, data);
	}
}
