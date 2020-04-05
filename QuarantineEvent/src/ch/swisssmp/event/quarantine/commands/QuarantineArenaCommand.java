package ch.swisssmp.event.quarantine.commands;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.event.quarantine.ArenaContainer;
import ch.swisssmp.event.quarantine.QuarantineArena;
import ch.swisssmp.event.quarantine.QuarantineEventPlugin;

public class QuarantineArenaCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length==0) {
			return false;
		}
		
		String prefix = QuarantineEventPlugin.getPrefix();
		String subcommand = args[0];
		
		World world;
		String arenaId;
		Player player = null;
				
		if(sender instanceof Player) {
			player = (Player) sender;
			world = player.getWorld();
			arenaId = args.length>1 ? args[1] : null;
			if(arenaId==null) {
				return false;
			}
		}
		else {
			if(args.length<3) return false;
			String worldName = args[1];
			arenaId = args[2];
			world = Bukkit.getWorld(worldName);
			if(world==null) {
				sender.sendMessage(prefix+ChatColor.RED+" Welt "+worldName+" nicht gefunden.");
				return true;
			}
		}
		
		ArenaContainer container = ArenaContainer.get(world);
		
		Optional<QuarantineArena> arenaQuery = container!=null ? container.getArena(arenaId) : null;
		if(!arenaQuery.isPresent() && !subcommand.equals("create")) {
			sender.sendMessage(prefix+ChatColor.RED+" Arena "+arenaId+" nicht gefunden.");
			return true;
		}
		
		QuarantineArena arena = arenaQuery.isPresent() ? arenaQuery.get() : null;
		
		switch(subcommand) {
		case "create":{
			if(arena==null) {
				arena = container.createArena(arenaId);
				container.save();
				sender.sendMessage(prefix+ChatColor.GREEN+" Arena "+arenaId+" erstellt.");
			}
			else {
				sender.sendMessage(prefix+ChatColor.RED+" Arena "+arenaId+" existiert bereits.");
			}
			if(player!=null) {
				world.dropItem(player.getEyeLocation(), arena.getItemStack());
			}
			return true;
		}
		case "delete":
		case "remove":{
			if(arena==null) {
				sender.sendMessage(prefix+ChatColor.RED+" Arena "+arenaId+" nicht gefunden.");
				return true;
			}
			arena.remove();
			container.save();
			sender.sendMessage(prefix+ChatColor.GREEN+" Arena "+arenaId+" gelöscht.");
		}
		default: return false;
		}
	}

}