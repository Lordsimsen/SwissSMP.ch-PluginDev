package ch.swisssmp.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ch.swisssmp.webcore.DataSource;

public final class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch(command.getName()){
		case "balance":{
			if(!(sender instanceof Player)) return true;
			Player player = (Player) sender;
			YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("players/balance.php", new String[]{"player="+URLEncoder.encode(player.getUniqueId().toString())});
			if(yamlConfiguration==null) return true;
			if(!yamlConfiguration.contains("message")) return true;
			for(String line : yamlConfiguration.getStringList("message"))
				sender.sendMessage(line);
			break;
		}
		case "seen":{
			if(args==null || args.length<1) return false;
			YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("players/seen.php", new String[]{"player="+URLEncoder.encode(args[0])});
			if(yamlConfiguration==null) return true;
			if(!yamlConfiguration.contains("message")) return true;
			for(String line : yamlConfiguration.getStringList("message"))
				sender.sendMessage(line);
			break;
		}
		case "more":{
			if(!(sender instanceof Player) || ((Player)sender).getGameMode()!=GameMode.CREATIVE) return true;
			PlayerInventory playerInventory = ((Player)sender).getInventory();
			ItemStack itemStack = playerInventory.getItemInMainHand()!=null ? playerInventory.getItemInMainHand() : playerInventory.getItemInOffHand();
			if(itemStack==null) return true;
			if(itemStack.getAmount()>=64) playerInventory.addItem(itemStack.clone());
			else itemStack.setAmount(64);
			break;
		}
		case "amount":{
			if(!(sender instanceof Player) || ((Player)sender).getGameMode()!=GameMode.CREATIVE) return true;
			int amount;
			try{
				amount = Integer.parseInt(args[0]);
			}
			catch(Exception e){
				sender.sendMessage("[SwissSMPUtils] Ungültige Menge angegeben.");
				return true;
			}
			PlayerInventory playerInventory = ((Player)sender).getInventory();
			ItemStack itemStack = playerInventory.getItemInMainHand()!=null ? playerInventory.getItemInMainHand() : playerInventory.getItemInOffHand();
			if(itemStack==null) return true;
			itemStack.setAmount(amount);
			break;
		}
		case "list":{
			List<String> arguments = new ArrayList<String>();
			for(Player player : Bukkit.getOnlinePlayers()){
				arguments.add("players[]="+URLEncoder.encode(player.getName()));
			}
			String[] argumentsArray = new String[arguments.size()];
			YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("players/list.php", arguments.toArray(argumentsArray));
			if(yamlConfiguration==null) return true;
			if(!yamlConfiguration.contains("message")) return true;
			for(String line : yamlConfiguration.getStringList("message"))
				sender.sendMessage(line);
			break;
		}
		case "worlds":{
			List<String> worldNames = new ArrayList<String>();
			for(World world : Bukkit.getWorlds()){
				worldNames.add(world.getName());
			}
			sender.sendMessage(String.join(", ", worldNames));
			break;
		}
		case "hauptstadt":{
			if(!(sender instanceof Player)){
				sender.sendMessage("Can only be used from within the game");
				return false;
			}
			Player player = (Player) sender;
			ArrayList<String> parameters = new ArrayList<String>();
			String[] parametersArray;
			if(args.length>0){
				parameters.add("maincity="+URLEncoder.encode(args[0]));
			}
			parameters.add("player="+player.getUniqueId());
			parametersArray = new String[parameters.size()];
			String message = DataSource.getResponse("players/cityswap.php", parameters.toArray(parametersArray));
			sender.sendMessage(message);
			break;
		}
		}
		return true;
	}

}
