package ch.swisssmp.utils;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class HauptstadtCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
        HTTPRequest request = DataSource.getResponse(UtilityCommandsPlugin.getInstance(), "cityswap.php", parameters.toArray(parametersArray));
        request.onFinish(()->{
            if(!request.getResponse().isEmpty()) sender.sendMessage(request.getResponse());
        });

        return true;
    }
}
