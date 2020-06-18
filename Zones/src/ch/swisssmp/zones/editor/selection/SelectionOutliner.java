package ch.swisssmp.zones.editor.selection;

import ch.swisssmp.zones.util.Edge;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class SelectionOutliner {

    protected static List<Edge> buildPolygonEdges(List<Block> points) {
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < points.size() - 1; i++) {
            Edge edge = new Edge(points.get(i).getLocation(), points.get(i + 1).getLocation());
            edges.add(edge);
        }
        if (points.size() > 2) {
            Edge edge = new Edge(points.get(points.size() - 1).getLocation(), points.get(0).getLocation());
            edges.add(edge);
        }
        return edges;
    }

    protected static List<Edge> buildBoxEdges(Block a, Block b) {
        World world = a.getWorld();
        double minX = a.getX();
        double minY = a.getY();
        double minZ = a.getZ();
        double maxX = b.getX();
        double maxY = b.getY();
        double maxZ = b.getZ();
        Location LA = new Location(world, minX, minY, minZ);
        Location LB = new Location(world, maxX, minY, minZ);
        Location LC = new Location(world, maxX, minY, maxZ);
        Location LD = new Location(world, minX, minY, maxZ);
        Location UA = new Location(world, minX, maxY, minZ);
        Location UB = new Location(world, maxX, maxY, minZ);
        Location UC = new Location(world, maxX, maxY, maxZ);
        Location UD = new Location(world, minX, maxY, maxZ);
        List<Edge> edges = new ArrayList<Edge>();
        //X
        edges.add(new Edge(LA, LB));
        edges.add(new Edge(LC, LD));
        edges.add(new Edge(UA, UB));
        edges.add(new Edge(UC, UD));
        //Z
        edges.add(new Edge(LA, LD));
        edges.add(new Edge(LB, LC));
        edges.add(new Edge(UA, UD));
        edges.add(new Edge(UB, UC));
        //Y
        edges.add(new Edge(LA, UA));
        edges.add(new Edge(LB, UB));
        edges.add(new Edge(LC, UC));
        edges.add(new Edge(LD, UD));

        return edges;
    }
}
