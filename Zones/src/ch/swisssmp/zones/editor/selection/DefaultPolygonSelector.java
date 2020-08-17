package ch.swisssmp.zones.editor.selection;

import ch.swisssmp.zones.PolygonZone;
import ch.swisssmp.zones.util.Edge;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultPolygonSelector implements PointSelector {

    private final Player player;
    private final PolygonZone zone;
    private final List<Block> points = new ArrayList<>();
    private int minY;
    private int maxY;
    private final List<Edge> edges = new ArrayList<>();
    private PointSelectionState state;

    public DefaultPolygonSelector(Player player, PolygonZone zone) {
        this.player = player;
        this.zone = zone;
    }

    @Override
    public void initialize() {
        World world = zone.getWorld();
        points.addAll(zone.getPoints().stream().map(p -> world.getBlockAt(p.getBlockX(), p.getBlockY(), p.getBlockZ())).collect(Collectors.toList()));
        minY = zone.getMinY();
        maxY = zone.getMaxY();
        recalculateEdges();
    }

    @Override
    public boolean click(Block block, ClickType click) {
        if (click == ClickType.LEFT) {
            // remove closest point
            Block closest = getClosest(points);
            if(closest==null) return false;
            points.remove(closest);
        } else {
            // add point

        }

        recalculateEdges();
        return true;
    }

    @Override
    public boolean apply() {
        zone.setPoints(points.stream().map(b -> new BlockVector(b.getX(), b.getY(), b.getZ())).collect(Collectors.toList()), minY, maxY);
        zone.save();
        return true;
    }

    @Override
    public List<Edge> getEdges() {
        return edges;
    }

    @Override
    public PointSelectionState getState() {
        return state;
    }

    @Override
    public String getInstructions(){
        return ChatColor.RED+"LINKSKLICK: Punkt hinzufügen | RECHTSKLICK: Punkt entfernen";
    }

    private Block getClosest(Collection<Block> points){
        Location location = player.getLocation();
        double closestDistance = Double.MAX_VALUE;
        Block closest = null;
        for(Block block : points){
            double distance = block.getLocation().distanceSquared(location);
            if(distance>=closestDistance) continue;
            closest = block;
            closestDistance = distance;
        }

        return closest;
    }

    public void recalculateEdges() {
        this.edges.clear();
        this.edges.addAll(SelectionOutliner.buildPolygonEdges(points));
        state = points.size() < 3 ? PointSelectionState.GOOD : PointSelectionState.NORMAL;
    }
}
