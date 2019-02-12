package ch.swisssmp.spawnmanager;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)){
			return true;
		}
		Player player = (Player) sender;
		switch(label){
		case "spawn":{
			HTTPRequest request = DataSource.getResponse(SpawnManager.getInstance(), "world_spawn.php", new String[]{
					"world="+URLEncoder.encode(player.getWorld().getName())
			});
			request.onFinish(()->{
				YamlConfiguration yamlConfiguration = request.getYamlResponse();
				if(yamlConfiguration==null) return;
				Location location = yamlConfiguration.getLocation("spawnpoint");
				if(location!=null){
					player.teleport(location);
				}
			});
			break;
		}
		case "settemplespawn":{
			if(args==null || args.length<1) return false;
			World world = player.getWorld();
			Location location = player.getLocation();
			HTTPRequest request = DataSource.getResponse(SpawnManager.getInstance(), "set_temple_spawn.php", new String[]{
				"city="+URLEncoder.encode(args[0]),
				"world="+URLEncoder.encode(world.getName()),
				"x="+(int)Math.round(location.getX()),
				"y="+(int)Math.round(location.getY()),
				"z="+(int)Math.round(location.getZ()),
				"pitch="+location.getPitch(),
				"yaw="+location.getYaw()
			});
			request.onFinish(()->{
				if(!request.getResponse().isEmpty()) player.sendMessage(request.getResponse());
			});
			
			return true;
		}
		}
		return true;
	}
}
