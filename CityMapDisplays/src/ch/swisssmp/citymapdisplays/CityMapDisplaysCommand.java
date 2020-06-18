package ch.swisssmp.citymapdisplays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CityMapDisplaysCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(CityMapDisplaysPlugin.getPrefix()+" Kann nur ingame verwendet werden.");
            return true;
        }

        CityMapDisplaysView.open((Player) sender);
        return true;
    }
}
