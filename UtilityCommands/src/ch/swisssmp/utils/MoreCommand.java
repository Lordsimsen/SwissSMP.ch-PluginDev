package ch.swisssmp.utils;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class MoreCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player) || ((Player)sender).getGameMode()!= GameMode.CREATIVE) return true;
        PlayerInventory playerInventory = ((Player)sender).getInventory();
        ItemStack itemStack = playerInventory.getItemInMainHand()!=null ? playerInventory.getItemInMainHand() : playerInventory.getItemInOffHand();
        if(itemStack==null) return true;
        if(itemStack.getAmount()>=64) playerInventory.addItem(itemStack.clone());
        else itemStack.setAmount(64);
        return true;
    }
}
