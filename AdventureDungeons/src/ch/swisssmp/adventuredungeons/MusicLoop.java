package ch.swisssmp.adventuredungeons;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class MusicLoop implements Runnable{
	private static HashMap<UUID, MusicLoop> musicLoops = new HashMap<UUID, MusicLoop>();
	
	private final Player player;
	private final String music_id;
	
	private BukkitTask task;
	
	private MusicLoop(Player player, String music_id){
		this.player = player;
		this.music_id = music_id;
	}
	
	@Override
	public void run(){
		if(!player.isOnline()){
			musicLoops.remove(this.player.getUniqueId());
			this.task.cancel();
			musicLoops.remove(this.player.getUniqueId());
		}
		player.stopSound(Sound.MUSIC_CREATIVE, SoundCategory.MUSIC);
		player.stopSound(Sound.MUSIC_CREDITS, SoundCategory.MUSIC);
		player.stopSound(Sound.MUSIC_DRAGON, SoundCategory.MUSIC);
		player.stopSound(Sound.MUSIC_END, SoundCategory.MUSIC);
		player.stopSound(Sound.MUSIC_GAME, SoundCategory.MUSIC);
		player.stopSound(Sound.MUSIC_MENU, SoundCategory.MUSIC);
		player.stopSound(Sound.MUSIC_NETHER_BASALT_DELTAS, SoundCategory.MUSIC);
		player.stopSound(Sound.MUSIC_NETHER_NETHER_WASTES, SoundCategory.MUSIC);
		player.stopSound(Sound.MUSIC_NETHER_CRIMSON_FOREST, SoundCategory.MUSIC);
		player.stopSound(Sound.MUSIC_NETHER_SOUL_SAND_VALLEY, SoundCategory.MUSIC);
		player.stopSound(Sound.MUSIC_NETHER_WARPED_FOREST, SoundCategory.MUSIC);
		player.playSound(player.getLocation(), this.music_id, SoundCategory.MUSIC, 500f, 1);
	}
	
	public void cancel(){
		player.stopSound(this.music_id);
		this.task.cancel();
	}
	
	private static MusicLoop run(Player player, String music_id, long looptime){
		MusicLoop previous = musicLoops.get(player.getUniqueId());
		if(previous!=null){
			previous.cancel();
		}
		MusicLoop result = new MusicLoop(player, music_id);
		result.task = Bukkit.getScheduler().runTaskTimer(AdventureDungeons.getInstance(), result, 0, looptime);
		musicLoops.put(player.getUniqueId(), result);
		return result;
	}
	
	public static void update(Player player){
    	if(musicLoops.containsKey(player.getUniqueId())){
    		return;
    	}
		DungeonInstance instance = DungeonInstance.get(player);
		if(instance!=null && instance.isRunning() && !instance.getBackgroundMusic().isEmpty()){
			MusicLoop.run(player, instance.getBackgroundMusic(), instance.getMusicLoopTime());
		}
	}
}
