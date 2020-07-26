package ch.swisssmp.zones.editor.visualization;

import ch.swisssmp.utils.Mathf;
import ch.swisssmp.zones.ZonesPlugin;
import ch.swisssmp.zones.util.Edge;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class WireframeVisualizer extends BukkitRunnable {

    private static final double maxDistanceSquared = 2500;
    private static final double animationSpeed = 0.5; //Blocks Per Frame (20 Frames / Second)
    private static final double averageMarkerDistance = 15;

    private final World world;
    private final Player player;
    private Color color;
    private final List<Edge> edges = new ArrayList<>();

    private float segmentLength;
    private int segmentCount;

    private long t = 0;

    private WireframeVisualizer(Player player, Color color){
        this.world = player.getWorld();
        this.player = player;
        this.color = color;
    }

    @Override
    public void run() {
        markRegion(edges);
        t++;
    }

    public void setEdges(List<Edge> edges){
        float length = 0;
        for(Edge edge : edges){
            length+=edge.getLength();
        }
        this.edges.clear();
        this.edges.addAll(edges);
        this.segmentCount = Mathf.ceilToInt(length/averageMarkerDistance);
        this.segmentLength = length / segmentCount;
    }

    public void setColor(Color color){
        this.color = color;
    }

    public Color getColor(){
        return color;
    }

    public void remove(){
        cancel();
    }

    private void markRegion(List<Edge> edges){
        Particle.DustOptions lineDust = new Particle.DustOptions(color, 2.5f);
        Location playerLocation = player.getLocation();
        for(int i = 0; i < segmentCount; i++){
            double offset = i*segmentLength + t * animationSpeed;
            Edge edge = edges.get(0);
            int edgeIndex = 0;
            while(offset>edge.getLength()){
                offset-=edge.getLength();
                edgeIndex++;
                if(edgeIndex>=edges.size())
                    edgeIndex = 0;
                edge = edges.get(edgeIndex);
            }
            Location location = edge.step(offset);
            if(location.distanceSquared(playerLocation)>maxDistanceSquared) continue;
            world.spawnParticle(Particle.REDSTONE, location, 1, lineDust);
        }
    }

    public static WireframeVisualizer start(Player player, Color color){
        Bukkit.getLogger().info("Starting visualizer with color "+(color.getRed()+","+color.getGreen()+","+color.getBlue()));
        WireframeVisualizer result = new WireframeVisualizer(player, color);
        result.runTaskTimer(ZonesPlugin.getInstance(), 0, 1);
        return result;
    }
}
