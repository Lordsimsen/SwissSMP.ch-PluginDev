package ch.swisssmp.warps;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveWarpCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("/warp kann nur ingame verwendet werden");
            return true;
        }
        Player player = (Player) sender;

        String name = args[0]; //supposing warp is given by name as second argument
        WarpPoint warp = WarpPoints.getWarp(name).orElse(null);
        if(warp != null) { //if warp found that way (which might not be accurate if there's more than one with the same name), it is removed
            World world = warp.getWorld();
            WarpPoints.remove(warp);
            player.sendMessage(WarpsPlugin.getPrefix() + ChatColor.GREEN + " Warppunkt "
                    + ChatColor.YELLOW + name + ChatColor.GREEN + " in "
                    + ChatColor.YELLOW + world.getName() + ChatColor.GREEN + " entfernt.");
            return true;
        } else { //warp with "name" wasn't found, so we suppose a world or the flag "-world" was given as second argument
            World world;
            if (name.equalsIgnoreCase("-world")) {
                world = player.getWorld();
            } else {
                world = Bukkit.getWorld(name);
            }
            if (world == null) {
                Bukkit.getLogger().info("Case delete, world is null");
                return false;
            }
            warp = WarpPoints.getWarp(name, world).orElse(null);
            if(warp == null){
                player.sendMessage(WarpsPlugin.getPrefix() + ChatColor.GRAY + " Konnte Warp " + ChatColor.RED + name + ChatColor.GRAY + " nicht finden.");
                return true;
            }
            WarpPoints.remove(warp);
            player.sendMessage(WarpsPlugin.getPrefix() + ChatColor.GREEN + " Warppunkt "
                    + ChatColor.YELLOW + name + ChatColor.GREEN + " in "
                    + ChatColor.YELLOW + world.getName() + ChatColor.GREEN + " entfernt.");
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command,String s,String[] args) {
        List<String> warpNames = WarpPoints.getAll().stream().map(WarpPoint::getName).collect(Collectors.toList());
        String current = args.length > 0 ? args[0] : "";

        return StringUtil.copyPartialMatches(current, warpNames, new ArrayList<>());
    }
}
