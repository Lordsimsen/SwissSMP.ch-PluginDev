package ch.swisssmp.logintroll;

import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoginTrollCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if(arguments==null || arguments.length==0) return false;
        switch (arguments[0]){
            case "reload":{
                NicknameMap.load();
                if(sender instanceof Player){
                    SwissSMPler.get((Player) sender).sendActionBar("Namen aktualisiert!");
                }
                return true;
            }
            default: return false;
        }
    }
}
