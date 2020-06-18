package ch.swisssmp.ceremonies;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Spectator {

	private final JavaPlugin plugin;
	private final Ceremony ceremony;
	private final Player player;
	private Location originalLocation;
	private GameMode originalGameMode;
	
	private boolean ready = false;
	
	protected Spectator(JavaPlugin plugin, Ceremony ceremony, Player player){
		this.plugin = plugin;
		this.ceremony = ceremony;
		this.player = player;
	}
	
	public void initialize(){
		this.originalLocation = player.getLocation();
		this.originalGameMode = player.getGameMode();
		this.player.teleport(ceremony.getInitialSpectatorLocation());
		this.player.setGameMode(GameMode.ADVENTURE);
		player.setInvulnerable(true);
		Bukkit.getScheduler().runTaskLater(plugin, ()->{
			ready = true;
			player.setInvulnerable(false);
			player.setGameMode(GameMode.SPECTATOR);
		}, 60L);
	}
	
	public void update(Location location){
		if(!ready) return;
		this.player.teleport(location);
	}
	
	public void leave(){
		player.setGameMode(originalGameMode);
		Bukkit.getScheduler().runTaskLater(plugin, ()->{
			this.player.teleport(originalLocation);
		}, 5L);
	}
}
