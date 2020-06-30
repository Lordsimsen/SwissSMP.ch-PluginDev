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

public class WarpCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("/warp kann nur ingame verwendet werden");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) { // Warppoint given by name as first and only argument
            String name = args[0];
            WarpPoint warp = WarpPoints.getWarp(name).orElse(null);
            if (warp == null) {
                player.sendMessage(WarpsPlugin.getPrefix() + ChatColor.GRAY + " Konnte Warp " + ChatColor.RED + name + ChatColor.GRAY + " nicht finden.");
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
        //neither creation nor removal of a warp, so maybe the world of the target warp was given as second argument
        World world;
        if(!args[0].equalsIgnoreCase("-world")){
            world = Bukkit.getWorld(args[0]);
        } else{
            world = player.getWorld();
        }
        if(world == null){
            Bukkit.getLogger().info("Case default, world is null");
            return false;
        }
        String name = args[1];
        WarpPoint warp = WarpPoints.getWarp(name, world).orElse(null);
        if(warp != null) {
            player.teleport(warp.getWarpLocation());
        } else{
            player.sendMessage(WarpsPlugin.getPrefix() + ChatColor.RED + " Warp und/oder Welt nicht gefunden.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command,String s,String[] args) {
        List<String> warpNames = WarpPoints.getAll().stream().map(WarpPoint::getName).collect(Collectors.toList());
        String current = args.length > 0 ? args[0] : "";

        return StringUtil.copyPartialMatches(current, warpNames, new ArrayList<>());
    }
}
