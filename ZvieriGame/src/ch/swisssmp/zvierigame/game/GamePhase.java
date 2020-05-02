package ch.swisssmp.zvierigame.game;

import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.Random;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zvierigame.ZvieriArena;
import ch.swisssmp.zvierigame.ZvieriGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GamePhase extends Phase implements Listener, Runnable { // Unterschied runnable/Bukkitrunnable ? Ausführungsgeschwindigkeit bei bukkitrunnable 20/sekunde?

	private final ZvieriGame game;
	private final ZvieriArena arena;
	private final Level level;

	private long time = 0L;
	private int score;
	private double difficulty;
	private Chest storageChest;
	private ItemStack[] ingredients;
	private ArrayList<Client> inQueue;
	private ArrayList<Counter> counters;

	private Random random = new Random();
	
	public GamePhase(ZvieriGame game) {
		super(game);

		this.game = game;
		this.arena = game.getArena();
		this.level = game.getLevel();

		this.storageChest = game.getArena().getStorageChest();
		this.ingredients = game.getLevel().getIngredients();

		this.inQueue = new ArrayList<Client>();
		this.counters = new ArrayList<Counter>();
		for(int i = 0; i < game.getArena().getCounters().length; i++) {
			this.counters.add(game.getArena().getCounters()[i]);
		}
	}

	@Override
	public void initialize() {
		initializeStorage();
		for (Player p : game.getParticipants()) {
			p.teleport(arena.getKitchen().getLocation(p.getWorld()));
			p.sendTitle("",ChatColor.GREEN + "Au travail!", 5, 30, 5);
		}
		score = 0;
	}

	public void initializeStorage(){
		storageChest.getBlockInventory().clear();

		ItemStack[] result = new ItemStack[ingredients.length];
		for(int i = 0; i < result.length; i++){
			result[i] = ingredients[i];
			result[i].setAmount(5); //TODO äuä je nach Zutat apasse odr
		}
		storageChest.getBlockInventory().setContents(ingredients);
	}

	@Override
	public void run() {
		time++; //time in ticks
		int remaining = Mathf.ceilToInt(level.duration - time/20);
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

		if(time >= (level.duration*20)) {
			finish();
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

	protected void subtractFromScore(int i){
		score -= i;
	}

	protected void addToScore(int i){
		score += i;
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

	@Override
	public void cancel(){

		for(Player p : game.getParticipants()) {
			p.teleport(arena.getQueue().getLocation(p.getWorld()));
			p.sendTitle("", ChatColor.RED + "Spiel abgebrochen", 5, 30, 5);
		}
//		for(int i = 0; i < arena.getCounters().length; i++) {
//			arena.getCounters()[i].reset();
//		}
//		for(Client client : inQueue){
//			client.getNPCInstance().getEntity().removePassenger(client.getNPCInstance().getEntity().getPassengers().get(0));
//			client.getNPCInstance().remove();
//		}
//		inQueue.clear();
	}

	@Override
	public void finish() {
		time = -1;
		storageChest.getBlockInventory().clear();

		for(int i = 0; i < arena.getCounters().length; i++) {
			arena.getCounters()[i].reset();
			}
		for(Client client : inQueue){
			client.getNPCInstance().getEntity().removePassenger(client.getNPCInstance().getEntity().getPassengers().get(0));
			client.getNPCInstance().remove();
		}
		inQueue.clear();
		HandlerList.unregisterAll(this);
		setCompleted();
	}

	@Override
	public void complete(){
		// mkay ...
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event){
		if(game.getParticipants().contains(event.getPlayer())) {
			event.getPlayer().teleport(arena.getKitchen().getLocation(arena.getWorld()));
		}
	}

}
