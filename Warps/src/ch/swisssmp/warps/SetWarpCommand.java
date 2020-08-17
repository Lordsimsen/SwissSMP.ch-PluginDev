package ch.swisssmp.warps;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("/warp kann nur ingame verwendet werden");
            return true;
        }
        Player player = (Player) sender;
        String name = args[0];
        Location location = player.getLocation();
        WarpPoints.createWarp(name, location);
        player.sendMessage(WarpsPlugin.getPrefix() + ChatColor.GREEN + " Warppunkt " + ChatColor.YELLOW + name + ChatColor.GREEN + " erstellt.");
        return true;
    }
}
