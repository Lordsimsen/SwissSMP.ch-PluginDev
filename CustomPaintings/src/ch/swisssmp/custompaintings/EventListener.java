package ch.swisssmp.custompaintings;

import ch.swisssmp.customitems.CreateCustomItemBuilderEvent;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.PlayerRenameItemEvent;
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
        if(!event.getJson().has("painting")){
            return;
        }

        final String id = event.getJson().get("painting").getAsString();

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
        CustomPainting painting = CustomPainting.get(paintingId).orElse(null);
        if(painting==null) return;
        event.setCancelled(true);
        boolean success = CustomPaintings.place(painting, event);
        if(!success){
            SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.RED+"Nicht genügend Platz.");
        }
    }

    @EventHandler
    private void onItemRename(PlayerRenameItemEvent event){
        if(!event.getPlayer().hasPermission("custompaintings.admin")) return;
        String paintingId = ItemUtil.getString(event.getItemStack(), CustomPainting.ID_PROPERTY);
        if(paintingId==null) return;
        CustomPainting painting = CustomPainting.get(paintingId).orElse(null);
        if(painting==null) return;
        painting.setName(event.getNewName());
        event.setName(ChatColor.AQUA+painting.getName());
        painting.save();
    }

    @EventHandler
    private void onChunkLoad(ChunkLoadEvent event){
        ChunkUtil.updateCustomPaintings(event.getChunk());
    }
}
