package ch.swisssmp.afkcontrol;

import ch.swisssmp.utils.TargetSelector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class AfkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player) && args.length<1){
            return true;
        }
        if(args.length==0){
            Player player = (Player) sender;
            AfkKicker.inst().toggleAfk(player);
            return true;
        }
        Collection<Player> players = TargetSelector.queryPlayers(sender, args[0]);
        boolean toggle = players.size()<2;
        for(Player player : players){
            if(toggle){
                AfkKicker.inst().toggleAfk(player);
            }
            else{
                AfkKicker.inst().setAfk(player, true);
            }
        }
        return true;
    }
}
