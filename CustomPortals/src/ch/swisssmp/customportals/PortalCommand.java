package ch.swisssmp.customportals;

import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PortalCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length==0){
            return false;
        }
        String prefix = CustomPortalsPlugin.getPrefix();
        if(!(sender instanceof Player)){
            sender.sendMessage(prefix+" Kann nur ingame verwendet werden.");
            return true;
        }
        Player player = (Player) sender;
        switch (args[0]){
            case "erstelle":
            case "create":{
                if(args.length<2) return false;
                String[] nameParts = new String[args.length-1];
                for(int i = 1; i < args.length; i++){
                    nameParts[i-1] = args[i];
                }
                String name = String.join(" ", nameParts);
                World world = player.getWorld();
                CustomPortalContainer container = CustomPortalContainer.get(world);
                CustomPortal portal = container.create(name);
                if(portal==null){
                    SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Das Portal konnte nicht erstellt werden.");
                    return true;
                }

                portal.getContainer().save();

                player.getInventory().addItem(portal.getItemStack());
                CustomPortalEditorView.open(player, portal);
                return true;
            }
            default: return false;
        }
    }
}
