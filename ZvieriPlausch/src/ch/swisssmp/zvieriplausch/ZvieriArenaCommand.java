package ch.swisssmp.zvieriplausch;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ZvieriArenaCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(args == null || args.length == 0) {
			return false;
		}
		switch(args[0]) {
		case "erstelle":
		case "create":{
			if(!(sender instanceof Player)) {
				sender.sendMessage("Nur ingame zu gebrauchen");
				return true;
			}
			if(args.length < 2) {
				return false;
			}
			Player p = (Player) sender;
			String name = args[1];
			ZvieriArena arena = ZvieriArena.create(p.getWorld(), name);
			if(arena == null) {
				p.sendMessage(ZvieriGamePlugin.getPrefix() + ChatColor.RED + " Arena konnte nicht erstellt werden");
			}
			p.getInventory().addItem(arena.getTokenStack());
			arena.openEditor(p);
			
			return true;
		}
		default: {
			return true;
		}
		}
	}
}
