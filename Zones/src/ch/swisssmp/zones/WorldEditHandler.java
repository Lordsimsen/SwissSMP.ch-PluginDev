package ch.swisssmp.zones;

import ch.swisssmp.utils.Mathf;
import com.sk89q.worldedit.EditSessionFactory;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.regions.selector.Polygonal2DRegionSelector;
import com.sk89q.worldedit.session.SessionManager;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.List;

public class WorldEditHandler {
    /**
     * Selects the Zone in WorldEdit
     */
    public static void select(Player player, Zone zone) {
        if (zone instanceof CuboidZone) select(player, (CuboidZone) zone);
        if (zone instanceof PolygonZone) select(player, (PolygonZone) zone);
    }

    private static void select(Player player, CuboidZone zone) {
        LocalSession session = getSession(player);
        CuboidRegionSelector selector = new CuboidRegionSelector();
        selector.selectPrimary(adapt(zone.getMin()), null);
        selector.selectSecondary(adapt(zone.getMax()), null);
        session.setRegionSelector(adapt(player.getWorld()), selector);
    }

    private static void select(Player player, PolygonZone zone) {
        LocalSession session = getSession(player);
        List<BlockVector2> corners = new ArrayList<>();
        for (BlockVector v : zone.getPoints()) {
            corners.add(BlockVector2.at(v.getBlockX(), v.getBlockZ()));
        }
        Polygonal2DRegionSelector selector = new Polygonal2DRegionSelector(adapt(zone.getWorld()), corners, zone.getMinY(), zone.getMaxY());
        session.setRegionSelector(adapt(player.getWorld()), selector);
    }

    /**
     * Applies the WorldEdit selection to the zone
     */
    public static boolean applySelection(Player player, Zone zone) {
        if (zone instanceof CuboidZone) return applySelection(player, (CuboidZone) zone);
        if (zone instanceof PolygonZone) return applySelection(player, (PolygonZone) zone);
        return false;
    }

    private static boolean applySelection(Player player, CuboidZone zone) {
        LocalSession session = getSession(player);
        CuboidRegion cuboidRegion;
        try {
            Region region = session.getSelection(adapt(player.getWorld()));
            if (!(region instanceof CuboidRegion)) return false;
            cuboidRegion = (CuboidRegion) region;
        } catch (IncompleteRegionException e) {
            return false;
        }

        BlockVector a = adapt(cuboidRegion.getPos1());
        BlockVector b = adapt(cuboidRegion.getPos2());
        zone.setPoints(a, b);
        return true;
    }

    private static boolean applySelection(Player player, PolygonZone zone) {
        LocalSession session = getSession(player);
        Polygonal2DRegion polygonRegion;
        try {
            Region region = session.getSelection(adapt(player.getWorld()));
            if (!(region instanceof Polygonal2DRegion)) return false;
            polygonRegion = (Polygonal2DRegion) region;
        } catch (IncompleteRegionException e) {
            return false;
        }

        List<BlockVector> corners = new ArrayList<>();
        int minY = polygonRegion.getMinimumY();
        int maxY = polygonRegion.getMaximumY();
        for (BlockVector2 c : polygonRegion.getPoints()) {
            corners.add(new BlockVector(c.getX(), minY + Mathf.floorToInt((maxY - minY) / 2f), c.getZ()));
        }
        zone.setPoints(corners, minY, maxY);
        return true;
    }

    private static LocalSession getSession(Player player) {
        SessionManager manager = WorldEdit.getInstance().getSessionManager();
        return manager.get(BukkitAdapter.adapt(player));
    }

    protected static com.sk89q.worldedit.world.World adapt(World world) {
        return BukkitAdapter.adapt(world);
    }

    protected static BlockVector3 adapt(BlockVector v) {
        return BlockVector3.at(v.getBlockX(), v.getBlockY(), v.getBlockZ());
    }

    protected static BlockVector adapt(BlockVector3 v) {
        return new BlockVector(v.getX(), v.getY(), v.getZ());
    }
}
