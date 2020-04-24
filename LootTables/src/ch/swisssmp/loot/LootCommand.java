package ch.swisssmp.loot;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class LootCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0){
			if(!(sender instanceof Player)) return false;
			LootTablesView.open((Player)sender);
			return true;
		}
		LootTable lootTable;
		if(sender instanceof Player){
			PlayerInventory playerInventory = ((Player)sender).getInventory();
			LootTable mainhandTable = LootTable.get(playerInventory.getItemInMainHand());
			LootTable offhandTable = LootTable.get(playerInventory.getItemInMainHand());
			if(mainhandTable!=null) lootTable = mainhandTable;
			else if(offhandTable!=null) lootTable = offhandTable;
			else lootTable = null;
		}
		else{
			lootTable = null;
		}
		switch(args[0].toLowerCase()){
		case "erstelle":
		case "erstellen":
		case "create":{
			if(args.length<2) return false;
			LootTable.get(args[1], true, (result)->{
				if(result==null){
					sender.sendMessage("[LootTables] Konnte die Beutetabelle nicht erstellen.");
				}
				else{
					sender.sendMessage("[LootTable] '"+result.getName()+"' erstellt.");
					if(sender instanceof Player){
						((Player)sender).getInventory().addItem(result.getInventoryToken(1));
						LootTableEditor.open((Player)sender, result);
					}
				}
			});
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
				lootTable = LootTable.get(args[1]);
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
				lootTable = LootTable.get(args[1]);
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
				lootTable = LootTable.get(args[1]);
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
				lootTable = LootTable.get(args[1]);
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
				lootTable = LootTable.get(args[1]);
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
					lootTable = LootTable.get(args[1]);
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
		case "lösche":
		case "entferne":
		case "delete":{
			if(args.length<2){
				if(lootTable==null) return false;
			}
			if(args.length>1){
				lootTable = LootTable.get(args[1]);
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
