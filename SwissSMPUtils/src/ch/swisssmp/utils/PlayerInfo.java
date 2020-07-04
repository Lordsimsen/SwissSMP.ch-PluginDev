package ch.swisssmp.utils;

import java.util.UUID;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerInfo {
	
	private final UUID player_uuid;
	private final String name;
	private final String display_name;
	private final String gender;
	
	public PlayerInfo(UUID player_uuid, String name, String display_name){
		this(player_uuid, name, display_name, "");
	}
	
	public PlayerInfo(UUID player_uuid, String name, String display_name, String gender){
		this.player_uuid = player_uuid;
		this.name = name;
		this.display_name = display_name;
		this.gender = gender;
	}
	
	public UUID getUniqueId(){
		return player_uuid;
	}
	
	public String getName(){
		return name;
	}
	
	public String getDisplayName(){
		return display_name;
	}
	
	public String getGender(){
		return gender;
	}
	
	public ItemStack getHead(){
		return SkullCreator.itemFromUuid(player_uuid);
	}
	
	public static PlayerInfo get(Player player){
		return new PlayerInfo(player.getUniqueId(),player.getName(),player.getDisplayName());
	}

	public static PlayerInfo get(ConfigurationSection dataSection){
		UUID player_uuid = UUID.fromString(dataSection.getString("player_uuid"));
		String name = dataSection.getString("name");
		String display_name = dataSection.getString("display_name");
		String gender = dataSection.contains("gender") ? dataSection.getString("gender") : "";
		return new PlayerInfo(player_uuid, name, display_name, gender);
	}
	
	public static PlayerInfo get(JsonObject json){
		UUID player_uuid = JsonUtil.getUUID("player_uuid", json);
		String name = JsonUtil.getString("name", json);
		String display_name = JsonUtil.getString("display_name", json);
		String gender = JsonUtil.getString("gender", json);
		return new PlayerInfo(player_uuid, name, display_name, gender);
	}
}
