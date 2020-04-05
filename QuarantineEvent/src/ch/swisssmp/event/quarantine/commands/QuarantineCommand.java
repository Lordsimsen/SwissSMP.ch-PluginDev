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
import ch.swisssmp.event.quarantine.QuarantineEventInstance;
import ch.swisssmp.event.quarantine.QuarantineEventPlugin;
import ch.swisssmp.event.quarantine.QuarantineEventInstance.Phase;

public class QuarantineCommand implements CommandExecutor {

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
		if(!arenaQuery.isPresent()) {
			sender.sendMessage(prefix+ChatColor.RED+" Arena "+arenaId+" nicht gefunden.");
			return true;
		}
		
		QuarantineArena arena = arenaQuery.get();
		QuarantineEventInstance instance = arena.getRunningInstance();
		
		switch(subcommand) {
		case "initialize":
		case "prepare":{
			if(instance!=null) {
				if(instance.getPhase()==Phase.Initialize || instance.getPhase()==Phase.Preparation) {
					sender.sendMessage(prefix+ChatColor.RED+" In der Arena "+arenaId+" wird bereits auf den Spielstart gewartet.");
					return true;
				}
				instance.setPhase(Phase.Initialize);
			}
			else {
				instance = arena.startInstance();
			}
			sender.sendMessage(prefix+ChatColor.GREEN+" Eine neue Partie wurde vorbereitet. Verwende /quarantine start [Arena-Id], um die Partie zu starten.");
			return true;
		}
		case "start":{
			if(instance==null) {
				sender.sendMessage(prefix+ChatColor.RED+" In der Arena "+arenaId+" ist noch keine Partie vorbereitet. Verwende /quarantine initialize [Arena-Id], um eine Partie vorzubereiten.");
				return true;
			}
			if(instance.getPhase()!=Phase.Preparation) {
				sender.sendMessage(prefix+ChatColor.RED+" Die Partie läuft bereits oder wurde noch nicht vorbereitet. Aktueller Status: "+instance.getPhase());
				return true;
			}
			sender.sendMessage(prefix+ChatColor.GREEN+" Partie gestartet!");
			instance.setPhase(Phase.BeforeStart);
			return true;
		}
		case "finish":
		case "cancel":{
			if(instance==null) {
				sender.sendMessage(prefix+ChatColor.RED+" In der Arena "+arenaId+" ist noch keine Partie vorbereitet. Verwende /quarantine initialize [Arena-Id], um eine Partie vorzubereiten.");
				return true;
			}
			sender.sendMessage(prefix+ChatColor.GRAY+" Partie beendet!");
			instance.setPhase(Phase.Finish);
			return true;
		}
		case "status":{
			if(instance==null) {
				sender.sendMessage(prefix+ChatColor.RED+" In der Arena "+arenaId+" ist noch keine Partie vorbereitet. Verwende /quarantine initialize [Arena-Id], um eine Partie vorzubereiten.");
				return true;
			}
			sender.sendMessage(prefix+" Aktuelle Phase: "+instance.getPhase());
			return true;
		}
		default: return false;
		}
	}
}
