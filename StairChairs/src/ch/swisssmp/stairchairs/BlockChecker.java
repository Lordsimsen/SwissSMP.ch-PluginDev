package ch.swisssmp.stairchairs;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.TrapDoor;

public class BlockChecker {

    private static final String[] WoodTypes = {"oak","birch","spruce","jungle","dark_oak","acacia","warped","crimson"};

    protected static boolean isWood(Block block) {
        String typeString = block.getType().toString().toLowerCase();
        for(String woodType : WoodTypes) {
            if(typeString.contains(woodType)) return true;
        }
        return false;
    }

    protected static boolean isChair(Block block){
        BlockData blockData = block.getBlockData();
        if(blockData instanceof Stairs){
            return ((Stairs)blockData).getHalf()!= Bisected.Half.TOP;
        }
        else if(blockData instanceof Slab){
            return ((Slab)blockData).getType()!= Slab.Type.TOP;
        }
        return false;
    }

    protected static boolean isObstructed(Block block){
        Material material = block.getType();
        return  !(material == Material.AIR ||
                material == Material.ACACIA_WALL_SIGN ||
                material == Material.BIRCH_WALL_SIGN ||
                material == Material.DARK_OAK_WALL_SIGN ||
                material == Material.JUNGLE_WALL_SIGN ||
                material == Material.OAK_WALL_SIGN ||
                material == Material.SPRUCE_WALL_SIGN ||
                material == Material.ITEM_FRAME ||
                material == Material.PAINTING ||
                material == Material.TORCH ||
                material == Material.REDSTONE_TORCH ||
                material == Material.REDSTONE_WALL_TORCH);
    }

    protected static boolean isTable(Block block){
        BlockData blockData = block.getBlockData();
        if(blockData instanceof Stairs){
            return ((Stairs)blockData).getHalf()!= Bisected.Half.TOP;
        }
        else if(blockData instanceof Slab){
            return ((Slab)blockData).getType()!= Slab.Type.TOP;
        }
        else if(blockData instanceof TrapDoor){
            return ((TrapDoor)blockData).getHalf()!= Bisected.Half.TOP && !((TrapDoor)blockData).isOpen();
        }
        else if(block.getType().toString().contains("FENCE")) return true;
        else if(block.getType()==Material.COBBLESTONE_WALL) return true;
        else return false;
    }
}
