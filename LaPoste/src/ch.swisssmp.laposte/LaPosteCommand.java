package ch.swisssmp.laposte;

import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LaPosteCommand implements CommandExecutor {
    @Override
    public boolean onCommand( CommandSender commandSender, Command command, String s, String[] strings) {
//        if(strings == null || strings.length == 0) return false;
//        if(strings.length == 1) {
//            Player player = Bukkit.getPlayer(strings[0]);
//            Bukkit.getLogger().info("Player: " + player);

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

    // 1. variants. Spieler für sich selbst on login listener
    // 2. variante: params: spielername, (optional) Name sender /laposte (spieler1) (spieler2)
    // if no arg and sender instanceof player, check for player, else
}
