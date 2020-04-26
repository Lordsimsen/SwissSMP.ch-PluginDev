package ch.swisssmp.knightstournament;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.EntityEffect;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Duel implements Listener {

	private final Tournament tournament;
	private final String name;
	private final TournamentParticipant participantOne;
	private final TournamentParticipant participantTwo;
	private final Player playerOne;
	private final Player playerTwo;
	private Horse horseOne;
	private Horse horseTwo;
	
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
	
	private boolean running = false;
	private boolean decided = false;
	
	public Duel(Tournament tournament, String name, TournamentParticipant participantOne, TournamentParticipant participantTwo){
		this.tournament = tournament;
		this.name = name;
		this.participantOne = participantOne;
		this.participantTwo = participantTwo;
		this.playerOne = Bukkit.getPlayer(participantOne.getPlayerUUID());
		this.playerTwo = Bukkit.getPlayer(participantTwo.getPlayerUUID());
		this.horseOne = this.participantOne.getHorse();
		this.horseTwo = this.participantTwo.getHorse();
		if(this.playerTwo!=null) this.participantOne.sendMessage(KnightsTournamentPlugin.prefix+" Dein nächster Zweikampf: "+this.name+" gegen "+this.playerTwo.getDisplayName());
		else this.participantOne.sendMessage(KnightsTournamentPlugin.prefix+" "+this.name+" - Freipass!");
		if(this.playerOne!=null) this.participantTwo.sendMessage(KnightsTournamentPlugin.prefix+" Dein nächster Zweikampf: "+this.name+" gegen "+this.playerOne.getDisplayName());
		else this.participantTwo.sendMessage(KnightsTournamentPlugin.prefix+" "+this.name+" - Freipass!");
	}
	
	public void prepare(){
		if(this.checkForfeit()) return;
		this.tournament.getArena().playCallSound();
		this.tournament.announce(this.name, this.playerOne.getDisplayName()+"§r§E vs. §r"+this.playerTwo.getDisplayName());
		playerOne.setHealth(playerOne.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		playerTwo.setHealth(playerTwo.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		participantOne.sendMessage(KnightsTournamentPlugin.prefix+" Begib dich zur §cROTEN §rMarkierung.");
		participantTwo.sendMessage(KnightsTournamentPlugin.prefix+" Begib dich zur §9BLAUEN §rMarkierung.");
		if(horseOne!=null){
			horseOne.removePotionEffect(PotionEffectType.SLOW);
		}
		if(horseTwo!=null){
			horseTwo.removePotionEffect(PotionEffectType.SLOW);
		}
		KnightsArena arena = this.tournament.getArena();
		waypointOne = new Waypoint(playerOne, arena.getPosOne(), 2f, Color.RED, new Runnable(){
			public void run(){
				if(participantOne.getHorse()==null){
					participantOne.sendActionBar("§cDu brauchst ein Pferd.");
					return;
				}
				horseOne = participantOne.getHorse();
				participantOne.getHorse().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999, 10));
				playerOneReady = true;
				if(playerOneReady && playerTwoReady) start();
			}
		});
		waypointTwo = new Waypoint(playerTwo, arena.getPosTwo(), 2f, Color.BLUE, new Runnable(){
			public void run(){
				if(participantTwo.getHorse()==null){
					participantTwo.sendActionBar("§cDu brauchst ein Pferd.");
					return;
				}
				horseTwo = participantTwo.getHorse();
				participantTwo.getHorse().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999, 10));
				playerTwoReady = true;
				if(playerOneReady && playerTwoReady) start();
			}
		});
	}
	
	private boolean checkForfeit(){
		if(decided)return true;
		boolean forfeitOne = (playerOne==null || !playerOne.isOnline());
		boolean forfeitTwo = (playerTwo==null || !playerTwo.isOnline());
		if(forfeitOne && !forfeitTwo){
			decided = true;
			this.tournament.announce(playerTwo.getDisplayName(), "gewinnt automatisch.");
			this.finish(this.participantTwo, this.participantOne);
			return true;
		}
		else if(!forfeitOne && forfeitTwo){
			decided = true;
			this.tournament.announce(playerOne.getDisplayName(), "gewinnt automatisch.");
			this.finish(this.participantOne, this.participantTwo);
			return true;
		}
		else if(forfeitOne && forfeitTwo){
			decided = true;
			this.tournament.announce("", "Teilnehmer offline, überspringe Match...");
			this.finish(this.participantOne, this.participantTwo);
			return true;
		}
		return false;
	}
	
	public void start(){
		if(this.checkForfeit()) return;
		if(playerOne!=null) heal(playerOne);
		if(playerTwo!=null) heal(playerTwo);
		participantOne.getHorse().removePotionEffect(PotionEffectType.SLOW);
		participantTwo.getHorse().removePotionEffect(PotionEffectType.SLOW);
		this.waypointOne = null;
		this.waypointTwo = null;
		this.tournament.announce("Start!", this.playerOne.getDisplayName()+"§r§E vs. §r"+this.playerTwo.getDisplayName());
		this.tournament.getArena().playCallSound();
		Bukkit.getPluginManager().registerEvents(this, KnightsTournamentPlugin.getInstance());
		this.running = true;
	}
	
//	@Override
//	public void run() {
//		if(playerOne.getLocation().distanceSquared(playerTwo.getLocation())>this.lanceRangeSquared){
//			return;
//		}
//		playerOneVector = playerOne.getEyeLocation().toVector().clone().setY(0);
//		playerTwoVector = playerTwo.getEyeLocation().toVector().clone().setY(0);
//		playerOneToTwo = playerTwoVector.clone().subtract(playerOneVector).normalize();
//		playerTwoToOne = playerOneVector.clone().subtract(playerTwoVector).normalize();
//		if(!playerOne.isBlocking() && playerOne.getEyeLocation().getDirection().setY(0).normalize().subtract(playerOneToTwo).lengthSquared()<0.05f){
//			if(!playerTwo.isBlocking()){
//				if(playerTwo.getNoDamageTicks()==0){
//					if(playerTwo.getHealth()>5f){
//						playerTwo.playEffect(EntityEffect.HURT);
//						playerTwo.setHealth(playerTwo.getHealth()-5f);
//						playerTwo.setNoDamageTicks(20);
//						playerTwo.getWorld().playSound(playerTwo.getPlayer().getLocation(), Sound.ENTITY_PLAYER_HURT, 10, 1);
//					}
//					else{
//						playerTwoThrownOff = true;
//					}
//				}
//			}
//			else{
//				playerOne.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 40, 3));
//			}
//		}
//		if(!playerTwo.isBlocking() && playerTwo.getEyeLocation().getDirection().setY(0).normalize().subtract(playerTwoToOne).lengthSquared()<0.05f){
//			if(!playerOne.isBlocking()){
//				if(playerOne.getNoDamageTicks()==0){
//					if(playerOne.getHealth()>5f){
//						playerOne.playEffect(EntityEffect.HURT);
//						playerOne.setHealth(playerOne.getHealth()-5f);
//						playerOne.setNoDamageTicks(20);
//						playerOne.getWorld().playSound(playerOne.getPlayer().getLocation(), Sound.ENTITY_PLAYER_HURT, 10, 1);
//					}
//					else{
//						playerOneThrownOff = true;
//					}
//				}
//			}
//			else{
//				playerTwo.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 40, 1));
//			}
//		}
//		if(playerOneThrownOff && !playerTwoThrownOff){
//			this.decided = true;
//			horseOne.eject();
//			playerOne.playEffect(EntityEffect.HURT);
//			win(participantTwo, participantOne);
//		}
//		else if(playerTwoThrownOff && !playerOneThrownOff){
//			this.decided = true;
//			horseTwo.eject();
//			playerTwo.playEffect(EntityEffect.HURT);
//			win(participantOne, participantTwo);
//		}
//		else if(playerOneThrownOff && playerTwoThrownOff){
//			playerOneThrownOff = false;
//			playerTwoThrownOff = false;
//		}
//	}
	
	public boolean isDecided(){
		return this.decided;
	}
	
	public boolean isParticipating(Player player){
		return this.participantOne.getPlayerUUID()==player.getUniqueId() || this.participantTwo.getPlayerUUID()==player.getUniqueId();
	}
	
	public TournamentParticipant getParticipant(Player player){
		if(participantOne.getPlayerUUID()==player.getUniqueId()) return this.participantOne;
		else if(participantTwo.getPlayerUUID()==player.getUniqueId()) return this.participantTwo;
		else return null;
	}
	
	public TournamentParticipant getOpponent(Player player){
		if(playerOne==player) return this.participantTwo;
		else if(playerTwo==player) return this.participantOne;
		else return null;
	}
	
	public void win(TournamentParticipant winner, TournamentParticipant loser){
		if(decided) return;
		decided = true;
		this.tournament.broadcast(Bukkit.getPlayer(winner.getPlayerUUID()).getDisplayName()+"§r besiegt "+Bukkit.getPlayer(loser.getPlayerUUID()).getDisplayName()+"!");
		this.tournament.announce(Bukkit.getPlayer(winner.getPlayerUUID()).getDisplayName(), "besiegt "+Bukkit.getPlayer(loser.getPlayerUUID()).getDisplayName()+"§r!");

		if(this.running){
			this.running = false;
		}
		Bukkit.getScheduler().runTaskLater(KnightsTournamentPlugin.plugin, new Runnable(){
			public void run(){
				finish(winner, loser);
			}
		}, 60L);
	}
	
	public void finish(TournamentParticipant winner, TournamentParticipant loser){
		if(winner!=null && loser!=null){
			winner.addWonAgainst(loser);
			loser.setLostAgainst(winner);
		}
		if(winner!=null) heal(winner.getPlayer());
		if(loser!=null) heal(loser.getPlayer());
		if(horseOne!=null){
			horseOne.removePotionEffect(PotionEffectType.SLOW);
		}
		if(horseTwo!=null){
			horseTwo.removePotionEffect(PotionEffectType.SLOW);
		}
		//cancel the loop task
		if(this.running){
			this.running = false;
		}
		//remove the waypoints
		if(this.waypointOne != null) this.waypointOne.cancel();
		if(this.waypointTwo != null) this.waypointTwo.cancel();
		if(this.tournament.runningDuel==this) this.tournament.runningDuel = null;
		Bukkit.getScheduler().runTaskLater(KnightsTournamentPlugin.plugin, new Runnable(){
			public void run(){
				tournament.proceed(winner);
			}
		}, 60L);
	}

	private void heal(Player player){
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		player.setSaturation(20);
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	private void onHit(EntityDamageByEntityEvent event){
		if(!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) return;
		if(!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) return;
		Player damageDealer = (Player) event.getDamager();
		Player damagee = (Player) event.getEntity();
		damageDealer = damageDealer == playerOne ? playerOne : ((damageDealer == playerTwo) ? playerTwo : null);
		damagee = damagee == playerOne ? playerOne : ((damageDealer == playerTwo) ? playerTwo : null);
		if(damageDealer==damagee || damageDealer==null || damagee==null) return;
		ItemStack mainHand = ((Player) event.getEntity()).getInventory().getItemInMainHand();
		if(!TournamentLance.isLance(mainHand)) return;
		if(damagee.getHealth()-event.getFinalDamage()<=0){
			event.setCancelled(true);
			damagee.leaveVehicle();
			win(getParticipant(damageDealer), getParticipant(damagee));
		}
	}
}
