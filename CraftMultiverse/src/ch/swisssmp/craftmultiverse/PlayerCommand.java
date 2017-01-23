package ch.swisssmp.craftmultiverse;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null) return false;
		else if(args.length<1) return false;
		switch(args[0]){
		case "generate":
		case "create":{
			//mv create [name] [environment] [type] [seed]
			//mv create Farmwelt NETHER LARGE_BIOMES 23423412
			String worldNameString = args[0];
			String worldEnvironmentString = args[1];
			String worldTypeString = args[2];
			String worldSeedString = args[3];
			if(Bukkit.getWorld(worldNameString)!=null){
				sender.sendMessage("Es existiert bereits eine Welt '"+worldNameString+"'!");
				return true;
			}
			WorldType worldType = WorldType.getByName(worldTypeString);
			Environment environment = Environment.valueOf(worldEnvironmentString);
			long worldSeed;
			if (StringUtils.isAlphanumeric(worldSeedString)){
				worldSeed = Long.parseLong(worldSeedString);
			}
			else{
				worldSeed = worldSeedString.hashCode();
			}
			WorldCreator worldCreator = new WorldCreator(worldNameString);
			worldCreator.environment(environment);
			worldCreator.type(worldType);
			worldCreator.seed(worldSeed);
			World world = worldCreator.createWorld();
			if(world!=null){
				sender.sendMessage("Welt wurde erstellt!");
			}
			break;
		}
		case "load":{
			//mv load Farmwelt
			break;
		}
		case "unload":{
			//mv unload Farmwelt
			break;
		}
		case "delete":{
			//mv delete Farmwelt
			break;
		}
		case "confirm":{
			//mv confirm
			break;
		}
		}
		return false;
	}

}
