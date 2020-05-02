package ch.swisssmp.zvierigame;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.Position;
import ch.swisssmp.zvierigame.game.Counter;
import ch.swisssmp.zvierigame.game.Level;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ZvieriArena {
	
	private final World world;
	private final UUID arena_id;

	private String name;
	private Position kitchen;
	private Position queue;
	private Counter[] counters;
	private Position storage;
	private Chest storageChest;
	
	private ZvieriGame game;
	private boolean gameRunning;
	private boolean gamePreparing;
	
	private final int maxCounters = 5;
	private int currentCounters;
	
	private ItemStack[] allIngredients;
	
	private ZvieriArena(World world, UUID arena_id, String name) {
		this.world = world;
		this.arena_id = arena_id;
		this.name = name;
		counters = new Counter[maxCounters];
		currentCounters = 0;
	}

	private ZvieriArena(World world, ConfigurationSection dataSection) {
		this.world = world;
		this.arena_id = UUID.fromString(dataSection.getString("id"));
		this.name = dataSection.getString("name");
		if(dataSection.getKeys(false).contains("kitchen")) this.kitchen = dataSection.getPosition("kitchen");
		if(dataSection.getKeys(false).contains("queue")) this.queue = dataSection.getPosition("queue");
		
		int i = 1;
		currentCounters = 0;
		counters = new Counter[maxCounters];
		while(dataSection.getKeys(false).contains("counter_" + i)) {
			counters[i-1] = new Counter(dataSection.getPosition("counter_" + i));
			i++;
			currentCounters++;
		}
		if(dataSection.getKeys(false).contains("storage")) {
			this.storage = dataSection.getPosition("storage");
			BlockState storageChestState = this.getStorage().getLocation(world).getBlock().getState();
			if (storageChestState instanceof Chest) {
				storageChest = (Chest) storageChestState;
			}
		}
	}
	
	public World getWorld() {
		return world;
	}
	
	public UUID getId() {
		return arena_id;
	}
	
	public String getName() {
		return name;
	}
	
	public Position getKitchen() {
		return kitchen;
	}
	
	public Position getQueue() {
		return queue;
	}
	
	public int getMaxCounters() {
		return maxCounters;
	}
	
	public Position getCounter(int i) {
		if (counters.length < i || counters == null) {
			return null;
		}
		return counters[i].getPosition();
	}
	
	public Counter[] getCounters() {
		return counters;
	}
	
	public int getCurrentCounters() {
		return currentCounters;
	}
	
	public Position getStorage() {
		return storage;
	}
	
	public Chest getStorageChest() {
		return storageChest;
	}
	
	public ItemStack[] getIngredients() {
		return allIngredients;
	}
	
	public ItemStack getIngredient(int i) {
		return allIngredients[i];
	}

	public boolean isGameRunning(){
		return gameRunning;
	}

	public boolean isGamePreparing(){
		return gamePreparing;
	}
	
	public ItemStack getTokenStack() {
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder("ZVIERI_ARENA"); //lieutenant
		itemBuilder.setDisplayName(ChatColor.AQUA + "Zvieriarena");
		itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemBuilder.setLore(this.getDescription());
		ItemStack result = itemBuilder.build();
		ItemUtil.setString(result, "zvieriarena", arena_id.toString());
		return result;
	}
	
	public ItemStack getStartNowItem() {
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder("EMBARK_NOW");		
		itemBuilder.setDisplayName(ChatColor.AQUA + this.name);
		itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		List<String> description = new ArrayList<String>();
		description.add(ChatColor.YELLOW+"Klicke um das Spiel");
		description.add(ChatColor.YELLOW+"sofort zu starten");
		itemBuilder.setLore(description);
		ItemStack result = itemBuilder.build();
		ItemUtil.setString(result, "arena", arena_id.toString());
		return result;
	}
	
	public List<String> getDescription() {
		List<String> description = new ArrayList<String>();
		description.add("Zvieri-Game Arena " + ChatColor.BLUE + name);
		return  description;
	}

	public ZvieriGame getGame(){
		return game;
	}

	public boolean isParticipant(Player player){
		return game.getParticipants().contains(player);
	}
	
	public static ZvieriArena get(UUID arena_id) {
		if(ZvieriArenen.containsKey(arena_id)) {
			return ZvieriArenen.get(arena_id);
		}
		return null;
	}
	
	public static ZvieriArena get(String name, boolean exactMatch){
		for(ZvieriArena arena : ZvieriArenen.getAll()){
			if(exactMatch && !arena.getName().toLowerCase().equals(name.toLowerCase())) {
				continue;
			}
			if(arena.getName().toLowerCase().contains(name.toLowerCase())) {
				return arena;
			}
		}
		return null;
	}
	
	public static ZvieriArena get(ItemStack tokenStack){
		String uuidString = ItemUtil.getString(tokenStack, "zvieriarena");
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
		ZvieriArenen.save(world);
	}
	
	public void setKitchen(Position kitchen) {
		this.kitchen = kitchen;
		ZvieriArenen.save(world);
	}
	
	public void setQueue(Position queue) {
		this.queue = queue;
		ZvieriArenen.save(world);
	}
	
	public void setStorage(Position storage) {
		this.storage = storage;
		ZvieriArenen.save(world);
	}
	
	public void setChef(NPCInstance npc) {
		JsonObject json = npc.getJsonData();
		if(json == null) {
			json = new JsonObject();
		}
		json.addProperty("zvieriarena", this.arena_id.toString());
		json.addProperty("name", npc.getName());
		npc.setJsonData(json);
		npc.setNameVisible(true);
	}
	
	public void addCounter(Position position) {
		for (int i = 0;(i <= currentCounters) && (i < maxCounters); i++) {
			if (counters[i] != null) {
			} else {
				counters[i] = new Counter(position);
			}
		}
		currentCounters++;
		ZvieriArenen.save(world);
	}

	public void clearCounters(){
		for(int i = 0; i < maxCounters; i++){
			counters[i] = null;
		}
		currentCounters = 0;
		ZvieriArenen.save(world);
	}
	
	public void updateTokens() {
		ItemStack tokenStack = this.getTokenStack();
		for (Player p : Bukkit.getOnlinePlayers()) {
			for(ItemStack itemStack : p.getInventory()) {
				if (itemStack == null) {
					continue;
				}
				ZvieriArena zvieriArena = ZvieriArena.get(itemStack);
				if(zvieriArena != this) {
					continue;
				}
				itemStack.setItemMeta(tokenStack.getItemMeta());
			}
		}
	}
	
	public ZvieriArenaEditor openEditor(Player player) {
		return ZvieriArenaEditor.open(player, this);
	}
	
	public void save(ConfigurationSection dataSection) {
		dataSection.set("id", this.arena_id.toString());
		dataSection.set("name", this.name);
		
		if(this.kitchen != null) {
			savePosition(dataSection, "kitchen", this.kitchen);
		}
		if(this.queue != null) {
			savePosition(dataSection, "queue", this.queue);
		}
		if(this.storage != null) {
			savePosition(dataSection, "storage", this.storage);			
		}
		if(this.counters != null) {
			for(int i = 1; i <= counters.length; i++) {
				if(counters[i-1] != null) {
					savePosition(dataSection, "counter_" + i, this.counters[i-1].getPosition());
				}
			}
		}		
	}
	
	public static ZvieriArena load(World world, ConfigurationSection dataSection) {
		return new ZvieriArena(world, dataSection);
	}
	
	public void unload() {
		ZvieriArenen.remove(arena_id);
	}

	public void prepareGame(Level level){
		if(this.game != null){
			return;
		}
		gamePreparing = true;
		game = ZvieriGame.prepare(this, level);
	}

	public void cancelGame() {
		this.game.cancel();
		this.game = null;
		gamePreparing = false;
		gameRunning = false;
	}
	
	public boolean isSetupComplete() {
		return !(
					this.kitchen == null ||
					this.queue == null ||
					this.counters == null ||
					this.storage == null);				
	}
	
	public void remove() {
		ZvieriArenen.remove(arena_id);
		ZvieriArenen.save(world);
	}
	
	private static void savePosition(ConfigurationSection dataSection, String label, Position position){
		ConfigurationSection positionSection = dataSection.createSection(label);
		positionSection.set("x", position.getX());
		positionSection.set("y", position.getY());
		positionSection.set("z", position.getZ());
		positionSection.set("yaw", position.getYaw());
		positionSection.set("pitch", position.getPitch());
	}
	
	public static ZvieriArena create(World world, String name) {
		ZvieriArena existing = ZvieriArena.get(name, true);
		if(existing != null) {
			return existing;
		}
		UUID newArenaId = UUID.randomUUID();
		ZvieriArena result = ZvieriArena.create(world, newArenaId, name);
		ZvieriArenen.save(world);
		return result;
	}
	
	public static ZvieriArena create(World world, UUID arena_id, String name) {
		ZvieriArena result = new ZvieriArena(world, arena_id, name);
		ZvieriArenen.put(result.arena_id, result);
		return result;
	}


//public LevelSelectionView openLevelSelection(Player p) {
//return LevelSelectionView.open(p, this);
//}

//public void prepareGame(ZvieriArena arena) {
//	if(this.activeGame != null) {
//		//To-Do (?)
//	}
//	this.activeGame = Game.prepare(this, arena);
//	// addOnEmbarkListener?
//}
	
}
