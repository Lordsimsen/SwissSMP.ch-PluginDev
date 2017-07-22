package ch.swisssmp.event.listeners.filter;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.ConfigurationSection;

public interface PlayerFilter {
	public default boolean checkPlayer(ConfigurationSection dataSection, Player player){
		boolean result = true;
		if(player==null){
			if(dataSection.contains("require_player")) return !dataSection.getBoolean("require_player");
			else return true;
		}
		if(dataSection.contains("player_uuid")){
			result &= UUID.fromString(dataSection.getString("player_uuid"))==player.getUniqueId();
		}
		if(dataSection.contains("game_mode")){
			result &= GameMode.valueOf(dataSection.getString("game_mode"))==player.getGameMode();
		}
		if(dataSection.contains("permissions")){
			for(String permission : dataSection.getStringList("permissions")){
				result &= player.hasPermission(permission);
			}
		}
		return result;
	}
}
