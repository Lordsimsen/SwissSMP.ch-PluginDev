package ch.swisssmp.flyday;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public class FlyDayCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args == null || args.length == 0) {
            return false;
        }
        switch (args[0].toLowerCase()) {
            case "reload": {
                FlyDay.updateState();
                sender.sendMessage(FlyDayPlugin.getPrefix() + " FlyDay Status aktualisiert.");
                break;
            }
            case "is": {
                sender.sendMessage(String.join(", ", FlyDay.getActiveWorlds()));
                break;
            }
            case "on": {
                if (args.length < 2) {
                    return false;
                }
                String[] worlds = args[1].split(",");
                ArrayList<String> arguments = new ArrayList<String>();
                for (String world : worlds) {
                    if (Bukkit.getWorld(world) == null) {
                        sender.sendMessage(FlyDayPlugin.getPrefix() + " Welt " + world + " nicht gefunden.");
                        continue;
                    }
                    arguments.add("worlds[]=" + URLEncoder.encode(world));
                }
                arguments.add("global_flight=1");
                HTTPRequest request = DataSource.getResponse(FlyDayPlugin.getInstance(), FlyDayUrl.SET, arguments.toArray(new String[0]));
                request.onFinish(() -> {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast " + ChatColor.GREEN + "Heute ist " + ChatColor.YELLOW + "FlyDay" + ChatColor.GREEN + "! Wir wünschen euch viel Vergnügen beim Bauen und Erkunden.");
                    FlyDay.updateState();
                    sender.sendMessage(FlyDayPlugin.getPrefix() + " FlyDay gestartet.");
                });
                break;
            }
            case "off": {
                String[] worlds = args.length > 1 ? args[1].split(",") : new String[]{Bukkit.getWorlds().get(0).getName()};
                ArrayList<String> arguments = new ArrayList<String>();
                for (String world : worlds) {
                    if (Bukkit.getWorld(world) == null) {
                        sender.sendMessage(FlyDayPlugin.getPrefix() + " Welt " + world + " nicht gefunden.");
                        continue;
                    }
                    arguments.add("worlds[]=" + URLEncoder.encode(world));
                }
                arguments.add("global_flight=0");
                HTTPRequest request = DataSource.getResponse(FlyDayPlugin.getInstance(), FlyDayUrl.SET, arguments.toArray(new String[0]));
                request.onFinish(() -> {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast " + ChatColor.GREEN + "Der " + ChatColor.YELLOW + "FlyDay" + ChatColor.GREEN + " ist nun vorbei.");
                    FlyDay.updateState();
                    sender.sendMessage(FlyDayPlugin.getPrefix() + " FlyDay beendet.");
                });
                break;
            }
            default:
                return false;
        }
        return true;
    }

}
