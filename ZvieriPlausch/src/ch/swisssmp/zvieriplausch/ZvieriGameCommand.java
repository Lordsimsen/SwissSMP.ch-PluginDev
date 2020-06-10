package ch.swisssmp.zvieriplausch;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ZvieriGameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length > 2) {
            return false;
        }
        if(strings.length == 1){
            if(!strings[0].equalsIgnoreCase("reload")) {
                return false;
            }
            ZvieriGamePlugin.getInstance().reloadConfig();
            commandSender.sendMessage(ZvieriGamePlugin.getPrefix() + ChatColor.GREEN + " Reloaded config");
            return true;
        }
        if(!strings[0].equalsIgnoreCase("cancel")) {
            return false;
        }
        String arenaName = strings[1];
        ZvieriArena arena = ZvieriArena.get(arenaName, true);
        if(arena==null) return false;
        arena.getGame().cancel();
        return true;
    }
}
