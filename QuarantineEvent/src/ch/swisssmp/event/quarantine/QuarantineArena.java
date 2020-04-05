package ch.swisssmp.event.quarantine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.Position;

public class QuarantineArena {
	
	public final static String CustomEnum = "QUARANTINE_ARENA";
	
	private final ArenaContainer container;
	private String id;
	private String name;

	private Position survivorStartPosition; //where the survivors start at the beginning of the round
	private Position respawnPosition; //where the infected respawn after being killed
	
	private Position boundingBoxMin; //min point for arena space
	private Position boundingBoxMax; //max point for arena space
	
	private HashMap<QuarantineMaterial,Float> spawnrates = new HashMap<QuarantineMaterial,Float>();
	
	private QuarantineEventInstance runningInstance = null;
	
	public QuarantineArena(ArenaContainer container, String id) {
		this.container = container;
		this.id = id;
	}
	
	private QuarantineArena(ArenaContainer container, ConfigurationSection dataSection) {
		this.container = container;
		this.id = dataSection.getString("id");
		this.name = dataSection.getString("name");

		this.survivorStartPosition = dataSection.getPosition("survivor_start");
		this.respawnPosition = dataSection.getPosition("respawn");
		
		this.boundingBoxMin = dataSection.getPosition("bounding_box_min");
		this.boundingBoxMax = dataSection.getPosition("bounding_box_max");
		
		ConfigurationSection spawnratesSection = dataSection.getConfigurationSection("spawnrates");
		if(spawnratesSection!=null) {
			for(String key : spawnratesSection.getKeys(false)) {
				float value = (float) spawnratesSection.getDouble(key);
				QuarantineMaterial material = QuarantineMaterial.fromId(key);
				if(material==null) continue;
				spawnrates.put(material, value);
			}
		}
	}
	
	public ArenaContainer getContainer() {
		return container;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		this.updateItemStacks();
	}
	
	public List<String> getDescription() {
		return Arrays.asList();
	}
	
	public World getWorld() {
		return container.getWorld();
	}
	
	public Position getSurvivorStart(){
		return this.survivorStartPosition;
	}
	
	public void setSurvivorStart(Position position){
		this.survivorStartPosition = position;
	}
	
	public Position getRespawn(){
		return this.respawnPosition;
	}
	
	public void setRespawn(Position position){
		this.respawnPosition = position;
	}
	
	public Position getBoundingBoxMin(){
		return boundingBoxMin;
	}
	
	public void setBoundingBoxMin(Position position){
		this.boundingBoxMin = position;
	}
	
	public Position getBoundingBoxMax(){
		return boundingBoxMax;
	}
	
	public void setBoundingBoxMax(Position position){
		this.boundingBoxMax = position;
	}
	
	public float getInfectedRatio() {
		return 0.25f;
	}
	
	public boolean contains(Location location) {
		Position min = getBoundingBoxMin();
		Position max = getBoundingBoxMax();
		
		return location.getWorld()==getWorld() && 
				location.getX()>=min.getX() && location.getX()<=max.getX() &&
				location.getY()>=min.getY() && location.getY()<=max.getY() &&
				location.getZ()>=min.getZ() && location.getZ()<=max.getZ();
	}
	
	public void updateItemStacks() {
		ItemStack template = this.getItemStack();
		for(Player player : Bukkit.getOnlinePlayers()) {
			for(ItemStack itemStack : player.getInventory()) {
				if(itemStack==null) continue;
				Optional<QuarantineArena> arena = QuarantineArena.get(itemStack);
				if(!arena.isPresent() || arena.get()!=this) continue;
				itemStack.setItemMeta(template.getItemMeta());
			}
		}
	}
	
	public boolean isReady() {
		return !(
				name==null || 
				name.isEmpty() ||

				this.boundingBoxMin==null || 
				this.boundingBoxMax==null
				);
	}
	
	public QuarantineEventInstance startInstance() {
		if(runningInstance!=null) return runningInstance;
		QuarantineEventInstance result = new QuarantineEventInstance(this);
		this.runningInstance = result;
		result.start();
		return result;
	}
	
	public QuarantineEventInstance getRunningInstance() {
		return runningInstance;
	}
	
	protected void clearRunningInstance() {
		runningInstance = null;
	}
	
	public void remove() {
		container.removeArena(this);
		if(runningInstance==null) return;
		runningInstance.cancel();
	}
	
	public float getSpawnrate(QuarantineMaterial material) {
		return spawnrates.containsKey(material) ? spawnrates.get(material) : material.getDefaultSpawnrate();
	}
	
	public void setSpawnrate(QuarantineMaterial material, float spawnrate) {
		spawnrates.put(material, spawnrate);
	}
	
	public ItemStack getItemStack() {
		String name = getName();
		if(name==null || name.isEmpty()) name = "Unbenannte Arena";
		CustomItemBuilder result = new CustomItemBuilder();
		result.setCustomEnum(CustomEnum);
		result.setDisplayName(name);
		result.setLore(getDescription());
		result.setAmount(1);
		ItemStack itemStack = result.build();
		ItemUtil.setString(itemStack, "quarantine_arena_world", container.getWorld().getName());
		ItemUtil.setString(itemStack, "quarantine_arena_id", id);
		return itemStack;
	}
	
	protected void save(ConfigurationSection dataSection) {
		dataSection.set("id", id);
		dataSection.set("name", this.name);

		if(this.survivorStartPosition!=null){
			savePosition(dataSection, "survivor_start", this.survivorStartPosition);
		}
		if(this.respawnPosition!=null){
			savePosition(dataSection, "respawn", this.respawnPosition);
		}
		if(this.boundingBoxMin!=null){
			savePosition(dataSection, "bounding_box_min", this.boundingBoxMin);
		}
		if(this.boundingBoxMax!=null){
			savePosition(dataSection, "bounding_box_max", this.boundingBoxMax);
		}
		
		ConfigurationSection spawnratesSection = dataSection.createSection("spawnrates");
		for(Entry<QuarantineMaterial,Float> entry : spawnrates.entrySet()) {
			spawnratesSection.set(entry.getKey().toString(), entry.getValue());
		}
	}
	
	public static Optional<QuarantineArena> get(ItemStack itemStack) {
		if(itemStack==null) {
			return Optional.empty();
		}
		String quarantineArenaId = ItemUtil.getString(itemStack, "quarantine_arena_id");
		String quarantineArenaWorld = ItemUtil.getString(itemStack, "quarantine_arena_world");
		if(quarantineArenaId==null || quarantineArenaWorld==null) {
			return Optional.empty();
		}
		World world = Bukkit.getWorld(quarantineArenaWorld);
		if(world==null) {
			return Optional.empty();
		}
		ArenaContainer container = ArenaContainer.get(world);
		return container.getArena(quarantineArenaId);
	}
	
	protected static QuarantineArena load(ArenaContainer container, ConfigurationSection dataSection) {
		if(!dataSection.contains("id")) {
			return null;
		}
		
		return new QuarantineArena(container, dataSection);
	}
	
	private static void savePosition(ConfigurationSection dataSection, String label, Position position){
		ConfigurationSection positionSection = dataSection.createSection(label);
		positionSection.set("x", position.getX());
		positionSection.set("y", position.getY());
		positionSection.set("z", position.getZ());
		positionSection.set("yaw", position.getYaw());
		positionSection.set("pitch", position.getPitch());
	}
}
