package ch.swisssmp.world;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.ChatRequest;

public class PlayerCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "reload":{
			WorldManager.loadWorlds();
			return true;
		}
		case "create":{
			if(!(sender instanceof Player) || args.length<2) return true;
			String worldName = args[1];
			World existing = Bukkit.getWorld(worldName);
			if(existing!=null){
				worldName = existing.getName();
			}
			else{
				File levelFile;
				for(File file : Bukkit.getWorldContainer().listFiles()){
					if(!file.isDirectory()) continue;
					levelFile = new File(file.getPath(), "level.dat");
					if(!levelFile.exists()) continue;
					if(file.getName().toLowerCase().equals(worldName.toLowerCase())){
						worldName = file.getName();
						break;
					}
				}
			}
			WorldEditor editor = WorldEditor.open(args[1], (Player)sender);
			if(args.length>2){
				editor.setSeed(args[2]);
			}
			return true;
		}
		case "load":{
			if(args.length<2) return false;
			if(WorldManager.loadWorld(args[1])==null){
				sender.sendMessage("[WorldManager] Konnte Welt "+args[1]+" nicht laden.");
			};
			return true;
		}
		case "unload":{
			if(args.length<2) return false;
			if(WorldManager.unloadWorld(args[1])){
				sender.sendMessage("[WorldManager] Welt "+args[1]+" deaktiviert.");
			}
			else{
				sender.sendMessage("[WorldManager] Konnte Welt "+args[1]+" nicht deaktivieren.");
			}
			return true;
		}
		case "upload":{
			if(args.length<2) return false;
			WorldManager.uploadWorld(sender, args[1]);
			return true;
		}
		case "download":{
			if(args.length<2) return false;
			WorldManager.downloadWorld(sender, args[1]);
			return true;
		}
		case "delete":{
			if(args.length<2) return false;
			if(sender instanceof Player){
				ChatRequest request = new ChatRequest("[WorldManager] Welt "+args[1]+" wirklich löschen?");
				request.addOption("Ja", "worldmanager delete_confirmed "+args[1]+" "+sender.getName());
				request.addOption("Nein", "");
				request.send(((Player)sender).getUniqueId());
			}
			else{
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "worldmanager delete_confirmed "+args[1]);
			}
			return true;
		}
		case "delete_confirmed":{
			if(args.length<2) return false;
			WorldManager.deleteWorld(args[1]);
			sender.sendMessage("[WorldManager] Welt "+args[1]+" gelöscht.");
			return true;
		}
		default: return false;
		}
	}

}
