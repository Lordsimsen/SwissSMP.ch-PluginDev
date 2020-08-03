package ch.swisssmp.city.command;

import ch.swisssmp.city.*;
import ch.swisssmp.utils.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CityCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) return false;
        String key = args[1];
        if (key.length() < 2) {
            sender.sendMessage(CitySystemPlugin.getPrefix() + " Name muss mindestens zwei Zeichen lang sein.");
            return true;
        }
        City city = CitySystem.findCity(key).orElse(null);
        if (city == null) {
            sender.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.RED + " Stadt " + key + " nicht gefunden.");
            return true;
        }
        switch (args[0]) {
            case "remove":
            case "delete": {
                city.delete((success) -> {
                    if (success) {
                        sender.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.GREEN + " Stadt " + city.getName() + " gelöscht.");
                    } else {
                        sender.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.GREEN + " Konnte Stadt " + city.getName() + " nicht löschen.");
                    }
                });
                return true;
            }
            case "ring": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(CitySystemPlugin.getPrefix() + " Befehl kann nur ngame verwendet werden.");
                    return true;
                }
                if (args.length < 3) return false;
                Player player = (Player) sender;
                PlayerData owner;
                String ringType;
                if (args.length > 3) {
                    ringType = args[3];
                    Citizenship citizenship = city.getCitizenship(args[2]).orElse(null);
                    if (citizenship == null) {
                        sender.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.RED + " Bürger " + args[2] + " nicht gefunden.");
                        return true;
                    }
                    owner = citizenship.getPlayerData();
                } else {
                    ringType = args[2];
                    owner = PlayerData.get(player);
                }

                ItemStack itemStack = ItemManager.createRing(ringType, city, owner);
                player.getWorld().dropItem(player.getEyeLocation(), itemStack);
                return true;
            }
            case "reload": {
                city.reload((success) -> {
                    if (success) {
                        sender.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.GREEN + " Stadt " + city.getName() + " aktualisiert.");
                    } else {
                        sender.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.GREEN + " Konnte Stadt " + city.getName() + " nicht aktualisieren.");
                    }
                });
                return true;
            }
            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            Collection<String> options = Arrays.asList("remove", "ring", "reload");
            String current = args.length > 0 ? args[0] : null;
            return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
        }
        Collection<String> cities = CitySystem.getCities().stream().map(City::getName).collect(Collectors.toList());
        String current = args[1];
        return StringUtil.copyPartialMatches(current, cities, new ArrayList<>());
    }
}
