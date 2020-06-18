package ch.swisssmp.zones.editor.selection;

import ch.swisssmp.zones.PolygonZone;
import ch.swisssmp.zones.util.Edge;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class DefaultPolygonSelector implements PointSelector {

    private final PolygonZone zone;
    private final List<Edge> edges = new ArrayList<>();

    public DefaultPolygonSelector(PolygonZone zone){
        this.zone = zone;
    }

    @Override
    public boolean click(Block block, ClickType click) {
        // TODO add code
        return false;
    }

    @Override
    public boolean apply() {
        // TODO add code
        return false;
    }

    @Override
    public List<Edge> getEdges() {
        return edges;
    }

    @Override
    public PointSelectionState getState() {
        // TODO add code
        return null;
    }
}
