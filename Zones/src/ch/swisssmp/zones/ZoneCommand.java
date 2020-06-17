package ch.swisssmp.zones;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ZoneCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String prefix = ZonesPlugin.getPrefix()+" ";
        if(!(sender instanceof Player)){
            sender.sendMessage(prefix+"Kann nur ingame verwendet werden!");
            return true;
        }

        if(args.length==0) return false;

        Player player = (Player) sender;

        switch(args[0]){
            case "create":{
                if(args.length<3) return false;
                String typeString = args[1];
                ZoneType type = typeString.equalsIgnoreCase("polygon")
                        ? Zones.getGenericPolygonZoneType()
                        : typeString.equalsIgnoreCase("cuboid")
                            ? Zones.getGenericCuboidZoneType()
                            : ZoneType.findByName(typeString).orElse(null);
                if(type==null){
                    sender.sendMessage(prefix+ ChatColor.RED+"Typ "+args[1]+" nicht gefunden.");
                    return true;
                }

                ZoneCollection collection = ZoneCollection.get(player.getWorld(), type).orElse(null);
                if(collection==null){
                    sender.sendMessage(prefix+ ChatColor.RED+"Typ "+args[1]+" ist noch nicht registriert.");
                    return true;
                }

                String[] nameParts = new String[args.length-2];
                System.arraycopy(args, 2, nameParts, 0, args.length - 2);
                String name = String.join(" ", nameParts);
                Zone zone = collection.createZone(name);
                if(zone==null){
                    sender.sendMessage(prefix+ChatColor.RED+"Beim erstellen der Zone "+name+" ist ein Fehler aufgetreten.");
                    return true;
                }
                sender.sendMessage(prefix+ChatColor.GREEN+"Zone "+name+" erstellt!");
                return true;
            }
            case "list":{
                if(args.length>1){
                    String typeString = args[1];
                    ZoneType type = ZoneType.findByName(typeString).orElse(null);
                    if(type!=null){
                        ZonesView.open(player, type);
                        return true;
                    }
                }

                ZoneTypesView.open(player);
                return true;
            }
            default: return false;
        }
    }
}
