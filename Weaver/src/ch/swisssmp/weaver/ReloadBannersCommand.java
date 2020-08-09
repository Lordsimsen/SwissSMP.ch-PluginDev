package ch.swisssmp.weaver;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadBannersCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(args.length == 0) return false;
        switch(args[0]){
            case "reload":{
                CityBanner.reloadBanners((success) -> {
                    if(success){
                        commandSender.sendMessage(WeaverPlugin.getPrefix() + ChatColor.GREEN + " Banners reloaded!");
                    } else{
                        commandSender.sendMessage(WeaverPlugin.getPrefix() + ChatColor.RED + " Etwas ist schief gelaufen.");
                    }
                });
                return true;
            }
            default: return false;
        }
    }
}
