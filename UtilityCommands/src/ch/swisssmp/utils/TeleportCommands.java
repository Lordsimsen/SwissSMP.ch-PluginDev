package ch.swisssmp.utils;

import ch.swisssmp.text.ClickEvent;
import ch.swisssmp.text.HoverEvent;
import ch.swisssmp.text.RawText;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TeleportCommands implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length < 2) {
            sender.sendMessage("Teleport kann nur ingame verwendet werden.");
            return true;
        }
        switch (label) {
            case "tp": {
                switch (args.length) {
                    case 1: {
                        Player player = (Player) sender;
                        Player destinationPlayer = Bukkit.getPlayer(args[0]);
                        if (destinationPlayer == null) {
                            player.sendMessage(ChatColor.RED + "Konnte " + ChatColor.YELLOW + args[0] + ChatColor.RED + " nicht finden");
                            return true;
                        }
                        player.teleport(destinationPlayer.getLocation());
                        return true;
                    }
                    case 2: {
                        Collection<Entity> teleported = TargetSelector.query(sender, args[0]);
                        Entity target = TargetSelector.query(sender, args[1]).stream().findFirst().orElse(null);
                        if (target == null) {
                            if (sender instanceof Player) {
                                ((Player) sender).spigot().sendMessage(new RawText(
                                        new RawText("Konnte Entität ").color(ChatColor.RED),
                                        new RawText(args[1]).color(ChatColor.YELLOW),
                                        new RawText(" nicht finden.").color(ChatColor.RED)).spigot());
                            } else {
                                sender.sendMessage("Konnte Entität " + args[1] + " nicht finden");
                            }
                            return true;
                        }
                        if (!sender.hasPermission("smp.commands.teleport.others")) {
                            if (sender instanceof Player) {
                                ((Player) sender).spigot().sendMessage(new RawText(
                                        new RawText("Du kannst andere Spieler nicht teleportieren.")
                                                .color(ChatColor.RED)
                                                .hoverEvent(HoverEvent.showText("Mod-Anleitung öffnen"))
                                                .clickEvent(ClickEvent.openUrl("https://swisssmp.ch/threads/staff-einf%C3%BChrung-mc.157/"))
                                ).spigot());
                            } else {
                                sender.sendMessage("Du kannst andere Spieler nicht teleportieren");
                            }
                            return true;
                        }
                        for(Entity entity : teleported) {
                            entity.teleport(target.getLocation());
                        }
                        return true;
                    }
                    default:
                        return false;
                }
            }
            case "tphere": {
                if (args.length != 1) return false;
                if(!(sender instanceof Player)){
                    sender.sendMessage("/tphere kann nur ingame verwendet werden");
                    return true;
                }
                Player player = (Player) sender;
                Collection<Entity> teleported = TargetSelector.query(sender, args[0]);
                for(Entity entity : teleported){
                    entity.teleport(player.getLocation());
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> playerNames = Bukkit.getServer().getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
        String current = args.length > 0 ? args[args.length - 1] : "";

        return StringUtil.copyPartialMatches(current, playerNames, new ArrayList<>());
    }
}
