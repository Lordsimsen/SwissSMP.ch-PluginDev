package ch.swisssmp.zones;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class WorldGuardHandler {

    protected static ProtectedCuboidRegion createCuboidRegion(World world, String id, BlockVector min, BlockVector max){
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

    protected static ProtectedPolygonalRegion createPolygonRegion(World world, String id, Collection<BlockVector> points){
        if(points.size()<3) return null;
        List<BlockVector2> corners = new ArrayList<>();
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for(BlockVector p : points){
            corners.add(BlockVector2.at(p.getBlockX(),p.getBlockZ()));
            minY = Math.min(minY, p.getBlockY());
            maxY = Math.max(maxY, p.getBlockY());
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

    protected static Optional<ProtectedRegion> getRegion(World world, String id){
        RegionManager manager = getManager(world);
        ProtectedRegion region = manager.getRegion(id);
        return region!=null ? Optional.of(region) : Optional.empty();
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

    protected static BlockVector getMin(Collection<BlockVector> points){
        int x = Integer.MAX_VALUE;
        int y = Integer.MAX_VALUE;
        int z = Integer.MAX_VALUE;
        for(BlockVector v : points){
            x = Math.min(x,v.getBlockX());
            y = Math.min(y,v.getBlockX());
            z = Math.min(z,v.getBlockX());
        }
        return new BlockVector(x,y,z);
    }

    protected static BlockVector getMax(Collection<BlockVector> points){
        int x = Integer.MIN_VALUE;
        int y = Integer.MIN_VALUE;
        int z = Integer.MIN_VALUE;
        for(BlockVector v : points){
            x = Math.max(x,v.getBlockX());
            y = Math.max(y,v.getBlockX());
            z = Math.max(z,v.getBlockX());
        }
        return new BlockVector(x,y,z);
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
