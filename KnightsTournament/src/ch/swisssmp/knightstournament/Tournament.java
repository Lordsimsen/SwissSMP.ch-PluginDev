package ch.swisssmp.knightstournament;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
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

import ch.swisssmp.utils.RandomizedLocation;
import ch.swisssmp.utils.SwissSMPler;

public class Tournament implements Listener{
	private static HashMap<UUID,Tournament> tournaments = new HashMap<UUID,Tournament>();
	private final KnightsArena arena;
	private SwissSMPler master;
	private final TournamentParticipant[] participants;
	private TournamentBracket bracket;
	
	protected Duel runningDuel = null;
	
	private Tournament(KnightsArena arena, Player master, int maxParticipants){
		this.arena = arena;
		this.master = SwissSMPler.get(master);
		this.participants = new TournamentParticipant[maxParticipants];
		Bukkit.getPluginManager().registerEvents(this, KnightsTournament.plugin);
		tournaments.put(master.getUniqueId(), this);
		this.arena.runTournament(this);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast In kürze startet ein §cRitterspiel!");
	}
	
	public static Tournament initialize(KnightsArena arena, Player master, int maxParticipants){
		if((maxParticipants&(maxParticipants-1))!=0){
			master.sendMessage(KnightsTournament.prefix+" Ungültige Teilnehmerzahl "+maxParticipants+". Zahl muss Zweierpotenz sein.");
			return null;
		}
		if(arena.getTournament()!=null){
			master.sendMessage(KnightsTournament.prefix+" In dieser Arena läuft bereits ein Turnier.");
			return null;
		}
		return new Tournament(arena, master, maxParticipants);
	}
	
	public SwissSMPler getMaster(){
		return this.master;
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
				runningDuel.finish(runningDuel.getOpponent(event.getPlayer()));
			}
		}
		for(int i = 0; i < participants.length; i++){
			if(participants[i].getPlayer()==event.getPlayer()){
				this.leave(event.getPlayer());
				return;
			}
		}
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
		Entity vehicle = player.getVehicle();
		SwissSMPler swissSMPler = SwissSMPler.get(player);
		if(vehicle==null || !(vehicle instanceof Horse)){
			swissSMPler.sendActionBar("Du brauchst ein Pferd.");
			return;
		}
		Horse horse = (Horse) vehicle;
		for(int i = 0; i < participants.length; i++){
			if(participants[i]==null) continue;
			if(participants[i].getPlayer()==player){
				return;
			}
		}
		for(int i = 0; i < participants.length; i++){
			if(participants[i]==null){
				participants[i] = new TournamentParticipant(player, horse);
				swissSMPler.sendActionBar("§aZum Ritterturnier angemeldet.");
				if(this.participantCountInfo()==this.participants.length){
					this.start();
					//this.master.sendMessage(KnightsTournament.prefix+" Alle Turnierplätze belegt. Starte das Turnier mit '/knightstournament begin'.");
				};
				return;
			}
		}
		swissSMPler.sendActionBar("Bereits "+participants.length+"/"+participants.length+" Spieler angemeldet.");
	}
	
	private int participantCountInfo(){
		int count = 0;
		for(int i = 0; i < this.participants.length; i++){
			if(participants[i]==null) continue;
			count++;
		}
		master.sendActionBar("§E"+count+"/"+this.participants.length+" Teilnehmer");
		return count;
	}
	
	public void leave(Player player){
		if(player==null) return;
		for(int i = 0; i < participants.length; i++){
			if(participants[i]==null) continue;
			if(participants[i].getPlayer()==player){
				participants[i] = null;
				SwissSMPler swissSMPler = SwissSMPler.get(player);
				swissSMPler.sendActionBar("§ERitterturnier verlassen.");
				return;
			}
		}
	}
	
	public void start(){
		this.bracket = new TournamentBracket(this, this.participants);
		this.bracket.getNextDuel().prepare();
	}
	
	public void proceed(TournamentParticipant lastWinner){
		this.bracket.getNextDuel();
		if(this.runningDuel!=null) this.runningDuel.prepare();
		else{
			if(lastWinner!=null){
				this.announce(lastWinner.getPlayer().getDisplayName(), "hat das Turnier gewonnen!");
				RandomizedLocation fireworksLocation = new RandomizedLocation(lastWinner.getPlayer().getLocation(), 5f);
				for(int i = 0; i < 10; i++){
					Bukkit.getScheduler().runTaskLater(KnightsTournament.plugin, new Runnable(){
						public void run(){
							spawnFirework(fireworksLocation);
							
						}
					}, i*5L);
				}
			}
			Bukkit.getScheduler().runTaskLater(KnightsTournament.plugin, new Runnable(){
				public void run(){
					finish();
				}
			}, 60L);
		}
	}
	
	private void spawnFirework(RandomizedLocation location){
		Firework firework = (Firework) location.getLocation().getWorld().spawnEntity(location.getLocation(), EntityType.FIREWORK);
		FireworkMeta fireworkMeta = firework.getFireworkMeta();
        //Our random generator
        Random random = new Random();   

        //Get the type
        int rt = random.nextInt(5) + 1;
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
        FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(random.nextBoolean()).build();
       
        //Then apply the effect to the meta
        fireworkMeta.addEffect(effect);
       
        //Generate some random power and set it
        int rp = random.nextInt(2) + 1;
        fireworkMeta.setPower(rp);
       
        //Then apply this to our rocket
        firework.setFireworkMeta(fireworkMeta); 
	}
	
	public void finish(){
		if(this.runningDuel!=null){
			this.runningDuel.finish(null);
		}
		this.announce("", "Turnier beendet.");
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
