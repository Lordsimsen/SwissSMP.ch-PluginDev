package ch.swisssmp.utils;

import ch.swisssmp.text.RawText;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BroadcastCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String message = String.join(" ", args);

        RawText text = new RawText(
                new RawText("[").color(ChatColor.DARK_GRAY),
                new RawText("Brotkasten").color(ChatColor.RED),
                new RawText("] ").color(ChatColor.DARK_GRAY),
                new RawText(message).color(ChatColor.GREEN)
        );
        BaseComponent spigotMessage = text.spigot();
        for(Player player : Bukkit.getOnlinePlayers()){
            player.spigot().sendMessage(spigotMessage);
        }
        return true;
    }
}
