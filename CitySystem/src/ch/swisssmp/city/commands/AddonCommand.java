package ch.swisssmp.city.commands;

import ch.swisssmp.city.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

public class AddonCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) return false;
        String cityKey = args[1];
        if (cityKey.length() < 2) {
            sender.sendMessage(CitySystemPlugin.getPrefix() + " Stadtname muss mindestens zwei Zeichen lang sein.");
            return true;
        }
        String addonKey = args[2];
        if (addonKey.length() < 2) {
            sender.sendMessage(CitySystemPlugin.getPrefix() + " Addon-Bezeichnung muss mindestens zwei Zeichen lang sein.");
            return true;
        }
        Addon addon = CitySystem.findAddon(cityKey, addonKey, false).orElse(null);
        if (addon == null) {
            sender.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.RED + " Addon " + addonKey + " der Stadt " + cityKey + " nicht gefunden.");
            return true;
        }
        switch (args[0]) {
            case "reload":
                addon.reload((success) -> {
                    if (success) {
                        sender.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.GREEN + " Addon " + addon.getName() + " aktualisiert.");
                    } else {
                        sender.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.RED + " Konnte Addon " + addon.getName() + " nicht aktualisieren.");
                    }
                });
                return true;
            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length<=1){
            Collection<String> options = Collections.singletonList("reload");
            String current = args.length>0 ? args[0] : "";
            return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
        }
        if(args.length==2){
            Collection<String> options = CitySystem.getCities().stream().map(City::getName).collect(Collectors.toList());
            String current = args[1];
            return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
        }
        if(args.length==3){
            String cityKey = args[1];
            City city = cityKey.length()>1 ? CitySystem.findCity(cityKey).orElse(null) : null;
            if(city==null) return null;
            Techtree techtree = city.getTechtree();
            if(techtree==null) return null;
            Collection<String> options = techtree.getAddonTypes().stream().map(AddonType::getAddonId).collect(Collectors.toList());
            String current = args[2];
            return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
        }

        return null;
    }
}
