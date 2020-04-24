package ch.swisssmp.zones.editor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

public class ZoneEditorUtil {

	protected static List<Edge> buildPolygonEdges(List<Location> locations){
		List<Edge> edges = new ArrayList<Edge>();
		for(int i = 0; i < locations.size()-1; i++){
			Edge edge = new Edge(locations.get(i), locations.get(i+1));
			edges.add(edge);
		}
		if(locations.size()>2){
			Edge edge = new Edge(locations.get(locations.size()-1), locations.get(0));
			edges.add(edge);
		}
		return edges;
	}
	
	protected static List<Edge> buildBoxEdges(Location a, Location b){
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
		edges.add(new Edge(LA,LB));
		edges.add(new Edge(LC,LD));
		edges.add(new Edge(UA,UB));
		edges.add(new Edge(UC,UD));
		//Z
		edges.add(new Edge(LA,LD));
		edges.add(new Edge(LB,LC));
		edges.add(new Edge(UA,UD));
		edges.add(new Edge(UB,UC));
		//Y
		edges.add(new Edge(LA,UA));
		edges.add(new Edge(LB,UB));
		edges.add(new Edge(LC,UC));
		edges.add(new Edge(LD,UD));
		
		return edges;
	}
}
