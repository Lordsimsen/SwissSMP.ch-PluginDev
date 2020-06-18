package ch.swisssmp.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public final class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch(command.getName()){
		case "help":{
			HTTPRequest request = DataSource.getResponse(UtilityCommands.getInstance(), "help.php");
			request.onFinish(()->{
				YamlConfiguration yamlConfiguration = request.getYamlResponse();
				if(yamlConfiguration==null || !yamlConfiguration.contains("message")){
					sender.sendMessage("[Hilfe] https://swisssmp.ch/forums/serverinfos.1/");
					return;
				}
				for(String line : yamlConfiguration.getStringList("message"))
					sender.sendMessage(line);
			});
			return true;
		}
		case "seen":{
			if(args==null || args.length<1) return false;
			HTTPRequest request = DataSource.getResponse(UtilityCommands.getInstance(), "seen.php", new String[]{"player="+URLEncoder.encode(args[0])});
			request.onFinish(()->{
				YamlConfiguration yamlConfiguration = request.getYamlResponse();
				if(yamlConfiguration==null || !yamlConfiguration.contains("message")) return;
				for(String line : yamlConfiguration.getStringList("message"))
					sender.sendMessage(line);
			});
			return true;
		}
		case "more":{
			if(!(sender instanceof Player) || ((Player)sender).getGameMode()!=GameMode.CREATIVE) return true;
			PlayerInventory playerInventory = ((Player)sender).getInventory();
			ItemStack itemStack = playerInventory.getItemInMainHand()!=null ? playerInventory.getItemInMainHand() : playerInventory.getItemInOffHand();
			if(itemStack==null) return true;
			if(itemStack.getAmount()>=64) playerInventory.addItem(itemStack.clone());
			else itemStack.setAmount(64);
			return true;
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
			return true;
		}
		case "list":{
			List<String> arguments = new ArrayList<String>();
			for(Player player : Bukkit.getOnlinePlayers()){
				arguments.add("players[]="+URLEncoder.encode(player.getName()));
			}
			String[] argumentsArray = new String[arguments.size()];
			HTTPRequest request = DataSource.getResponse(UtilityCommands.getInstance(), "list.php", arguments.toArray(argumentsArray));
			request.onFinish(()->{
				YamlConfiguration yamlConfiguration = request.getYamlResponse();
				if(yamlConfiguration==null || !yamlConfiguration.contains("message")) return;
				for(String line : yamlConfiguration.getStringList("message"))
					sender.sendMessage(line);
			});
			return true;
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
			HTTPRequest request = DataSource.getResponse(UtilityCommands.getInstance(), "cityswap.php", parameters.toArray(parametersArray));
			request.onFinish(()->{
				if(!request.getResponse().isEmpty()) sender.sendMessage(request.getResponse());
			});
			
			return true;
		}
		case "choose":{
			if(!(sender instanceof Player)) return true;
			if(args==null) return true;
			if(args.length<2) return true;
			Player player = (Player) sender;
			String requestIDString = args[0];
			ChatRequest request = ChatRequest.get(requestIDString);
			if(request==null) return true;
			String key = args[1];
			request.choose(player, key);
			return true;
		}
		case "gravity":
		{
			Player player;
			if(args.length>0){
				player = Bukkit.getPlayer(args[0]);
			}
			else if(sender instanceof Player){
				player = (Player) sender;
			}
			else{
				sender.sendMessage("/gravity kann nur ingame verwendet werden.");
				return true;
			}

			if(player==null){
				sender.sendMessage(args[0]+" nicht gefunden.");
				return true;
			}

			player.setGravity(!player.hasGravity());
			sender.sendMessage(player.getName()+(player.hasGravity()?" hat nun Schwerkraft." : " hat nun keine Schwerkraft mehr."));
			return true;
		}
		default:return false;
		}
	}

}
