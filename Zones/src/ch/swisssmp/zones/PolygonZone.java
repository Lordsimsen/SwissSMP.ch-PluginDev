package ch.swisssmp.zones;

import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.World;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PolygonZone extends Zone {

    private final List<BlockVector> points = new ArrayList<>();

    private BlockVector min;
    private BlockVector max;

    protected PolygonZone(ZoneCollection collection, UUID uid, ZoneType type){
        super(collection, uid, type);

    }

    @Override
    public BlockVector getMin() {
        return min;
    }

    @Override
    public BlockVector getMax() {
        return max;
    }

    @Override
    public boolean isSetupComplete() {
        return !(
                points.size()<3 ||
                getName()==null
        );
    }

    @Override
    public void createWorldGuardRegion(){
        String regionId = getUniqueId().toString();
        World world = getWorld();
        WorldGuardHandler.createPolygonRegion(world, regionId, points);
    }

    @Override
    protected JsonObject saveData() {
        JsonObject json = new JsonObject();
        JsonArray pointsArray = new JsonArray();
        for(BlockVector v : points){
            pointsArray.add(JsonUtil.toJsonObject(v));
        }
        json.add("points", pointsArray);
        return json;
    }

    @Override
    protected void loadData(JsonObject json) {
        min = JsonUtil.getBlockVector("min", json);
        max = JsonUtil.getBlockVector("max", json);
    }
}
