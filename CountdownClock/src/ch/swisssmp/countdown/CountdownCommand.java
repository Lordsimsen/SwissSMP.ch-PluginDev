package ch.swisssmp.countdown;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CountdownCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "reload":{
			CountdownClockPlugin.reload();
			sender.sendMessage("[CountdownClock] Einstellungen neu geladen.");
			return true;
		}
		case "start":{
			if(!(sender instanceof Player)){
				sender.sendMessage("[CountdownClock] Kann nur ingame verwendet werden.");
				return true;
			}
			if(args.length<5) return false;
			Player player = (Player) sender;
			String name = args[1];
			String dateString = args[2];
			String timeString = args[3];
			String numbersString = args[4].toUpperCase();
			String gapsString = (args.length>5 ? args[5].toUpperCase() : "AIR");
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			Date parsedDate;
			Material numberMaterial;
			Material gapMaterial;
			try{
				parsedDate = dateFormat.parse(dateString+" "+timeString);
				numberMaterial = Material.valueOf(numbersString);
				gapMaterial = Material.valueOf(gapsString);
				
			}
			catch(Exception e){
				return false;
			}
			long deadline = parsedDate.getTime();
			CountdownClock.run(name, player.getLocation().getBlock(), this.getFacing(player), numberMaterial, gapMaterial, deadline);
			sender.sendMessage("[CountdownClock] Countdown gestartet.");
			return true;
		}
		case "stop":{
			if(args.length<2) return false;
			CountdownClock clock = CountdownClock.get(args[1]);
			if(clock==null){
				sender.sendMessage("[CountdownClock] Countdown '"+args[1]+"' nicht gefunden.");
				return true;
			}
			clock.stop();
			sender.sendMessage("[CountdownClock] Countdown '"+clock.getName()+"' gestoppt.");
			return true;
		}
		case "list":{
			if(!(sender instanceof Player)){
				sender.sendMessage("[CountdownClock] Kann nur ingame verwendet werden.");
				return true;
			}
			Player player = (Player) sender;
			sender.sendMessage("[CountdownClock] Alle aktiven Countdowns:");
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			for(CountdownClock clock : CountdownClock.getAll(player.getWorld())){
				sender.sendMessage("- "+clock.getName()+" ("+dateFormat.format(new Date(clock.getDeadline()))+")");
			}
			return true;
		}
		default:{
			return false;
		}
		}
	}
	
	
	private BlockFace getFacing(Player player){
		Vector direction = player.getEyeLocation().getDirection();
		if(Math.abs(direction.getX())>Math.abs(direction.getZ())){
			if(direction.getX()>0){
				return BlockFace.EAST;
			}
			else{
				return BlockFace.WEST;
			}
		}
		else{
			if(direction.getZ()<0){
				return BlockFace.NORTH;
			}
			else{
				return BlockFace.SOUTH;
			}
		}
	}
}
