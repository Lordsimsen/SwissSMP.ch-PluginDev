package ch.swisssmp.zvierigame;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.Position;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.zvierigame.game.Counter;
import ch.swisssmp.zvierigame.game.Level;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Lectern;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;
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
	private String arenaRegion;
	private Position lecternPosition;
	private BlockState lectern;
	
	private ZvieriGame game;
	private boolean gameRunning;
	private boolean gamePreparing;
	
	private final int maxCounters = 5;
	private int currentCounters;

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
		this.arenaRegion = dataSection.getString("region");
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
		if(dataSection.getKeys(false).contains("lectern")){
			this.lecternPosition = dataSection.getPosition("lectern");
			this.lectern = lecternPosition.getLocation(world).getBlock().getState();
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

	public BlockState getLectern(){
		return lectern;
	}

	public Position getLecternPosition() { return lecternPosition; }
	
	public Chest getStorageChest() {
		return storageChest;
	}

	public boolean isGameRunning(){
		return gameRunning;
	}

	public boolean isGamePreparing(){
		return gamePreparing;
	}
	
	public ItemStack getTokenStack() {
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder("ZVIERI_ARENA");
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

	public String getArenaRegion(){
		return this.arenaRegion;
	}

	public void getHighscore(ItemStack book){
		if(book.getType() != Material.WRITTEN_BOOK) return;
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		bookMeta.setTitle("Highscores " + this.name);
		bookMeta.setGeneration(BookMeta.Generation.ORIGINAL);
		bookMeta.setAuthor("");
		List<String> pages = new ArrayList<String>();
		for(int i = 1; i <= 5; i++){
			pages.add("Level " + i + ": " + getHighscore(i) + "\n" + "\n" + "Spieler: " + "\n" + String.join(", ", getHighscorePlayers(i)));
		}
		bookMeta.setPages(pages);
		book.setItemMeta(bookMeta);
	}

	public int getHighscore(int level){
		int highscore = 0;
		try {
			highscore = this.getConfigurationSection().getConfigurationSection("highscores").getConfigurationSection("level_" + level).getInt("highscore");
		} catch (NullPointerException e){ }
		return highscore;
	}

	public List<String> getHighscorePlayers(int level) {
		List<String> highscorePlayers = new ArrayList<String>();
		try {
			ConfigurationSection playersSection = this.getConfigurationSection().getConfigurationSection("highscores")
					.getConfigurationSection("level_" + level).getConfigurationSection("players");
			int i = 1;
			while(playersSection.get("player_" + i) != null){
				highscorePlayers.add(playersSection.getString("player_" + i));
				i++;
			}
		} catch(NullPointerException e){
			highscorePlayers.add(null);
		}
		return highscorePlayers;
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

	public static ZvieriArena get(String arenaRegion){
		for(ZvieriArena arena : ZvieriArenen.getAll()){
			if(!arena.getArenaRegion().equalsIgnoreCase(arenaRegion)) continue;
			return arena;
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

	public ConfigurationSection getConfigurationSection(){
		YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new File(world.getWorldFolder(), "plugindata/ZvieriGame/arenen.yml"));
		if (yamlConfiguration.contains("arenen")) {
			ConfigurationSection arenenSection = yamlConfiguration.getConfigurationSection("arenen");
			for (String key : arenenSection.getKeys(false)) {
				ConfigurationSection arenaSection = arenenSection.getConfigurationSection(key);
				if (UUID.fromString(arenaSection.getString("id")).equals(this.arena_id)) {
					return arenaSection;
				}
			}
		}
		return null;
	}
	
	public void setName(String name) {
		this.name = name;
		ZvieriArenen.save(world);
	}

	public void setArenaRegion(String region){
		this.arenaRegion = region;
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

	public void setLectern(Position lecternPosition){
		this.lecternPosition = lecternPosition;
		ZvieriArenen.save(world);
	}
	
	public void setChef(NPCInstance npc) {
		JsonObject json = npc.getJsonData();
		if(json == null) {
			json = new JsonObject();
		}

		npc.setName("Chef de cuisine");
		npc.setIdentifier("chef");
		json.addProperty("zvieriarena", this.arena_id.toString());
		json.addProperty("name", npc.getName());
		npc.setJsonData(json);
		Villager villager = (Villager) npc.getEntity();
		villager.setProfession(Villager.Profession.BUTCHER);
		npc.setNameVisible(true);
	}

	public void setLogisticsNPC(NPCInstance npc){
		JsonObject json = npc.getJsonData();
		if(json == null) {
			json = new JsonObject();
		}
		npc.setName("Logistiker");
		npc.setIdentifier("logistics");
		json.addProperty("zvieriarena", this.arena_id.toString());
		json.addProperty("logistics", true);
		npc.setJsonData(json);
		Villager villager = (Villager) npc.getEntity();
		villager.setProfession(Villager.Profession.CARTOGRAPHER);
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

	public boolean updateHighscore(int level, int score, List<Player> participants) {
		Bukkit.getLogger().info("updating highscore for level " + level);
		File dataFile = new File(world.getWorldFolder(), "plugindata/ZvieriGame/arenen.yml");
		YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(dataFile);
		if (yamlConfiguration.contains("arenen")) {
			ConfigurationSection arenenSection = yamlConfiguration.getConfigurationSection("arenen");
			ConfigurationSection arenaSection = null;
			for (String key : arenenSection.getKeys(false)) {
				ConfigurationSection section = arenenSection.getConfigurationSection(key);
				if (!UUID.fromString(section.getString("id")).equals(this.arena_id)) {
					continue;
				}
				arenaSection = section;
				break;
			}
			if (arenaSection == null) arenaSection = arenenSection.createSection("arena_" + arenenSection.getKeys(false).size());
			if (!arenaSection.getKeys(false).contains("highscores")) {
				arenaSection.createSection("highscores");
			}
			ConfigurationSection highscoreSection = arenaSection.getConfigurationSection("highscores");
			if (!highscoreSection.getKeys(false).contains("level_" + level)) {
				highscoreSection.createSection("level_" + level);
			}
			ConfigurationSection levelSection = highscoreSection.getConfigurationSection("level_" + level);
			int highscore = levelSection.getInt("highscore");
			if (score > highscore) {
				Bukkit.getLogger().info("new highscore for level " + level);
				highscore = score;
				levelSection.set("highscore", highscore);
				if(levelSection.getConfigurationSection("players") != null) levelSection.remove("players");
				ConfigurationSection playerSection = levelSection.createSection("players");
				for(int i = 1; i <= participants.size(); i++){
					playerSection.set("player_" + i , participants.get(i-1).getDisplayName());
				}
				yamlConfiguration.save(dataFile);
				return true;
			}
			return false;
		}
		return false;
	}
	
	public ZvieriArenaEditor openEditor(Player player) {
		return ZvieriArenaEditor.open(player, this);
	}
	
	public void save(ConfigurationSection dataSection) {
		dataSection.set("id", this.arena_id.toString());
		dataSection.set("name", this.name);
		dataSection.set("region", this.arenaRegion);
		
		if(this.kitchen != null) {
			savePosition(dataSection, "kitchen", this.kitchen);
		}
		if(this.queue != null) {
			savePosition(dataSection, "queue", this.queue);
		}
		if(this.storage != null) {
			savePosition(dataSection, "storage", this.storage);			
		}
		if(this.lecternPosition != null){
			savePosition(dataSection, "lectern", this.lecternPosition);
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

	public void prepareGame(Level level){
		if(this.game != null){
			return;
		}
		gamePreparing = true;
		game = ZvieriGame.prepare(this, level);
	}

	public void endGame() {
		this.game = null;
		gamePreparing = false;
		gameRunning = false;
	}
	
	public boolean isSetupComplete() {
		return !(
					this.kitchen == null ||
					this.queue == null ||
					this.counters == null ||
					this.storage == null) ||
					this.arenaRegion == null ||
					!(this.storage.getLocation(world).getBlock() instanceof Chest);
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
}
