package ch.swisssmp.craftmmo.mmosound;

import org.bukkit.entity.Player;

import com.plexon21.AreaSounds.AreaSounds;

public class MmoSound {
	public static AreaSounds soundManager;
	public static void play(Player player, int sound_id){
		play(player, String.valueOf(sound_id));
	}
	public static void play(Player player, String sound_id){
		player.playSound(player.getLocation(), sound_id, 1, 1);
	}
}
