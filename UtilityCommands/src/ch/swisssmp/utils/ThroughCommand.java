package ch.swisssmp.utils;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * Not used as WorldEdit provides the same command in a probably better way.
 * Kept in case WorldEdit fails.
 */
public class ThroughCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if(args.length>0){
            return false;
        }
        if(!(sender instanceof Player)){
            sender.sendMessage("/through kann nur ingame verwendet werden.");
            return true;
        }
        player = (Player) sender;
        World world = player.getWorld();
        Location playerLocation = player.getLocation();
        BlockFace playerFace = player.getFacing();
        Location location;
        switch(playerFace){
            case NORTH:{
                location = new Location(world,0,0,-1);
                break;
            }
            case EAST:{
                location = new Location(world,1,0,0);
                break;
            }
            case SOUTH:{
                location = new Location(world,0,0,1);
                break;
            }
            case WEST:{
                location = new Location(world,-1,0,0);
                break;
            }
            case UP:{
                location = new Location(world,0,1,0);
                break;
            }
            case DOWN:{
                location = new Location(world,0,-1,0);
                break;
            }
            default: location = new Location(world,0,0,-1);
        }
        Location thruLocation = playerLocation;
        int i = 0;
        while(i < 200){
            thruLocation = thruLocation.add(location);
            if(world.getBlockAt(thruLocation).getType() == Material.AIR && world.getBlockAt(location.add(0,1,0)).getType() == Material.AIR){
                player.sendMessage(ChatColor.GREEN + "Whoosh!");
                player.teleport(thruLocation);
                return true;
            }
        }
        player.sendMessage(ChatColor.RED + "Kein Platz in der näheren Umgebung..");
        return true;
    }
}
