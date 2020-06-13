package ch.swisssmp.custompaintings;

import ch.swisssmp.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import static ch.swisssmp.custompaintings.CustomPainting.*;

public class PaintingPlacer {
    public static boolean place(CustomPainting data, Block block, BlockFace right, BlockFace up, BlockFace itemFrameFacing) {
        int width = data.getWidth();
        int height = data.getHeight();
        if (!checkVolume(block, right, up, width, height)) return false;
        int[][] mapIds = data.getReservedMapIds();
        World world = block.getWorld();
        for (int y = 0; y < height; y++) {
            Block rowStart = block.getRelative(up, y);
            for (int x = 0; x < width; x++) {
                int remappedY = mapIds.length - y - 1;
                int mapId = mapIds[remappedY][x]; // inverse y axis
                MapView view = Bukkit.getMap(mapId);
                if (view == null) {
                    Bukkit.getLogger().warning(CustomPaintingsPlugin.getPrefix() + " Map " + mapId + " nicht gefunden!");
                    continue;
                }
                Block current = rowStart.getRelative(right, x);
                ItemFrame itemFrame = (ItemFrame) world.spawnEntity(current.getLocation(), EntityType.ITEM_FRAME);
                itemFrame.setFacingDirection(itemFrameFacing, true);
                ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
                MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
                mapMeta.setMapView(Bukkit.getMap(mapId));
                itemStack.setItemMeta(mapMeta);
                ItemUtil.setString(itemStack, ID_PROPERTY, data.getId());
                ItemUtil.setInt(itemStack, SLOT_X_PROPERTY, x);
                ItemUtil.setInt(itemStack, SLOT_Y_PROPERTY, remappedY);
                itemFrame.setItem(itemStack);
                itemFrame.setRotation(getRotation(up, itemFrameFacing));
            }
        }

        return true;
    }

    private static boolean checkVolume(Block block, BlockFace right, BlockFace up, int width, int height) {
        for (int y = 0; y < height; y++) {
            Block rowStart = block.getRelative(up, y);
            for (int x = 0; x < width; x++) {
                Block current = rowStart.getRelative(right, x);
                if (current.getType() != Material.AIR) return false;
            }
        }

        return true;
    }

    private static Rotation getRotation(BlockFace up, BlockFace facing) {
        if (facing == BlockFace.DOWN) up = up.getOppositeFace();
        switch (up) {
            case EAST:
                return Rotation.CLOCKWISE;
            case SOUTH:
                return Rotation.FLIPPED;
            case WEST:
                return Rotation.COUNTER_CLOCKWISE;
            default:
                return Rotation.NONE;
        }
    }

    private static BlockFace getLeftDirection(BlockFace right) {
        switch (right) {
            case NORTH:
                return BlockFace.EAST;
            case EAST:
                return BlockFace.SOUTH;
            case SOUTH:
                return BlockFace.WEST;
            case WEST:
                return BlockFace.NORTH;
            default:
                return null;
        }
    }
}
