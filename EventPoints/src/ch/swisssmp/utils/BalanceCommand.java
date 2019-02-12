package ch.swisssmp.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public final class BalanceCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return true;
		Player player = (Player) sender;
		String currencyType = (args!=null && args.length>0) ? args[0] : "EVENT_POINT";
		HTTPRequest request = DataSource.getResponse(EventPoints.getInstance(), "balance.php", new String[]{
				"player="+URLEncoder.encode(player.getUniqueId().toString()),
				"currency="+URLEncoder.encode(currencyType)
				});
		request.onFinish(()->{
			sendResponse(request.getYamlResponse(), player);
		});
		return true;
	}

	private void sendResponse(YamlConfiguration yamlConfiguration, Player player){
		if(yamlConfiguration==null) return;
		if(!yamlConfiguration.contains("message")) return;
		for(String line : yamlConfiguration.getStringList("message"))
			player.sendMessage(line);
	}
}
