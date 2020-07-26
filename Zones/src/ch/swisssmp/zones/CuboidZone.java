package ch.swisssmp.zones;

import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

import java.util.UUID;

public class CuboidZone extends Zone {

    private BlockVector a;
    private BlockVector b;

    protected CuboidZone(ZoneCollection collection, UUID uid, String regionId, ZoneType type){
        super(collection, uid, regionId, type);

    }

    public void setPoints(BlockVector a, BlockVector b){
        this.a = a;
        this.b = b;
        updateWorldGuardRegion();
    }

    public void setPoints(Block a, Block b){
        this.a = new BlockVector(a.getX(), a.getY(), a.getZ());
        this.b = new BlockVector(b.getX(), b.getY(), b.getZ());
        updateWorldGuardRegion();
    }

    public BlockVector getPointA(){
        return a;
    }

    public BlockVector getPointB(){
        return b;
    }

    @Override
    public BlockVector getMin() {
        return b==null ? a : a==null ? b : new BlockVector(Math.min(a.getX(),b.getX()),Math.min(a.getY(),b.getY()),Math.min(a.getZ(),b.getZ()));
    }

    @Override
    public BlockVector getMax() {
        return a==null ? b : b==null ? a : new BlockVector(Math.max(a.getX(),b.getX()),Math.max(a.getY(),b.getY()),Math.max(a.getZ(),b.getZ()));
    }

    @Override
    public boolean isSetupComplete() {
        return !(
                a ==null ||
                b ==null ||
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
        WorldGuardHandler.updateCuboidRegion(world, regionId, getMin(), getMax());
    }

    @Override
    protected JsonObject saveData() {
        JsonObject json = new JsonObject();
        if(a !=null) JsonUtil.set("a", a, json);
        if(b !=null) JsonUtil.set("b", b, json);
        return json;
    }

    @Override
    protected void loadData(JsonObject json) {
        a = JsonUtil.getBlockVector("a", json);
        b = JsonUtil.getBlockVector("b", json);
    }
}
