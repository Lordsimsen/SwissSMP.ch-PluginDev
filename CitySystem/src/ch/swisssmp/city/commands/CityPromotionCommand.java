package ch.swisssmp.city.commands;

import ch.swisssmp.city.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CityPromotionCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length<3) return false;
        City city = CitySystem.findCity(args[1]).orElse(null);
        Techtree techtree = args.length>3 ? CitySystem.getTechtree(args[2]).orElse(null) : city!=null ? city.getTechtree() : null;
        CityLevel level = techtree!=null ? techtree.getLevel(args.length>3 ? args[3] : args[2]).orElse(null) : null;
        if(city==null){
            sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.RED+" Stadt "+args[1]+" nicht gefunden.");
            return true;
        }
        if(techtree==null){
            sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.RED+" Techtree "+(args.length>3 ? args[2] : city.getTechtreeId())+" nicht gefunden.");
            return true;
        }
        if(level==null){
            sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.RED+" Level "+(args.length>3 ? args[3] : args[2])+" nicht gefunden.");
            return true;
        }
        switch(args[0]){
            case "add":{
                if(city.hasLevel(level)){
                    sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.GRAY+" "+city.getName()+" hat die Stufe "+level.getId()+" vom Techtree "+techtree.getId()+" bereits freigeschaltet.");
                    return true;
                }
                city.unlockLevel(level, (success)->{
                    if(success){
                        sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.GREEN+" "+city.getName()+" hat die Stufe "+level.getId()+" vom Techtree "+techtree.getId()+" nun freigeschaltet!");
                    }
                    else{
                        sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.RED+" Konnte die Stufe "+level.getId()+" vom Techtree "+techtree.getId()+" für die Stadt "+city.getName()+" nicht freischalten. (Systemfehler)");
                    }
                });
                return true;
            }
            case "remove":{
                if(!city.hasLevel(level)){
                    sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.GRAY+" "+city.getName()+" hat die Stufe "+level.getId()+" vom Techtree "+techtree.getId()+" noch nicht freigeschaltet.");
                    return true;
                }
                city.lockLevel(level, (success)->{
                    if(success){
                        sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.GRAY+" "+city.getName()+" hat die Stufe "+level.getId()+" vom Techtree "+techtree.getId()+" nun nicht mehr freigeschaltet.");
                    }
                    else{
                        sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.RED+" Konnte die Stufe "+level.getId()+" vom Techtree "+techtree.getId()+" für die Stadt "+city.getName()+" nicht deaktivieren. (Systemfehler)");
                    }
                });
                return true;
            }
            case "key": {

                // /city key <Stadt> [<Techtree-Id>] <Level>

                if (!(sender instanceof Player)) {
                    sender.sendMessage(CitySystemPlugin.getPrefix() + " Befehl kann nur ngame verwendet werden.");
                    return true;
                }
                Player player = (Player) sender;
                ItemStack itemStack = level.getKeyStack(city);
                player.getWorld().dropItem(player.getEyeLocation(), itemStack);
                return true;
            }
            default: return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length<=1){
            List<String> options = Arrays.asList("add", "key", "remove");
            String current = args.length>0 ? args[0] : "";
            return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
        }
        if(args.length==2){
            List<String> options = CitySystem.getCities().stream().map(City::getName).collect(Collectors.toList());
            String current = args[1];
            return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
        }
        boolean remove = args[0].equals("remove");
        if(args.length==3){
            City city = CitySystem.findCity(args[1]).orElse(null);
            if(city==null) return null;
            Techtree techtree = city.getTechtree();
            if(techtree==null) return null;
            List<String> options = Stream.concat(CitySystem.getTechtrees().stream().map(Techtree::getId),techtree.getLevels().stream().filter(l->(!remove && !city.hasLevel(l)) || (remove && city.hasLevel(l))).map(CityLevel::getId)).collect(Collectors.toList());
            String current = args[2];
            return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
        }
        if(args.length==4){
            City city = CitySystem.findCity(args[1]).orElse(null);
            if(city==null) return null;
            Techtree techtree = CitySystem.getTechtree(args[2]).orElse(null);
            if(techtree==null) return null;
            List<String> options = techtree.getLevels().stream().filter(l->(!remove && !city.hasLevel(l)) || (remove && city.hasLevel(l))).map(CityLevel::getId).collect(Collectors.toList());
            String current = args[3];
            return StringUtil.copyPartialMatches(current,options,new ArrayList<>());
        }
        return null;
    }
}
