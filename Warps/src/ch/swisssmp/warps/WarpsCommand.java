package ch.swisssmp.warps;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("/warp kann nur ingame verwendet werden");
            return true;
        }
        Player player = (Player) sender;
        WarpsView.open(player);
        return true;
    }
}
