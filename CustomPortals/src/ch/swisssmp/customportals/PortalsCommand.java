package ch.swisssmp.customportals;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PortalsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = CustomPortalsPlugin.getPrefix();
        if(!(sender instanceof Player)){
            sender.sendMessage(prefix+" Kann nur ingame verwendet werden.");
            return true;
        }
        CustomPortalsView.open((Player) sender);
        return true;
    }
}
