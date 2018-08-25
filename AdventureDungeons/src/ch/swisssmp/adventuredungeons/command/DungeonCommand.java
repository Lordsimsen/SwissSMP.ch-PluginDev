package ch.swisssmp.adventuredungeons.command;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.adventuredungeons.AdventureSound;
import ch.swisssmp.adventuredungeons.Dungeon;
import ch.swisssmp.adventuredungeons.DungeonInstance;

public class DungeonCommand implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	switch(label){
	    	case "dungeon":{
	    		if(args==null || args.length==0) return false;
	    		switch(args[0]){
			    	case "announce":{
			    		if(args.length<3) return false;
			    		try{
			    			int instance_id = Integer.parseInt(args[1]);
			    			List<String> messageParts = new ArrayList<String>();
			    			for(int i = 2; i < args.length; i++){
			    				messageParts.add(args[i]);
			    			}
			    			DungeonInstance dungeonInstance = DungeonInstance.get(instance_id);
			    			if(dungeonInstance==null){
			    				sender.sendMessage("[AdventureDungeons] Instanz nicht gefunden.");
			    				return true;
			    			}
			    			else{
			    				dungeonInstance.getPlayerManager().announce(String.join(" ", messageParts));
			    			}
			    		}
			    		catch(Exception e){
			    			e.printStackTrace();
			    			return false;
			    		}
			    		break;
			    	}
			    	case "join":{
			    		//dungeon join [Dungeon-ID] [Schwierigkeit] (Spieler) (Anderer Spieler)
			    		Player player;
			    		DungeonInstance targetInstance;
			    		if(args.length<2){
			    			return true;
			    		}
			    		Difficulty difficulty;
			    		if(args.length<3){
			    			difficulty = Difficulty.HARD;
			    			targetInstance = null;
			    			if(!(sender instanceof Player)){
			    				sender.sendMessage("Bitte Spieler angeben oder ingame ausführen.");
			    				return true;
			    			}
			    			player = (Player)sender;
			    		}
			    		else{
				    		try{
				    			difficulty = Difficulty.valueOf(args[2]);
				    		}
				    		catch(Exception e){
				    			sender.sendMessage("[AdventureDungeons] Unbekannte Schwierigkeit "+args[2]+", setze auf HARD");
				    			difficulty = Difficulty.HARD;
				    		}
				    		
				    		if(args.length<4){
				    			if(!(sender instanceof Player)){
				    				sender.sendMessage("Bitte Spieler angeben oder ingame ausführen.");
				    				return true;
				    			}
				    			player = (Player)sender;
				    			targetInstance = null;
				    		}
				    		else{
				    			String playerName = args[3];
				    			player = Bukkit.getPlayer(playerName);
				    			if(player==null){
				    				sender.sendMessage("Spieler "+playerName+" nicht gefunden.");
				    				return true;
				    			}
				    			if(args.length>4){
				    				String otherPlayerName = args[4];
				    				Player otherPlayer = Bukkit.getPlayer(otherPlayerName);
				    				targetInstance = DungeonInstance.get(otherPlayer);
				    			}
				    			else{
				    				targetInstance = null;
				    			}
				    		}
			    		}
			    		
			    		if(!StringUtils.isNumeric(args[1])){
			    			sender.sendMessage(args[1]+" ist keine Dungeon-ID.");
			    			return true;
			    		}
			    		int dungeon_id = Integer.parseInt(args[1]);
			    		Dungeon dungeon = Dungeon.get(dungeon_id);
			    		if(dungeon==null){
			    			sender.sendMessage("Dungeon nicht gefunden.");
			    			return true;
			    		}
			    		dungeon.join(player, targetInstance, difficulty);
			    		break;
			    	}
			    	case "leave":{
			    		String playerName;
			    		if(args.length<2 && !(sender instanceof Player)){
			    			return true;
			    		}
			    		else if(args.length<2){
			    			playerName = ((Player)sender).getName();
			    		}
			    		else{
			    			playerName = args[1];
			    		}
			    		Player player = Bukkit.getPlayer(playerName);
			    		DungeonInstance dungeonInstance = DungeonInstance.get(player);
			    		if(dungeonInstance==null){
			    			player.sendMessage(playerName+" ist momentan nicht in einem Dungeon.");
			    			return true;
			    		}
			    		Dungeon mmoDungeon = Dungeon.get(dungeonInstance);
			    		if(mmoDungeon==null){
			    			player.sendMessage("Beim Verlassen des Dungeons ist ein Fehler aufgetreten. Bitte kontaktiere den Support.");
			    			return true;
			    		}
			    		mmoDungeon.leave(player.getUniqueId());
			    		break;
			    	}
			    	case "edit":{
			    		if(!(sender instanceof Player)){
			    			sender.sendMessage("[AdventureDungeons] Du kannst nur ingame Dungeons bearbeiten.");
			    		}
			    		if(args.length<2){
			    			sender.sendMessage("Dungeon-ID nicht spezifiziert. (Siehe Web-Tool)");
			    			return true;
			    		}
			    		int dungeon_id = Integer.parseInt(args[1]);
			    		Dungeon dungeon = Dungeon.get(dungeon_id);
			    		if(dungeon==null){
			    			sender.sendMessage("Dungeon "+args[1]+" nicht gefunden.");
			    			return true;
			    		}
			    		dungeon.initiateEditor((Player)sender);
			    		break;
			    	}
			    	case "endedit":{
			    		Integer dungeon_id;
			    		if(args.length<2 && sender instanceof Player){
			    			World world = ((Player)sender).getWorld();
			    			String worldName = world.getName();
			    			if(worldName.contains("dungeon_template_")){
			    				dungeon_id = Integer.parseInt(worldName.replace("dungeon_template_", ""));
			    			}
			    			else{
				    			return true;
			    			}
			    		}
			    		else if(args.length>=2){
				    		dungeon_id = Integer.parseInt(args[1]);
			    		}
			    		else return true;
			    		Dungeon dungeon = Dungeon.get(dungeon_id);
			    		if(dungeon==null){
			    			sender.sendMessage("Dungeon "+args[1]+" nicht gefunden.");
			    			return true;
			    		}
			    		if(dungeon.saveTemplate(sender)){
			    			sender.sendMessage("[AdventureDungeons] "+ChatColor.GREEN+"Dungeon gespeichert.");
			    		}
			    		else{
			    			sender.sendMessage("[AdventureDungeons] "+ChatColor.RED+"Konnte den Bearbeitungsmodus nicht beenden.");
			    		}
			    		break;
			    	}
			    	case "warp":{
			    		if(args.length<2){
			    			sender.sendMessage("[AdventureDungeons] Dungeon ID nicht angegeben.");
			    			return true;
			    		}
			    		else if(!(sender instanceof Player)){
			    			sender.sendMessage("[AdventureDungeons] Kann nur ingame verwendet werden.");
			    			return true;
			    		}
			    		int dungeon_id = Integer.parseInt(args[1]);
			    		Dungeon dungeon = Dungeon.get(dungeon_id);
			    		if(dungeon==null){
			    			sender.sendMessage("[AdventureDungeons] Dungeon "+args[1]+" nicht gefunden.");
			    			return true;
			    		}
			    		World templateWorld  = Bukkit.getWorld("template_"+dungeon_id);
			    		if(sender instanceof Player && templateWorld!=null){
			    			Player player = (Player)sender;
			    			Location joinLocation = dungeon.getLobbyJoin().getLocation(templateWorld);
			    			player.teleport(joinLocation);
			    		}
			    		else if(templateWorld==null){
			    			sender.sendMessage(ChatColor.RED+"Der Editor dieses Dungeons ist nicht initiiert.");
			    		}
			    		break;
			    	}
			    	case "respawn_index":{
			    		if(args.length<3) return false;
			    		try{
			    			String worldName = args[1];
				    		int respawnIndex = Integer.parseInt(args[2]);
				    		World world = Bukkit.getWorld(worldName);
				    		if(world==null){
				    			Bukkit.getLogger().info("[AdventureDungeons] Konnte den Respawn Index für die Welt '"+worldName+"' nicht auf "+respawnIndex+" setzen. (Welt nicht gefunden)");
				    			return true;
				    		}
				    		DungeonInstance instance = DungeonInstance.get(world);
				    		if(instance==null){
				    			sender.sendMessage("[AdventureDungeons] Instanz "+worldName+" nicht gefunden.");
				    			return true;
				    		}
				    		if(instance.setRespawnIndex(respawnIndex) && args.length>3){
				    			List<String> messageParts = new ArrayList<String>();
				    			for(int i = 3; i < args.length; i++){
				    				messageParts.add(args[i]);
				    			}
				    			instance.getPlayerManager().announce(String.join(" ", messageParts));
				    		}
			    		}
			    		catch(Exception e){
			    			return false;
			    		}
			    		break;
			    	}
			    	case "play_sound":{
			    		if(args.length<3) return false;
			    		try{
				    		int instance_id = Integer.parseInt(args[1]);
				    		int sound_id = Integer.parseInt(args[2]);
				    		DungeonInstance dungeonInstance = DungeonInstance.get(instance_id);
				    		if(dungeonInstance==null){
				    			sender.sendMessage("[AdventureDungeons] Instanz "+instance_id+" nicht gefunden.");
				    			return true;
				    		}
				    		for(String player_uuid_string : dungeonInstance.getPlayerManager().getPlayers()){
				    			Player player = Bukkit.getPlayer(UUID.fromString(player_uuid_string));
				    			if(player==null) continue;
				    			AdventureSound.play(player, sound_id);
				    		}
			    		}
			    		catch(Exception e){
			    			e.printStackTrace();
			    			return false;
			    		}
			    		break;
			    	}
			    	default: return false;
				}
	    		return true;
	    	}
	    	case "dungeons":{
	    		return true;
	    	}
	    	default: return false;
    	}
	}
}
