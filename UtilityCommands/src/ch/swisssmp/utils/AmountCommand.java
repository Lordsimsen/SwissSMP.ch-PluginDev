package ch.swisssmp.utils;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class AmountCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player) || ((Player)sender).getGameMode()!= GameMode.CREATIVE) return true;
        int amount;
        try{
            amount = Integer.parseInt(args[0]);
        }
        catch(Exception e){
            sender.sendMessage("[SwissSMPUtils] Ungültige Menge angegeben.");
            return true;
        }
        PlayerInventory playerInventory = ((Player)sender).getInventory();
        ItemStack itemStack = playerInventory.getItemInMainHand()!=null ? playerInventory.getItemInMainHand() : playerInventory.getItemInOffHand();
        if(itemStack==null) return true;
        itemStack.setAmount(amount);
        return true;
    }
}
