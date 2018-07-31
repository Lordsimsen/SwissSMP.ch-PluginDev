package ch.swisssmp.mobcamps;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class CampCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		MobCamp mobCamp;
		if(sender instanceof Player){
			PlayerInventory playerInventory = ((Player)sender).getInventory();
			MobCampQuery mainhandQuery = MobCamp.get(playerInventory.getItemInMainHand());
			MobCampQuery offhandQuery = MobCamp.get(playerInventory.getItemInMainHand());
			if(mainhandQuery.getMobCamp()!=null) mobCamp = mainhandQuery.getMobCamp();
			else if(offhandQuery.getMobCamp()!=null) mobCamp = offhandQuery.getMobCamp();
			else mobCamp = null;
		}
		else{
			mobCamp = null;
		}
		switch(args[0].toLowerCase()){
		case "info":
		case "auflisten":
		case "liste":
		case "list":{
			YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("camps/list_camps.php");
			if(yamlConfiguration==null || !yamlConfiguration.contains("lines")){
				sender.sendMessage("[MobCamps] Ein Fehler ist aufgetreten.");
				return true;
			}
			sender.sendMessage("[MobCamps] Alle Mob Camps:");
			for(String line : yamlConfiguration.getStringList("lines")){
				sender.sendMessage(line);
			}
			return true;
		}
		case "erstelle":
		case "erstellen":
		case "create":{
			if(args.length<2) return false;
			MobCamp result = MobCamp.create(args[1]);
			if(result==null){
				sender.sendMessage("[MobCamps] Konnte das Mob Camp nicht erstellen.");
			}
			else{
				sender.sendMessage("[MobCamps] '"+result.getName()+"' erstellt.");
				Bukkit.dispatchCommand(sender, "camp get "+result.getName());
				if(sender instanceof Player){
					MobCampEditor.open((Player)sender, result);
				}
			}
			return true;
		}
		case "item":
		case "get":{
			if(!(sender instanceof Player)){
				sender.sendMessage("[MobCamps] Diesen Befehl kannst du nur ingame verwenden.");
				return true;
			}
			if(args.length<2){
				if(mobCamp==null) return false;
			}
			else{
				mobCamp = MobCamp.get(args[1]);
			}
			if(mobCamp==null){
				if(args.length>1){
					sender.sendMessage("[MobCamps] '"+args[1]+"' nicht gefunden.");
				}
				else{
					sender.sendMessage("[MobCamps] Kein Mob Camp ausgewählt.");
				}
				return true;
			}
			int amount = 1;
			if(args.length>2 && StringUtils.isNumeric(args[2])){
				amount = Integer.parseInt(args[2]);
			}
			ItemStack itemStack = mobCamp.getInventoryToken(amount);
			((Player)sender).getInventory().addItem(itemStack);
			return true;
		}
		case "lade":
		case "load":
		case "reload":{
			if(args.length<2){
				MobCampInstance.loadAll();
				sender.sendMessage("[MobCamps] Mob Camp Instanzen geladen.");
				return true;
			}
			World world = Bukkit.getWorld(args[1]);
			if(world==null){
				sender.sendMessage("[MobCamps] Welt '"+args[1]+"' nicht gefunden.");
				return true;
			}
			MobCampInstance.loadAll(world);
			sender.sendMessage("[MobCamps] Mob Camp Instanzen geladen.");
			return true;
		}
		case "aktualisiere":
		case "update":{
			if(!(sender instanceof Player)){
				sender.sendMessage("[MobCamps] Diesen Befehl kannst du nur ingame verwenden.");
				return true;
			}
			if(args.length<2){
				if(mobCamp==null) return false;
			}
			else{
				mobCamp = MobCamp.get(args[1]);
			}
			if(mobCamp==null){
				if(args.length>1){
					sender.sendMessage("[MobCamps] '"+args[1]+"' nicht gefunden.");
				}
				else{
					sender.sendMessage("[LootTables] Kein Mob Camp ausgewählt.");
				}
				return true;
			}
			mobCamp.updateInstances();
			mobCamp.updateTokens();
			return true;
		}
		case "umbenennen":
		case "name":
		case "nenne":
		case "rename":{
			if(args.length<2) return false;
			if(args.length<3){
				if(mobCamp==null) return false;
			}
			else{
				mobCamp = MobCamp.get(args[1]);
			}
			if(mobCamp==null){
				if(args.length>2) sender.sendMessage("[MobCamps] '"+args[1]+"' nicht gefunden.");
				else sender.sendMessage("[MobCamps] Mob Camp nicht gefunden.");
				return true;
			}
			String oldName = mobCamp.getName();
			mobCamp.setName(args.length>2 ? args[2] : args[1]);
			sender.sendMessage("[LootTables] '"+oldName+"' zu '"+mobCamp.getName()+"' umbenennt.");
			return true;
		}
		case "radius":{
			if(args.length<2) return false;
			String spawnRadiusString;
			int spawnRadius;
			if(args.length>2){
				mobCamp = MobCamp.get(args[1]);
				spawnRadiusString = args[2];
			}
			else{
				spawnRadiusString = args[1];
			}
			if(mobCamp==null){
				if(args.length>1) sender.sendMessage("[MobCamps] '"+args[1]+"' nicht gefunden.");
				else sender.sendMessage("[MobCamps] Mob Camp nicht gefunden.");
				return true;
			}
			try{
				spawnRadius = Integer.parseInt(spawnRadiusString);
			}
			catch(Exception e){
				sender.sendMessage("[MobCamps] Ungültige Zahl '"+spawnRadiusString+"'");
				return true;
			}
			if(spawnRadius<17){
				sender.sendMessage("[MobCamps] Der Spawn Radius muss mindestens 16 Blöcke betragen.");
				return true;
			}
			mobCamp.setSpawnRadius(spawnRadius);
			return true;
		}
		case "limit":
		case "max_entities":
		case "max_spawns":
		case "max_nearby":
		case "max_nearby_entities":{
			if(args.length<2) return false;
			String maxNearbyEntitiesString;
			int maxNearbyEntities;
			if(args.length>2){
				mobCamp = MobCamp.get(args[1]);
				maxNearbyEntitiesString = args[2];
			}
			else{
				maxNearbyEntitiesString = args[1];
			}
			if(mobCamp==null){
				if(args.length>1) sender.sendMessage("[MobCamps] '"+args[1]+"' nicht gefunden.");
				else sender.sendMessage("[MobCamps] Mob Camp nicht gefunden.");
				return true;
			}
			try{
				maxNearbyEntities = Integer.parseInt(maxNearbyEntitiesString);
			}
			catch(Exception e){
				sender.sendMessage("[MobCamps] Ungültige Zahl '"+maxNearbyEntitiesString+"'");
				return true;
			}
			mobCamp.setMaxNearbyEntities(maxNearbyEntities);
			return true;
		}
		case "distanz":
		case "player_range":
		case "aggro_range":
		case "trigger":
		case "auslöser":{
			if(args.length<2) return false;
			String requiredPlayerRangeString;
			int requiredPlayerRange;
			if(args.length>2){
				mobCamp = MobCamp.get(args[1]);
				requiredPlayerRangeString = args[2];
			}
			else{
				requiredPlayerRangeString = args[1];
			}
			if(mobCamp==null){
				if(args.length>1) sender.sendMessage("[MobCamps] '"+args[1]+"' nicht gefunden.");
				else sender.sendMessage("[MobCamps] Mob Camp nicht gefunden.");
				return true;
			}
			try{
				requiredPlayerRange = Integer.parseInt(requiredPlayerRangeString);
			}
			catch(Exception e){
				sender.sendMessage("[MobCamps] Ungültige Zahl '"+requiredPlayerRangeString+"'");
				return true;
			}
			mobCamp.setRequiredPlayerRange(requiredPlayerRange);
			return true;
		}
		case "bearbeite":
		case "bearbeiten":
		case "edit":{
			if(!(sender instanceof Player)){
				sender.sendMessage("[LootTables] Du kannst Mob Camps nur ingame bearbeiten.");
				return true;
			}
			if(args.length>1){
				mobCamp = MobCamp.get(args[1]);
			}
			if(mobCamp==null){
				if(args.length>1) sender.sendMessage("[LootTables] '"+args[1]+"' nicht gefunden.");
				else sender.sendMessage("[LootTables] Mob Camp nicht gefunden.");
				return true;
			}
			MobCampEditor.open((Player)sender, mobCamp);
			return true;
		}
		default: return false;
		}
	}
}
