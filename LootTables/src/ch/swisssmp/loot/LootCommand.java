package ch.swisssmp.loot;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class LootCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		LootTable lootTable;
		if(sender instanceof Player){
			PlayerInventory playerInventory = ((Player)sender).getInventory();
			LootTableQuery mainhandQuery = LootTable.get(playerInventory.getItemInMainHand());
			LootTableQuery offhandQuery = LootTable.get(playerInventory.getItemInMainHand());
			if(mainhandQuery.getLootTable()!=null) lootTable = mainhandQuery.getLootTable();
			else if(offhandQuery.getLootTable()!=null) lootTable = offhandQuery.getLootTable();
			else lootTable = null;
		}
		else{
			lootTable = null;
		}
		switch(args[0].toLowerCase()){
		case "info":
		case "auflisten":
		case "liste":
		case "list":{
			YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("loot/list_tables.php");
			if(yamlConfiguration==null || !yamlConfiguration.contains("lines")){
				sender.sendMessage("[LootTables] Ein Fehler ist aufgetreten.");
				return true;
			}
			sender.sendMessage("[LootTables] Alle Beutetabellen:");
			for(String line : yamlConfiguration.getStringList("lines")){
				sender.sendMessage(line);
			}
			return true;
		}
		case "erstelle":
		case "erstellen":
		case "create":{
			if(args.length<2) return false;
			LootTable result = LootTable.get(args[1], true);
			if(result==null){
				sender.sendMessage("[LootTables] Konnte die Beutetabelle nicht erstellen.");
			}
			else{
				sender.sendMessage("[LootTable] '"+result.getName()+"' erstellt.");
				Bukkit.dispatchCommand(sender, "loot get "+result.getName());
				if(sender instanceof Player){
					LootTableEditor.open((Player)sender, result);
				}
			}
			return true;
		}
		case "item":
		case "get":{
			if(!(sender instanceof Player)){
				sender.sendMessage("[LootTables] Diesen Befehl kannst du nur ingame verwenden.");
				return true;
			}
			if(args.length<2){
				if(lootTable==null) return false;
			}
			else{
				lootTable = LootTable.get(args[1], false);
			}
			if(lootTable==null){
				if(args.length>1){
					sender.sendMessage("[LootTables] '"+args[1]+"' nicht gefunden.");
				}
				else{
					sender.sendMessage("[LootTables] Keine Beutetabelle ausgewählt.");
				}
				return true;
			}
			int amount = 1;
			if(args.length>2 && StringUtils.isNumeric(args[2])){
				amount = Integer.parseInt(args[2]);
			}
			ItemStack itemStack = lootTable.getInventoryToken(amount);
			((Player)sender).getInventory().addItem(itemStack);
			return true;
		}
		case "aktualisiere":
		case "update":{
			if(!(sender instanceof Player)){
				sender.sendMessage("[LootTables] Diesen Befehl kannst du nur ingame verwenden.");
				return true;
			}
			if(args.length<2){
				if(lootTable==null) return false;
			}
			else{
				lootTable = LootTable.get(args[1], false);
			}
			if(lootTable==null){
				if(args.length>1){
					sender.sendMessage("[LootTables] '"+args[1]+"' nicht gefunden.");
				}
				else{
					sender.sendMessage("[LootTables] Keine Beutetabelle ausgewählt.");
				}
				return true;
			}
			lootTable.updateTokens();
			return true;
		}
		case "umbenennen":
		case "name":
		case "nenne":
		case "rename":{
			if(args.length<2) return false;
			if(args.length<3){
				if(lootTable==null) return false;
			}
			else{
				lootTable = LootTable.get(args[1], false);
			}
			if(lootTable==null){
				if(args.length>2) sender.sendMessage("[LootTables] '"+args[1]+"' nicht gefunden.");
				else sender.sendMessage("[LootTables] Beutetabelle nicht gefunden.");
				return true;
			}
			String oldName = lootTable.getName();
			lootTable.setName(args.length>2 ? args[2] : args[1]);
			sender.sendMessage("[LootTables] '"+oldName+"' zu '"+lootTable.getName()+"' umbenennt.");
			return true;
		}
		case "kategorie":
		case "category":{
			if(args.length<2) return false;
			String categoryString;
			LootType lootType;
			if(args.length>2){
				lootTable = LootTable.get(args[1], false);
				categoryString = args[2];
			}
			else{
				categoryString = args[1];
			}
			if(lootTable==null){
				if(args.length>1) sender.sendMessage("[LootTables] '"+args[1]+"' nicht gefunden.");
				else sender.sendMessage("[LootTables] Beutetabelle nicht gefunden.");
				return true;
			}
			lootType = LootType.getByName(categoryString);
			if(lootType==null){
				sender.sendMessage("[LootTables] '"+categoryString+"' ist keine gültige Loot Kategorie.");
				return true;
			}
			lootTable.setLootType(lootType);
			return true;
		}
		case "wahrscheinlichkeit":
		case "probability":
		case "chance":{
			if(args.length<2) return false;
			String chanceString;
			double chance;
			if(args.length>2){
				lootTable = LootTable.get(args[1], false);
				chanceString = args[2];
			}
			else{
				chanceString = args[1];
			}
			if(lootTable==null){
				if(args.length>1) sender.sendMessage("[LootTables] '"+args[1]+"' nicht gefunden.");
				else sender.sendMessage("[LootTables] Beutetabelle nicht gefunden.");
				return true;
			}
			try{
				if(chanceString.contains("%")) chanceString.replace("%","");
				chance = Double.parseDouble(chanceString)*0.01;
			}
			catch(Exception e){
				sender.sendMessage("[LootTables] Ungültige Zahl '"+chanceString+"'");
				return true;
			}
			lootTable.setChance(chance);
			return true;
		}
		case "max_items":
		case "max_stacks":
		case "anzahl":
		case "max_rolls":{
			if(args.length<2) return false;
			int minRolls;
			int maxRolls;
			try{
			//all arguments provided: LootTable, Min, Max
			if(args.length>3){
				lootTable = LootTable.get(args[1], false);
				minRolls = Integer.parseInt(args[2]);
				maxRolls = Integer.parseInt(args[3]);
			}
			//either one of these versions: LootTable, Amount || Min, Max
			else if(args.length>2){
				//First is numeric, so the version is Min, Max
				if(StringUtils.isNumeric(args[1])){
					minRolls = Integer.parseInt(args[1]);
					maxRolls = Integer.parseInt(args[2]);
				}
				//First is a LootTable Name, so the version is LootTable, Amount
				else{
					lootTable = LootTable.get(args[1],false);
					minRolls = Integer.parseInt(args[2]);
					maxRolls = minRolls;
				}
			}
			else{
				minRolls = Integer.parseInt(args[1]);
				maxRolls = minRolls;
			}
			}
			catch(Exception e){
				return false;
			}
			if(lootTable==null){
				if(args.length>1) sender.sendMessage("[LootTables] '"+args[1]+"' nicht gefunden.");
				else sender.sendMessage("[LootTables] Beutetabelle nicht gefunden.");
				return true;
			}
			lootTable.setRolls(minRolls, maxRolls);
			return true;
		}
		case "bearbeite":
		case "bearbeiten":
		case "edit":{
			if(!(sender instanceof Player)){
				sender.sendMessage("[LootTables] Du kannst Beutetabellen nur ingame bearbeiten.");
				return true;
			}
			if(args.length>1){
				lootTable = LootTable.get(args[1], false);
			}
			if(lootTable==null){
				if(args.length>1) sender.sendMessage("[LootTables] '"+args[1]+"' nicht gefunden.");
				else sender.sendMessage("[LootTables] Beutetabelle nicht gefunden.");
				return true;
			}
			LootTableEditor.open((Player)sender, lootTable);
			return true;
		}
		case "lösche":
		case "entferne":
		case "delete":{
			if(args.length<2){
				if(lootTable==null) return false;
			}
			if(args.length>1){
				lootTable = LootTable.get(args[1], false);
			}
			if(lootTable==null){
				if(args.length>1) sender.sendMessage("[LootTables] '"+args[1]+"' nicht gefunden.");
				else sender.sendMessage("[LootTables] Beutetabelle nicht gefunden.");
				return true;
			}
			lootTable.remove();
			sender.sendMessage("[LootTables] '"+lootTable.getName()+"' entfernt.");
			return true;
		}
		default: return false;
		}
	}
}
