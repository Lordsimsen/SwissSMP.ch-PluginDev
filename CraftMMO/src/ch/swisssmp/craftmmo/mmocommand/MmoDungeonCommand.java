package ch.swisssmp.craftmmo.mmocommand;

import ch.swisssmp.craftmmo.mmoplayer.MmoPlayer;
import ch.swisssmp.craftmmo.mmoworld.MmoDungeon;
import ch.swisssmp.craftmmo.mmoworld.MmoDungeonInstance;

import java.util.UUID;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class MmoDungeonCommand implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	switch(label){
	    	case "mmodungeon":{
	    		switch(args[0]){
			    	case "help":
			    		displayHelp(sender);
			    		break;
			    	case "reload":
						try {
							boolean fullload = false;
							if(args.length>1){
								fullload = (args[1].equals("all"));
							}
							MmoDungeon.loadDungeons(fullload);
							sender.sendMessage("[CraftMMO] Dungeon-Konfiguration neu geladen.");
						} catch (Exception e) {
							sender.sendMessage("Fehler beim laden der Daten! Mehr Details in der Konsole...");
							e.printStackTrace();
						}
						break;
			    	case "join":{
			    		UUID player_uuid;
			    		if(args.length<2){
			    			return true;
			    		}
			    		else if(args.length<3){
			    			if(!(sender instanceof Player)){
			    				sender.sendMessage("Bitte Spieler angeben oder ingame ausführen.");
			    				return true;
			    			}
			    			Player player = (Player)sender;
			    			player_uuid = player.getUniqueId();
			    		}
			    		else{
			    			String playerName = args[2];
			    			Player player = Bukkit.getPlayer(playerName);
			    			if(player==null){
			    				sender.sendMessage("Spieler "+playerName+" nicht gefunden.");
			    				return true;
			    			}
			    			player_uuid = player.getUniqueId();
			    		}
			    		if(!StringUtils.isNumeric(args[1])){
			    			sender.sendMessage(args[1]+" ist keine mmo_dungeon_id.");
			    			return true;
			    		}
			    		int mmo_dungeon_id = Integer.parseInt(args[1]);
			    		MmoDungeon mmoDungeon = MmoDungeon.get(mmo_dungeon_id);
			    		if(mmoDungeon==null){
			    			sender.sendMessage("Dungeon nicht gefunden.");
			    			return true;
			    		}
			    		mmoDungeon.join(player_uuid);
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
			    		MmoDungeonInstance dungeonInstance = MmoDungeon.getInstance(player.getUniqueId());
			    		if(dungeonInstance==null){
			    			player.sendMessage(playerName+" ist momentan nicht in einem Dungeon.");
			    			return true;
			    		}
			    		MmoDungeon mmoDungeon = MmoDungeon.get(dungeonInstance);
			    		if(mmoDungeon==null){
			    			player.sendMessage("Beim Verlassen des Dungeons ist ein Fehler aufgetreten. Bitte kontaktiere den Support.");
			    			return true;
			    		}
			    		mmoDungeon.leave(player.getUniqueId());
			    		break;
			    	}
			    	case "list":{
			    		for(MmoDungeon dungeon : MmoDungeon.dungeons.values()){
			    			sender.sendMessage("["+dungeon.mmo_dungeon_id+"] - "+dungeon.name);
			    		}
			    		if(MmoDungeon.dungeons.size()<1){
			    			sender.sendMessage("Keine Dungeons gefunden.");
			    		}
			    		break;
			    	}
			    	case "edit":{
			    		if(args.length<2){
			    			sender.sendMessage("mmo_dungeon_id nicht spezifiziert. (Siehe Web-Tool)");
			    			return true;
			    		}
			    		int mmo_dungeon_id = Integer.parseInt(args[1]);
			    		MmoDungeon mmoDungeon = MmoDungeon.get(mmo_dungeon_id);
			    		if(mmoDungeon==null){
			    			sender.sendMessage("MmoDungeon "+args[1]+" nicht gefunden.");
			    			return true;
			    		}
			    		World templateWorld  = mmoDungeon.editTemplate();
			    		if(sender instanceof Player && templateWorld!=null){
			    			Player player = (Player)sender;
			    			Vector joinLocation = templateWorld.getSpawnLocation().toVector();
			    			if(mmoDungeon.lobby_join!=null){
			    				joinLocation = mmoDungeon.lobby_join;
			    			}
			    			player.teleport(new Location(templateWorld, joinLocation.getX(), joinLocation.getY(), joinLocation.getZ()));
			    		}
			    		else if(templateWorld==null){
			    			sender.sendMessage(ChatColor.RED+"Konnte den Bearbeitungsmodus nicht initiieren.");
			    		}
			    		break;
			    	}
			    	case "endedit":{
			    		Integer mmo_dungeon_id;
			    		if(args.length<2 && sender instanceof Player){
			    			World world = ((Player)sender).getWorld();
			    			String worldName = world.getName();
			    			if(worldName.contains("dungeon_template_")){
			    				mmo_dungeon_id = Integer.parseInt(worldName.replace("dungeon_template_", ""));
			    			}
			    			else{
				    			return true;
			    			}
			    		}
			    		else if(args.length>=2){
				    		mmo_dungeon_id = Integer.parseInt(args[1]);
			    		}
			    		else return true;
			    		MmoDungeon mmoDungeon = MmoDungeon.get(mmo_dungeon_id);
			    		if(mmoDungeon==null){
			    			sender.sendMessage("MmoDungeon "+args[1]+" nicht gefunden.");
			    			return true;
			    		}
			    		if(mmoDungeon.saveTemplate()){
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
			    		MmoDungeon mmoDungeon = MmoDungeon.get(mmo_dungeon_id);
			    		if(mmoDungeon==null){
			    			sender.sendMessage("MmoDungeon "+args[1]+" nicht gefunden.");
			    			return true;
			    		}
			    		World templateWorld  = Bukkit.getWorld("template_"+mmo_dungeon_id);
			    		if(sender instanceof Player && templateWorld!=null){
			    			Player player = (Player)sender;
			    			Vector joinLocation = templateWorld.getSpawnLocation().toVector();
			    			if(mmoDungeon.lobby_join!=null){
			    				joinLocation = mmoDungeon.lobby_join;
			    			}
			    			player.teleport(new Location(templateWorld, joinLocation.getX(), joinLocation.getY(), joinLocation.getZ()));
			    		}
			    		else if(templateWorld==null){
			    			sender.sendMessage(ChatColor.RED+"Der Editor dieses Dungeons ist nicht initiiert.");
			    		}
			    		break;
			    	}
			    	case "players":{
			    		for(Entry<String, Integer> entry : MmoDungeon.playerMap.entrySet()){
			    			Player player = Bukkit.getPlayer(UUID.fromString(entry.getKey()));
			    			if(player!=null){
				    			sender.sendMessage("- "+player.getName()+": "+entry.getValue());
			    			}
			    		}
			    	}
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
	    		MmoDungeonInstance dungeonInstance = MmoDungeon.getInstance(uuid);
	    		if(dungeonInstance==null) return true;
	    		if(dungeonInstance.running) return true;
	    		if(dungeonInstance.toggleReady(player_uuid)){
	    			MmoPlayer.sendMessage(UUID.fromString(player_uuid), "Du bist nun bereit.");
	    		}
	    		else{
	    			MmoPlayer.sendMessage(UUID.fromString(player_uuid), "Du bist nun nicht mehr bereit.");
	    		}
	    		break;
	    	}
    	}
    	
		return true;
	}
    public void displayHelp(CommandSender sender){
    	sender.sendMessage("/MmoDungeon = /mmodungeon");
    	sender.sendMessage("-----");
    	sender.sendMessage("/mmodungeon help - Zeigt diese Hilfe an");
    	sender.sendMessage("/mmodungeon reload - Lädt die Konfigurationen neu");
    }
}
