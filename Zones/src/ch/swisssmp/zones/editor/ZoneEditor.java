package ch.swisssmp.zones.editor;

import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zones.Zone;
import ch.swisssmp.zones.ZonesPlugin;
import ch.swisssmp.zones.editor.selection.PointSelectionState;
import ch.swisssmp.zones.editor.selection.PointSelector;
import ch.swisssmp.zones.editor.visualization.VisualizationColorScheme;
import ch.swisssmp.zones.editor.visualization.WireframeVisualizer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;

public class ZoneEditor implements Listener {

    private final Player player;
    private final Zone zone;
    private final PointSelector selector;
    private final VisualizationColorScheme colorScheme;

    private WireframeVisualizer visualizer;
    private boolean finished = false;

    private ZoneEditor(Player player, Zone zone, PointSelector selector, VisualizationColorScheme colorScheme){
        this.player = player;
        this.zone = zone;
        this.selector = selector;
        this.colorScheme = colorScheme;
    }

    private void initialize(){
        if(selector==null){
            Bukkit.getLogger().info("ZoneEditor is missing its selector!");
            return;
        }
        selector.initialize();
        visualizer = WireframeVisualizer.start(player, getVisualizationColor());
        visualizer.setEdges(selector.getEdges());
        Bukkit.getPluginManager().registerEvents(this, ZonesPlugin.getInstance());
        ZoneEditors.add(player,this);
        SwissSMPler.get(player).sendMessage("["+ChatColor.YELLOW+"Zonen"+ChatColor.RESET+"] "+ChatColor.YELLOW+"Auswahl-Modus gestartet! Punkte mit Rechtsklick hinzufügen und mit Linksklick entfernen. Beende die Auswahl mit Schleichen + Rechtsklick.");
    }

    public Player getPlayer(){
        return player;
    }

    public Zone getZone(){
        return zone;
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event){
        if(event.getPlayer()!=player) return;
        if(event.getAction()!= Action.RIGHT_CLICK_BLOCK && event.getAction()!=Action.LEFT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        if(player.isSneaking()) return;
        ClickType click = event.getAction()==Action.LEFT_CLICK_BLOCK ? ClickType.LEFT : ClickType.RIGHT;
        event.setCancelled(true);
        boolean changed = selector.click(event.getClickedBlock(), click);
        if(!changed) return;
        visualizer.setEdges(selector.getEdges());
        visualizer.setColor(getVisualizationColor());
    }

    private Color getVisualizationColor(){
        switch(selector.getState()){
            case NORMAL:return colorScheme.getNormalColor();
            case GOOD:return colorScheme.getGoodColor();
            case BAD:return colorScheme.getBadColor();
            default: return Color.WHITE;
        }
    }

    public void complete(){
        if(finished) return;
        finished = true;
        boolean success = selector.apply();
        if(!success){
            SwissSMPler.get(player).sendMessage(ZonesPlugin.getPrefix()+ChatColor.RED+" Die Zone konnte nicht gespeichert werden.");
            finish();
            return;
        }
        zone.save();
        SwissSMPler.get(player).sendActionBar(ChatColor.GREEN+"Zone gespeichert!");
        finish();
    }

    public void cancel(){
        if(finished) return;
        finished = true;
        finish();
    }

    private void finish(){
        visualizer.cancel();
        HandlerList.unregisterAll(this);
        ZoneEditors.remove(player);
    }

    public static ZoneEditor start(Player player, Zone zone){
        Optional<ZoneEditor> existing = get(player);
        existing.ifPresent(ZoneEditor::cancel);
        PointSelector selector = zone.getType().createSelector(player, zone);
        VisualizationColorScheme colorScheme = zone.getType().getVisualizationColorScheme();
        ZoneEditor editor = new ZoneEditor(player, zone, selector, colorScheme);
        editor.initialize();
        return editor;
    }

    public static Optional<ZoneEditor> get(Player player){
        return ZoneEditors.get(player);
    }
}
