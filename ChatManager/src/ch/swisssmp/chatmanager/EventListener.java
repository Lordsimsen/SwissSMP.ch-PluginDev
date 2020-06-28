package ch.swisssmp.chatmanager;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.webcore.DataSource;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class EventListener implements Listener {
    @EventHandler(ignoreCancelled=true)
    private void onPlayerJoin(PlayerJoinEvent event){
        UUID playerUid = event.getPlayer().getUniqueId();
        String name = event.getPlayer().getName();
        String world = event.getPlayer().getWorld().getName();
        String message = "joined";
        ChatManager.log(playerUid, name, world, message);
    }

    @EventHandler(ignoreCancelled=true)
    private void onPlayerQuit(PlayerQuitEvent event){
        UUID playerUid = event.getPlayer().getUniqueId();
        String name = event.getPlayer().getName();
        String world = event.getPlayer().getWorld().getName();
        String message = "quit";
        ChatManager.log(playerUid, name, world, message);
    }

    @EventHandler(ignoreCancelled=true,priority= EventPriority.LOWEST)
    private void onChat(AsyncPlayerChatEvent event){
        UUID playerUid = event.getPlayer().getUniqueId();
        String name = event.getPlayer().getName();
        String world = event.getPlayer().getWorld().getName();
        String message = event.getMessage();
        ChatManager.log(playerUid, name, world, message);
    }

    @EventHandler(ignoreCancelled=true)
    private void onPlayerCommand(PlayerCommandPreprocessEvent event){
        String message = event.getMessage();
        String[] alertCommands = new String[]{
                "/msg ",
                "/w ",
                "/t ",
                "/pm ",
                "/emsg ",
                "/epm ",
                "/tell ",
                "/etell ",
                "/whisper ",
                "/ewhisper ",
                "/m ",
                "/r ",
                "/a "
        };
        boolean isAlertCommand = false;
        for(String s : alertCommands){
            if(message.toLowerCase().contains(s)){
                isAlertCommand = true;
                break;
            }
        }

        if (!isAlertCommand) {
            return;
        }

        String recipient = message.split(" ")[1];
        Player player = Bukkit.getPlayer(recipient);
        if(player==null) return;
        if(player.hasPermission("chatnotifier.personalalert") || event.getPlayer().hasPermission("chatnotifier.personalalert")){
            UUID playerUid = event.getPlayer().getUniqueId();
            String name = event.getPlayer().getName();
            String world = event.getPlayer().getWorld().getName();
            message = ChatManager.extractMessage(message);
            ChatManager.log(playerUid, name, recipient, world, message);
        }
    }
}
