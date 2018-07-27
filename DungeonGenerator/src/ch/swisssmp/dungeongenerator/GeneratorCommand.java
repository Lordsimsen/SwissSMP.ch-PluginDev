package ch.swisssmp.dungeongenerator;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StringUtils;

public class GeneratorCommand implements CommandExecutor{
	private static Random random = new Random();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		if(!(sender instanceof Player)){
			sender.sendMessage("[DungeonGenerator] Kann nur ingame verwendet werden.");
			return true;
		}
		Player player = (Player) sender;
		World world = player.getWorld();
		switch(args[0]){
		case "info":{
			if(args.length<2){
				player.sendMessage("[DungeonGenerator] Alle geladenen Generatoren:");
				for(DungeonGenerator generator : DungeonGenerator.getAll()){
					player.sendMessage("#-------");
					for(String infoLine : generator.getInfo()){
						player.sendMessage(infoLine);
					}
				}
			}
			else{
				String name = args[1];
				DungeonGenerator generator = DungeonGenerator.get(world, name);
				if(generator==null){
					player.sendMessage("[DungeonGenerator] Generator '"+name+"' nicht gefunden.");
				}
				else{
					player.sendMessage("[DungeonGenerator] Dungeon-Generator Info:");
					for(String infoLine : generator.getInfo()){
						player.sendMessage(infoLine);
					}
				}
			}
			return true;
		}
		case "erstelle":
		case "create":{
			if(args.length<2) return false;
			String name = args[1];
			int partSizeXZ = 16;
			if(args.length>2 && StringUtils.isNumeric(args[2])){
				try{
					partSizeXZ = Integer.parseInt(args[2]);
				}
				catch(Exception e){
					sender.sendMessage("[DungeonGenerator] Ungültige Grösse '"+args[2]+"'");
					return true;
				}
			}
			int partSizeY = 16;
			if(args.length>3 && StringUtils.isNumeric(args[3])){
				try{
					partSizeY = Integer.parseInt(args[3]);
				}
				catch(Exception e){
					sender.sendMessage("[DungeonGenerator] Ungültige Grösse '"+args[3]+"'");
					return true;
				}
			}
			DungeonGenerator generator = DungeonGenerator.create(player.getWorld(), name, partSizeXZ, partSizeY);
			if(generator==null){
				sender.sendMessage("[DungeonGenerator] Konnte den Generator nicht erstellen.");
			}
			else{
				sender.sendMessage("[DungeonGenerator] Generator '"+name+"' erstellt!");
			}
			return true;
		}
		case "aktualisiere":
		case "aktualisieren":
		case "update":{
			if(args.length<2) return false;
			String name = args[1];
			DungeonGenerator generator = DungeonGenerator.get(world, name);
			if(generator==null){
				player.sendMessage("[DungeonGenerator] Generator '"+name+"' nicht gefunden.");
			}
			else{
				generator.update();
				sender.sendMessage("[DungeonGenerator] Generator aktualisiert.");
			}
			return true;
		}
		case "hierhin":
		case "movehere":{
			if(args.length<2) return false;
			String name = args[1];
			DungeonGenerator generator = DungeonGenerator.get(world, name);
			if(generator==null){
				player.sendMessage("[DungeonGenerator] Generator '"+name+"' nicht gefunden.");
			}
			else{
				Block block = null;
				if(args.length<5){
					Location location = player.getLocation();
					location.add(2, 1, 2); //move one on XZ so the player is not inside the first GeneratorPart and another one on XYZ because of its bounding box
					block = location.getBlock();
				}
				else{
					try{
						block = player.getWorld().getBlockAt(Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
					}
					catch(Exception e){
						sender.sendMessage("[DungeonGenerator] Ungültige Koordinaten '"+args[2]+","+args[3]+","+args[4]+"'");
						return true;
					}
				}
				generator.setTemplateOrigin(block);
			}
			sender.sendMessage("[DungeonGenerator] Vorlage neu platziert.");
			return true;
		}
		case "bearbeite":
		case "bearbeiten":
		case "edit":{
			if(args.length<3) return false;
			String name = args[1];
			DungeonGenerator generator = DungeonGenerator.get(world, name);
			if(generator==null){
				player.sendMessage("[DungeonGenerator] Generator '"+name+"' nicht gefunden.");
				return true;
			}
			String[] variableInput;
			for(int i = 2; i < args.length; i++){
				variableInput = args[i].split(":");
				if(variableInput.length<2) continue;
				switch(variableInput[0].toLowerCase()){
				case "name": generator.setName(variableInput[1]); break;
				case "grössexz":
				case "grösse_xz":
				case "sizexz":
				case "size_xz": if(StringUtils.isNumeric(variableInput[1])) generator.setPartSizeXZ(Integer.parseInt(variableInput[1])); break;
				case "grössey":
				case "grösse_y":
				case "sizey":
				case "size_y": if(StringUtils.isNumeric(variableInput[1])) generator.setPartSizeY(Integer.parseInt(variableInput[1])); break;
				}
			}
			sender.sendMessage("[DungeonGenerator] Generator aktualisiert.");
			return true;
		}
		case "generiere":
		case "generate":{
			if(args.length<2) return false;
			String name = args[1];
			int size = 100;
			if(args.length>2 && StringUtils.isNumeric(args[2])){
				try{
					size = Integer.parseInt(args[2]);
				}
				catch(Exception e){
					sender.sendMessage("[DungeonGenerator] Ungültige Grösse '"+args[2]+"'");
					return true;
				}
			}
			Long seed;
			if(args.length>3){
				seed = Long.valueOf(args[3].hashCode());
			}
			else{
				seed = random.nextLong();
			}
			DungeonGenerator generator = DungeonGenerator.get(world, name);
			if(generator==null){
				player.sendMessage("[DungeonGenerator] Generator '"+name+"' nicht gefunden.");
			}
			else{
				generator.generate(new BlockVector(player.getLocation().toVector().add(new Vector(1,0,1))), seed, size);
				sender.sendMessage("[DungeonGenerator] Dungeon '"+name+"' mit Seed '"+seed+"' generiert.");
			}
			return true;
		}
		default: return false;
		}
	}

}
