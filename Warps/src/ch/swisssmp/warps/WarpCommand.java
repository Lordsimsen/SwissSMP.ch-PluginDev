package ch.swisssmp.warps;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("/warp kann nur ingame verwendet werden");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            String name = args[0];
            WarpPoint warp = WarpPoints.getWarp(name);
            if (warp == null) {
                player.sendMessage(WarpsPlugin.getPrefix() + ChatColor.GRAY + " Konnte Warppunkt " + ChatColor.RED + name + ChatColor.GRAY + " nicht finden.");
                return true;
            }
            try {
                player.teleport(warp.getWarpLocation());
                return true;
            } catch (Exception e) {
                player.sendMessage(WarpsPlugin.getPrefix() + ChatColor.GRAY + " Konnte Warppunkt " + ChatColor.RED + name + ChatColor.GRAY + " nicht erreichen.");
                return true;
            }
        }
        if(args.length == 2) {
            if (!args[0].equalsIgnoreCase("set") && !args[0].equalsIgnoreCase("create")) {
                return false;
            }
            String name = args[1];
            Location location = player.getLocation();
            WarpPoint warp = WarpPoint.setWarp(name, location);
            player.sendMessage(WarpsPlugin.getPrefix() + ChatColor.GREEN + " Warppunkt " + ChatColor.YELLOW + name + ChatColor.GREEN + " erstellt.");
            return true;
        } else{
            return false;
        }
    }
}
