package ch.swisssmp.adventuredungeons.sound;

import org.bukkit.entity.Player;

public class AdventureSound {
	public static void play(Player player, int sound_id){
		play(player, String.valueOf(sound_id));
	}
	public static void play(Player player, String sound_id){
		player.playSound(player.getLocation(), sound_id, 100, 1);
	}
}
