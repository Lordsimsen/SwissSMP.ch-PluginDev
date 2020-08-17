package ch.swisssmp.utils;

import java.util.Optional;
import java.util.UUID;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerData {
	
	private final UUID uid;
	private final String name;
	private final String displayName;
	private final String gender;
	
	public PlayerData(UUID playerUid, String name, String display_name){
		this(playerUid, name, display_name, "");
	}
	
	public PlayerData(UUID playerUid, String name, String displayName, String gender){
		this.uid = playerUid;
		this.name = name;
		this.displayName = displayName;
		this.gender = gender;
	}
	
	public UUID getUniqueId(){
		return uid;
	}
	
	public String getName(){
		return name;
	}
	
	public String getDisplayName(){
		return displayName;
	}
	
	public String getGender(){
		return gender;
	}
	
	public ItemStack getHead(){
		return SkullCreator.itemFromUuid(uid);
	}
	
	public static PlayerData get(Player player){
		return new PlayerData(player.getUniqueId(),player.getName(),player.getDisplayName());
	}

	public JsonObject toJson(){
		JsonObject json = new JsonObject();
		JsonUtil.set("player_uuid", uid, json);
		JsonUtil.set("name", name, json);
		JsonUtil.set("display_name", displayName, json);
		JsonUtil.set("gender", gender, json);
		return json;
	}
	
	public static Optional<PlayerData> get(JsonObject json){
		UUID uid = JsonUtil.getUUID("player_uuid", json);
		String name = JsonUtil.getString("name", json);
		String displayName = JsonUtil.getString("display_name", json);
		if(uid==null || name==null || displayName==null) return Optional.empty();
		String gender = JsonUtil.getString("gender", json);
		return Optional.of(new PlayerData(uid, name, displayName, gender));
	}
}
