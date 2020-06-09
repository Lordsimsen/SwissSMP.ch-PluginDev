package ch.swisssmp.custompaintings;

import ch.swisssmp.customitems.CreateCustomItemBuilderEvent;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {
    @EventHandler
    private void onCreateCustomItemBuilder(CreateCustomItemBuilderEvent event){
        if(!event.getConfigurationSection().contains("painting")){
            return;
        }

        final String id = event.getConfigurationSection().getString("painting");

        event.getCustomItemBuilder().addComponent((itemStack)->{
            ItemUtil.setString(itemStack, CustomPainting.ID_PROPERTY, id);
        });
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event){
        if(event.getAction()!= Action.RIGHT_CLICK_BLOCK) return;
        BlockFace face = event.getBlockFace();
        if(face==BlockFace.UP || face==BlockFace.DOWN) return;
        ItemStack itemStack = event.getItem();
        if(itemStack==null) return;
        String paintingId = ItemUtil.getString(itemStack, CustomPainting.ID_PROPERTY);
        if(paintingId==null) return;
        if(ItemUtil.getInt(itemStack, CustomPainting.SLOT_X_PROPERTY, -1)>=0) return;
        PaintingData data = PaintingData.get(paintingId).orElse(null);
        if(data==null) return;
        Block block = event.getClickedBlock().getRelative(face);
        if(block.getType()!= Material.AIR){
            event.setCancelled(true);
            return;
        }

        BlockFace right;
        BlockFace up;
        if(face==BlockFace.UP || face==BlockFace.DOWN){
            BlockFace direction = event.getPlayer().getFacing();
            right = getRightFace(direction);
            up = direction;
            if(face==BlockFace.UP) right = right.getOppositeFace();
        }
        else{
            right = getRightFace(face);
            up = BlockFace.UP;
        }

        boolean success = PaintingPlacer.place(data, block, right, up, face);
        if(!success){
            SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.RED+"Nicht genügend Platz.");
        }
    }

    @EventHandler
    private void onChunkLoad(ChunkLoadEvent event){
        ChunkUtil.updateCustomPaintings(event.getChunk());
    }

    private static BlockFace getRightFace(BlockFace face){
        switch (face){
            case EAST: return BlockFace.NORTH;
            case NORTH: return BlockFace.WEST;
            case WEST: return BlockFace.SOUTH;
            case SOUTH: return BlockFace.EAST;
            default: return null;
        }
    }
}
