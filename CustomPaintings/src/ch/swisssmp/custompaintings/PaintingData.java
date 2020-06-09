package ch.swisssmp.custompaintings;

import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Optional;

public class PaintingData {
    private final String id;
    private final int width;
    private final int height;

    private final int[][] reservedMapIds;

    protected PaintingData(String id, int width, int height, int[][] reservedMapIds){
        this.id = id;
        this.width = width;
        this.height = height;
        this.reservedMapIds = reservedMapIds;
    }

    public String getId(){
        return id;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public int[][] getReservedMapIds(){
        return reservedMapIds;
    }

    public ItemStack getItemStack(){
        ItemStack itemStack = new ItemStack(Material.PAINTING);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RESET+"Gemälde ("+id+")");
        itemStack.setItemMeta(itemMeta);
        ItemUtil.setString(itemStack, CustomPainting.ID_PROPERTY, id);
        return itemStack;
    }

    public void render(){
        PaintingRenderer.render(this);
    }

    public void save(){
        PaintingDataContainer.save(this);
    }

    protected void save(File file){
        JsonObject result = new JsonObject();
        JsonUtil.set("id", id, result);
        JsonUtil.set("width", width, result);
        JsonUtil.set("height", height, result);
        JsonArray mapIdsArray = new JsonArray();
        for (int[] row : reservedMapIds) {
            JsonArray rowArray = new JsonArray();
            for (int x : row) {
                rowArray.add(x);
            }
            mapIdsArray.add(rowArray);
        }

        result.add("map_ids", mapIdsArray);

        JsonUtil.save(file, result);
    }

    public static Optional<PaintingData> get(String paintingId){
        return PaintingDataContainer.getPainting(paintingId);
    }

    protected static PaintingData load(JsonObject json){
        String paintingId = JsonUtil.getString("id", json);
        if(paintingId==null) return null;
        int width = JsonUtil.getInt("width", json);
        int height = JsonUtil.getInt("height", json);
        JsonArray mapIdsArray = json.getAsJsonArray("map_ids");
        int[][] reservedMapIds = new int[height][width];
        for(int y = 0; y < mapIdsArray.size() && y < height; y++){
            JsonArray row = mapIdsArray.get(y).getAsJsonArray();
            for(int x = 0; x < row.size() && x < width; x++){
                reservedMapIds[y][x] = row.get(x).getAsInt();
            }
        }

        return new PaintingData(paintingId, width, height, reservedMapIds);
    }
}
