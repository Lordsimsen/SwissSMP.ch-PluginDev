package ch.swisssmp.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldsCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player){
			WorldsView.open((Player)sender);
			return true;
		}
		else{
			List<String> worldNames = new ArrayList<String>();
			for(World world : Bukkit.getWorlds()){
				worldNames.add(world.getName());
			}
			sender.sendMessage("[WorldManager] Geladene Welten: "+String.join(", ", worldNames));
			return true;
		}
	}
}
