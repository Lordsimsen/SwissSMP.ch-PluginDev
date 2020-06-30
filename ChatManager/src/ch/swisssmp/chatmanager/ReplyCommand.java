package ch.swisssmp.chatmanager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ReplyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            return true;
        }

        Player player = (Player) sender;
        UUID playerUid = ((Player) sender).getUniqueId();
        UUID recipient = PlayerConversations.getConversationPartner(playerUid).orElse(null);
        if(recipient==null){
            player.sendMessage(ChatColor.RED+"Du hast niemanden, dem du antworten könntest.");
            return true;
        }

        OfflinePlayer other = Bukkit.getOfflinePlayer(recipient);
        if(other==null){
            player.sendMessage(ChatColor.RED+"Empfänger nicht gefunden.");
            return true;
        }
        if(!other.isOnline()){
            player.sendMessage(ChatColor.RED+other.getName()+" ist nicht mehr online.");
            return true;
        }

        player.performCommand("tell "+other.getName()+" "+String.join(" ", args));
        return true;
    }
}
