package ch.swisssmp.city.commands;

import ch.swisssmp.city.CitySystem;
import ch.swisssmp.city.CitySystemPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CityPromotionsCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length<1) return false;
        switch(args[0]){
            case "reload":{
                CitySystem.reloadCityPromotions((success)->{
                    if(success){
                        sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.GREEN+" Stufenaufstiege aktualisiert.");
                    }
                    else{
                        sender.sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.RED+" Konnte Stufenaufstiege nicht aktualisieren.");
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
            List<String> options = Collections.singletonList("reload");
            String current = args.length>0 ? args[0] : null;
            return StringUtil.copyPartialMatches(current,options,new ArrayList<>());
        }

        return null;
    }
}
