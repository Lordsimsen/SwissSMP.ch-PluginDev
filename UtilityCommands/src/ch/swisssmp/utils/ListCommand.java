package ch.swisssmp.utils;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ListCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<String> arguments = new ArrayList<String>();
        for(Player player : Bukkit.getOnlinePlayers()){
            arguments.add("players[]="+URLEncoder.encode(player.getName()));
        }
        String[] argumentsArray = new String[arguments.size()];
        HTTPRequest request = DataSource.getResponse(UtilityCommandsPlugin.getInstance(), "list.php", arguments.toArray(argumentsArray));
        request.onFinish(()->{
            YamlConfiguration yamlConfiguration = request.getYamlResponse();
            if(yamlConfiguration==null || !yamlConfiguration.contains("message")) return;
            for(String line : yamlConfiguration.getStringList("message"))
                sender.sendMessage(line);
        });
        return true;
    }
}
