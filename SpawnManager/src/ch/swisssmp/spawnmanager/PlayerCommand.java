package ch.swisssmp.spawnmanager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.bukkit.Location;
import org.bukkit.World;
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
		switch(label){
		case "spawn":{
			try {
				YamlConfiguration yamlConfiguration;
				yamlConfiguration = DataSource.getYamlResponse("spawn/world_spawn.php", new String[]{
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
			break;
		}
		case "settemplespawn":{
			if(args==null || args.length<1) return false;
			World world = player.getWorld();
			Location location = player.getLocation();
			try{
				String response = DataSource.getResponse("spawn/set_temple_spawn.php", new String[]{
					"city="+URLEncoder.encode(args[0], "utf-8"),
					"world="+URLEncoder.encode(world.getName(), "utf-8"),
					"x="+(int)Math.round(location.getX()),
					"y="+(int)Math.round(location.getY()),
					"z="+(int)Math.round(location.getZ()),
					"pitch="+location.getPitch(),
					"yaw="+location.getYaw()
				});
				player.sendMessage(response);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		}
		return true;
	}

}
