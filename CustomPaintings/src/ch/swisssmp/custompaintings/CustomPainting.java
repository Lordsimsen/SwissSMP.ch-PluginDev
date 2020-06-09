package ch.swisssmp.custompaintings;

import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.UUID;

public class CustomPainting {
    public static final String ID_PROPERTY = "CustomPainting";
    public static final String SLOT_X_PROPERTY = "CustomPaintingX";
    public static final String SLOT_Y_PROPERTY = "CustomPaintingY";

    private final UUID uid;
    private final String paintingId;
    private final int width;
    private final int height;

    private final Block origin;
    private final BlockFace right;
    private final BlockFace up;

    protected CustomPainting(UUID uid, String paintingId, Block origin, BlockFace right, BlockFace up, int width, int height){
        this.uid = uid;
        this.paintingId = paintingId;
        this.width = width;
        this.height = height;

        this.origin = origin;
        this.right = right;
        this.up = up;
    }

    public UUID getUniqueId(){
        return uid;
    }

    public String getPainting(){
        return paintingId;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public Block getOrigin(){
        return origin;
    }

    public BlockFace getRight(){
        return right;
    }

    public BlockFace getUp(){
        return up;
    }

    protected JsonObject save(){
        JsonObject result = new JsonObject();
        JsonUtil.set("uid", uid, result);
        JsonUtil.set("painting", paintingId, result);
        JsonUtil.set("origin", origin, result);
        JsonUtil.set("right", right.toString(), result);
        JsonUtil.set("up", up.toString(), result);
        JsonUtil.set("width", width, result);
        JsonUtil.set("height", height, result);
        return result;
    }

    protected static CustomPainting load(World world, JsonObject json){
        UUID uid;
        try{
            String uidString = JsonUtil.getString("uid", json);
            uid = uidString!=null ? UUID.fromString(uidString) : null;
            if(uid==null) return null;
        } catch(Exception e){
            return null;
        }

        String paintingId = JsonUtil.getString("painting", json);
        Block origin = JsonUtil.getBlock("origin", world, json);
        BlockFace right = BlockFace.valueOf(JsonUtil.getString("right", json));
        BlockFace up = BlockFace.valueOf(JsonUtil.getString("up", json));
        int width = JsonUtil.getInt("width", json);
        int height = JsonUtil.getInt("height", json);

        return new CustomPainting(uid, paintingId, origin, right, up, width, height);
    }
}
