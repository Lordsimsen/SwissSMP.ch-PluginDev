package ch.swisssmp.knightstournament;

import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.Random;
import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.util.Vector;

public class Duel extends BukkitRunnable implements Listener {

	private static final int BLOCK_TIME = 10;
	private static final int BLOCK_COOLDOWN = 30;
	private static final int NO_SHOW_TIMEOUT = 1200; // 1 minute
	private static final double ATTACK_DAMAGE = 8;
	private static final double PARRY_DAMAGE = 6;

	private static final Random random = new Random();

	private final Tournament tournament;
	private final String name;
	private final TournamentParticipant participantOne;
	private final TournamentParticipant participantTwo;
	private final Player playerOne;
	private final Player playerTwo;
	private Horse horseOne;
	private Horse horseTwo;

	private Objective sidebarHealthObjective;
	private Objective belowNameHealthObjective;
	
	private Waypoint waypointOne;
	private Waypoint waypointTwo;
	
	private boolean playerOneReady = false;
	private boolean playerTwoReady = false;

	private long playerOneBlockTime = 0;
	private long playerTwoBlockTime = 0;
	private long playerOneBlockCooldown = 0;
	private long playerTwoBlockCooldown = 0;

	private NoShowTimeout noShowTimeout = null;

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

		Objective sidebarHealthObjective = tournament.getScoreboard().registerNewObjective("health1", "health", "Duell", RenderType.HEARTS);
		this.sidebarHealthObjective = sidebarHealthObjective;

		Objective belowNameHealthObjective = tournament.getScoreboard().registerNewObjective("health2", "health", ChatColor.RED+"♥", RenderType.HEARTS);
		this.belowNameHealthObjective = belowNameHealthObjective;

		this.tournament.getArena().playCallSound();
		this.tournament.announce(this.name, this.playerOne.getDisplayName()+"§r§E vs. §r"+this.playerTwo.getDisplayName());
		playerOne.setHealth(playerOne.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		tournament.getTeamRed().addEntry(playerOne.getName());
		sidebarHealthObjective.getScore(playerOne.getName()).setScore(Mathf.roundToInt(playerOne.getHealth()));
		belowNameHealthObjective.getScore(playerOne.getName()).setScore(Mathf.roundToInt(playerOne.getHealth()));
		playerTwo.setHealth(playerTwo.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		tournament.getTeamBlue().addEntry(playerTwo.getName());
		sidebarHealthObjective.getScore(playerTwo.getName()).setScore(Mathf.roundToInt(playerTwo.getHealth()));
		belowNameHealthObjective.getScore(playerTwo.getName()).setScore(Mathf.roundToInt(playerTwo.getHealth()));
		participantOne.sendMessage(KnightsTournamentPlugin.prefix+" Begib dich zur §cROTEN §rMarkierung.");
		participantTwo.sendMessage(KnightsTournamentPlugin.prefix+" Begib dich zur §9BLAUEN §rMarkierung.");
		if(horseOne!=null){
			horseOne.removePotionEffect(PotionEffectType.SLOW);
		}
		if(horseTwo!=null){
			horseTwo.removePotionEffect(PotionEffectType.SLOW);
		}
		KnightsArena arena = this.tournament.getArena();
		waypointOne = new Waypoint(playerOne, arena.getPosOne(), 2f, Color.RED, () -> {
			if(participantOne.getHorse()==null){
				participantOne.sendActionBar("§cDu brauchst ein Pferd.");
				return;
			}
			horseOne = participantOne.getHorse();
			participantOne.getHorse().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999, 10));
			playerOneReady = true;
			if(playerOneReady && playerTwoReady) start();
		});
		waypointTwo = new Waypoint(playerTwo, arena.getPosTwo(), 2f, Color.BLUE, () -> {
			if(participantTwo.getHorse()==null){
				participantTwo.sendActionBar("§cDu brauchst ein Pferd.");
				return;
			}
			horseTwo = participantTwo.getHorse();
			participantTwo.getHorse().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999, 10));
			playerTwoReady = true;
			if(playerOneReady && playerTwoReady) start();
		});

		NoShowTimeout noShowTimeout = new NoShowTimeout(this::checkNoShow);
		noShowTimeout.runTaskLater(KnightsTournamentPlugin.getInstance(), NO_SHOW_TIMEOUT);
		this.noShowTimeout = noShowTimeout;
	}

	private void checkNoShow(){
		// Bukkit.getLogger().info("Check no show");
		if(this.playerOneReady && !this.playerTwoReady){
			this.forfeit(participantOne);
		}
		if(this.playerTwoReady && !this.playerOneReady){
			this.forfeit(participantTwo);
		}
		if(!this.playerOneReady && !this.playerTwoReady){
			this.cancel();
		}
	}
	
	private boolean checkForfeit(){
		if(decided) return true;
		boolean forfeitOne = (playerOne==null || !playerOne.isOnline() || participantOne.isOut());
		boolean forfeitTwo = (playerTwo==null || !playerTwo.isOnline() || participantTwo.isOut());
		if(forfeitOne && !forfeitTwo){
			this.tournament.announce(playerTwo.getDisplayName(), "gewinnt automatisch.");
			this.forfeit(this.participantTwo);
			return true;
		}
		else if(!forfeitOne && forfeitTwo){
			this.tournament.announce(playerOne.getDisplayName(), "gewinnt automatisch.");
			this.forfeit(this.participantOne);
			return true;
		}
		else if(forfeitOne && forfeitTwo){
			this.tournament.announce("", "Teilnehmer offline, überspringe Match...");
			this.cancel();
			return true;
		}
		return false;
	}
	
	public void start(){
		if(this.noShowTimeout!=null) this.noShowTimeout.cancel();
		if(this.checkForfeit()) return;
		if(playerOne!=null){
			heal(playerOne);
			playerOne.setGameMode(GameMode.SURVIVAL);
			playerOne.setInvulnerable(false);
		}
		if(playerTwo!=null){
			heal(playerTwo);
			playerTwo.setGameMode(GameMode.SURVIVAL);
			playerTwo.setInvulnerable(false);
		}
		participantOne.getHorse().removePotionEffect(PotionEffectType.SLOW);
		participantTwo.getHorse().removePotionEffect(PotionEffectType.SLOW);
		this.waypointOne = null;
		this.waypointTwo = null;
		this.tournament.announce("Start!", this.playerOne.getDisplayName()+"§r§E vs. §r"+this.playerTwo.getDisplayName());
		this.tournament.getArena().playCallSound();
		this.running = true;

		this.sidebarHealthObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.belowNameHealthObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);

		Bukkit.getPluginManager().registerEvents(this, KnightsTournamentPlugin.getInstance());
		this.runTaskTimer(KnightsTournamentPlugin.getInstance(), 0, 1L);
	}

	@EventHandler(ignoreCancelled = false)
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getAction()!= Action.LEFT_CLICK_AIR && event.getAction()!=Action.LEFT_CLICK_BLOCK) return;
		Player player = event.getPlayer();
		if(!this.isParticipating(player)) return;
		if(player==playerOne){
			if(playerOneBlockCooldown>0){
				event.setCancelled(true);
				SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Blocken bereit in "+(playerOneBlockCooldown / 20.0)+"s");
				return;
			}
			playerOneBlockTime = BLOCK_TIME;
			playerOneBlockCooldown = BLOCK_COOLDOWN;
		}
		else if(player==playerTwo){
			if(playerTwoBlockCooldown>0){
				event.setCancelled(true);
				SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Blocken bereit in "+(playerTwoBlockCooldown / 20.0)+"s");
				return;
			}
			playerTwoBlockTime = BLOCK_TIME;
			playerTwoBlockCooldown = BLOCK_COOLDOWN;
		}
	}

	@Override
	public void run(){
		if(playerOneBlockTime>0){
			playerOneBlockTime--;
		}
		if(playerTwoBlockTime>0){
			playerTwoBlockTime--;
		}
		if(playerOneBlockCooldown>0){
			playerOneBlockCooldown--;
			if(playerOneBlockCooldown==0) SwissSMPler.get(playerOne).sendActionBar(ChatColor.GREEN+"Blocken bereit!");
		}
		if(playerTwoBlockCooldown>0){
			playerTwoBlockCooldown--;
			if(playerTwoBlockCooldown==0) SwissSMPler.get(playerTwo).sendActionBar(ChatColor.GREEN+"Blocken bereit!");
		}
	}

	protected void onHit(EntityDamageByLanceAttackEvent event){
		if(!running || decided){
			event.setCancelled(true);
			return;
		}

		long blockTime = (event.getEntity()==this.playerOne ? playerOneBlockTime : playerTwoBlockTime);
		if(blockTime>0){
			event.setDamage(0);
			event.setChargeEnds(true);
			Location location = event.getEntity().getLocation().add(0,event.getEntity().getHeight()/2,0);
			World world = location.getWorld();
			world.playSound(location, Sound.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1f, 1f);
			SwissSMPler.get(event.getDamager()).sendActionBar(event.getEntity().getName()+ChatColor.RESET+ChatColor.RED+" hat geblockt!");
			for(int i = 0; i < 4; i++){
				Vector randomOffset = new Vector(random.nextDouble()*2-1,random.nextDouble()*2-1,random.nextDouble()*2-1);
				Location particleLocation = location.clone().add(randomOffset.multiply(0.3f));
				world.spawnParticle(Particle.REDSTONE, particleLocation, 1, new Particle.DustOptions(Color.WHITE, 5));
			}

			onDamage(event.getDamager(), PARRY_DAMAGE);
			event.getDamager().damage(Math.min(PARRY_DAMAGE, event.getDamager().getHealth()-1));

			return;
		}

		onDamage((Player) event.getEntity(), ATTACK_DAMAGE);
		event.setDamage(Math.min(ATTACK_DAMAGE, ((Player)event.getEntity()).getHealth()-1));
	}

	private void onDamage(Player damagee, double amount){
		if(amount>=damagee.getHealth()){
			this.win(this.getOpponent(damagee), this.getParticipant(damagee), 30L);
		}
	}
	
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
		this.win(winner, loser, 0);
	}

	public void win(TournamentParticipant winner, TournamentParticipant loser, long delay){
		if(decided) return;
		decided = true;
		this.resetScoreboard();
		this.tournament.addScore(winner);
		this.tournament.broadcast(Bukkit.getPlayer(winner.getPlayerUUID()).getDisplayName()+"§r besiegt "+Bukkit.getPlayer(loser.getPlayerUUID()).getDisplayName()+"!");
		this.tournament.announce(Bukkit.getPlayer(winner.getPlayerUUID()).getDisplayName(), "besiegt "+Bukkit.getPlayer(loser.getPlayerUUID()).getDisplayName()+"§r!");

		Player loserPlayer = loser.getPlayer();
		if(loserPlayer!=null && loserPlayer.getVehicle()!=null){
			LoanerData loaner = LoanerData.load(loserPlayer).orElse(null);
			if(loaner!=null){
				loaner.apply(loserPlayer);
				loaner.delete();
			}
			else{
				loserPlayer.getVehicle().removePassenger(loserPlayer);
			}
		}

		Bukkit.getScheduler().runTaskLater(KnightsTournamentPlugin.plugin, () -> finish(winner, loser), delay);
	}

	public void forfeit(TournamentParticipant winner){
		if(decided) return;
		decided = true;
		this.tournament.addScore(winner);
		this.resetScoreboard();
		Bukkit.getScheduler().runTaskLater(KnightsTournamentPlugin.plugin, () -> finish(winner, null), 0);
	}

	@Override
	public void cancel(){
		if(this.decided) return;
		this.decided = true;
		this.resetScoreboard();
		this.finish(null, null);
	}
	
	private void finish(TournamentParticipant winner, TournamentParticipant loser){
		//cancel the loop task
		if(this.running){
			this.running = false;
			super.cancel(); // cancel the task which this object extends
		}
		if(this.noShowTimeout!=null) this.noShowTimeout.cancel();

		HandlerList.unregisterAll(this);

		if(playerOne!=null) heal(playerOne);
		if(playerTwo!=null) heal(playerTwo);
		if(participantOne!=winner) participantOne.setOut();
		if(participantTwo!=winner) participantTwo.setOut();

		if(horseOne!=null){
			horseOne.removePotionEffect(PotionEffectType.SLOW);
		}
		if(horseTwo!=null){
			horseTwo.removePotionEffect(PotionEffectType.SLOW);
		}
		//remove the waypoints
		if(this.waypointOne != null) this.waypointOne.cancel();
		if(this.waypointTwo != null) this.waypointTwo.cancel();

		tournament.concludeDuel(this, winner, 60);
	}

	private void resetScoreboard(){
		tournament.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
		tournament.getScoreboard().clearSlot(DisplaySlot.BELOW_NAME);
		if(sidebarHealthObjective!=null){
			sidebarHealthObjective.unregister();
		}
		if(belowNameHealthObjective!=null){
			belowNameHealthObjective.unregister();
		}
		this.tournament.clearTeams();
		this.tournament.showScores();
	}

	private void heal(Player player){
		if(player==null) return;
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		player.setSaturation(20);
	}

	private class NoShowTimeout extends BukkitRunnable{

		private final Runnable callback;
		private boolean executed = false;

		public NoShowTimeout(Runnable callback){
			this.callback = callback;
		}

		@Override
		public void run() {
			if(executed) return;
			executed = true;
			callback.run();
		}
	}
}
