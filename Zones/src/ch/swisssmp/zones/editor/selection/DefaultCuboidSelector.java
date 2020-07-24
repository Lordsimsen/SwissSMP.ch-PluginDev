package ch.swisssmp.zones.editor.selection;

import ch.swisssmp.zones.CuboidZone;
import ch.swisssmp.zones.ZonesPlugin;
import ch.swisssmp.zones.util.Edge;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.List;

public class DefaultCuboidSelector implements PointSelector {

    private static final Material aMaterial = Material.REDSTONE_BLOCK;
    private static final Material bMaterial = Material.LAPIS_BLOCK;
    private static final String instructions = ChatColor.YELLOW+"LINKSKLICK: "+ChatColor.RED+"Punkt A"+ChatColor.YELLOW+" | RECHTSKLICK: "+ChatColor.BLUE+"Punkt B";

    private final Player player;
    private final World world;
    private final CuboidZone zone;
    private final List<Edge> edges = new ArrayList<>();

    private Block a;
    private Block b;

    public DefaultCuboidSelector(Player player, CuboidZone zone) {
        this.player = player;
        World world = zone.getWorld();
        this.world = world;
        this.zone = zone;

        BlockVector a = zone.getPointA();
        BlockVector b = zone.getPointB();
        this.a = a!=null ? world.getBlockAt(a.getBlockX(), a.getBlockY(), a.getBlockZ()) : null;
        this.b = b!=null ? world.getBlockAt(b.getBlockX(), b.getBlockY(), b.getBlockZ()) : null;
    }

    @Override
    public void initialize() {
        this.recalculateEdges();
        if(a!=null) markBlock(a, aMaterial);
        if(b!=null) markBlock(b, bMaterial);
    }

    @Override
    public boolean click(Block block, ClickType click) {
        if (block.getWorld() != world) return false;

        Material markerMaterial;
        if (click == ClickType.LEFT) {
            if(a==block){
                markBlock(block, aMaterial);
                return false;
            }
            if(a!=null) unmarkBlock(a);
            a = block;
            markerMaterial = aMaterial;
        } else {
            if(b==block) return false;
            if(b!=null) unmarkBlock(b);
            b = block;
            markerMaterial = bMaterial;
        }

        recalculateEdges();
        markBlock(block, markerMaterial);
        return true;
    }

    @Override
    public boolean apply() {
        zone.setPoints(a, b);
        zone.save();
        if(a!=null) unmarkBlock(a);
        if(b!=null) unmarkBlock(b);
        return true;
    }

    @Override
    public List<Edge> getEdges() {
        return edges;
    }

    @Override
    public PointSelectionState getState() {
        return a != null && b != null ? PointSelectionState.GOOD : PointSelectionState.NORMAL;
    }

    @Override
    public String getInstructions(){
        return instructions;
    }

    private void recalculateEdges() {
        this.edges.clear();
        this.edges.addAll(SelectionOutliner.buildBoxEdges(a, b));
    }

    protected void markBlock(Block block, Material material){
        Bukkit.getScheduler().runTaskLater(ZonesPlugin.getInstance(), ()->player.sendBlockChange(block.getLocation(), Bukkit.createBlockData(material)), 2L);
    }

    protected void unmarkBlock(Block block){
        player.sendBlockChange(block.getLocation(), block.getBlockData());
    }
}
