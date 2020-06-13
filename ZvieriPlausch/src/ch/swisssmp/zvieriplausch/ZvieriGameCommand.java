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
//        try{
//            if(strings[2].equalsIgnoreCase("all")){
//                for(ZvieriArena arena : ZvieriArenen.getAll()){
//                    if(arena.getGame() != null) arena.getGame().cancel();
//                }
//            }
//        } catch (IndexOutOfBoundsException e){}
//        if(strings.length > 2) {
//            for (int i = 2; i < strings.length; i++) {
//                arenaName += (" " + strings[i]);
//            }
//        }
        ZvieriArena arena = ZvieriArena.get(arenaName, true);
        if(arena==null) return false;
        arena.getGame().cancel();
        return true;
    }
}
