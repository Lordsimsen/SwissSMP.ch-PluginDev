package ch.swisssmp.zvierigame.game;

import ch.swisssmp.zvierigame.ZvieriArena;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StopGameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length!=2) return false;
        if(!strings[0].equalsIgnoreCase("cancel")) return false;
        String arenaName = strings[1];
        ZvieriArena arena = ZvieriArena.get(arenaName, true);
        if(arena==null) return false;
        arena.cancelGame();
        return true;
    }
}
