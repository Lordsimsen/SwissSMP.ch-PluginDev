package ch.swisssmp.city.ceremony;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.SwissSMPler;

public class SpectateCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return true;
		Player player = (Player) sender;
		if(player.getWorld()!=Bukkit.getWorlds().get(0)){
			SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Befehl funktioniert nur in der Hauptwelt");
			return true;
		}
		Ceremony ceremony = args!=null && args.length>0 ? Ceremony.get(args[0].toLowerCase()) : Ceremony.getLast();
		if(ceremony==null){
			SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Momentan läuft keine Zeremonie.");
			return true;
		}
		ceremony.addSpectator(player);
		return true;
	}

}
