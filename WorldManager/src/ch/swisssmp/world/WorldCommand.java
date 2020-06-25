package ch.swisssmp.world;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.world.border.WorldBorder;
import ch.swisssmp.world.border.WorldBorderManager;
import ch.swisssmp.world.transfer.WorldTransferManager;
import org.bukkit.util.StringUtil;

public class WorldCommand implements TabExecutor {

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
			sender.sendMessage(WorldManager.getPrefix()+ChatColor.GREEN+" Welt "+args[1]+" geladen!");
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
		case "name":{
			if(args.length<3) return false;
			World world = Bukkit.getWorld(args[1]);
			YamlConfiguration yamlConfiguration = WorldManager.getWorldSettings(world.getName());
			if(yamlConfiguration==null) yamlConfiguration = new YamlConfiguration();
			ConfigurationSection dataSection = yamlConfiguration.contains("world") ? yamlConfiguration.getConfigurationSection("world") : yamlConfiguration.createSection("world");
			List<String> nameParts = new ArrayList<String>();
			for(int i = 2; i < args.length; i++){
				nameParts.add(args[i]);
			}
			dataSection.set("display_name", String.join(" ", nameParts));
			WorldManager.saveWorldSettings(world);
			
			if(sender instanceof Player){
				SwissSMPler.get((Player)sender).sendActionBar(ChatColor.GREEN+"Welt umbenennt.");
			}
			return true;
		}
		case "goto":{
			if(!(sender instanceof Player)) return true;
			if(args.length<2) return false;
			String destination = args[1];
			World world = Bukkit.getWorld(destination);
			Player player = (Player) sender;
			if(world==null){
				player.sendMessage("[WorldManager] Welt "+args[1]+" nicht gefunden.");
				return true;
			}
			player.teleport(world.getSpawnLocation());
			return true;
		}
		case "upload":{
			if(args.length<2) return false;
			WorldTransferManager.uploadWorld(sender, args[1]);
			return true;
		}
		case "download":{
			if(args.length<2) return false;
			WorldTransferManager.downloadWorld(sender, args[1]);
			return true;
		}
		case "delete":{
			if(args.length<2) return false;
			WorldManager.deleteWorld(args[1]);
			sender.sendMessage("[WorldManager] Welt "+args[1]+" gelöscht.");
			return true;
		}
		case "trim":{
			if(args.length<2) return false;
			String prefix = WorldManager.getPrefix();
			if(!(sender instanceof Player)) {
				sender.sendMessage(prefix+ChatColor.RED+" Kann nur ingame verwendet werden.");
				return true;
			}
			String worldName = args[1];
			World world = Bukkit.getWorld(worldName);
			if(world==null) {
				sender.sendMessage(prefix+ChatColor.RED+" Welt "+worldName+" nicht gefunden.");
				return true;
			}
			WorldBorder worldBorder = WorldBorderManager.getWorldBorder(worldName);
			if(worldBorder==null) {
				sender.sendMessage(prefix+ChatColor.RED+" Welt "+worldName+" hat keinen Weltrand.");
				return true;
			}
			List<String> params = new ArrayList<String>(args.length-2);
			for(int i = 2; i < args.length; i++) {
				params.add(args[i]);
			}
			if(WorldManager.trim((Player) sender, world,worldBorder, params)) {
				return true;
			}
			sender.sendMessage(prefix+ChatColor.RED+" WorldBorder-Plugin benötigt.");
			return true;
		}
		default: return false;
		}
	}


	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if(args.length<=1){
			List<String> subcommands = Arrays.asList("reload", "create", "load", "unload", "name", "goto", "upload", "download", "delete", "trim");
			String current = args.length>0 ? args[0] : "";
			return StringUtil.copyPartialMatches(current, subcommands, new ArrayList<>());
		}
		switch(args[0]){
			case "name":
				if(args.length>2) return Collections.emptyList();
			case "unload":
			case "goto":
			case "delete":
			case "trim":
				List<String> worldNames = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
				String current = args.length>1 ? args[1] : "";
				return StringUtil.copyPartialMatches(current, worldNames, new ArrayList<>());
			case "reload":
			case "create":
			case "download":
			case "upload":
			case "load":
			default:
				return Collections.emptyList();
		}
	}
}
