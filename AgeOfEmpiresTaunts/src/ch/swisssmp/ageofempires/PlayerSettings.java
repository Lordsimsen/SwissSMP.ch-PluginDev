package ch.swisssmp.ageofempires;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class PlayerSettings {
	private static HashMap<Player,TauntSetting> playerSettings = new HashMap<Player,TauntSetting>();
	
	public static void set(Player player, TauntSetting setting) {
		playerSettings.put(player, setting);
	}
	
	public static void unset(Player player) {
		playerSettings.remove(player);
	}
	
	public static TauntSetting get(Player player) {
		return playerSettings.containsKey(player) ? playerSettings.get(player) : TauntSetting.ALLOW;
	}
}
