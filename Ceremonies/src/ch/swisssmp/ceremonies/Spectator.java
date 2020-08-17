package ch.swisssmp.ceremonies;

import ch.swisssmp.camerastudio.CameraStudio;
import ch.swisssmp.camerastudio.ViewerInfo;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Spectator {

	private final JavaPlugin plugin;
	private final Ceremony ceremony;
	private final Player player;
	
	private boolean ready = false;
	
	protected Spectator(JavaPlugin plugin, Ceremony ceremony, Player player){
		this.plugin = plugin;
		this.ceremony = ceremony;
		this.player = player;
	}

	public Player getPlayer(){return player;}
	
	public void initialize(){
		ViewerInfo.of(player).save();
		this.player.teleport(ceremony.getInitialSpectatorLocation());
		this.player.setGameMode(GameMode.ADVENTURE);
		player.setInvulnerable(true);
		CameraStudio.inst().hidePlayer(player);
		Bukkit.getScheduler().runTaskLater(plugin, ()->{
			ready = true;
			player.setGameMode(GameMode.ADVENTURE);
			player.setInvulnerable(true);
		}, 5L);
	}
	
	public void update(Location location){
		if(!ready) return;
		this.player.teleport(location);
	}

	public void cancel(){
		ready = false;
	}

	public void leave(){
		Bukkit.getScheduler().runTaskLater(plugin, ()->{
			CameraStudio.inst().unhidePlayer(player);
			ViewerInfo.load(player).ifPresent((info)->{
				info.apply(player);
				info.delete();
			});
		}, 5L);
	}
}
