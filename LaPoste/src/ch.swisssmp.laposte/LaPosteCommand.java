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
        if(strings.length != 2) return true;
        String sender = strings[0];
        String recipient = strings[1];
        Player player = Bukkit.getPlayer(recipient);
        Bukkit.getScheduler().runTaskLater(LaPostePlugin.getInstance(), ()->{
            try {
                player.sendMessage(LaPostePlugin.getPrefix() + " Du hast soeben Post von " + ChatColor.AQUA + sender + ChatColor.RESET + " erhalten!");
            } catch (NullPointerException e){
                return;
            }
        },100L);
        return true;
    }
}
