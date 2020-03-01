package ch.swisssmp.travel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.travel.phase.ArrivePhase;
import ch.swisssmp.travel.phase.EmbarkPhase;
import ch.swisssmp.travel.phase.Phase;
import ch.swisssmp.travel.phase.PreparationPhase;
import ch.swisssmp.travel.phase.TravelPhase;
import ch.swisssmp.utils.SwissSMPler;

public class Journey implements Runnable, Listener {
	
	private final static long PREPARATION_TIME = 600; //value is in server ticks
	
	private static List<Journey> journeys = new ArrayList<Journey>();
	
	private List<Player> players = new ArrayList<Player>();
	private List<Player> sleeping = new ArrayList<Player>();
	private List<Entity> entities = new ArrayList<Entity>();
	
	private final TravelStation start; //where the journey starts
	private TravelStation destination; //where the journey ends
	
	private Phase currentPhase;
	
	private BukkitTask task;
	
	private String travelWorldInstanceName = "Fernreise_"+UUID.randomUUID().toString();
	private List<Runnable> embarkListeners = new ArrayList<Runnable>();
	
	private World worldInstance;
	
	private Journey(TravelStation start, TravelStation destination){
		this.start = start;
		this.destination = destination;
	}

	@Override
	public void run() {
		if(!currentPhase.isCompleted()) currentPhase.run();
		if(currentPhase.isCompleted()){
			currentPhase.complete();
			currentPhase.finish();
			if(currentPhase instanceof PreparationPhase){
				this.triggerOnEmbark();
			}
			this.currentPhase = getNextPhase(this.currentPhase);
			if(this.currentPhase==null){
				this.complete();
			}
			else{
				this.currentPhase.initialize();
			}
		}
	}
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		if(!this.players.contains(event.getPlayer())) return;
		if(!(this.currentPhase instanceof TravelPhase)) return;
		Block block = event.getClickedBlock();
		if(!(block.getBlockData() instanceof Bed)) return;
		event.setCancelled(true);
		//Player player = event.getPlayer();
		this.skipTravel();
		//this.setSleeping(player, block);
		/*
		if(this.sleeping.size()>=this.players.size()){
			this.skipTravel();
		}
		*/
	}
	
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		if(!this.players.contains(event.getPlayer())) return;
		this.leave(event.getPlayer());
	}
	
	@EventHandler
	private void onPlayerDeath(PlayerRespawnEvent event){
		if(!this.players.contains(event.getPlayer())) return;
		this.currentPhase.onPlayerRespawn(event);
	}
	
	/*
    private void setSleeping(Player player, Block block) {
    	player.sleep(block.getLocation(), true);
        sleeping.add(player);
    }
    */
    
    public void setWorldInstance(World world){
    	this.worldInstance = world;
    }
    
    public void exitBed(Player player) {
        sleeping.remove(player);
    }
	
	private void triggerOnEmbark(){
		for(Runnable runnable : this.embarkListeners){
			try{
				runnable.run();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private void skipTravel(){
		Bukkit.getScheduler().runTaskLater(TravelSystem.getInstance(), ()->{
			if(!(this.currentPhase instanceof TravelPhase)) return;
			this.currentPhase.setCompleted();
		}, 60L);
	}
	
	private Phase getNextPhase(Phase currentPhase){
		if(currentPhase instanceof PreparationPhase) return new EmbarkPhase(this);
		if(currentPhase instanceof EmbarkPhase) return new TravelPhase(this, this.worldInstance, this.start.getTravelTime(this.destination));
		if(currentPhase instanceof TravelPhase) return new ArrivePhase(this);
		return null;
	}
	
	public void embarkNow(){
		if(!(currentPhase instanceof PreparationPhase)) return;
		currentPhase.setCompleted();
		this.triggerOnEmbark();
	}
	
	public String getTravelWorldInstanceName(){
		return travelWorldInstanceName;
	}
	
	public void sendMessage(String message){
		for(Player player : this.players){
			player.sendMessage(message);
		}
	}
	
	public void addOnEmbarkListener(Runnable runnable){
		this.embarkListeners.add(runnable);
	}
	
	public void join(Player player){
		this.players.add(player);
		SwissSMPler.get(player).sendActionBar(ChatColor.GREEN+"Reise beigetreten");
	}
    
    public void join(Entity entity){
    	this.entities.add(entity);
    }
	
	public void leave(Player player){
		this.players.remove(player);
		if(this.sleeping.contains(player)){
			this.exitBed(player);
		}
		if(!(this.currentPhase instanceof PreparationPhase)){
			player.teleport(this.start.getWaypoint().getLocation(this.start.getWorld()));
		}
		SwissSMPler.get(player).sendActionBar(ChatColor.GREEN+"Reise verlassen");
		if(this.players.size()==0){
			this.cancel();
		}
	}
	
	private void start(){
		this.currentPhase = new PreparationPhase(this, PREPARATION_TIME);
		task = Bukkit.getScheduler().runTaskTimer(TravelSystem.getInstance(), this, 0, 1);
		Bukkit.getPluginManager().registerEvents(this, TravelSystem.getInstance());
	}
	
	public void setDestination(TravelStation destination){
		if(!(this.currentPhase instanceof PreparationPhase)) return;
		this.destination = destination;
		((PreparationPhase)this.currentPhase).reset();
	}
	
	public List<Player> getPlayers(){
		return new ArrayList<Player>(players);
	}
	
	private void finish(){
		if(task!=null) task.cancel();
		for(Player sleeping : new ArrayList<Player>(this.sleeping)){
			this.exitBed(sleeping);
		}
		journeys.remove(this);
		HandlerList.unregisterAll(this);
	}
	
	public void cancel(){
		if(this.currentPhase!=null){
			this.currentPhase.cancel();
			this.currentPhase.finish();
		}
		finish();
		for(Player player : players){
			SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Reise abgebrochen");
		}
	}
	
	private void complete(){
		finish();
	}
	
	public TravelStation getStart(){
		return start;
	}
	
	public TravelStation getDestination(){
		return destination;
	}
	
	public Collection<Entity> getEntities(){
		return entities;
	}
	
	public static Journey prepare(TravelStation start, TravelStation destination){
		Journey journey = new Journey(start, destination);
		journeys.add(journey);
		journey.start();
		return journey;
	}
}
