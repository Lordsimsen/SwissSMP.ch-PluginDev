package ch.swisssmp.stairchairs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.util.Vector;

import java.util.Optional;

public class ChairScanner {

    public static Optional<Location> getSittingLocation(Block block){
        Location location = block.getLocation();
        location.add(0.5, 0.30, 0.5);
        BlockData data = block.getBlockData();
        if(data instanceof Stairs){
            Stairs stairs = (Stairs) data;
            if(stairs.getHalf()== Bisected.Half.TOP) {
                StairChairs.debug("Stairs upside down");
                return Optional.empty();
            }
            double offset = 0.2;
            switch(stairs.getFacing()){
                case SOUTH:
                    location = location.add(0, 0, -offset);
                    location.setDirection(new Vector(0,0,-1));
                    break;
                case WEST:
                    location = location.add(offset, 0, 0);
                    location.setDirection(new Vector(1,0,0));
                    break;
                case NORTH:
                    location = location.add(0, 0, offset);
                    location.setDirection(new Vector(0,0,1));
                    break;
                case EAST:
                    location = location.add(-offset, 0, 0);
                    location.setDirection(new Vector(-1,0,0));
                    break;
                default:
                    StairChairs.debug("What is this direction: "+stairs.getFacing());
                    return Optional.empty();
            }
        }
        else if(data instanceof Slab){
            Vector direction = new Vector(0, 0, 0);
            Block north = block.getRelative(BlockFace.NORTH);
            Block east = block.getRelative(BlockFace.EAST);
            Block south = block.getRelative(BlockFace.SOUTH);
            Block west = block.getRelative(BlockFace.WEST);
            if(BlockChecker.isTable(north)){
                direction.add(new Vector(0,0,-1));
            }
            else if(north.getType()!= Material.AIR){
                direction.add(new Vector(0.001,0,1));
            }
            if(BlockChecker.isTable(east)){
                direction.add(new Vector(1,0,0));
            }
            else if(east.getType()!=Material.AIR){
                direction.add(new Vector(-1,0,0.001));
            }
            if(BlockChecker.isTable(south)){
                direction.add(new Vector(0,0,1));
            }
            else if(south.getType()!=Material.AIR){
                direction.add(new Vector(0.001,0,-1));
            }
            if(BlockChecker.isTable(west)){
                direction.add(new Vector(-1,0,0));
            }
            else if(west.getType()!=Material.AIR){
                direction.add(new Vector(1,0,0.001));
            }
            if(direction.lengthSquared()!=0){
                location.setDirection(direction);
            }
        }
        else {
            StairChairs.debug("Neither stair nor slab");
            return Optional.empty();
        }
        return Optional.of(location);
    }
}
