package ch.swisssmp.custompaintings;

import ch.swisssmp.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

public class ChunkUtil {
    protected static void updateAll(){
        for(World world : Bukkit.getWorlds()){
            updateAll(world);
        }
    }

    private static void updateAll(World world){
        for(Chunk chunk : world.getLoadedChunks()){
            updateCustomPaintings(chunk);
        }
    }

    public static void updateCustomPaintings(Chunk chunk){
        for(Entity entity : chunk.getEntities()){
            updateCustomPaintings(entity);
        }
    }

    private static void updateCustomPaintings(Entity entity){
        if(entity.getType()!= EntityType.ITEM_FRAME) return;
        ItemFrame itemFrame = (ItemFrame) entity;
        if(itemFrame.getItem()==null) return;
        ItemStack itemStack = itemFrame.getItem();
        if(itemStack.getType()!= Material.FILLED_MAP) return;
        String paintingId = ItemUtil.getString(itemStack, CustomPainting.ID_PROPERTY);
        if(paintingId==null) return;
        int slotX = ItemUtil.getInt(itemStack, CustomPainting.SLOT_X_PROPERTY, -1);
        int slotY = ItemUtil.getInt(itemStack, CustomPainting.SLOT_Y_PROPERTY, -1);
        if(slotX<0 || slotY<0) return;
        PaintingData data = PaintingData.get(paintingId).orElse(null);
        if(data==null) return;
        int[][] mapIds = data.getReservedMapIds();
        if(slotY>=mapIds.length || slotX >= mapIds[slotY].length) return;
        int mapId = mapIds[slotY][slotX];
        MapView view = Bukkit.getMap(mapId);
        if(view==null) return;
        MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
        mapMeta.setMapView(view);
        itemStack.setItemMeta(mapMeta);
        itemFrame.setItem(itemStack);
    }
}
