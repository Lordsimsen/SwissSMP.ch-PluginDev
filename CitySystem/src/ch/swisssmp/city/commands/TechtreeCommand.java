package ch.swisssmp.city.commands;

import ch.swisssmp.city.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

public class TechtreeCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length<2) return false;
        String techtreeId = args[1];
        Techtree techtree = CitySystem.getTechtree(techtreeId).orElse(null);
        if(techtree==null){
            sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.RED+" Techtree "+techtreeId+" nicht gefunden.");
            return true;
        }
        switch(args[0]){
            case "view":{
                if(!(sender instanceof Player)){
                    sender.sendMessage(CitySystemPlugin.getPrefix()+" Kann nur ingame verwendet werden.");
                    return true;
                }
                Player player = (Player) sender;
                City city = args.length>2 ? CitySystem.findCity(args[2]).orElse(null) : null;
                TechtreeView.open(player, techtree, city);
                return true;
            }
            case "reload":{
                techtree.reload((success)->{
                    if(success){
                        sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.GREEN+" Techtree "+techtreeId+" aktualisiert.");
                    }
                    else{
                        sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.RED+" Konnte Techtree "+techtreeId+" nicht aktualisieren.");
                    }
                });
                return true;
            }
            default: return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length<=1){
            List<String> options = Arrays.asList("reload", "view");
            String current = args.length>0 ? args[0] : "";
            return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
        }

        if(args.length==2){
            Collection<String> options = CitySystem.getTechtrees().stream().map(Techtree::getId).collect(Collectors.toList());
            String current = args.length>1 ? args[1] : "";
            return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
        }
        switch(args[0]){
            case "view":{
                List<String> options = CitySystem.getCities().stream().map(City::getName).collect(Collectors.toList());
                String current = args[2];
                return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
            }
            default: return null;
        }
    }
}
