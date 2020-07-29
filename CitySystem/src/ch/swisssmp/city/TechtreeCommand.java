package ch.swisssmp.city;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

public class TechtreeCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length<1) return false;
        switch(args[0]){
            case "reload":{
                if(args.length>1){
                    String techtreeId = args[1];
                    Techtree techtree = CitySystem.getTechtree(techtreeId).orElse(null);
                    if(techtree==null){
                        sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.RED+" Techtree "+techtreeId+" nicht gefunden.");
                        return true;
                    }

                    techtree.reload(()->{
                        sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.GREEN+" Techtree "+techtreeId+" aktualisiert.");
                    });
                    return true;
                }

                Techtrees.reloadAll(()->{
                    sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.GREEN+" Alle Techtrees aktualisiert.");
                });
                return true;
            }
            default: return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length<=1){
            List<String> options = Collections.singletonList("reload");
            String current = args.length>0 ? args[0] : "";
            return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
        }

        Collection<Techtree> techtrees = CitySystem.getTechtrees();
        String current = args.length>1 ? args[1] : "";
        return StringUtil.copyPartialMatches(current, techtrees.stream().map(Techtree::getId).collect(Collectors.toList()), new ArrayList<>());
    }
}
