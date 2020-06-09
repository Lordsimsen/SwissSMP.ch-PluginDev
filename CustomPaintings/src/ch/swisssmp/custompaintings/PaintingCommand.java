package ch.swisssmp.custompaintings;

import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PaintingCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length==0){
            return false;
        }
        String prefix = CustomPaintingsPlugin.getPrefix()+" ";
        switch(args[0].toLowerCase()){
            case "reload":{
                PaintingDataContainer.unloadAll();
                PaintingDataContainer.loadAll();
                sender.sendMessage(prefix+ ChatColor.GREEN+"Gemälde neu geladen!");
                return true;
            }
            case "get":{
                if(!(sender instanceof Player)){
                    sender.sendMessage(prefix+"Kann nur ingame verwendet werden!");
                    return true;
                }
                if(args.length<2) return false;
                Player player = (Player) sender;
                String paintingId = args[1];
                PaintingData painting = PaintingData.get(paintingId).orElse(null);
                if(painting==null){
                    SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Gemälde "+paintingId+" nicht gefunden.");
                    return true;
                }
                ItemStack itemStack = painting.getItemStack();
                player.getInventory().addItem(itemStack);
                return true;
            }
            case "create":{
                if(args.length<5) return false;
                String id = args[1];
                String url = args[2];
                int width;
                int height;
                try{
                    width = Integer.parseInt(args[3]);
                    height = Integer.parseInt(args[4]);
                }
                catch(Exception e){
                    return false;
                }

                PaintingData data = PaintingCreator.create(id, url, width, height);
                if(data!=null){
                    sender.sendMessage(prefix+ ChatColor.GREEN+"Gemälde erstellt!");
                }
                else{
                    sender.sendMessage(prefix+ ChatColor.GRAY+"Das Gemälde konnte nicht erstellt werden.");
                }
                return true;
            }
            case "replace":{
                if(args.length<3) return false;
                String id = args[1];
                String url = args[2];

                if(PaintingCreator.replace(id, url)){
                    sender.sendMessage(prefix+ ChatColor.GREEN+"Gemälde ersetzt!");
                }
                else{
                    sender.sendMessage(prefix+ ChatColor.GRAY+"Das Gemälde konnte nicht ersetzt werden.");
                }
                return true;
            }
            default: return false;
        }
    }
}
