package ch.swisssmp.antiguest.preventions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.SwissSMPler;

public abstract class Prevention {
	public void trigger(Player player) {
		SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Keine Berechtigung.");
	}
}
