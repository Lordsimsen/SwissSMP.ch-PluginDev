package ch.swisssmp.event.quarantine.editor;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.event.quarantine.QuarantineArena;
import ch.swisssmp.utils.Position;
import ch.swisssmp.waypoints.MarkerType;
import ch.swisssmp.waypoints.editor.WaypointSlot;

public class RespawnSlot extends WaypointSlot {
private final QuarantineArena arena;
	
	public RespawnSlot(CustomEditorView view, int slot, QuarantineArena arena) {
		super(view, slot);
		this.arena = arena;
	}

	@Override
	protected void applyWaypoint(Position position) {
		arena.setRespawn(position);
	}

	@Override
	protected MarkerType getMarkerType() {
		return MarkerType.BLUE;
	}

	@Override
	protected Position getWaypoint() {
		return arena.getRespawn();
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Respawn";
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList("Respawn für Infizierte");
	}

	@Override
	protected World getAttachedWorld() {
		return arena.getContainer().getWorld();
	}
}
