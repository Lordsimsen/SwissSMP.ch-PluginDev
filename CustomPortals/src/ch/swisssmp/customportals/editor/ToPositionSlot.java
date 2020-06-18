package ch.swisssmp.customportals.editor;

import ch.swisssmp.customportals.CustomPortal;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.utils.Position;
import ch.swisssmp.waypoints.MarkerType;
import ch.swisssmp.waypoints.editor.WaypointSlot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import java.util.Arrays;
import java.util.List;

public class ToPositionSlot extends WaypointSlot {

    private final CustomPortal portal;

    public ToPositionSlot(CustomEditorView view, int slot, CustomPortal portal) {
        super(view, slot);
        this.portal = portal;
    }

    @Override
    protected World getAttachedWorld() {
        return portal.getTargetWorld()!=null ? Bukkit.getWorld(portal.getTargetWorld()) : null;
    }

    @Override
    protected MarkerType getMarkerType() {
        return portal.getToPosition()!=null ? MarkerType.BLUE : MarkerType.MISSING;
    }

    @Override
    protected Position getWaypoint() {
        return portal.getToPosition();
    }

    @Override
    protected void applyWaypoint(Position position) {
        this.portal.setToPosition(position);
        this.portal.getContainer().save();
        this.portal.updateTokens();
    }

    @Override
    protected void applyAttachedWorld(World world){
        this.portal.setTargetWorld(world.getName());
        this.portal.getContainer().save();
        this.portal.updateTokens();
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+"Ziel";
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList(ChatColor.GRAY+"Spieler werden hierhin",ChatColor.GRAY+"teleportiert.");
    }
}
