package ch.swisssmp.loginrewards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public class LoginRewards extends JavaPlugin{
	
	protected static void trigger(Player player){
		HTTPRequest request = DataSource.getResponse(LoginRewardsPlugin.getInstance(), "trigger_rewards.php", new String[]{
				"player_uuid="+player.getUniqueId().toString()
		});
		request.onFinish(()->{
			trigger(request.getJsonResponse(), player);
		});
	}
	
	private static void trigger(JsonObject json, Player player){
		if(json==null || !json.has("message")) return;
		JsonElement messageElement = json.get("message");
		List<String> lines;
		if(messageElement.isJsonPrimitive()){
			lines = Collections.singletonList(messageElement.getAsString());
		}
		else{
			lines = new ArrayList<>();
			for(JsonElement element : messageElement.getAsJsonArray()){
				lines.add(element.getAsString());
			}
		}
		for(String line : lines){
			SwissSMPler.get(player).sendRawMessage(line);
		}
	}
	
	protected static void reset(){
		DataSource.getResponse(LoginRewardsPlugin.getInstance(), "reset_rewards.php");
	}
}
