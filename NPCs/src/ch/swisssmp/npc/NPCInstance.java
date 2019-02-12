package ch.swisssmp.npc;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.YamlConfiguration;

public class NPCInstance {
	private final UUID npc_id;
	private final Entity visible;
	private final ArmorStand base;
	
	private NPCInstance(UUID npc_id, Entity visible, ArmorStand base){
		this.npc_id = npc_id;
		this.visible = visible;
		this.base = base;
	}
	
	public UUID getNPCId(){
		return npc_id;
	}
	
	public void setIdentifier(String identifier){
		this.base.setCustomNameVisible(true);
		this.base.setCustomName(identifier);
		this.base.setCustomNameVisible(false);
	}
	
	public String getIdentifier(){
		this.base.setCustomNameVisible(true);
		String result = this.base.getCustomName();
		this.base.setCustomNameVisible(false);
		return result;
	}
	
	public void setName(String displayName){
		visible.setCustomName(displayName);
	}
	
	public String getName(){
		return visible.getCustomName();
	}
	
	public void setNameVisible(boolean visible){
		this.visible.setCustomNameVisible(visible);
	}
	
	public boolean isNameVisible(){
		return this.visible.isCustomNameVisible();
	}
	
	public void setSilent(boolean silent){
		this.visible.setSilent(silent);
	}
	
	public boolean isSilent(){
		return this.visible.isSilent();
	}
	
	public void setYamlConfiguration(YamlConfiguration yamlConfiguration){
		ItemStack itemStack = base.getChestplate();
		ItemUtil.setString(itemStack, "data", yamlConfiguration.saveToString());
		base.setChestplate(itemStack);
	}
	
	public YamlConfiguration getYamlConfiguration(){
		ItemStack itemStack = base.getChestplate();
		if(itemStack==null) return null;
		String dataString = ItemUtil.getString(itemStack, "data");
		if(dataString==null) return null;
		YamlConfiguration result = new YamlConfiguration();
		result.loadFromString(dataString);
		return result;
	}
	
	public void teleport(Location location){
		if(location==null || location.getWorld()==null) return;
		base.teleport(location);
		visible.teleport(location);
		Bukkit.getScheduler().runTaskLater(NPCs.getInstance(), ()->{
			base.addPassenger(visible);
		}, 1L);
	}
	
	public void remove(){
		this.visible.remove();
		this.base.remove();
	}
	
	public Entity getEntity(){
		return visible;
	}
	
	public ArmorStand getBase(){
		return base;
	}
	
	public static NPCInstance create(EntityType entityType, Location location){
		UUID npc_id = UUID.randomUUID();
		ArmorStand armorStand = createBase(entityType, location);
		Entity visible = createVisible(location, entityType);
		armorStand.addPassenger(visible);
		ItemStack itemStack = createNPCTag(npc_id);
		armorStand.setChestplate(itemStack);
		return new NPCInstance(npc_id, visible, armorStand);
	}
	
	public static NPCInstance get(Entity entity){
		if(entity.getVehicle()!=null){
			return get(entity.getVehicle());
		}
		if(!(entity instanceof ArmorStand) || entity.getPassengers().size()==0){
			return null;
		}
		ArmorStand armorStand = (ArmorStand) entity;
		ItemStack npcTag = armorStand.getChestplate();
		String npcIdString = ItemUtil.getString(npcTag, "npc_id");
		if(npcIdString==null){
			return null;
		}
		UUID npc_id = UUID.fromString(npcIdString);
		Entity visible = armorStand.getPassengers().get(0);
		return new NPCInstance(npc_id, visible, armorStand);
	}
	
	private static ArmorStand createBase(EntityType entityType, Location location){
		Location spawnLocation = location.clone().add(getBaseOffset(entityType));
		ArmorStand result = (ArmorStand) location.getWorld().spawnEntity(spawnLocation, EntityType.ARMOR_STAND);
		result.setInvulnerable(true);
		result.setVisible(false);
		result.setSmall(true);
		result.setSilent(true);
		result.setGravity(false);
		return result;
	}
	
	private static Entity createVisible(Location location, EntityType entityType){
		Entity result = location.getWorld().spawnEntity(location, entityType);
		result.setInvulnerable(true);
		result.setGravity(false);
		if(result instanceof LivingEntity) ((LivingEntity)result).setRemoveWhenFarAway(false);
		return result;
	}
	
	private static ItemStack createNPCTag(UUID npc_id){
		ItemStack itemStack = new ItemStack(Material.BARRIER);
		ItemUtil.setString(itemStack, "npc_id", npc_id.toString());
		return itemStack;
	}
	
	private static Vector getBaseOffset(EntityType entityType){
		switch(entityType){
		case VILLAGER: return new Vector(0,-0.75,0);
		default: return new Vector(0, 0.85f, 0);
		}
		
	}
}
