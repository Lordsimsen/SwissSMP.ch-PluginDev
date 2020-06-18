package ch.swisssmp.custompaintings;

import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PaintingsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = CustomPaintingsPlugin.getPrefix()+" ";
        if(!(sender instanceof Player)){
            sender.sendMessage(prefix+"Kann nur ingame verwendet werden!");
            return true;
        }
        Player player = (Player) sender;
        PaintingsView.open(player);
        return true;
    }
}
