package ch.swisssmp.observatory;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class MultiblockStructure {

    /**
     * Should find out whether given block is part of the multiblockstructure.
     */
    public static boolean isMultiblockStructure(Block block){
        World world = block.getWorld();
        int relativeYzero = block.getY()-2; //or only minus 1?
        Material powerMaterial = block.getRelative(BlockFace.DOWN).getType();
        if(!powerMaterial.equals(Material.EMERALD_BLOCK) && !powerMaterial.equals(Material.IRON_BLOCK)
                && !powerMaterial.equals(Material.GOLD_BLOCK) && !powerMaterial.equals(Material.DIAMOND_BLOCK)
                && !powerMaterial.equals(Material.NETHERITE_BLOCK)) return false;

        Orientation observatoryOrientation;
        

        return true;
    }

    private enum Orientation{
        NS,
        WE
    }
}
