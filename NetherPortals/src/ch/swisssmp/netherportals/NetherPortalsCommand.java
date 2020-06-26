package ch.swisssmp.netherportals;

import ch.swisssmp.netherportals.configuration.NetherPortalConfigurationView;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NetherPortalsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(NetherPortalsPlugin.getPrefix()+" Kann nur ingame verwendet werden.");
            return true;
        }

        Player player = (Player) sender;
        WorldConfiguration configuration = WorldConfiguration.get(player.getWorld());
        NetherPortalConfigurationView.open(player, configuration);
        return true;
    }
}
