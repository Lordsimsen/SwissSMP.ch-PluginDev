package ch.swisssmp.knightstournament;

import org.bukkit.Color;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Duel extends BukkitRunnable{

	private final Tournament tournament;
	private final String name;
	private final TournamentParticipant playerOne;
	private final TournamentParticipant playerTwo;
	
	private Waypoint waypointOne;
	private Waypoint waypointTwo;
	
	private boolean playerOneReady = false;
	private boolean playerTwoReady = false;
	
	private Vector playerOneVector;
	private Vector playerTwoVector;
	private Vector playerOneToTwo;
	private Vector playerTwoToOne;
	private float lanceRangeSquared = 16f;
	boolean playerOneThrownOff = false;
	boolean playerTwoThrownOff = false;
	
	public Duel(Tournament tournament, String name, TournamentParticipant playerOne, TournamentParticipant playerTwo){
		this.tournament = tournament;
		this.name = name;
		this.playerOne = playerOne;
		this.playerTwo = playerTwo;
		this.playerOne.sendMessage(KnightsTournament.prefix+" "+this.name+" gegen "+this.playerTwo.getPlayer().getDisplayName());
		this.playerTwo.sendMessage(KnightsTournament.prefix+" "+this.name+" gegen "+this.playerOne.getPlayer().getDisplayName());
	}
	
	public void prepare(){
		this.tournament.announce(this.name, this.playerOne.getPlayer().getDisplayName()+"§r§E vs. §r"+this.playerTwo.getPlayer().getDisplayName());
		if(!playerOne.getPlayer().isOnline() && playerTwo.getPlayer().isOnline()){
			this.tournament.announce(playerTwo.getPlayer().getDisplayName(), "gewinnt! "+playerOne.getPlayer().getDisplayName()+"§r ist offline.");
			this.finish(this.playerTwo);
			return;
		}
		else if(!playerTwo.getPlayer().isOnline() && playerOne.getPlayer().isOnline()){
			this.tournament.announce(playerOne.getPlayer().getDisplayName(), "gewinnt! "+playerTwo.getPlayer().getDisplayName()+"§r ist offline.");
			this.finish(this.playerOne);
			return;
		}
		else if(!playerOne.getPlayer().isOnline()&&!playerTwo.getPlayer().isOnline()){
			this.tournament.announce("", "Teilnehmer offline, überspringe Match...");
			this.finish(null);
			return;
		}
		playerOne.getPlayer().setHealth(playerOne.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		playerTwo.getPlayer().setHealth(playerTwo.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		playerOne.sendMessage(KnightsTournament.prefix+" Begib dich zur §cROTEN §rMarkierung.");
		playerTwo.sendMessage(KnightsTournament.prefix+" Begib dich zur §9BLAUEN §rMarkierung.");
		KnightsArena arena = this.tournament.getArena();
		waypointOne = new Waypoint(playerOne.getPlayer(), arena.getPosOne(), 2f, Color.RED, new Runnable(){
			public void run(){
				playerOne.getHorse().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999, 10));
				playerOneReady = true;
				if(playerOneReady && playerTwoReady) start();
			}
		});
		waypointTwo = new Waypoint(playerTwo.getPlayer(), arena.getPosTwo(), 2f, Color.BLUE, new Runnable(){
			public void run(){
				playerTwo.getHorse().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999, 10));
				playerTwoReady = true;
				if(playerOneReady && playerTwoReady) start();
			}
		});
	}
	
	public void start(){
		playerOne.getHorse().removePotionEffect(PotionEffectType.SLOW);
		playerTwo.getHorse().removePotionEffect(PotionEffectType.SLOW);
		this.waypointOne = null;
		this.waypointTwo = null;
		this.tournament.announce("Start!", this.playerOne.getPlayer().getDisplayName()+"§r§E vs. §r"+this.playerTwo.getPlayer().getDisplayName());
		this.runTaskTimer(KnightsTournament.plugin, 0, 1l);
	}
	
	@Override
	public void run() {
		if(playerOne.getPlayer().getLocation().distanceSquared(playerTwo.getPlayer().getLocation())>this.lanceRangeSquared){
			return;
		}
		playerOneVector = playerOne.getPlayer().getEyeLocation().toVector().clone().setY(0);
		playerTwoVector = playerTwo.getPlayer().getEyeLocation().toVector().clone().setY(0);
		playerOneToTwo = playerTwoVector.clone().subtract(playerOneVector).normalize();
		playerTwoToOne = playerOneVector.clone().subtract(playerTwoVector).normalize();
		if(!playerOne.getPlayer().isBlocking() && playerOne.getPlayer().getEyeLocation().getDirection().setY(0).normalize().subtract(playerOneToTwo).lengthSquared()<0.05f){
			if(!playerTwo.getPlayer().isBlocking()){
				if(playerOne.getPlayer().getHealth()>5f){
					playerTwo.getPlayer().damage(5f, playerOne.getPlayer());
				}
				else{
					playerTwoThrownOff = true;
				}
			}
			else{
				playerOne.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 40, 3));
			}
		}
		if(!playerTwo.getPlayer().isBlocking() && playerTwo.getPlayer().getEyeLocation().getDirection().setY(0).normalize().subtract(playerTwoToOne).lengthSquared()<0.05f){
			if(!playerOne.getPlayer().isBlocking()){
				if(playerOne.getPlayer().getHealth()>5f){
					playerOne.getPlayer().damage(5f, playerTwo.getPlayer());
				}
				else{
					playerOneThrownOff = true;
				}
			}
			else{
				playerTwo.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 40, 1));
			}
		}
		if(playerOneThrownOff && !playerTwoThrownOff){
			playerOne.getHorse().eject();
			win(playerTwo, playerOne);
		}
		else if(playerTwoThrownOff && !playerOneThrownOff){
			playerTwo.getHorse().eject();
			win(playerOne, playerTwo);
		}
		else if(playerOneThrownOff && playerTwoThrownOff){
			playerOneThrownOff = false;
			playerTwoThrownOff = false;
		}
	}
	
	public boolean isParticipating(Player player){
		return this.playerOne.getPlayer()==player || this.playerTwo.getPlayer()==player;
	}
	
	public TournamentParticipant getParticipant(Player player){
		if(playerOne.getPlayer()==player) return this.playerOne;
		else return this.playerTwo;
	}
	
	public TournamentParticipant getOpponent(Player player){
		if(playerOne.getPlayer()==player) return this.playerTwo;
		else return this.playerOne;
	}
	
	public void win(TournamentParticipant winner, TournamentParticipant loser){
		winner.addWonAgainst(loser);
		loser.setLostAgainst(winner);
		this.tournament.broadcast(winner.getPlayer().getDisplayName()+"§r besiegt "+loser.getPlayer().getDisplayName()+"!");
		this.finish(winner);
	}
	
	public void finish(TournamentParticipant winner){
		this.cancel();
		if(this.waypointOne != null) this.waypointOne.cancel();
		if(this.waypointTwo != null) this.waypointTwo.cancel();
		if(this.tournament.runningDuel==this) this.tournament.runningDuel = null;
		if(winner!=null){
			this.tournament.proceed(winner);
		}
	}
}
