package ch.swisssmp.adventuredungeons.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.adventuredungeons.world.Dungeon;
import ch.swisssmp.adventuredungeons.world.DungeonInstance;
import ch.swisssmp.utils.ChatRequest;
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
			DungeonInstance targetInstance = Dungeon.getInstance(otherPlayer);
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
			Location entryLocation = dungeon.lobby_leave.getLocation();
			if(player.getLocation().getWorld()!=entryLocation.getWorld()){
				player.sendMessage(ChatColor.RED+"Du bist nicht in der Nähe des Dungeon-Eingangs.");
				break;
			}
			if(player.getLocation().distance(entryLocation)>maxDistance){
				player.sendMessage(ChatColor.RED+"Du bist nicht in der Nähe des Dungeon-Eingangs.");
				break;
			}
			dungeon.join(player.getUniqueId(), targetInstance, targetInstance.getDifficulty());
			break;
		}
		case "quit":
		case "exit":
		case "verlassen":
		case "leave":{
			if(!(sender instanceof Player)) return true;
			Player player = (Player) sender;
			DungeonInstance dungeonInstance = Dungeon.getInstance(player);
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
			Dungeon playerDungeon = Dungeon.get(player);
			DungeonInstance dungeonInstance = Dungeon.getInstance(player);
			if(playerDungeon==null || dungeonInstance==null){
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
			Location entryLocation = playerDungeon.lobby_leave.getLocation();
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
		default:{
			break;
		}
		}
		return true;
	}

}
