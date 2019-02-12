package ch.swisssmp.world.border;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldBorderCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("[WorldManager] Kann nur ingame verwendet werden.");
			return true;
		}
		if(args==null || args.length==0) return false;
		
		Player player = (Player) sender;
		World world = player.getWorld();
		WorldBorder worldBorder = WorldBorderManager.getWorldBorder(world.getName());
		
		switch(args[0]){
		case "set":{
			if(args.length<2){
				sender.sendMessage(Bukkit.getPluginCommand("worldborder set").getUsage());
				return true;
			}
			if(worldBorder==null) worldBorder = WorldBorder.create(world);
			int radius;
			try{
				radius = Integer.parseInt(args[1]);
			}
			catch(Exception e){
				sender.sendMessage(Bukkit.getPluginCommand("worldborder set").getUsage());
				return true;
			}
			worldBorder.setRadius(radius);
			WorldBorderManager.setWorldBorder(world.getName(), worldBorder);
			sender.sendMessage("[WorldManager]"+ChatColor.GREEN+" Weltrand angepasst.");
			return true;
		}
		case "get":{
			if(worldBorder==null){
				sender.sendMessage("[WorldManager] In dieser Welt ist kein Weltrand definiert.");
				return true;
			}
			sender.sendMessage("[WorldManager] "
					+ "Zentrum: "+worldBorder.getCenterX()+", "+worldBorder.getCenterZ()+"; "
					+ "Radius: "+worldBorder.getRadius()+"; "
					+ "Rand: "+worldBorder.getMargin()+"; "
					+ "Runde Welt: "+(worldBorder.doWrap() ? "Ja" : "Nein"));
			return true;
		}
		case "center":{
			if(worldBorder==null) worldBorder = WorldBorder.create(world);
			Location center = player.getLocation();
			worldBorder.setCenterX(center.getBlockX());
			worldBorder.setCenterZ(center.getBlockZ());
			WorldBorderManager.setWorldBorder(world.getName(), worldBorder);
			sender.sendMessage("[WorldManager]"+ChatColor.GREEN+" Zentrum angepasst.");
			return true;
		}
		case "margin":{
			if(worldBorder==null) worldBorder = WorldBorder.create(world);
			int margin;
			try{
				margin = Integer.parseInt(args[1]);
			}
			catch(Exception e){
				sender.sendMessage(Bukkit.getPluginCommand("worldborder margin").getUsage());
				return true;
			}
			worldBorder.setMargin(margin);
			WorldBorderManager.setWorldBorder(world.getName(), worldBorder);
			sender.sendMessage("[WorldManager]"+ChatColor.GREEN+" Rand angepasst.");
			return true;
		}
		case "wrap":{
			if(args.length<2){
				sender.sendMessage(Bukkit.getPluginCommand("worldborder set").getUsage());
				return true;
			}
			if(worldBorder==null) worldBorder = WorldBorder.create(world);
			boolean wrap = (args[1].toLowerCase().equals("true"));
			worldBorder.setDoWrap(wrap);
			WorldBorderManager.setWorldBorder(world.getName(), worldBorder);
			sender.sendMessage("[WorldManager]"+ChatColor.GREEN+" Weltrand angepasst.");
			return true;
		}
		case "clear":{
			if(worldBorder==null){
				sender.sendMessage("[WorldManager] In dieser Welt ist kein Weltrand definiert.");
				return true;
			}
			WorldBorderManager.removeWorldBorder(player.getWorld().getName());
			sender.sendMessage("[WorldManager] "+ChatColor.RED+"Weltrand entfernt.");
			return true;
		}
		default: return false;
		}
	}
}
