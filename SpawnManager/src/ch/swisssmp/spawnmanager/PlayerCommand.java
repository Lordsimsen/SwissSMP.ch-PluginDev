package ch.swisssmp.spawnmanager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)){
			return true;
		}
		Player player = (Player) sender;
		try {
			YamlConfiguration yamlConfiguration;
			yamlConfiguration = DataSource.getYamlResponse("players/spawn.php", new String[]{
					"player="+player.getUniqueId().toString(),
					"world="+URLEncoder.encode(player.getWorld().getName(), "utf-8")
			});
			if(yamlConfiguration==null) return true;
			Location location = yamlConfiguration.getLocation("spawnpoint");
			if(location!=null){
				player.teleport(location);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
