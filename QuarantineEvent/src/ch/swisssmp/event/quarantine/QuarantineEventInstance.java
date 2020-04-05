package ch.swisssmp.event.quarantine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.event.quarantine.tasks.BeforeStartPhase;
import ch.swisssmp.event.quarantine.tasks.FinishPhase;
import ch.swisssmp.event.quarantine.tasks.InitializePhase;
import ch.swisssmp.event.quarantine.tasks.PlayPhase;
import ch.swisssmp.event.quarantine.tasks.PreparationPhase;
import ch.swisssmp.event.quarantine.tasks.QuarantineEventInstanceTask;
import ch.swisssmp.event.quarantine.tasks.ReviewPhase;
import ch.swisssmp.event.quarantine.tasks.StartPhase;
import ch.swisssmp.event.quarantine.tasks.TaskExecutor;
import ch.swisssmp.utils.Position;
import ch.swisssmp.utils.SwissSMPler;

public class QuarantineEventInstance {
	
	private final QuarantineArena arena;
	
	private Phase phase;
	
	private final TaskExecutor taskExecutor = new TaskExecutor();
	private BukkitTask bukkitTask;
	
	private final Collection<UUID> playerUids = new ArrayList<UUID>();
	private final Collection<UUID> survivors = new ArrayList<UUID>();
	private final Collection<UUID> infected = new ArrayList<UUID>();
	private final Collection<MaterialSpawner> spawners = new ArrayList<MaterialSpawner>();
	
	private final ScoreController controller = new ScoreController();
	
	protected QuarantineEventInstance(QuarantineArena arena) {
		this.arena = arena;
	}
	
	public Collection<UUID> getPlayers(){
		return playerUids;
	}
	
	protected void start() {
		if(bukkitTask!=null) return;
		bukkitTask = Bukkit.getScheduler().runTaskTimer(QuarantineEventPlugin.getInstance(), taskExecutor, 0, 1);
		setPhase(Phase.Initialize);
	}
	
	public void cancel() {
		if(bukkitTask!=null) bukkitTask.cancel();
		taskExecutor.stopActiveTask();
		arena.clearRunningInstance();
		
		String prefix = QuarantineEventPlugin.getPrefix();
		Bukkit.getLogger().info(prefix+ChatColor.GRAY+" EventInstance cancelled!");
		
		
		for(UUID playerUid : this.playerUids) {
			SwissSMPler.get(playerUid).sendMessage(prefix+ChatColor.GRAY+" Spielpartie abgebrochen!");
		}
	}
	
	public QuarantineArena getArena() {
		return arena;
	}
	
	public ScoreController getScoreController() {
		return controller;
	}
	
	public void setPlayers(Collection<UUID> playerUids) {
		if(phase!=Phase.Initialize) return;
		this.playerUids.clear();
		this.playerUids.addAll(playerUids);
	}
	
	public void addPlayer(Player player) {
		addPlayer(player.getUniqueId());
	}
	
	public void addPlayer(UUID playerUid) {
		this.playerUids.add(playerUid);
		Player player = Bukkit.getPlayer(playerUid);
		if(player==null) {
			removePlayer(playerUid);
			return;
		}
		
		player.setGameMode(GameMode.ADVENTURE);
	}
	
	public void removePlayer(UUID playerUid) {
		this.playerUids.remove(playerUid);
	}
	
	public boolean isSurvivor(Player player) {
		return isSurvivor(player.getUniqueId());
	}
	
	public boolean isSurvivor(UUID playerUid) {
		return survivors.contains(playerUid);
	}
	
	public boolean isInfected(Player player) {
		return isInfected(player.getUniqueId());
	}
	
	public boolean isInfected(UUID playerUid) {
		return infected.contains(playerUid);
	}
	
	public void setSurvivors(Collection<UUID> survivors) {
		this.survivors.clear();
		this.survivors.addAll(survivors);
	}
	
	public void setInfected(Collection<UUID> infected) {
		this.infected.clear();
		this.infected.addAll(infected);
	}
	
	public void infect(Player survivor, Player infected) {
		if(!isSurvivor(survivor)) return;
		World world = survivor.getWorld();
		this.infected.add(survivor.getUniqueId());
		this.survivors.remove(survivor.getUniqueId());
		for(ItemStack itemStack : survivor.getInventory()) {
			if(itemStack==null) continue;
			world.dropItem(survivor.getLocation(), itemStack);
		}
		this.clearEffects(survivor);
		this.initializeInfected(survivor);
		survivor.setHealth(survivor.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		survivor.sendMessage(ChatColor.RED+"Du wurdest infiziert! Zeit, die Krankheit zu verteilen!");
		if(infected!=null) {
			SwissSMPler.get(infected).sendActionBar(survivor.getDisplayName()+ChatColor.RESET+ChatColor.LIGHT_PURPLE+" infiziert!");
		}
		world.playSound(survivor.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, SoundCategory.HOSTILE, 1, 1);
		if(this.survivors.size()==0) {
			this.setPhase(Phase.Finish);
		}
		
		controller.removeScore(survivor.getUniqueId(), 10);
		if(infected!=null) {
			controller.addScore(infected.getUniqueId(), 10);
		}
	}
	
	public void cure(Player infected, Player survivor) {
		if(!isInfected(infected)) return;
		World world = infected.getWorld();
		this.infected.remove(infected.getUniqueId());
		this.survivors.add(infected.getUniqueId());
		this.clearEffects(infected);
		this.initializeSurvivor(infected);
		infected.sendMessage(ChatColor.AQUA+"Du wurdest geheilt! Hilf nun den Überlebenden bei der Suche nach einem Heilmittel.");
		SwissSMPler.get(survivor).sendActionBar(infected.getDisplayName()+ChatColor.RESET+ChatColor.AQUA+" geheilt!");
		world.playSound(infected.getLocation(), Sound.ITEM_TOTEM_USE, SoundCategory.HOSTILE, 1, 1);
		if(this.infected.size()==0) {
			this.setPhase(Phase.Finish);
		}
		controller.removeScore(infected.getUniqueId(), 10);
		if(infected!=null) {
			controller.addScore(survivor.getUniqueId(), 10);
		}
	}
	
	public void initializeInfected(Player player) {
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		player.setSaturation(Float.MAX_VALUE);
		Location spawn = getArena().getRespawn().getLocation(getArena().getWorld());
		player.setBedSpawnLocation(spawn, true);
		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, true));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1, false, true));
		String displayName = ChatColor.LIGHT_PURPLE+player.getName();
		player.setDisplayName(displayName);
		player.setPlayerListName(displayName);
	}
	
	public void initializeSurvivor(Player player) {
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		player.setSaturation(0);
		String displayName = ChatColor.GREEN+player.getName();
		player.setDisplayName(displayName);
		player.setPlayerListName(displayName);
	}
	
	public void clearEffects(Player player) {
		player.setGameMode(GameMode.ADVENTURE);
		player.getInventory().clear();
		for(PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		player.setSaturation(0);
		player.setFoodLevel(20);
		player.setExp(0);
	}
	
	public boolean isPlaying(Player player) {
		return playerUids.contains(player.getUniqueId());
	}
	
	public boolean canJoin(Player player) {
		return player.getGameMode()!=GameMode.CREATIVE && player.getGameMode()!=GameMode.SPECTATOR && player.hasPermission("quarantine.participate") && !isPlaying(player);
	}
	
	public Optional<MaterialSpawner> getSpawner(Block block) {
		return spawners.stream().filter(s->s.isAt(block)).findAny();
	}
	
	public void findSpawners() {
		if(spawners.size()>0) {
			for(MaterialSpawner spawner : this.spawners) {
				spawner.reset();
			}
			spawners.clear();
		}
		World world = arena.getWorld();
		Position min = arena.getBoundingBoxMin();
		Position max = arena.getBoundingBoxMax();
		for(int x = min.getBlockX(); x <=max.getBlockX(); x++) {
			for(int y = min.getBlockY(); y <= max.getBlockY(); y++) {
				for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
					Block block = world.getBlockAt(x, y, z);
					QuarantineMaterial material = QuarantineMaterial.of(block.getType());
					if(material==null || material==QuarantineMaterial.EMPTY_SHELF) continue;
					float spawnrate = this.arena.getSpawnrate(material);
					MaterialSpawner spawner = new MaterialSpawner(block,material,spawnrate);
					spawners.add(spawner);
				}
			}
		}
	}
	
	public Collection<MaterialSpawner> getSpawners(){
		return spawners;
	}
	
	public Phase getPhase() {
		return phase;
	}
	
	public void setPhase(Phase phase) {
		
		this.phase = phase;
		
		QuarantineEventInstanceTask task;
		switch(phase) {
		case Initialize:
			task = new InitializePhase(this);
			break;
		case Preparation:
			task = new PreparationPhase(this);
			break;
		case BeforeStart:
			task = new BeforeStartPhase(this);
			break;
		case Start:
			task = new StartPhase(this);
			break;
		case Play:
			task = new PlayPhase(this);
			break;
		case Finish:
			task = new FinishPhase(this);
			break;
		case Review:
			task = new ReviewPhase(this);
			break;
		default: return;
		}
		taskExecutor.setTask(task);
	}
	
	public enum Phase{
		Initialize,
		Preparation,
		BeforeStart,
		Start,
		Play,
		Finish,
		Review
	}
}
