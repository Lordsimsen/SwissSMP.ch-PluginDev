package ch.swisssmp.utils;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if(args.length>0){
            player = Bukkit.getPlayer(args[0]);
        } else if(sender instanceof Player){
            player = (Player) sender;
        } else{
            sender.sendMessage("/heal kann nur ingame verwendet werden.");
            return true;
        }
        if(player == null){
            sender.sendMessage(args[0] + " nicht gefunden.");
            return true;
        }
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.setSaturation(20f);
        return true;
    }
}
