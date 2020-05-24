package ch.swisssmp.laposte;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LaPosteCommand implements CommandExecutor {
    @Override
    public boolean onCommand( CommandSender commandSender, Command command, String s, String[] strings) {

        if(strings.length != 2) return false;
        String sender = strings[0];
        String recipient = strings[1];
        try{
            Player player = Bukkit.getPlayer(recipient);
            Bukkit.getScheduler().runTaskLater(LaPostePlugin.getInstance(), ()->{
                player.sendMessage(LaPostePlugin.getPrefix() + " Du hast soeben Post von " + ChatColor.AQUA + sender + ChatColor.RESET + " erhalten!");
            },100L);
            return true;
        } catch (NullPointerException e){
            return true;
        }
    }
}
