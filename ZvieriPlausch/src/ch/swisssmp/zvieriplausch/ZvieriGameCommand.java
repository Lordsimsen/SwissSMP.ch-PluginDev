package ch.swisssmp.zvieriplausch;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ZvieriGameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 1){
            switch(strings[0]){
                case "reload":{
                    ZvieriPlauschPlugin.getInstance().reloadConfig();
                    commandSender.sendMessage(ZvieriPlauschPlugin.getPrefix() + ChatColor.GREEN + " Reloaded config");
                    return true;
                }
                case "resethighscores":{
                    for(ZvieriArena arena : ZvieriArenen.getAll()){
                        arena.getPlayerDataContainer().resetHighscores();
                    }
                    commandSender.sendMessage(ZvieriPlauschPlugin.getPrefix() + ChatColor.GREEN + " Highscores reset.");
                    return true;
                }
            }
        }
        if(!strings[0].equalsIgnoreCase("cancel")) {
            return false;
        }
        String arenaName;
        if(strings.length > 2){
            String[] nameParts = new String[strings.length-1];
            for(int i = 1; i < strings.length; i++){
                nameParts[i-1] = strings[i];
            }
            arenaName = String.join(" ", nameParts);
        }
        else{
            try {
                arenaName = strings[1];
            } catch (Exception e){
                return false;
            }
        }
        ZvieriArena arena = ZvieriArena.get(arenaName, true);
        if(arena==null) return false;
        arena.getGame().cancel();
        return true;
    }
}
