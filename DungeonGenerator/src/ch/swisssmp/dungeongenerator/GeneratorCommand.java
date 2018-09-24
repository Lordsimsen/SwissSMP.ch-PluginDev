package ch.swisssmp.dungeongenerator;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.ObservableRoutine;

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
		GeneratorManager manager = GeneratorManager.get(world);
		switch(args[0]){
		case "info":{
			if(args.length<2){
				player.sendMessage("[DungeonGenerator] Alle geladenen Generatoren:");
				for(DungeonGenerator generator : manager.getAll()){
					player.sendMessage("#-------");
					for(String infoLine : generator.getInfo()){
						player.sendMessage(infoLine);
					}
				}
			}
			else{
				String name = args[1];
				DungeonGenerator generator = manager.get(name);
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
			DungeonGenerator generator = manager.create(name, partSizeXZ, partSizeY);
			if(generator==null){
				sender.sendMessage("[DungeonGenerator] Konnte den Generator nicht erstellen.");
			}
			else{
				manager.saveAll();
				sender.sendMessage("[DungeonGenerator] Generator '"+name+"' erstellt!");
			}
			return true;
		}
		case "aktualisiere":
		case "aktualisieren":
		case "update":{
			if(args.length<2) return false;
			String name = args[1];
			DungeonGenerator generator = manager.get(name);
			if(generator==null){
				player.sendMessage("[DungeonGenerator] Generator '"+name+"' nicht gefunden.");
			}
			else{
				generator.update();
				sender.sendMessage("[DungeonGenerator] Generator '"+generator.getName()+"' aktualisiert.");
			}
			return true;
		}
		case "item":
		case "get":{
			if(!(sender instanceof Player)){
				sender.sendMessage("[DungeonGenerator] Diesen Befehl kannst du nur ingame verwenden.");
				return true;
			}
			String name = args[1];
			DungeonGenerator generator = manager.get(name);
			if(generator==null){
				player.sendMessage("[DungeonGenerator] Generator '"+name+"' nicht gefunden.");
				return true;
			}
			int amount = 1;
			if(args.length>2 && StringUtils.isNumeric(args[2])){
				amount = Integer.parseInt(args[2]);
			}
			ItemStack itemStack = ItemManager.getInventoryToken(generator, amount);
			((Player)sender).getInventory().addItem(itemStack);
			return true;
		}
		case "bearbeite":
		case "bearbeiten":
		case "edit":{
			if(args.length<3) return false;
			String name = args[1];
			DungeonGenerator generator = manager.get(name);
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
				case "grösse": generator.setDefaultSize(Integer.parseInt(variableInput[1])); break;
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
			DungeonGenerator generator = manager.get(name);
			if(generator==null){
				player.sendMessage("[DungeonGenerator] Generator '"+name+"' nicht gefunden.");
				return true;
			}
			if(sender instanceof Player){
				generator.inspectInBrowser((Player)sender);
			}
			int size = generator.getDefaultSize();
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
			ObservableRoutine observableRoutine = generator.generate(sender, seed, size);
			observableRoutine.addObserver(player);
			return true;
		}
		case "reset":{
			if(args.length<2) return false;
			String name = args[1];
			DungeonGenerator generator = manager.get(name);
			if(generator==null){
				player.sendMessage("[DungeonGenerator] Generator '"+name+"' nicht gefunden.");
				return true;
			}
			ObservableRoutine activeRoutine = generator.reset(sender);
			if(activeRoutine==null){
				sender.sendMessage("[DungeonGenerator] Generator '"+generator.getName()+"' zurückgesetzt.");
			}
			else if(!(activeRoutine instanceof ResetRoutine)){
				sender.sendMessage("[DungeonGenerator] Der Generator ist beschäftigt. ("+activeRoutine.getClass().getSimpleName()+")");
				activeRoutine.addOnFinishListener(()->{
					sender.sendMessage("[DungeonGenerator] Der Generator '"+generator.getName()+"' ist nun frei.");
				});
			}
			else{
				activeRoutine.addOnFinishListener(()->{
					sender.sendMessage("[DungeonGenerator] Generator '"+generator.getName()+"' zurückgesetzt.");
				});
			}
			return true;
		}
		default: return false;
		}
	}

}
