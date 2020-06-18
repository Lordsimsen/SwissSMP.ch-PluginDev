package ch.swisssmp.zones.editor.selection;

import ch.swisssmp.zones.CuboidZone;
import ch.swisssmp.zones.util.Edge;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.List;

public class DefaultCuboidSelector implements PointSelector {

    private final World world;
    private final CuboidZone zone;
    private final List<Edge> edges = new ArrayList<>();

    private Block min;
    private Block max;

    public DefaultCuboidSelector(CuboidZone zone){
        World world = zone.getWorld();
        this.world = world;
        this.zone = zone;

        BlockVector min = zone.getMin();
        BlockVector max = zone.getMax();
        this.min = world.getBlockAt(min.getBlockX(),min.getBlockY(),min.getBlockZ());
        this.max = world.getBlockAt(max.getBlockX(),max.getBlockY(),max.getBlockZ());
    }

    @Override
    public boolean click(Block block, ClickType click) {
        if(block.getWorld()!=world) return false;

        if(click==ClickType.LEFT){
            setPoints(block, max);
        }
        else{
            setPoints(min, block);
        }

        recalculateEdges();
        return true;
    }

    private void setPoints(Block a, Block b){
        int minX = Math.min(a.getX(),b.getX());
        int minY = Math.min(a.getY(),b.getY());
        int minZ = Math.min(a.getZ(),b.getZ());

        int maxX = Math.max(a.getX(), b.getX());
        int maxY = Math.max(a.getX(), b.getX());
        int maxZ = Math.max(a.getX(), b.getX());

        this.min = world.getBlockAt(minX,minY,minZ);
        this.max = world.getBlockAt(maxX,maxY,maxZ);
    }

    @Override
    public boolean apply() {
        zone.setPoints(min,max);
        return true;
    }

    @Override
    public List<Edge> getEdges() {
        return edges;
    }

    @Override
    public PointSelectionState getState() {
        return min!=null && max!=null ? PointSelectionState.GOOD : PointSelectionState.NORMAL;
    }

    private void recalculateEdges(){
        this.edges.clear();
        this.edges.addAll(SelectionOutliner.buildBoxEdges(min,max));
    }
}
