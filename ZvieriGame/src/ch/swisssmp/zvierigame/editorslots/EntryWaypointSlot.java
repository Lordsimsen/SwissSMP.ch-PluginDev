package ch.swisssmp.zvierigame.editorslots;

import ch.swisssmp.utils.Position;
import ch.swisssmp.waypoints.MarkerType;
import ch.swisssmp.waypoints.editor.WaypointSlot;
import ch.swisssmp.zvierigame.ZvieriArenaEditor;
import org.bukkit.ChatColor;
import org.bukkit.World;

import java.util.Arrays;
import java.util.List;

public class EntryWaypointSlot extends WaypointSlot {

    private final ZvieriArenaEditor view;

    public EntryWaypointSlot(ZvieriArenaEditor view, int slot) {
        super(view, slot);
        this.view = view;
    }

    @Override
    protected void applyWaypoint(Position position) {
        view.getArena().setEntry(position);
    }

    @Override
    protected World getAttachedWorld() {
        return view.getArena().getWorld();
    }

    @Override
    protected MarkerType getMarkerType() {
        return MarkerType.RED;
    }

    @Override
    protected Position getWaypoint() {
        return view.getArena().getEntry();
    }

    @Override
    public String getName() {
        return ChatColor.AQUA + "Waypoint Eingang (ausserhalb Arenaregion)";
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList("Spieler die sich am Anfang des Spiels in der",
                "Arena befinden werden hierhin teleportiert");
    }
}
