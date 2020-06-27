package ch.swisssmp.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GodCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if(args.length>0){
            player = Bukkit.getPlayer(args[0]);
        }
        else if(sender instanceof Player){
            player = (Player) sender;
        }
        else{
            sender.sendMessage("/god kann nur ingame verwendet werden.");
            return true;
        }
        if(player==null){
            sender.sendMessage(args[0]+" nicht gefunden.");
            return true;
        }
        if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR){
            player.sendMessage(ChatColor.AQUA + "Du bist bereits unverwundbar durch deinen Gamemode.");
            return true;
        }
        if (player.isInvulnerable()) {
            player.setInvulnerable(false);
            player.sendMessage(ChatColor.AQUA + "Du bist wieder verwundbar.");
        } else {
            player.setInvulnerable(true);
            player.sendMessage(ChatColor.AQUA + "Du bist nun unverwundbar.");
        }
        if(!player.equals(sender)) sender.sendMessage(ChatColor.AQUA + "Unverwundbarkeit für " + ChatColor.DARK_PURPLE + player.getName()
                + ChatColor.AQUA + (player.isInvulnerable()?" aktiviert." : " deaktiviert."));
        return true;
    }
}
