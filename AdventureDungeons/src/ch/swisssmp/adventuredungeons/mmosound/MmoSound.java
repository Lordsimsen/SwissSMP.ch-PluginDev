package ch.swisssmp.adventuredungeons.mmosound;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.plexon21.AreaSounds.AreaSounds;

import ch.swisssmp.adventuredungeons.Main;

public class MmoSound {
	public static HashMap<UUID, BukkitTask> musicLoops = new HashMap<UUID, BukkitTask>();
	public static AreaSounds soundManager;
	public static void play(Player player, int sound_id){
		play(player, String.valueOf(sound_id));
	}
	public static void play(Player player, String sound_id){
		player.playSound(player.getLocation(), sound_id, 100, 1);
	}
	public static void playMusic(Player player, int sound_id, Long looptime){
		if(player==null) return;
		if(!player.isOnline()) return;
		Main.info("Starting sound "+sound_id+" for player "+player.getName());
		player.playSound(player.getLocation(), String.valueOf(sound_id), SoundCategory.RECORDS, 500f, 1);
		musicLoops.put(player.getUniqueId(), Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable(){
			public void run(){
				playMusic(player, sound_id, looptime);
			}
		}, looptime));
	}
}
