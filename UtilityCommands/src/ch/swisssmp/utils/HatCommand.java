package ch.swisssmp.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class HatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("/hat kann nur ingame verwendet werden");
            return true;
        }
        Player player = (Player) sender;
        PlayerInventory inventory = player.getInventory();
        int slot = inventory.getHeldItemSlot();
        ItemStack to = inventory.getItem(slot);
        ItemStack from = inventory.getHelmet();

        inventory.setHelmet(to);
        inventory.setItem(slot, from);

        return true;
    }
}
