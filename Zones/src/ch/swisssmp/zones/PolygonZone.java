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
    private int minY;
    private int maxY;

    private BlockVector min;
    private BlockVector max;

    protected PolygonZone(ZoneCollection collection, UUID uid, String regionId, ZoneType type){
        super(collection, uid, regionId, type);

    }

    public List<BlockVector> getPoints(){
        return new ArrayList<>(points);
    }

    public int getMaxY(){
        return maxY;
    }

    public int getMinY(){
        return minY;
    }

    public void setPoints(List<BlockVector> points, int minY, int maxY){
        this.points.clear();
        this.points.addAll(points);
        this.minY = minY;
        this.maxY = maxY;
        this.recalculateBounds();
        updateWorldGuardRegion();
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

    private void recalculateBounds(){
        min = WorldGuardHandler.getMin(points, minY);
        max = WorldGuardHandler.getMax(points, maxY);
    }

    @Override
    public boolean tryLoadWorldGuardRegion(){
        String regionId = getRegionId();
        World world = getWorld();
        return WorldGuardHandler.loadPolygonRegion(this);
    }

    @Override
    public void updateWorldGuardRegion(){
        String regionId = getRegionId();
        World world = getWorld();
        List<BlockVector> points = new ArrayList<>(this.points);
        if(points.size()==0){
            points.add(new BlockVector(0,64,0));
        }
        if(points.size()<3){
            for(int i = points.size(); i < 3; i++){
                points.add(points.get(0));
            }
        }
        WorldGuardHandler.updatePolygonRegion(world, regionId, points, minY, maxY);
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
