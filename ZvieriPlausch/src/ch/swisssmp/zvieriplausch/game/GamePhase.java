package ch.swisssmp.zvieriplausch.game;

import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.Random;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zvieriplausch.ZvieriArena;
import ch.swisssmp.zvieriplausch.ZvieriGame;
import ch.swisssmp.zvieriplausch.ZvieriGamePlugin;
import ch.swisssmp.zvieriplausch.ZvieriSound;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.LecternInventory;
import org.bukkit.scoreboard.*;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GamePhase extends Phase { // Unterschied runnable/Bukkitrunnable ? Ausführungsgeschwindigkeit bei bukkitrunnable 20/sekunde?

	private final ZvieriGame game;
	private final ZvieriArena arena;
	private final Level level;

	private long time = 0L;
	private int score;
	private Chest storageChest;
	private HashMap<String,ItemStack> ingredients;
	private ArrayList<Client> inQueue;
	private ArrayList<Counter> counters;
	private ScoreboardManager scoreBoardManager;
	private Scoreboard scoreboard;
	private Objective objective;
	private boolean restockAllowed = false;

	private GamePhaseListener gamePhaseListener;

	private Random random = new Random();
	
	public GamePhase(ZvieriGame game) {
		super(game);

		this.game = game;
		this.arena = game.getArena();
		this.level = game.getLevel();

		this.storageChest = game.getArena().getStorageChest();
		this.ingredients = game.getLevel().getIngredients();

		this.scoreBoardManager = Bukkit.getScoreboardManager();
		this.scoreboard = scoreBoardManager.getNewScoreboard();

		this.inQueue = new ArrayList<Client>();
		this.counters = new ArrayList<Counter>();
		for(int i = 0; i < game.getArena().getCounters().length; i++) {
			this.counters.add(game.getArena().getCounters()[i]);
		}
	}

	@Override
	public void initialize() {
		restockAllowed = true;
		initializeStorage();
		setRecipesBook();
		for (Player player : game.getParticipants()) {
			player.teleport(arena.getKitchen().getLocation(arena.getWorld()));
			player.sendTitle(ChatColor.GREEN + "Au travail!", "", 5, 30, 5);
		}
		gamePhaseListener = new GamePhaseListener(this);
		Bukkit.getPluginManager().registerEvents(gamePhaseListener, ZvieriGamePlugin.getInstance());
		kickPlayersOutOfArena();
		initializeBrewingStands();
		this.objective = scoreboard.registerNewObjective("scoreboard", "dummy", ChatColor.YELLOW + "Saldo");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		score = 20;
		displayScore();
		playMusic();
	}

	private void playMusic(){
		if(arena.getMusic() == null || arena.getJukebox() == null) {
			return;
		}
		arena.getWorld().playSound(arena.getJukebox().getLocation(), arena.getMusic(), SoundCategory.RECORDS, 8f, 1f);
	}

	private void initializeBrewingStands(){
		World world = arena.getWorld();
		ProtectedRegion region = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getRegion(arena.getArenaRegion());
		BlockVector3 min = region.getMinimumPoint();
		BlockVector3 max = region.getMaximumPoint();
		for(int i = min.getBlockX(); i <= max.getBlockX(); i++){
			for(int j = min.getBlockY(); j <= max.getBlockY(); j++){
				for(int k = min.getBlockZ(); k <= max.getBlockZ(); k++){
					Block block = arena.getWorld().getBlockAt(i, j, k);
					if(block.getType() == Material.BREWING_STAND){
						((BrewingStand) block.getState()).getInventory().clear();
						ItemStack fuel = new ItemStack(Material.BLAZE_POWDER);
						fuel.setAmount(8);
						ItemUtil.setBoolean(fuel, "zvieriGameItem", true);
						((BrewingStand) block.getState()).getInventory().setFuel(new ItemStack(fuel));
					}
				}
			}
		}
	}

	protected void kickPlayersOutOfArena(){
		ProtectedRegion region = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(arena.getWorld())).getRegion(arena.getArenaRegion());
		BlockVector3 min = region.getMinimumPoint();
		BlockVector3 max = region.getMaximumPoint();
		BoundingBox arenaBox = new BoundingBox(min.getX(),min.getY(),min.getZ(),max.getX(),max.getY(),max.getZ());
		for(Entity entity : arena.getWorld().getNearbyEntities(arenaBox)){
			if(!(entity instanceof Player)) continue;
			Player player = (Player) entity;
			if(game.getParticipants().contains(player)) continue;
			player.teleport(arena.getEntry().getLocation(arena.getWorld()));
		}
	}

	private void initializeStorage(){
		storageChest.getBlockInventory().clear();
		ConfigurationSection ingredientsSection = ZvieriGamePlugin.getInstance().getConfig().getConfigurationSection("ingredients");

		ItemStack[] result = new ItemStack[ingredients.size()];
		int j = 0;
		for(String key : ingredients.keySet()){
			result[j] = ingredients.get(key);
			result[j].setAmount(ingredientsSection.getInt(key + ".initialAmount"));
			ItemUtil.setBoolean(result[j], "zvieriGameItem", true);
			j++;
		}
		storageChest.getBlockInventory().setContents(result);
	}

	private void setRecipesBook(){
		Lectern lectern = arena.getRecipesLectern();
		LecternInventory inventory = (LecternInventory) lectern.getInventory();
		ItemStack book = arena.getRecipeDisplay().getItemStack();
		Bukkit.getLogger().info("" + ItemUtil.serialize(book));
		inventory.setItem(0, book);
	}

	private void removeRecipeBook(){
		Lectern lectern = arena.getRecipesLectern();
		LecternInventory inventory = (LecternInventory) lectern.getInventory();
		inventory.clear();
	}

	@Override
	public void run() {
		time++; //time in ticks
		if(time % (117*20) == 0) playMusic(); //only works with FRENCH MUSIC
		int remaining = Mathf.ceilToInt(level.duration - time/20);
		if(remaining < 21) restockAllowed = false;
		sendCountdown(remaining);
		Client newClient = level.spawnClient(time, arena.getQueue().getLocation(arena.getWorld()));
		if(newClient != null) inQueue.add(newClient);
		for(Counter counter : counters) {
			if(!counter.isOccupied()) {
				if(!inQueue.isEmpty()) {
					Client client = inQueue.get(0);
					counter.setClient(client);
					inQueue.remove(0);
				}
			} else{
				counter.getClient().increaseWaitingTime();
			}
		}
		for(Client client : inQueue){
			client.increaseWaitingTime();
		}
		if(time - (level.duration*20) > 0) {
			setCompleted();
		}
	}

	protected ZvieriArena getArena(){
		return this.arena;
	}

	protected Level getLevel(){
		return this.level;
	}

	protected List<Counter> getCounters(){
		return new ArrayList<Counter>(counters);
	}

	protected List<Client> getInQueue(){
		return new ArrayList<Client>(inQueue);
	}

	protected boolean isRestockAllowed(){
		return restockAllowed;
	}

	protected void subtractFromScore(int i){
		score -= i;
	}

	protected void addToScore(int i){
		score += i;
	}

	protected int getScore(){
		return score;
	}

	protected void resetScore(){
		score = 0;
	}
	
	private void sendCountdown(int remaining){
		if(remaining == level.duration) return;
		if (time % (30*20) == 0){
			for(Player player : game.getParticipants()) {
				SwissSMPler.get(player).sendTitle("", ChatColor.YELLOW + "Noch " + ChatColor.AQUA + remaining + ChatColor.YELLOW + " Sekunden");
			}
		}
		if((level.duration*20) - (time) < (89*20)) {
			if(time % (15*20) == 0 && time % (30*20) != 0 && remaining > 10){
				for(Player player : game.getParticipants()) {
					SwissSMPler.get(player).sendTitle("", ChatColor.YELLOW + "Noch " + ChatColor.AQUA + remaining + ChatColor.YELLOW + " Sekunden");
				}
			}
		}
		if(level.duration*20 - (time) < 11*20 && time % 20 == 0 && !(time >= level.duration * 20)) {
			for(Player player : game.getParticipants()) {
				SwissSMPler.get(player).sendTitle("", ChatColor.RED + "Noch " + ChatColor.AQUA + remaining + ChatColor.RED + " Sekunden");
			}
		}
	}

	protected void displayScore(){
		this.scoreboard = scoreBoardManager.getNewScoreboard();
		this.objective = scoreboard.registerNewObjective("scoreboard", "dummy", ChatColor.YELLOW + "Zvieriplausch");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score score = objective.getScore(ChatColor.GREEN + "Smaragdmünzen: ");
		score.setScore(this.getScore());
		for(Player player : game.getParticipants()){
			player.setScoreboard(scoreboard);
		}
	}

	@Override
	public void cancel(){
		game.clearArena();
		for(Player p : game.getParticipants()) {
			p.teleport(arena.getQueue().getLocation(p.getWorld()));
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			p.playSound(p.getLocation(), ZvieriSound.FAILED, SoundCategory.RECORDS, 1f, 1f);
		}
	}

	@Override
	public void finish() {
		storageChest.getBlockInventory().clear();
		removeRecipeBook();
		for(int i = 0; i < arena.getCounters().length; i++) {
			arena.getCounters()[i].reset();
			}
		for(Client client : inQueue){
			client.getNPCInstance().getEntity().getPassengers().get(0).remove();
			client.getNPCInstance().remove();
		}
		inQueue.clear();
		game.setScore(this.getScore());
		if(gamePhaseListener != null) {
			HandlerList.unregisterAll(this.gamePhaseListener);
			gamePhaseListener = null;
		}
		if(arena.getMusic() != null && arena.getJukebox() != null) {
			for (Player player : arena.getWorld().getNearbyEntities(arena.getJukebox().getLocation(), 50, 50, 50)
					.stream().filter(e -> e instanceof Player).map(e -> (Player) e).collect(Collectors.toList())) {
				player.stopSound(arena.getMusic(), SoundCategory.RECORDS);
			}
		}
	}

	@Override
	public void complete(){
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event){
		if(game.getParticipants().contains(event.getPlayer())) {
			event.getPlayer().teleport(arena.getKitchen().getLocation(arena.getWorld()));
		}
	}
}
