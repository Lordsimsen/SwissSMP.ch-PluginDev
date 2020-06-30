package ch.swisssmp.zvieriplausch;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.custompaintings.CustomPainting;
import ch.swisssmp.custompaintings.CustomPaintings;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.Position;
import ch.swisssmp.zvieriplausch.game.Counter;
import ch.swisssmp.zvieriplausch.game.GamePhase;
import ch.swisssmp.zvieriplausch.game.Level;
import ch.swisssmp.zvieriplausch.game.RecipeDisplay;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ZvieriArena {
	
	private final World world;
	private final UUID arena_id;
	private final String music = ZvieriSound.FRENCH_MUSIC;

	private String name;
	private Position entry;
	private Position kitchen;
	private Position queue;
	private Counter[] counters;
	private Block storage;
	private Block highscoresLectern;
	private Block recipesLectern;
	private Block jukebox;
	private String arenaRegion;
	private RecipeDisplay display;

	private PlayerDataContainer playerDataContainer;

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
		if(dataSection.getKeys(false).contains("entry")) this.entry = dataSection.getPosition("entry");
		if(dataSection.getKeys(false).contains("storageChest")) this.storage = world.getBlockAt(dataSection.getPosition("storageChest").getLocation(this.world));
		if(dataSection.getKeys(false).contains("highscores_lectern")) this.highscoresLectern = world.getBlockAt(dataSection.getPosition("highscores_lectern").getLocation(this.world));
		if(dataSection.getKeys(false).contains("recipes_lectern")) this.recipesLectern = world.getBlockAt(dataSection.getPosition("recipes_lectern").getLocation(this.world));
		if(dataSection.getKeys(false).contains("jukebox")) this.jukebox = world.getBlockAt(dataSection.getPosition("jukebox").getLocation(this.world));

		int i = 1;
		currentCounters = 0;
		counters = new Counter[maxCounters];
		while(dataSection.getKeys(false).contains("counter_" + i)) {
			counters[i-1] = new Counter(dataSection.getPosition("counter_" + i));
			i++;
			currentCounters++;
		}

		this.playerDataContainer = PlayerDataContainer.initialize(this);
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

	public String getMusic() { return music; }

	public Position getEntry() { return entry; }
	
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

	public Lectern getHighscoresLectern(){
		if(highscoresLectern == null) return null;
		if(highscoresLectern.getState() instanceof Lectern) {
			return (Lectern) highscoresLectern.getState();
		}
		return null;
	}

	public Lectern getRecipesLectern(){
		if(recipesLectern == null) return null;
		if(recipesLectern.getState() instanceof Lectern){
			return (Lectern) recipesLectern.getState();
		}
		return null;
	}

	public Chest getStorageChest() {
		if(storage.getState() instanceof Chest) {
			return (Chest) storage.getState();
		}
		return null;
	}

	public Jukebox getJukebox(){
		if(jukebox.getState() instanceof Jukebox) {
			return (Jukebox) jukebox.getState();
		}
		return null;
	}

	public RecipeDisplay getRecipeDisplay(){
		return display;
	}

	public PlayerDataContainer getPlayerDataContainer(){
		return playerDataContainer;
	}

	public boolean isGameRunning(){
		return game.getCurrentPhase() instanceof GamePhase;
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

	public void getHighscoreBook(ItemStack book, UUID playerId){
		if(book.getType() != Material.WRITTEN_BOOK) return;
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		bookMeta.setTitle("Highscores " + this.name);
		bookMeta.setGeneration(BookMeta.Generation.ORIGINAL);
		bookMeta.setAuthor("");
		List<String> pages = new ArrayList<String>();
		for(int i = 1; i <= 5; i++){
			pages.add("Level " + i + ": " + ChatColor.GOLD + playerDataContainer.getHighscoreScore(i) + ChatColor.RESET +
					"\n" + "\n" + "Spieler: " + "\n" + ChatColor.DARK_PURPLE + String.join(", "
					, playerDataContainer.getHighscorePlayers(i)) + ChatColor.RESET + "\n" + "\n" + "Persönlicher Highscore: "
					+ ChatColor.GRAY + playerDataContainer.getPersonalHighscore(playerId, i));
		}
		bookMeta.setPages(pages);
		book.setItemMeta(bookMeta);
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
		if(ZvieriArenen.getAll().isEmpty() || ZvieriArenen.getAll() == null) return null;
		for(ZvieriArena arena : ZvieriArenen.getAll()){
			String region = arena.getArenaRegion();
			if(region == null || region.equals("")) return null;
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
	
	public void setName(String name) {
		this.name = name;
		ZvieriArenen.save(world);
	}

	public void setArenaRegion(String region){
		this.arenaRegion = region;
		ZvieriArenen.save(world);
	}

	public void setEntry(Position entry){
		this.entry = entry;
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

	public void setStorage(Block chest){
		this.storage = chest;
		ZvieriArenen.save(world);
	}

	public void setHighscoresLectern(Block lectern){
		this.highscoresLectern = lectern;
		ZvieriArenen.save(world);
	}

	public void setRecipesLectern(Block lectern){
		this.recipesLectern = lectern;
		ZvieriArenen.save(world);
	}

	public void setJukebox(Block jukebox){
		this.jukebox = jukebox;
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

	public void updatePlayerData(Level level, int score, List<Player> participants){
		for(Player player : participants){
			playerDataContainer.updatePersonalHighscore(level.getLevelNumber(), score, player.getUniqueId());
		}
		int threshhold = level.getThreshhold();
		if(score >= threshhold){
			List<String> playerIds = new ArrayList<>();
			for(Player player : participants){
				playerIds.add(player.getUniqueId().toString());
			}
			playerDataContainer.updateLevelUnlocks(level.getLevelNumber(), playerIds);
		}
		if(score > playerDataContainer.getHighscoreScore(level.getLevelNumber())) {
			playerDataContainer.updateHighscore(level.getLevelNumber(), score, participants);
		}
		playerDataContainer.save();
	}

	public boolean isHighscore(int level, int score){
		int highscore = playerDataContainer.getHighscoreScore(level);
		if(score > highscore) {
			return true;
		}
		return false;
	}

	public boolean canPlayLevel(Level level, Player player){
		int levelNumber = level.getLevelNumber();
		Configuration config = ZvieriGamePlugin.getInstance().getConfig();
		org.bukkit.configuration.ConfigurationSection levels = config.getConfigurationSection("levels");
		if(levels.getBoolean("level_" + levelNumber + ".unlocked")) return true;

		List<String> playersList = playerDataContainer.getUnlockedPlayers(levelNumber);
		if(!playersList.contains(player.getUniqueId().toString())) return false;
		return true;
	}
	
	public ZvieriArenaEditor openEditor(Player player) {
		return ZvieriArenaEditor.open(player, this);
	}
	
	public void save(ConfigurationSection dataSection) {
		dataSection.set("id", this.arena_id.toString());
		dataSection.set("name", this.name);
		dataSection.set("region", this.arenaRegion);

		if(this.entry != null){
			savePosition(dataSection, "entry", this.entry);
		}
		if(this.kitchen != null) {
			savePosition(dataSection, "kitchen", this.kitchen);
		}
		if(this.queue != null) {
			savePosition(dataSection, "queue", this.queue);
		}
		if(this.storage != null){
			savePosition(dataSection, "storageChest", new Position(this.storage.getLocation()));
		}
		if(this.highscoresLectern != null){
			savePosition(dataSection, "highscores_lectern", new Position(this.highscoresLectern.getLocation()));
		}
		if(this.recipesLectern != null){
			savePosition(dataSection, "recipes_lectern", new Position(this.recipesLectern.getLocation()));
		}
		if(this.jukebox != null){
			savePosition(dataSection, "jukebox", new Position(this.jukebox.getLocation()));
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
		ZvieriArena arena = new ZvieriArena(world, dataSection);
		CustomPainting painting = getPainting(arena);
		arena.display = new RecipeDisplay(arena, painting);
		return arena;
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
					this.entry == null ||
					this.kitchen == null ||
					this.queue == null ||
					this.counters == null ||
					this.storage == null ||
					this.jukebox == null ||
					this.recipesLectern == null ||
					this.arenaRegion == null);
	}
	
	public void remove() {
		display.remove();
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
		CustomPainting painting = getPainting(result);
		result.display = new RecipeDisplay(result, painting);
		ZvieriArenen.save(world);
		return result;
	}

	private static CustomPainting getPainting(ZvieriArena arena){
		return CustomPainting.get(arena.getId().toString()).orElse(CustomPaintings.create(arena.getId().toString(), arena.getName(), 3, 2));
	}
	
	public static ZvieriArena create(World world, UUID arena_id, String name) {
		ZvieriArena result = new ZvieriArena(world, arena_id, name);
		ZvieriArenen.put(result.arena_id, result);
		return result;
	}
}
