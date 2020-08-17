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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ZoneEditor implements Listener {

    private final Player player;
    private final Zone zone;
    private final PointSelector selector;
    private final VisualizationColorScheme colorScheme;

    private WireframeVisualizer visualizer;
    private InstructionDisplay instructionDisplay;
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
        instructionDisplay = InstructionDisplay.run(player, selector);
        Bukkit.getPluginManager().registerEvents(this, ZonesPlugin.getInstance());
        ZoneEditors.add(player,this);
        SwissSMPler.get(player).sendMessage("["+ChatColor.YELLOW+"Zonen"+ChatColor.RESET+"] "+ChatColor.YELLOW+"Auswahl-Modus gestartet! Beende die Auswahl, indem du den Kartentisch anklickst.");
    }

    public Player getPlayer(){
        return player;
    }

    public Zone getZone(){
        return zone;
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event){
        if(event.getPlayer()!=this.player) return;
        if(event.getAction()!= Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if(player.isSneaking() || block.getType() == Material.CARTOGRAPHY_TABLE) return;
        ItemStack itemStack = event.getItem();
        if(Zone.get(itemStack).orElse(null)!=zone) return;
        event.setCancelled(true);
        onPlayerInteractBlock(block, ClickType.RIGHT);
    }

    @EventHandler
    private void onPlayerDamageBlock(BlockDamageEvent event){
        Bukkit.getLogger().info("Block Damage!");
        if(event.getPlayer()!=this.player){
            Bukkit.getLogger().info("Wrong player");
            return;
        }
        Block block = event.getBlock();
        if(player.isSneaking() || block.getType() == Material.CARTOGRAPHY_TABLE){
            Bukkit.getLogger().info("Sneaking or cartography table");
            return;
        }
        ItemStack itemStack = event.getItemInHand();
        if(Zone.get(itemStack).orElse(null)!=zone){
            Bukkit.getLogger().info("Wrong zone");
            return;
        }
        event.setCancelled(true);
        onPlayerInteractBlock(block, ClickType.LEFT);
        Bukkit.getLogger().info("Mark Block!");
    }

    private void onPlayerInteractBlock(Block block, ClickType clickType){
        boolean changed = selector.click(block, clickType);
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
        if(visualizer!=null) visualizer.cancel();
        if(instructionDisplay!=null) instructionDisplay.cancel();
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
