package ch.swisssmp.fortressassault;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Player player = (Player) sender;
		if(args==null && !label.equals("teams")) return false;
		if(args.length<1 && !label.equals("teams")) return false;
		if(label.equals("fortress") || label.equals("fa")){
			switch(args[0]){
				case "reset":
				{
					if(!FortressAssault.game.isFinished()){
						FortressAssault.game.setFinished(null);
					}
				}
				case "advance":{
					if(FortressAssault.game.getGameState()==GameState.PREGAME){
						FortressAssault.game.setBuildphase();
					}
					else if(FortressAssault.game.getGameState()==GameState.BUILD){
						FortressAssault.game.setFightphase();
					}
					else if(FortressAssault.game.getGameState()==GameState.FIGHT){
						FortressAssault.game.setFinished(null);
					}
					else{
						sender.sendMessage("Die Partie ist bereits vorbei. Neues Spiel starten mit /fa reset");
						return true;
					}
					break;
				}
				case "reload":{
					try {
						FortressAssault.loadYamls();
						PlayerClass.loadClasses();
						sender.sendMessage("Konfiguration neu geladen.");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
				case "debug":{
					FortressAssault.debug = !FortressAssault.debug;
					if(FortressAssault.debug){
						sender.sendMessage("Debug Modus eingeschaltet.");
					}
					else{
						sender.sendMessage("Debug Modus ausgeschaltet.");
					}
					break;
				}
				case "edit":{
					if(!(sender instanceof Player)) return false;
					Player player = (Player)sender;
					World template = FortressAssault.game.editTemplate();
					player.teleport(template.getSpawnLocation());
					break;
				}
				case "endedit":{
					FortressAssault.game.saveTemplate();
					break;
				}
				default:
					break;
			}
		}
		else if(label.equals("sign")){
			if(!(sender instanceof Player)){
				sender.sendMessage("Can only be used within the game.");
				return true;
			}
			ChatColor color = null;
			if(args.length>1){
				color = ChatColor.valueOf(args[1]);
			}
			if(color==null) color = ChatColor.WHITE;
			Player player = (Player) sender;
	        ArmorStand am = (ArmorStand) player.getWorld().spawn(player.getLocation(), ArmorStand.class);
	        am.setVisible(false);
	        am.setCustomName(color+args[0]);
	        am.setCustomNameVisible(true);
	        am.setGravity(false);
		}
		else if(label.equals("teams")){
			for(FortressTeam team : FortressTeam.teams.values()){
				sender.sendMessage(ChatColor.DARK_AQUA+"Team "+team.color+team.name+ChatColor.DARK_AQUA+" ("+team.player_uuids.size()+" Spieler)");
				ArrayList<String> names = new ArrayList<String>();
				for(UUID player_uuid : team.player_uuids){
					Player player = Bukkit.getPlayer(player_uuid);
					if(player==null) continue;
					names.add(player.getDisplayName());
				}
				if(names.size()>0){
					sender.sendMessage(String.join(ChatColor.GRAY+", ", names));
				}
			}
		}
		return true;
	}

}
