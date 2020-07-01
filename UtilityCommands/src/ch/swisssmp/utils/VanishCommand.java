package ch.swisssmp.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

public class VanishCommand implements CommandExecutor {

    public final static String vanishPrefix = ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + "Vanish" + ChatColor.DARK_AQUA + "]";

    private static HashSet<UUID> vanishedPlayers = new HashSet<>();

    protected static HashSet<UUID> getVanishedPlayers(){
        return vanishedPlayers;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length>0){
            sender.sendMessage("/vanish funktioniert nur bei dir selbst");
            return true;
        }
        if(!(sender instanceof Player)) {
            sender.sendMessage("/vanish kann nur ingame verwendet werden.");
            return true;
        }
        Player vanisher = (Player) sender;
        if(vanishedPlayers.contains(vanisher.getUniqueId())){
            for(Player player : Bukkit.getServer().getOnlinePlayers()){
                if(player == vanisher) continue;
                if(player.hasPermission("smp.commands.vanish")) {
                    player.sendMessage(vanishPrefix + " " + ChatColor.AQUA + vanisher.getName()
                            + ChatColor.DARK_AQUA + " ist wieder sichtbar. Poof!");
                    continue;
                }
                player.showPlayer(UtilityCommandsPlugin.getInstance(), vanisher);
            }
            vanishedPlayers.remove(vanisher.getUniqueId());
            vanisher.sendMessage(vanishPrefix + " " + ChatColor.DARK_AQUA + "Du bist wieder sichtbar. Poof!");
            vanisher.playSound(vanisher.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1, 1);
        } else{
            for(Player player : Bukkit.getServer().getOnlinePlayers()){
                if(player == vanisher) continue;
                if(player.hasPermission("smp.commands.vanish")) {
                    player.sendMessage(vanishPrefix + " " + ChatColor.AQUA + vanisher.getName() + ChatColor.DARK_AQUA + " ist nun unsichtbar. Poof!");
                    continue;
                }
                player.hidePlayer(UtilityCommandsPlugin.getInstance(), vanisher);
            }
            vanishedPlayers.add(vanisher.getUniqueId());
            vanisher.sendMessage(vanishPrefix + ChatColor.DARK_AQUA + "Du bist verschwunden. Poof!");
            vanisher.playSound(vanisher.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1, 1);
        }
        return true;
    }
}
