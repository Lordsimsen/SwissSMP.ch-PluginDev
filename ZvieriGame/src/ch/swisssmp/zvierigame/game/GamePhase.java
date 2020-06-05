package ch.swisssmp.zvierigame.game;

import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.Random;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zvierigame.ZvieriArena;
import ch.swisssmp.zvierigame.ZvieriGame;
import ch.swisssmp.zvierigame.ZvieriGamePlugin;
import ch.swisssmp.zvierigame.ZvieriSound;
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
		gamePhaseListener = new GamePhaseListener(this);
		Bukkit.getPluginManager().registerEvents(gamePhaseListener, ZvieriGamePlugin.getInstance());
		for (Player player : game.getParticipants()) {
			player.teleport(arena.getKitchen().getLocation(player.getWorld()));
			player.sendTitle("",ChatColor.GREEN + "Au travail!", 5, 30, 5);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg am " + game.getArena().getArenaRegion() + " " + player.getName());
		}
		kickPlayersOutOfArena();
		initializeBrewingStands();
		this.objective = scoreboard.registerNewObjective("scoreboard", "dummy", ChatColor.YELLOW + "Saldo");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		score = 20;
		displayScore();
		playMusic();
	}

	private void playMusic(){
		if(arena.getMusic() == null) return;
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
						((BrewingStand) block.getState()).setFuelLevel(20);
					}
				}
			}
		}
	}

	private void kickPlayersOutOfArena(){
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

	public void initializeStorage(){
		storageChest.getBlockInventory().clear();
		ConfigurationSection ingredientsSection = ZvieriGamePlugin.getInstance().getConfig().getConfigurationSection("ingredients");

		ItemStack[] result = new ItemStack[ingredients.size() + level.getRecipes().length];
		int j = 0;
		for(String key : ingredients.keySet()){
			result[j] = ingredients.get(key);
			result[j].setAmount(ingredientsSection.getInt(key + ".initialAmount"));
			ItemUtil.setBoolean(result[j], "zvieriGameItem", true);
			j++;
		}
//		for(int i = 0; i < ingredients.length; i++){
//			result[i] = ingredients[i];
//			result[i].setAmount(ingredientsSection.getInt(ingredients[i].getType().toString() + ".initialAmount"));
//			ItemUtil.setBoolean(result[i], "zvieriGameItem", true);
//		}
		for(int i = ingredients.size(); i < ingredients.size() + level.getRecipes().length; i++){
			result[i] = level.getRecipes()[i-ingredients.size()];
			result[i].setAmount(1);
		}
		storageChest.getBlockInventory().setContents(result);
	}

	@Override
	public void run() {
		time++; //time in ticks
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
		if (remaining % 60 == 0){
			for(Player player : game.getParticipants()) {
				SwissSMPler.get(player).sendActionBar(ChatColor.YELLOW + "Noch " + ChatColor.AQUA + remaining + ChatColor.YELLOW + " Sekunden");
			}
		}
		if(level.duration - (time/20) < 11) {
			for(Player player : game.getParticipants()) {
				SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Noch " + ChatColor.AQUA + remaining + ChatColor.RED + " Sekunden");
			}
		}
	}

	protected void displayScore(){
		this.scoreboard = scoreBoardManager.getNewScoreboard();
		this.objective = scoreboard.registerNewObjective("scoreboard", "dummy", ChatColor.YELLOW + "Saldo");
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
		if(arena.getMusic() != null) {
			for (Player player : arena.getWorld().getNearbyEntities(arena.getJukebox().getLocation(), 50, 50, 50)
					.stream().filter(e -> e instanceof Player).map(e -> (Player) e).collect(Collectors.toList())) {
				player.stopSound(arena.getMusic(), SoundCategory.RECORDS);
			}
		}
	}

	@Override
	public void complete(){
		// nu'n
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event){
		if(game.getParticipants().contains(event.getPlayer())) {
			event.getPlayer().teleport(arena.getKitchen().getLocation(arena.getWorld()));
		}
	}
}
