package ch.swisssmp.zones;

import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

import java.util.UUID;

public class CuboidZone extends Zone {

    private BlockVector min;
    private BlockVector max;

    protected CuboidZone(ZoneCollection collection, UUID uid, String regionId, ZoneType type){
        super(collection, uid, regionId, type);

    }

    public void setPoints(BlockVector min, BlockVector max){
        this.min = min;
        this.max = max;
        updateWorldGuardRegion();
    }

    public void setPoints(Block min, Block max){
        this.min = new BlockVector(min.getX(), min.getY(), min.getZ());
        this.max = new BlockVector(max.getX(), max.getY(), max.getZ());
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
                min==null ||
                max==null ||
                getName()==null
        );
    }

    @Override
    public boolean tryLoadWorldGuardRegion(){
        return WorldGuardHandler.loadCuboidRegion(this);
    }

    @Override
    public void updateWorldGuardRegion(){
        String regionId = getRegionId();
        World world = getWorld();
        WorldGuardHandler.updateCuboidRegion(world, regionId, min, max);
    }

    @Override
    protected JsonObject saveData() {
        JsonObject json = new JsonObject();
        JsonUtil.set("min", min, json);
        JsonUtil.set("max", max, json);
        return json;
    }

    @Override
    protected void loadData(JsonObject json) {
        min = JsonUtil.getBlockVector("min", json);
        max = JsonUtil.getBlockVector("max", json);
    }
}
