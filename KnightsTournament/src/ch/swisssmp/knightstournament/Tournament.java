package ch.swisssmp.knightstournament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import ch.swisssmp.utils.Random;
import ch.swisssmp.utils.SwissSMPUtils;
import ch.swisssmp.utils.SwissSMPler;

public class Tournament implements Listener{
	private static HashMap<UUID,Tournament> tournaments = new HashMap<UUID,Tournament>();
	private final KnightsArena arena;
	private SwissSMPler master;
	private final List<Player> registeredPlayers = new ArrayList<Player>();
	private TournamentParticipant[] participants;
	private TournamentBracket bracket;
	
	private Random random = new Random();
	
	protected Duel runningDuel = null;
	
	private Tournament(KnightsArena arena, Player master){
		this.arena = arena;
		this.master = SwissSMPler.get(master);
		Bukkit.getPluginManager().registerEvents(this, KnightsTournamentPlugin.plugin);
		tournaments.put(master.getUniqueId(), this);
		this.arena.runTournament(this);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast In kürze startet ein §cRitterspiel!");
	}
	
	public static Tournament initialize(KnightsArena arena, Player master){
		if(arena.getTournament()!=null){
			master.sendMessage(KnightsTournamentPlugin.prefix+" In dieser Arena läuft bereits ein Turnier.");
			return null;
		}
		return new Tournament(arena, master);
	}
	
	public SwissSMPler getMaster(){
		return this.master;
	}
	
	@EventHandler
	private void onEntityDismount(EntityDismountEvent event){
		Entity entity = event.getEntity();
		if(!(entity instanceof Player)) return;
		Player player = (Player) entity;
		if(!this.registeredPlayers.contains(player)) return;
		if(this.runningDuel==null) return;
		if(this.runningDuel.isParticipating(player) && !this.runningDuel.isDecided())
			this.runningDuel.win(this.runningDuel.getOpponent(player), this.runningDuel.getParticipant(player));
	}
	
	@EventHandler
	private void onEntityRegainHealth(EntityRegainHealthEvent event){
		if(this.runningDuel==null) return;
		if(event.getEntityType()!=EntityType.PLAYER) return;
		Player player = (Player) event.getEntity();
		if(!this.runningDuel.isParticipating(player)) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		if(this.runningDuel!=null){
			if(runningDuel.isParticipating(event.getPlayer())){
				runningDuel.finish(runningDuel.getOpponent(event.getPlayer()), runningDuel.getParticipant(event.getPlayer()));
			}
		}
		this.registeredPlayers.remove(event.getPlayer());
	}
	
	public void announce(String title, String subtitle){
		for(Entity entity : this.master.getWorld().getNearbyEntities(this.master.getLocation(), 50, 50, 50)){
			if(entity instanceof Player){
				SwissSMPler.get((Player)entity).sendTitle(title, subtitle);
			}
		}
	}
	
	public void broadcast(String message){
		for(Entity entity : this.master.getWorld().getNearbyEntities(this.master.getLocation(), 50, 50, 50)){
			if(entity instanceof Player){
				SwissSMPler.get((Player)entity).sendMessage(message);
			}
		}
	}
	
	
	public void join(Player player){
		if(player==null) return;
		SwissSMPler swissSMPler = SwissSMPler.get(player);
		if(this.registeredPlayers.contains(player)){
			swissSMPler.sendActionBar("§cBereits angemeldet.");
			return;
		}
		Entity vehicle = player.getVehicle();
		if(vehicle==null || !(vehicle instanceof Horse)){
			swissSMPler.sendActionBar("§cDu brauchst ein Pferd.");
			return;
		}
		if(this.participants!=null){
			swissSMPler.sendActionBar("§cTurnier bereits gestartet.");
			return;
		}
		this.registeredPlayers.add(player);
		swissSMPler.sendActionBar("§aZum Turnier angemeldet.");

		master.sendActionBar("§E"+this.registeredPlayers.size()+" Teilnehmer");
	}
	
	public void leave(Player player){
		if(player==null) return;
		if(!this.registeredPlayers.contains(player)) return;
		this.registeredPlayers.remove(player);
		SwissSMPler swissSMPler = SwissSMPler.get(player);
		swissSMPler.sendActionBar("§ERitterturnier verlassen.");

		master.sendActionBar("§E"+this.registeredPlayers.size()+" Teilnehmer");
	}
	
	private boolean prepareTournament(){
		int participantsCount = this.registeredPlayers.size();
		if(this.registeredPlayers.size()<2){
			this.master.sendActionBar("§cNicht genügend Teilnehmer.");
			return false;
		}
		int power = participantsCount == 0 ? 0 : 32 - Integer.numberOfLeadingZeros(participantsCount - 1);
		int maxParticipants = (int)Math.pow(2, power);
		this.master.sendMessage("[§4Ritterspiele§r] Initiiere Turnier mit "+maxParticipants+" Plätzen.");
		participants = new TournamentParticipant[maxParticipants];
		Player player;
		for(int i = 0; i < participants.length; i++){
			if(i<this.registeredPlayers.size()){
				player = this.registeredPlayers.get(i);
			}
			else{
				player = null;
			}
			participants[i] = new TournamentParticipant(player);
		}
		return true;
	}
	
	public boolean start(){
		if(!this.prepareTournament()) return false;
		this.bracket = new TournamentBracket(this, this.participants);
		Duel firstDuel = this.bracket.getNextDuel();
		Bukkit.getScheduler().runTaskLater(KnightsTournamentPlugin.plugin, new Runnable(){
			public void run(){
				firstDuel.prepare();
			}
		}, 200L);
		this.announce("Turnier startet", this.registeredPlayers.size()+" Teilnehmer");
		this.arena.playBeginSound();
		for(Player player : this.registeredPlayers){
			Bukkit.dispatchCommand(SwissSMPUtils.getPluginSender(), "advancement grant "+player.getName()+" only swisssmp:events/knights_tournament/participate_in_tournament");
		}
		return true;
	}
	
	public void proceed(TournamentParticipant lastWinner){
		this.bracket.getNextDuel();
		if(this.runningDuel!=null) this.runningDuel.prepare();
		else{
			if(lastWinner!=null){
				Player player = Bukkit.getPlayer(lastWinner.getPlayerUUID());
				if(player!=null){
					this.arena.playEndSound();
					this.announce(player.getDisplayName(), "hat das Turnier gewonnen!");
					if(this.registeredPlayers.size()>=8){
						Bukkit.dispatchCommand(SwissSMPUtils.getPluginSender(), "advancement grant "+player.getName()+" only swisssmp:events/knights_tournament/win_tournament");
					}
					Location location = player.getLocation();
					for(int i = 0; i < 10; i++){
						Bukkit.getScheduler().runTaskLater(KnightsTournamentPlugin.plugin, new Runnable(){
							public void run(){
								spawnFirework(location);
								
							}
						}, i*5L);
					}
				}
			}
			Bukkit.getScheduler().runTaskLater(KnightsTournamentPlugin.plugin, new Runnable(){
				public void run(){
					finish();
				}
			}, 60L);
		}
	}
	
	private void spawnFirework(Location location){
		Vector randomVector = this.random.insideUnitSphere().multiply(5f);
		Firework firework = (Firework) location.getWorld().spawnEntity(location.clone().add(randomVector.getX(), randomVector.getY()+10, randomVector.getZ()), EntityType.FIREWORK);
		FireworkMeta fireworkMeta = firework.getFireworkMeta();

        //Get the type
        int rt = this.random.nextInt(5) + 1;
        Type type = Type.BALL;       
        if (rt == 1) type = Type.BALL;
        if (rt == 2) type = Type.BALL_LARGE;
        if (rt == 3) type = Type.BURST;
        if (rt == 4) type = Type.CREEPER;
        if (rt == 5) type = Type.STAR;
       
        //Get our random colours   
        Color c1 = Color.RED;
        Color c2 = Color.WHITE;
       
        //Create our effect with this
        FireworkEffect effect = FireworkEffect.builder().flicker(this.random.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(this.random.nextBoolean()).build();
       
        //Then apply the effect to the meta
        fireworkMeta.addEffect(effect);
       
        //Generate some random power and set it
        int rp = this.random.nextInt(2) + 1;
        fireworkMeta.setPower(rp);
       
        //Then apply this to our rocket
        firework.setFireworkMeta(fireworkMeta); 
	}
	
	public void finish(){
		if(this.runningDuel!=null){
			this.runningDuel.finish(null, null);
		}
		this.announce("", "Turnier beendet.");
		this.broadcast("[§4Ritterspiele§r] §ETurnier beendet. Danke für deine Teilnahme!");
		tournaments.remove(this.master.getUniqueId());
		this.arena.runTournament(null);
	}
	
	public KnightsArena getArena(){
		return this.arena;
	}
	
	public static Tournament get(Player player){
		return tournaments.get(player.getUniqueId());
	}
}
