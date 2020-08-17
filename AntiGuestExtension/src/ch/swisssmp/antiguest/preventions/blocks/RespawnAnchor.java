package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class RespawnAnchor extends BlockInteractPrevention {

    @Override
    protected boolean isMatch(Block block) {
        return block.getType()== Material.RESPAWN_ANCHOR;
    }

    @Override
    protected String getSubPermission() {
        return "smoker";
    }
}
