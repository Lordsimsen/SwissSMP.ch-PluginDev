package ch.swisssmp.weaver;

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

        //TODO check whether "banner" is actually the registered city banner. Mind people with multiple citizenships

        ItemStack from = inventory.getHelmet();

        inventory.setHelmet(banner);
        inventory.setItem(slot, from);

        return true;
    }
}
