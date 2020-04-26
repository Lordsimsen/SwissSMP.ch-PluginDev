package ch.swisssmp.knightstournament;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.world.WorldManager;

public class KnightsArena {
	protected static final String arenaIdProperty = "KnightsArenaId";
	private static List<KnightsArena> loadedArenas = new ArrayList<KnightsArena>();
	private final World world;
	private final UUID id;
	private String name;
	private Location posOne;
	private Location center;
	private Location posTwo;
	private String beginSound;
	private String callSound;
	private String endSound;
	private String arenaRegion;
	
	private Tournament tournament;
	
	private KnightsArena(UUID id, World world){
		this.id = id;
		this.world = world;
		
	}
	
	private void load(JsonObject json) {
		posOne = JsonUtil.getLocation("pos_1", world, json);
		center = JsonUtil.getLocation("center", world, json);
		posTwo = JsonUtil.getLocation("pos_2", world, json);
		beginSound = JsonUtil.getString("begin_sound", json);
		callSound = JsonUtil.getString("call_sound", json);
		endSound = JsonUtil.getString("end_sound", json);	
		name = JsonUtil.getString("name", json);
		arenaRegion = JsonUtil.getString("arena_region", json);

	}
	
	public UUID getId() {
		return id;
	}
	
	public World getWorld() {
		return world;
	}
	
	public static Collection<KnightsArena> getLoadedArenas(){
		return loadedArenas;
	}
	
	public String getName(){
		return this.name;
	}
	
	public Location getPosOne(){
		return this.posOne;
	}
	
	public Location getCenter() {
		return this.center;
	}
	
	public Location getPosTwo(){
		return this.posTwo;
	}
	
	public String getBeginSound() {
		return beginSound;
	}
	
	public String getCallSound() {
		return callSound;
	}
	
	public String getEndSound() {
		return endSound;
	}

	public String getArenaRegion(){return arenaRegion;}

	public boolean isReady(){
		return name!=null
				&& arenaRegion!=null
				&& this.posOne!=null
				&& this.posTwo!=null
				&& this.center!=null;
	}
	
	public void playBeginSound(){
		this.center.getWorld().playSound(center, this.beginSound, 50, 1);
	}
	
	public void playCallSound(){
		this.center.getWorld().playSound(center, this.callSound, 50, 1);
	}
	
	public void playEndSound(){
		this.center.getWorld().playSound(center, this.endSound, 50, 1);
	}

	protected void runTournament(Tournament tournament){
		this.tournament = tournament;
	}
	
	public Tournament getTournament(){
		return this.tournament;
	}
	
	private List<String> getDescription(){
		List<String> description = new ArrayList<String>();
		description.add("Ritterspiele-Arena " + name);
		return description;
	}
	
	public static KnightsArena get(UUID arena_id) {
		for(KnightsArena arena : loadedArenas) {
			if(arena.getId().equals(arena_id)) return arena;
		}
		return null;
	}
	
	public static KnightsArena get(ItemStack tokenStack){
		String uuidString = ItemUtil.getString(tokenStack, arenaIdProperty);
		if(uuidString == null) {
			return null;
		}
		UUID arena_id = UUID.fromString(uuidString);
		if(arena_id == null) {
			return null;
		}
		return get(arena_id);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setPosOne(Location location) {
		posOne = location;
	}
	
	public void setCenter(Location location) {
		center = location;
	}
	
	public void setPosTwo(Location location) {
		posTwo = location;
	}
	
	public void setBeginSound(String beginSound) {
		this.beginSound = beginSound;
	}
	
	public void setCallSound(String callSound) {
		this.callSound = callSound;
	}
	
	public void setEndSound(String endSound) {
		this.endSound = endSound;
	}

	public void setArenaRegion(String arenaRegion) {
		this.arenaRegion = arenaRegion;
	}
	
	public ItemStack getTokenStack() {
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder("KNIGHTS_TOURNAMENT_ARENA");
		itemBuilder.setDisplayName(ChatColor.AQUA + this.getName());
		itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		List<String> description = this.getDescription();
		itemBuilder.setLore(description);
		ItemStack result = itemBuilder.build();
		ItemUtil.setString(result, arenaIdProperty, id.toString());
		return result;
	}
	
	public void updateTokens() {
		ItemStack tokenStack = this.getTokenStack();
		for (Player p : Bukkit.getOnlinePlayers()) {
			for(ItemStack itemStack : p.getInventory()) {
				if (itemStack == null) {
					continue;
				}
				KnightsArena arena = KnightsArena.get(itemStack);
				if(arena != this) {
					continue;
				}
				itemStack.setItemMeta(tokenStack.getItemMeta());
			}
		}
	}
	
	public KnightsArenaEditor openEditor(Player player) {
		return KnightsArenaEditor.open(player, this);
	}
	
	private JsonObject save() {
		
		JsonObject json = new JsonObject();
			
		JsonUtil.set("id", id, json);
		if(posOne!=null)JsonUtil.set("pos_1", posOne, json);
		if(center!=null)JsonUtil.set("center", center, json);
		if(posTwo!=null)JsonUtil.set("pos_2", posTwo, json);
		if(beginSound!=null)JsonUtil.set("begin_sound", beginSound, json);
		if(callSound!=null)JsonUtil.set("call_sound", callSound, json);
		if(endSound!=null)JsonUtil.set("end_sound", endSound, json);	
		if(name!=null)JsonUtil.set("name", name, json);
		if(arenaRegion!=null)JsonUtil.set("arena_region", arenaRegion, json);
		
		return json;
	}
	
	public static boolean save(World world) {
		Collection<KnightsArena> arenas = loadedArenas
				.stream()
				.filter(a->a.world==world)
				.collect(Collectors.toList());
		
		JsonArray arenasArray = new JsonArray();
		for(KnightsArena arena : arenas) {
			arenasArray.add(arena.save());
		}
		JsonObject json = new JsonObject();
		json.add("arenas", arenasArray);
		File file = getArenasFile(world);
		
		return JsonUtil.save(file, json);
	}
	
	private static KnightsArena load(World world, JsonObject json) {
		UUID id = JsonUtil.getUUID("id", json);
		if(id == null) return null;
		Optional<KnightsArena> existing = KnightsArena.get(world, id);
		if(existing.isPresent()) return existing.get();

		KnightsArena result = new KnightsArena(id, world);
		result.load(json);
		KnightsArena.loadedArenas.add(result);
		return result;
	}
	
	private static File getArenasFile(World world) {
		File directory = WorldManager.getPluginDirectory(KnightsTournamentPlugin.getInstance(), world);
		File file = new File(directory, "arenas.json");
		
		return file;
	}
	
	protected static void load(World world){
		unload(world);
		File file = getArenasFile(world);
		if(!file.exists()) return;
		JsonObject json = JsonUtil.parse(file);
		if(json==null) return;
		JsonArray arenasArray = json.get("arenas").getAsJsonArray();
		
		Collection<KnightsArena> arenas = new ArrayList<KnightsArena>();
		for(JsonElement element : arenasArray) {
			if(!element.isJsonObject()) continue;
			KnightsArena arena = load(world, element.getAsJsonObject());
			if(arena == null || loadedArenas.contains(arena)) continue;
			arenas.add(arena);
		}
		loadedArenas.addAll(arenas);
	}
	
	protected static void unload(World world) {
		Collection<KnightsArena> arenas = loadedArenas
				.stream()
				.filter(a->a.world==world)
				.collect(Collectors.toList());
		for(KnightsArena arena : arenas) {
			if(arena.tournament==null) continue;
			arena.tournament.finish();
		}
		loadedArenas.removeAll(arenas);

		Bukkit.getLogger().info("Arenas: " + loadedArenas.size());
	}
	
	public static Optional<KnightsArena> get(World world, String name){
		return loadedArenas
				.stream()
				.filter(a->a.world==world && a.name.equalsIgnoreCase(name))
				.findAny();
	}

	public static Optional<KnightsArena> get(World world, UUID id){
		return loadedArenas
				.stream()
				.filter(a->a.world==world && a.id.equals(id))
				.findAny();
	}
	
	public static KnightsArena create(World world) {
		UUID id = UUID.randomUUID();
		KnightsArena arena = new KnightsArena(id, world);
		arena.setName("Unbenannte Arena");
		loadedArenas.add(arena);
		
		return arena;
	}

	
	public void remove(KnightsArena arena) {
		if(arena.tournament!=null) tournament.finish();
		loadedArenas.remove(arena);
		save(arena.getWorld());
	}
}
