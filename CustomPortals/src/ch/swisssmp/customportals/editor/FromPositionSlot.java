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

public class FromPositionSlot extends WaypointSlot {

    private final CustomPortal portal;

    public FromPositionSlot(CustomEditorView view, int slot, CustomPortal portal) {
        super(view, slot);
        this.portal = portal;
    }

    @Override
    protected World getAttachedWorld() {
        return portal.getContainer().getWorld();
    }

    @Override
    protected MarkerType getMarkerType() {
        return portal.getFromPosition()!=null ? MarkerType.RED : MarkerType.MISSING;
    }

    @Override
    protected Position getWaypoint() {
        return portal.getFromPosition();
    }

    @Override
    protected void applyWaypoint(Position position) {
        this.portal.setFromPosition(position);
        this.portal.getContainer().save();
        this.portal.updateTokens();
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+"Referenzpunkt";
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList(ChatColor.GRAY+"Benötigt für",ChatColor.GRAY+"relative Teleportation");
    }
}
