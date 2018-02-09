package ch.swisssmp.adventuredungeons.sound;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.adventuredungeons.AdventureDungeons;

public class MusicLoop {
	private final Player player;
	private int music_id;
	private long music_length;
	private BukkitTask pending;
	public MusicLoop(Player player, int music_id, long music_length){
		this.player = player;
		this.music_id = music_id;
		this.music_length = music_length;
	}
	public void setMusicId(int music_id){
		this.music_id = music_id;
	}
	public int getMusicId(){
		return this.music_id;
	}
	public void setMusicLength(long music_length){
		this.music_length = music_length;
	}
	public void start(){
		runLoop();
	}
	private void runLoop(){
		if(!player.isOnline()){
			this.cancel();
			AdventureSound.musicLoops.remove(this.player.getUniqueId());
		}
		player.playSound(player.getLocation(), String.valueOf(music_id), 500f, 1);
		if(this.music_length==0){
			Bukkit.getLogger().info("[AdventureDungeons] Warnung: Musik-Loop kann mit Länge 0 nicht starten! Track: "+music_id);
			return;
		}
		pending = Bukkit.getScheduler().runTaskLater(AdventureDungeons.plugin, new Runnable(){
			public void run(){
				runLoop();
			}
		}, this.music_length);
	}
	public void cancel(){
		if(pending!=null) pending.cancel();
	}
}
