package ch.swisssmp.npc;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;

public class NPCUnpacker implements Listener {
	
	private static final HashMap<World,NPCUnpacker> instances = new HashMap<World,NPCUnpacker>();
	
	private final World world;
	
	private NPCUnpacker(World world) {
		this.world = world;
	}
	
	private void initialize() {
		for(Entity entity : world.getEntities()) {
			unpackEntity(entity);
		}
	}
	
	@EventHandler
	private void onChunkLoad(ChunkLoadEvent event) {
		for(Entity entity : event.getChunk().getEntities()) {
			unpackEntity(entity);
		}
	}
	
	private void unpackEntity(Entity entity) {
		if(entity==null || entity.getType()!=EntityType.ARMOR_STAND || entity.getVehicle()!=null || !entity.isInvulnerable()) return;
		ArmorStand armorStand = (ArmorStand) entity;
		ItemStack spawnEgg = armorStand.getEquipment().getHelmet();
		if(spawnEgg==null || spawnEgg.getType()!=Material.BOOK) return;
		try {
			NPCPacker.unpack(armorStand, spawnEgg);
			armorStand.getEquipment().setHelmet(null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	protected static void initialize(World world) {
		if(instances.containsKey(world)) {
			return;
		}
		NPCUnpacker unpacker = new NPCUnpacker(world);
		Bukkit.getPluginManager().registerEvents(unpacker, NPCs.getInstance());
		instances.put(world, unpacker);
		unpacker.initialize();
	}
	
	protected static void stop(World world) {
		NPCUnpacker unpacker = instances.get(world);
		if(unpacker==null) {
			return;
		}
		instances.remove(world);
		HandlerList.unregisterAll(unpacker);
	}
}
