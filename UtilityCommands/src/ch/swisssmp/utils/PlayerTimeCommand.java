package ch.swisssmp.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerTimeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("/ptime kann nur ingame verwendet werden.");
            return true;
        }
        Player player = (Player) sender;
        switch(args[0]){
            case "set":{
                long inputTime = Long.parseLong(args[1]);
                long time = inputTime - player.getWorld().getTime();
                player.setPlayerTime(time, true);
                player.sendMessage(ChatColor.AQUA + "Persönliche Zeit auf " + ChatColor.DARK_AQUA + inputTime + ChatColor.AQUA + " Ticks gesetzt.");
                return true;
            }
            case "reset":{
                player.resetPlayerTime();
                player.sendMessage(ChatColor.AQUA + "Persönliche Zeit mit Serverzeit synchronisiert.");
                return true;
            }
            case "pause":{
                long playerTime = player.getPlayerTime();
                player.setPlayerTime(playerTime, false);
                player.sendMessage(ChatColor.AQUA + "Persönliche Zeit fixiert.");
                return true;
            }
            case "resume":{
                long playerTime = player.getPlayerTime();
                player.setPlayerTime(playerTime, true);
                player.sendMessage(ChatColor.AQUA + "Persönliche Zeit läuft wieder voran.");
                return true;
            }
            default: return false;
        }
    }
}
