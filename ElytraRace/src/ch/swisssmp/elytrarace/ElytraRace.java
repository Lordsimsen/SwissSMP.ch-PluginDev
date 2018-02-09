package ch.swisssmp.elytrarace;

import java.io.File;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import ch.swisssmp.utils.SwissSMPler;

public class ElytraRace extends JavaPlugin implements Listener{
	private Logger logger;
	private Server server;
	protected static JavaPlugin plugin;
	protected static File dataFolder;
	protected static WorldGuardPlugin worldGuardPlugin;
	
	protected static RaceContest currentContest;
	protected static HashMap<UUID, PlayerRace> races = new HashMap<UUID, PlayerRace>(); 
	
	private Random random = new Random();
	
	public void onEnable() {
		plugin = this;
		PluginDescriptionFile pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		server = getServer();
		dataFolder = this.getDataFolder();

		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("verlassen").setExecutor(playerCommand);
		this.getCommand("strecken").setExecutor(playerCommand);
		this.getCommand("wettkampf").setExecutor(playerCommand);
		this.getCommand("spielen").setExecutor(playerCommand);
		this.getCommand("zuschauen").setExecutor(playerCommand);
		this.getCommand("rangliste").setExecutor(playerCommand);
		this.getCommand("reset").setExecutor(playerCommand);
		server.getPluginManager().registerEvents(this, this);
		Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if(worldGuard instanceof WorldGuardPlugin){
			worldGuardPlugin = (WorldGuardPlugin) worldGuard;
		}
		else{
			new NullPointerException("WorldGuard missing");
		}
		RaceCourse.loadCourses();
	}
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
		HandlerList.unregisterAll(plugin);
	}
	@EventHandler(ignoreCancelled=true)
	private void onRegionEnter(RegionEnterEvent event){
		String regionName = event.getRegion().getId();
		SwissSMPler.get(event.getPlayer()).sendActionBar("");
		if(regionName.contains("course_")){
			int course_id = Integer.parseInt(regionName.split("_")[1]);
			RaceCourse raceCourse = RaceCourse.get(course_id);
			if(raceCourse==null){
				Bukkit.getLogger().info("[ElytraRace] Course "+course_id+" not found.");
				return;
			}
			event.getPlayer().teleport(raceCourse.getWorld().getSpawnLocation().add(0,0.5,0));
			event.getPlayer().setFallDistance(0);
			return;
		}
		else if(regionName.contains("boost_")){
			Player player = event.getPlayer();
			Bukkit.getScheduler().runTaskLater(ElytraRace.plugin, new Runnable(){
				public void run(){
		        	player.playSound(player.getLocation(), "14", 500, 0.95f+random.nextFloat()*0.1f);
					player.setVelocity(player.getVelocity().normalize().multiply(10).setY(0));
				}
			}, 3L);
			return;
		}
		if(event.getPlayer().getGameMode()!=GameMode.ADVENTURE) return;
		RaceCourse course = RaceCourse.get(event.getPlayer().getWorld());
		if(course==null){
			Bukkit.getLogger().info("[ElytraRace] "+regionName+" ist nicht Teil einer Rennstrecke");
			return;
		}
		int contest_id;
		if(currentContest!=null && currentContest.isRunning()) contest_id = currentContest.getContestId();
		else contest_id = 0;
		PlayerRace race = races.get(event.getPlayer().getUniqueId());
		if(regionName.equals("start")){
			race = new PlayerRace(course.getCourseId(), contest_id, event.getPlayer());
			race.start();
		}
		else if(regionName.equals("finish")){
			if(race==null) return;
			race.finish();
		}
		else if(regionName.contains("checkpoint_")){
			if(race==null) return;
			race.passCheckpoint(regionName);
		}
	}
	@EventHandler(ignoreCancelled=true)
	private void onPlayerLogin(PlayerLoginEvent event){
		Runnable runnable = new Runnable(){
			public void run(){
				preparePlayerPlay(event.getPlayer());
			}
		};
		Bukkit.getScheduler().runTaskLater(this, runnable, 20L);
	}
	@EventHandler(ignoreCancelled=true)
	private void onPlayerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		RaceCourse course = RaceCourse.get(player.getWorld());
		if(course==null)return;
		PlayerRace race = races.get(player.getUniqueId());
		if(race!=null) race.cancel();
	}
	@EventHandler
	private void onPlayerRespawn(PlayerRespawnEvent event){
		event.setRespawnLocation(event.getPlayer().getWorld().getSpawnLocation());
	}
	@EventHandler(ignoreCancelled=true)
	private void onWeatherChange(WeatherChangeEvent event){
		event.getWorld().setWeatherDuration(0);
	}
	@EventHandler(ignoreCancelled=true)
	private void onPlayerDamage(EntityDamageEvent event){
		if(!(event.getEntity() instanceof Player)) return;
		Player player = (Player)event.getEntity();
		RaceCourse course = RaceCourse.get(player.getWorld());
		if(course==null) return;
		PlayerRace race = races.get(player.getUniqueId());
		if(race!=null && race.running) race.cancel();
		else return;
		player.playEffect(EntityEffect.HURT);
		player.playSound(player.getLocation(), "22", SoundCategory.RECORDS, 500f,1f);
		event.setCancelled(true);
	}
	@EventHandler
	private void onPlayerWorldChange(PlayerChangedWorldEvent event){
		Player player = event.getPlayer();
		RaceCourse course = RaceCourse.get(player.getWorld());
		if(course==null) return;
		Bukkit.getScheduler().runTaskLater(ElytraRace.plugin, new Runnable(){
			public void run(){
				player.playSound(new Location(player.getWorld(),0,0,0), course.getSoundtrack(), SoundCategory.RECORDS, 1f, 1f);
				course.showIntroduction(player);
			}
		}, 5L);
		Bukkit.getScheduler().runTaskLater(ElytraRace.plugin, new Runnable(){
			public void run(){
				player.stopSound(course.getSoundtrack());
			}
		}, 20L);
	}
	protected static void preparePlayerPlay(Player player){
		RaceCourse raceCourse = RaceCourse.get(player.getWorld());
		if(raceCourse==null){
			player.sendMessage("[ElytraRace] §8Betrete zuerst eine Rennstrecke.");
			return;
		}
		player.setGameMode(GameMode.ADVENTURE);
		player.teleport(raceCourse.getWorld().getSpawnLocation().add(0,0.5,0));
		player.getInventory().clear();
		player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
		player.setHealth(20);
		player.setFlying(false);
		player.setAllowFlight(false);
		
	}
	protected static void preparePlayerSpectate(Player player){
		RaceCourse raceCourse = RaceCourse.get(player.getWorld());
		if(raceCourse==null){
			player.sendMessage("[ElytraRace] §8Betrete zuerst eine Rennstrecke.");
			return;
		}
		player.setGameMode(GameMode.SPECTATOR);
		player.teleport(raceCourse.getWorld().getSpawnLocation().add(0,0.5,0));
	}
	protected static String getContestName(){
		if(currentContest==null || (!currentContest.isRunning()&&!currentContest.isFinished())){
			return "Aufwärmen";
		}
		else if(currentContest.isFinished()){
			return "Einzelflug";
		}
		else{
			return currentContest.getName();
		}
	}
}
