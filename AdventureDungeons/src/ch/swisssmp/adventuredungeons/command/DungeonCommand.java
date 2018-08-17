package ch.swisssmp.adventuredungeons.command;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.adventuredungeons.sound.AdventureSound;
import ch.swisssmp.adventuredungeons.world.Dungeon;
import ch.swisssmp.adventuredungeons.world.DungeonInstance;
import ch.swisssmp.utils.SwissSMPler;

public class DungeonCommand implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	switch(label){
	    	case "dungeon":{
	    		if(args==null || args.length==0) return false;
	    		switch(args[0]){
			    	case "reload":
						try {
							Dungeon.loadDungeons();
							sender.sendMessage("[AdventureDungeons] Dungeon-Konfiguration neu geladen.");
						} catch (Exception e) {
							sender.sendMessage("Fehler beim laden der Daten! Mehr Details in der Konsole...");
							e.printStackTrace();
						}
						break;
			    	case "announce":{
			    		if(args.length<3) return false;
			    		try{
			    			int instance_id = Integer.parseInt(args[1]);
			    			List<String> messageParts = new ArrayList<String>();
			    			for(int i = 2; i < args.length; i++){
			    				messageParts.add(args[i]);
			    			}
			    			DungeonInstance dungeonInstance = Dungeon.getInstance(instance_id);
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
			    		UUID player_uuid;
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
			    			Player player = (Player)sender;
			    			player_uuid = player.getUniqueId();
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
				    			Player player = (Player)sender;
				    			player_uuid = player.getUniqueId();
				    			targetInstance = null;
				    		}
				    		else{
				    			String playerName = args[3];
				    			Player player = Bukkit.getPlayer(playerName);
				    			if(player==null){
				    				sender.sendMessage("Spieler "+playerName+" nicht gefunden.");
				    				return true;
				    			}
				    			player_uuid = player.getUniqueId();
				    			if(args.length>4){
				    				String otherPlayerName = args[4];
				    				Player otherPlayer = Bukkit.getPlayer(otherPlayerName);
				    				targetInstance = Dungeon.getInstance(otherPlayer);
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
			    		dungeon.join(player_uuid, targetInstance, difficulty);
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
			    		DungeonInstance dungeonInstance = Dungeon.getInstance(player.getUniqueId());
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
			    	case "list":{
			    		for(Dungeon dungeon : Dungeon.dungeons.values()){
			    			sender.sendMessage("["+dungeon.dungeon_id+"] - "+dungeon.name);
			    		}
			    		if(Dungeon.dungeons.size()<1){
			    			sender.sendMessage("Keine Dungeons gefunden.");
			    		}
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
			    		World templateWorld  = dungeon.editTemplate();
			    		if(templateWorld!=null){
			    			Player player = (Player)sender;
			    			Location joinLocation = templateWorld.getSpawnLocation();
			    			if(dungeon.lobby_join!=null){
			    				joinLocation = dungeon.lobby_join.getLocation(templateWorld);
			    			}
			    			player.teleport(joinLocation);
			    			player.setGameMode(GameMode.CREATIVE);
			    		}
			    		else{
			    			sender.sendMessage(ChatColor.RED+"Konnte den Bearbeitungsmodus nicht initiieren.");
			    		}
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
			    		if(dungeon.saveTemplate()){
			    			sender.sendMessage("Dungeon gespeichert.");
			    		}
			    		else{
			    			sender.sendMessage(ChatColor.RED+"Konnte den Bearbeitungsmodus nicht beenden.");
			    		}
			    		break;
			    	}
			    	case "warp":{
			    		if(args.length<2){
			    			sender.sendMessage("mmo_dungeon_id nicht spezifiziert.");
			    			return true;
			    		}
			    		else if(!(sender instanceof Player)){
			    			sender.sendMessage("Kann nur ingame verwendet werden.");
			    			return true;
			    		}
			    		int mmo_dungeon_id = Integer.parseInt(args[1]);
			    		Dungeon mmoDungeon = Dungeon.get(mmo_dungeon_id);
			    		if(mmoDungeon==null){
			    			sender.sendMessage("MmoDungeon "+args[1]+" nicht gefunden.");
			    			return true;
			    		}
			    		World templateWorld  = Bukkit.getWorld("template_"+mmo_dungeon_id);
			    		if(sender instanceof Player && templateWorld!=null){
			    			Player player = (Player)sender;
			    			Location joinLocation = templateWorld.getSpawnLocation();
			    			if(mmoDungeon.lobby_join!=null){
			    				joinLocation = mmoDungeon.lobby_join.getLocation();
			    			}
			    			player.teleport(joinLocation);
			    		}
			    		else if(templateWorld==null){
			    			sender.sendMessage(ChatColor.RED+"Der Editor dieses Dungeons ist nicht initiiert.");
			    		}
			    		break;
			    	}
			    	case "players":{
			    		for(Entry<String, Integer> entry : Dungeon.playerMap.entrySet()){
			    			Player player = Bukkit.getPlayer(UUID.fromString(entry.getKey()));
			    			if(player!=null){
				    			sender.sendMessage("- "+player.getName()+": "+entry.getValue());
			    			}
			    		}
			    		break;
			    	}
			    	case "respawn_index":{
			    		if(args.length<3) return false;
			    		try{
			    			String worldName = args[1];
				    		int respawnIndex = Integer.parseInt(args[2]);
				    		DungeonInstance instance = Dungeon.getInstance(worldName);
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
				    		DungeonInstance dungeonInstance = Dungeon.getInstance(instance_id);
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
	    		break;
	    	}
	    	case "ready":{
	    		String player_uuid;
	    		if((sender instanceof Player)){
		    		Player player = (Player) sender;
		    		player_uuid = player.getUniqueId().toString();
	    		}
	    		else{
	    			if(args.length>0){
	    				player_uuid = args[0];
	    				Player player = Bukkit.getPlayer(player_uuid);
	    				if(player!=null){
	    					player_uuid = player.getUniqueId().toString();
	    				}
	    			}
	    			else{
	    				sender.sendMessage("Keinen Spieler definiert.");
	    				return true;
	    			}
	    		}
    			UUID uuid;
	    		try{
	    			uuid = UUID.fromString(player_uuid);
	    		}
	    		catch(Exception e){
	    			sender.sendMessage(player_uuid+" ist ungültig.");
	    			return true;
	    		}
	    		DungeonInstance dungeonInstance = Dungeon.getInstance(uuid);
	    		if(dungeonInstance==null) return true;
	    		if(dungeonInstance.isRunning()) return true;
    			SwissSMPler swisssmpler = SwissSMPler.get(UUID.fromString(player_uuid));
	    		if(dungeonInstance.getPlayerManager().toggleReady(player_uuid)){
	    			if(swisssmpler!=null) swisssmpler.sendActionBar("Du bist nun bereit.");
	    		}
	    		else{
	    			if(swisssmpler!=null) swisssmpler.sendActionBar("Du bist nun nicht mehr bereit.");
	    		}
	    		break;
	    	}
    	}
    	
		return true;
	}
}
