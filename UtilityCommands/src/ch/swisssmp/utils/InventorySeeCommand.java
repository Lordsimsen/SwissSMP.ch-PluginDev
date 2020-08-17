package ch.swisssmp.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InventorySeeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("/invsee kann nur ingame verwendet werden");
            return true;
        }
        Player player;
        if(args.length>0){
            player = Bukkit.getPlayer(args[0]);
        } else return false;

        if(player==null){
            sender.sendMessage(args[0]+" nicht gefunden.");
            return true;
        }
        Player stalker = (Player) sender;
        stalker.openInventory(player.getInventory());
        return true;
    }
}
