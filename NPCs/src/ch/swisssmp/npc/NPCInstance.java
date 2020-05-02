package ch.swisssmp.npc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
	
	public void setDialog(List<String> dialog) {
		JsonObject json = this.getJsonData();
		if(json==null) json = new JsonObject();
		JsonArray dialogArray = new JsonArray();
		for(String s : dialog) {
			dialogArray.add(s);
		}
		json.add("dialog", dialogArray);
		this.setJsonData(json);
	}
	
	public List<String> getDialog(){
		JsonObject json = this.getJsonData();
		if(json==null) return null;
		
		JsonArray dialogArray = json.has("dialog") ? json.get("dialog").getAsJsonArray() : null;
		if(dialogArray==null) return null;
		
		List<String> dialog = new ArrayList<String>();
		for(JsonElement e : dialogArray) {
			dialog.add(e.getAsString());
		}
		return dialog;
	}
	
	public void setJsonData(JsonObject json){
		EntityEquipment equipment = base.getEquipment();
		ItemStack itemStack = equipment.getChestplate();
		ItemUtil.setString(itemStack, "data", json.toString());
		equipment.setChestplate(itemStack);
	}
	
	public JsonObject getJsonData(){
		EntityEquipment equipment = base.getEquipment();
		ItemStack itemStack = equipment.getChestplate();
		if(itemStack==null) return null;
		String dataString = ItemUtil.getString(itemStack, "data");
		if(dataString==null) return null;
		try {
			JsonParser parser = new JsonParser();
			return parser.parse(dataString).getAsJsonObject();
		}
		catch(Exception e) {
			YamlConfiguration result = new YamlConfiguration();
			result.loadFromString(dataString);
			return result.toJson().getAsJsonObject();
		}
	}

	public void teleport(Location location){
		teleport(location, null);
	}

	public void teleport(Location location, Runnable afterTeleportCallback){
		if(location==null || location.getWorld()==null){
			throw new NullPointerException("Invalid Location supplied! "+(location!=null
					? location.getX()+", "+location.getY()+", "+location.getZ()+" ("+(location.getWorld()!=null
						? location.getWorld().getName()
						: "null")+")"
					: "null"));
		}
		base.teleport(location);
		visible.teleport(location);
		Bukkit.getScheduler().runTaskLater(NPCs.getInstance(), ()->{
			base.addPassenger(visible);
			if(afterTeleportCallback!=null) afterTeleportCallback.run();
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
		EntityEquipment equipment = armorStand.getEquipment();
		ItemStack itemStack = createNPCTag(npc_id);
		equipment.setChestplate(itemStack);
		if(visible instanceof Villager) {
			Villager villager = (Villager) visible;
			villager.setProfession(Profession.NITWIT);
			villager.setVillagerType(Villager.Type.PLAINS);
			villager.setVillagerLevel(5);
			villager.setVillagerExperience(Integer.MAX_VALUE);
		}

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
		EntityEquipment equipment = armorStand.getEquipment();
		ItemStack npcTag = equipment.getChestplate();
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
		default: return new Vector(0, -0.9f, 0);
		}
		
	}
}
