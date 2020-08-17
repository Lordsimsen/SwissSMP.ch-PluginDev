package ch.swisssmp.city.ceremony.promotion;

import ch.swisssmp.utils.Mathf;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Stack;

public class HayPile {
    public static boolean checkSize(Block onTop,  Material material, int size){

        int radius = Mathf.ceilToInt(Math.sqrt(Math.sqrt(size)) * 2.5);
        BoundingBox box = BoundingBox.of(onTop.getRelative(BlockFace.DOWN, Mathf.floorToInt(radius/2f)).getLocation().add(0.5,0.5,0.5), radius, radius, radius);

        HashSet<BlockVector> found = new HashSet<>();
        Stack<Block> queue = new Stack<>();
        BlockFace[] directions = new BlockFace[]{BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

        queue.add(onTop);

        int i = 0;
        while(!queue.isEmpty() && found.size() < size){
            // Bukkit.getLogger().info("Cycle: " + i);
            Block current = queue.pop();
            for(BlockFace face : directions){
                Block relative = current.getRelative(face);
                if(relative.getType() != material) {
                    continue;
                }
                BlockVector relativeVector = toVector(relative);
                if(!box.contains(relativeVector) || found.contains(relativeVector)) {
                    continue;
                }
                found.add(relativeVector);
                queue.push(relative);
            }
            i++;
        }
        return found.size() >= size;
    }

    private static BlockVector toVector(Block block){
        return new BlockVector(block.getX(), block.getY(), block.getZ());
    }
}
