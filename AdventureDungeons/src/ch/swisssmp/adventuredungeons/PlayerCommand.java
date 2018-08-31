package ch.swisssmp.adventuredungeons;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.ChatRequest;
import ch.swisssmp.utils.SwissSMPler;
import net.md_5.bungee.api.ChatColor;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch(label){
		case "join":{
			if(!(sender instanceof Player)){
				return true;
			}
			if(args==null) {
				sender.sendMessage("Keinen Spieler definiert.");
				return false;
			}
			if(args.length<1) {
				sender.sendMessage("Keinen Spieler definiert.");
				return false;
			}
			String otherPlayerName = args[0];
			Player otherPlayer = Bukkit.getPlayer(otherPlayerName);
			if(otherPlayer==null){
				sender.sendMessage(ChatColor.RED+otherPlayerName+" nicht gefunden.");
				return true;
			}
			DungeonInstance targetInstance = DungeonInstance.get(otherPlayer);
			if(targetInstance==null) {
				sender.sendMessage(otherPlayerName+" ist momentan nicht in einem Dungeon.");
				return true;
			}
			if(targetInstance.isRunning()){
				sender.sendMessage(ChatColor.RED+"Diese Instanz wurde bereits gestartet und kann nicht mehr betreten werden.");
				break;
			}
			Player player = (Player) sender;
			if(targetInstance.getPlayerManager().getPlayers().contains(player.getUniqueId().toString())){
				sender.sendMessage(ChatColor.YELLOW+"Du bist bereits in dieser Instanz.");
				break;
			}
			if(!targetInstance.getPlayerManager().isInvitedPlayer(player.getUniqueId())){
				sender.sendMessage(ChatColor.RED+"Du bist nicht in diese Instanz eingeladen worden.");
				sender.sendMessage(ChatColor.YELLOW+"Ein Mitglied dieser Instanz kann dich mit '/invite "+player.getName()+"' einladen.");
				break;
			}
			Dungeon dungeon = Dungeon.get(targetInstance.getDungeonId());
			int maxDistance = 50;
			Location entryLocation = dungeon.getLobbyLeave().getLocation(Bukkit.getWorlds().get(0));
			if(player.getLocation().getWorld()!=entryLocation.getWorld()){
				player.sendMessage(ChatColor.RED+"Du bist nicht in der Nähe des Dungeon-Eingangs.");
				break;
			}
			if(player.getLocation().distance(entryLocation)>maxDistance){
				player.sendMessage(ChatColor.RED+"Du bist nicht in der Nähe des Dungeon-Eingangs.");
				break;
			}
			dungeon.join(player, targetInstance, targetInstance.getDifficulty());
			break;
		}
		case "quit":
		case "exit":
		case "verlassen":
		case "leave":{
			if(!(sender instanceof Player)) return true;
			Player player = (Player) sender;
			DungeonInstance dungeonInstance = DungeonInstance.get(player);
			if(dungeonInstance==null) return true;
			dungeonInstance.getPlayerManager().leave(player.getUniqueId());
			break;
		}
		case "refuse":{
			if(!(sender instanceof Player)) return true;
			if(args==null) return true;
			if(args.length<1) return true;
			Player player = (Player) sender;
			String otherPlayerName = args[0];
			Player otherPlayer = Bukkit.getPlayer(otherPlayerName);
			if(otherPlayer==null) return true;
			otherPlayer.sendMessage(player.getDisplayName()+ChatColor.RED+" hat deine Anfrage abgelehnt.");
			break;
		}
		case "inv":
		case "invite":{
			if(!(sender instanceof Player)) return true;
			if(args==null) return false;
			if(args.length<1) return false;
			Player player = (Player) sender;
			DungeonInstance dungeonInstance = DungeonInstance.get(player);
			if(dungeonInstance==null){
				return true;
			}
			if(dungeonInstance.isRunning()){
				sender.sendMessage(ChatColor.RED+"Die Instanz wurde bereits gestartet und es können keine neuen Spieler eingeladen werden.");
				break;
			}
			String otherPlayerName = args[0];
			Player otherPlayer = Bukkit.getPlayer(otherPlayerName);
			if(otherPlayer==null){
				sender.sendMessage(ChatColor.RED+otherPlayerName+" nicht gefunden.");
				return true;
			}
			int maxDistance = 50;
			Location entryLocation = dungeonInstance.getDungeon().getLobbyLeave().getLocation(Bukkit.getWorlds().get(0));
			if(entryLocation==null){
				player.sendMessage(ChatColor.RED+"Ein Fehler ist aufgetreten. Konnte nicht überprüfen, ob "+otherPlayer.getDisplayName()+ChatColor.RESET+" in der Nähe des Eingangs ist.");
				return true;
			}
			if(otherPlayer.getLocation().getWorld()!=entryLocation.getWorld()){
				player.sendMessage(ChatColor.RED+otherPlayer.getDisplayName()+ChatColor.RED+" ist nicht in "+entryLocation.getWorld().getName()+".");
				break;
			}
			if(otherPlayer.getLocation().distance(entryLocation)>maxDistance){
				player.sendMessage(ChatColor.RED+otherPlayer.getDisplayName()+ChatColor.RED+" ist zu weit weg.");
				break;
			}
			dungeonInstance.getPlayerManager().addInvitedPlayer(otherPlayer.getUniqueId());
		    ChatRequest mmoRequest = new ChatRequest(ChatColor.YELLOW+"Möchtest du der Gruppe von "+player.getDisplayName()+ChatColor.YELLOW+" beitreten?");
		    mmoRequest.addOption("Beitreten", "join "+player.getName());
		    mmoRequest.addOption("Ablehnen", "refuse "+player.getName());
		    mmoRequest.send(otherPlayer.getUniqueId());
		    dungeonInstance.getPlayerManager().addInvitedPlayer(otherPlayer.getUniqueId());
			break;
		}
    	case "ready":{
    		String player_string;
    		Player player;
    		if((sender instanceof Player)){
    			player = (Player) sender;
	    		player_string = player.getUniqueId().toString();
    		}
    		else{
    			if(args.length>0){
    				player_string = args[0];
    				player = Bukkit.getPlayer(player_string);
    				if(player!=null){
    					player_string = player.getUniqueId().toString();
    				}
    				else{
    					sender.sendMessage("[AdventureDungeons] Spieler '"+player_string+"' nicht gefunden.");
    					return true;
    				}
    			}
    			else return false;
    		}
    		DungeonInstance dungeonInstance = DungeonInstance.get(player);
    		if(dungeonInstance==null) return true;
    		if(dungeonInstance.isRunning()) return true;
			SwissSMPler swisssmpler = SwissSMPler.get(player);
    		if(dungeonInstance.getPlayerManager().toggleReady(player_string)){
    			swisssmpler.sendActionBar("Du bist nun bereit.");
    		}
    		else{
    			swisssmpler.sendActionBar("Du bist nun nicht mehr bereit.");
    		}
    		break;
    	}
		default:{
			break;
		}
		}
		return true;
	}

}
