package ch.swisssmp.adventuredungeons.sound;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

public class AdventureSound {
	public static HashMap<UUID, MusicLoop> musicLoops = new HashMap<UUID, MusicLoop>();
	public static void play(Player player, int sound_id){
		play(player, String.valueOf(sound_id));
	}
	public static void play(Player player, String sound_id){
		player.playSound(player.getLocation(), sound_id, 100, 1);
	}
	public static void playMusic(Player player, int sound_id, Long looptime){
		if(player==null) return;
		if(!player.isOnline()) return;
		if(musicLoops.containsKey(player.getUniqueId())){
			MusicLoop running = musicLoops.get(player.getUniqueId());
			if(running.getMusicId()==sound_id) return;
			running.setMusicId(sound_id);
			running.setMusicLength(looptime);
			running.cancel();
			running.start();
		}
		MusicLoop musicLoop = new MusicLoop(player, sound_id, looptime);
		musicLoops.put(player.getUniqueId(), musicLoop);
		musicLoop.start();
	}
}
