package ch.swisssmp.weaver;

import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("/banner kann nur ingame verwendet werden");
            return true;
        }
        Player player = (Player) sender;
        PlayerInventory inventory = player.getInventory();
        int slot = inventory.getHeldItemSlot();
        ItemStack banner = inventory.getItem(slot);

        if(!CityBanner.isBanner(banner, player)) {
            SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Ungültiges Banner");
            return true;
        }

        ItemStack helmet = inventory.getHelmet();
        inventory.setHelmet(banner);
        inventory.setItem(slot, helmet);

        return true;
    }
}
