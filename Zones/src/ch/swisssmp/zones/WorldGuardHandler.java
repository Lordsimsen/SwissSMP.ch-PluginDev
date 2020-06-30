package ch.swisssmp.zones;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class WorldGuardHandler {

    private static ProtectedCuboidRegion createCuboidRegion(World world, String id, BlockVector min, BlockVector max){
        if(min==null) min = new BlockVector();
        if(max==null) max = new BlockVector();
        RegionManager manager = getManager(world);
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(id, BlockVector3.at(min.getBlockX(),min.getBlockY(),min.getBlockZ()), BlockVector3.at(max.getBlockX(),max.getBlockY(),max.getBlockZ()));
        manager.addRegion(region);
        try {
            manager.save();
        } catch (StorageException e) {
            e.printStackTrace();
        }
        return region;
    }

    protected static ProtectedCuboidRegion updateCuboidRegion(World world, String id, BlockVector min, BlockVector max){
        RegionManager manager = getManager(world);
        ProtectedRegion existing = manager.getRegion(id);
        if(!(existing instanceof ProtectedCuboidRegion)){
            if(existing!=null) manager.removeRegion(id, RemovalStrategy.UNSET_PARENT_IN_CHILDREN);
            ProtectedCuboidRegion region = createCuboidRegion(world, id, min, max);
            if(existing!=null) region.copyFrom(existing);
            return region;
        }
        ProtectedCuboidRegion region = (ProtectedCuboidRegion) existing;
        region.setMinimumPoint(adapt(min));
        region.setMaximumPoint(adapt(max));
        try {
            manager.save();
        } catch (StorageException e) {
            e.printStackTrace();
        }
        return region;
    }

    protected static boolean loadCuboidRegion(CuboidZone zone){
        World world = zone.getWorld();
        String regionId = zone.getRegionId();
        RegionManager manager = getManager(world);
        ProtectedRegion region = manager.getRegion(regionId);
        if(!(region instanceof ProtectedCuboidRegion)) return false;
        ProtectedCuboidRegion cuboidRegion = (ProtectedCuboidRegion) region;
        BlockVector min = adapt(cuboidRegion.getMinimumPoint());
        BlockVector max = adapt(cuboidRegion.getMaximumPoint());
        zone.setPoints(min, max);
        return true;
    }

    private static ProtectedPolygonalRegion createPolygonRegion(World world, String id, Collection<BlockVector> points, int minY, int maxY){
        if(points.size()<3) return null;
        List<BlockVector2> corners = new ArrayList<>();
        for(BlockVector p : points){
            corners.add(BlockVector2.at(p.getBlockX(),p.getBlockZ()));
        }
        RegionManager manager = getManager(world);
        ProtectedPolygonalRegion region = new ProtectedPolygonalRegion(id, corners, minY, maxY);
        manager.addRegion(region);
        try {
            manager.save();
        } catch (StorageException e) {
            e.printStackTrace();
        }
        return region;
    }

    protected static ProtectedPolygonalRegion updatePolygonRegion(World world, String id, Collection<BlockVector> points, int minY, int maxY){
        if(points.size()<3) return null;
        List<BlockVector2> corners = new ArrayList<>();
        for(BlockVector p : points){
            corners.add(BlockVector2.at(p.getBlockX(),p.getBlockZ()));
        }
        RegionManager manager = getManager(world);
        ProtectedRegion existing = manager.getRegion(id);
        ProtectedPolygonalRegion result;
        if(!(existing instanceof ProtectedPolygonalRegion)){
            if(existing!=null) manager.removeRegion(id, RemovalStrategy.UNSET_PARENT_IN_CHILDREN);
            result = createPolygonRegion(world, id, points, minY, maxY);
            if(existing!=null) result.copyFrom(existing);
        }
        else{
            ProtectedPolygonalRegion region = (ProtectedPolygonalRegion) existing;
            manager.removeRegion(region.getId());
            result = new ProtectedPolygonalRegion(id, corners, minY, maxY);
            result.copyFrom(region);
            manager.addRegion(result);
        }
        try {
            manager.save();
        } catch (StorageException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected static boolean loadPolygonRegion(PolygonZone zone){
        World world = zone.getWorld();
        String regionId = zone.getRegionId();
        RegionManager manager = getManager(world);
        ProtectedRegion region = manager.getRegion(regionId);
        if(!(region instanceof ProtectedPolygonalRegion)) return false;
        ProtectedPolygonalRegion polygonRegion = (ProtectedPolygonalRegion) region;
        List<BlockVector> points = new ArrayList<>();
        int minY = polygonRegion.getMinimumPoint().getY();
        int maxY = polygonRegion.getMaximumPoint().getY();
        for(BlockVector2 v : polygonRegion.getPoints()){
            points.add(new BlockVector(v.getX(),minY,v.getZ()));
        }

        zone.setPoints(points, minY, maxY);
        return true;
    }

    protected static void removeRegion(World world, String id){
        RegionManager manager = getManager(world);
        manager.removeRegion(id);
        try {
            manager.save();
        } catch (StorageException e) {
            e.printStackTrace();
        }
    }

    protected static Optional<Zone> importRegion(World world, RegionType regionType, String regionId, String name){
        ZoneType type;
        switch (regionType){
            case CUBOID:{
                type = Zones.getGenericCuboidZoneType();
                break;
            }
            case POLYGON:{
                type = Zones.getGenericPolygonZoneType();
                break;
            }
            default: {
                Bukkit.getLogger().warning(ZonesPlugin.getPrefix()+" Kann eine Region vom Typ "+regionType+" nicht importieren!");
                return Optional.empty();
            }
        }

        ZoneCollection collection = ZoneCollection.get(world, type).orElse(null);
        if(collection==null){
            Bukkit.getLogger().warning(ZonesPlugin.getPrefix()+" ZoneType "+type.getKey()+" ist nicht initiiert.");
            return Optional.empty();
        }

        return Optional.of(collection.createZone(regionId, name));
    }

    protected static RegionType getRegionType(World world, String regionId){
        RegionManager manager = getManager(world);
        ProtectedRegion region = manager.getRegion(regionId);
        if(region==null) return null;
        switch(region.getType()){
            case CUBOID:return RegionType.CUBOID;
            case POLYGON:return RegionType.POLYGON;
            case GLOBAL:return RegionType.GLOBAL;
            default: return null;
        }
    }

    protected static BlockVector getMin(Collection<BlockVector> points, int minY){
        int x = Integer.MAX_VALUE;
        int z = Integer.MAX_VALUE;
        for(BlockVector v : points){
            x = Math.min(x,v.getBlockX());
            z = Math.min(z,v.getBlockX());
        }
        return new BlockVector(x,minY,z);
    }

    protected static BlockVector getMax(Collection<BlockVector> points, int maxY){
        int x = Integer.MIN_VALUE;
        int z = Integer.MIN_VALUE;
        for(BlockVector v : points){
            x = Math.max(x,v.getBlockX());
            z = Math.max(z,v.getBlockX());
        }
        return new BlockVector(x,maxY,z);
    }

    protected static BlockVector3 adapt(BlockVector v){
        return BlockVector3.at(v.getBlockX(),v.getBlockY(),v.getBlockZ());
    }

    protected static BlockVector adapt(BlockVector3 v){
        return new BlockVector(v.getX(),v.getY(),v.getZ());
    }

    private static RegionManager getManager(World world){
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
    }
}
