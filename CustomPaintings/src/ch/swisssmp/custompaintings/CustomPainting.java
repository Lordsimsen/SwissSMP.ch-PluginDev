package ch.swisssmp.custompaintings;

import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class CustomPainting {

    public static final String ID_PROPERTY = "CustomPainting";
    public static final String SLOT_X_PROPERTY = "CustomPaintingX";
    public static final String SLOT_Y_PROPERTY = "CustomPaintingY";

    private final String id;
    private String name;
    private final int width;
    private final int height;

    private final int[][] reservedMapIds;

    protected CustomPainting(String id, String name, int width, int height, int[][] reservedMapIds){
        this.id = id;
        this.name = name;
        this.width = width;
        this.height = height;
        this.reservedMapIds = reservedMapIds;
    }

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
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

    public String getDisplayName(){
        return (name!=null ? name : "Gemälde ("+id+")");
    }

    public ItemStack getItemStack(){
        ItemStack itemStack = new ItemStack(Material.PAINTING);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RESET+getDisplayName());
        itemStack.setItemMeta(itemMeta);
        ItemUtil.setString(itemStack, CustomPainting.ID_PROPERTY, id);
        return itemStack;
    }

    public void render(){
        PaintingRenderer.render(this);
    }

    public void save(){
        CustomPaintingContainer.save(this);
    }

    public void remove(){
        CustomPaintingContainer.removePainting(id);
    }

    protected void unlink(){
        CustomPaintings.unlink(id);
        Collection<MapView> views = new ArrayList<>();
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int mapId = reservedMapIds[y][x];
                MapView view = Bukkit.getMap(mapId);
                if(view==null) continue;
                views.add(view);
            }
        }
        MapPool.unlinkMaps(views);
    }

    protected void save(File file){
        JsonObject result = new JsonObject();
        JsonUtil.set("id", id, result);
        if(name!=null) JsonUtil.set("name", name, result);
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

    public static Optional<CustomPainting> get(String paintingId){
        return CustomPaintingContainer.getPainting(paintingId);
    }

    protected static CustomPainting load(JsonObject json){
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

        String name = JsonUtil.getString("name", json);

        return new CustomPainting(paintingId, name, width, height, reservedMapIds);
    }
}
