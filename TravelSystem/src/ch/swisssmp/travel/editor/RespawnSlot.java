package ch.swisssmp.travel.editor;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;

import ch.swisssmp.travel.TravelStationEditor;
import ch.swisssmp.waypoints.MarkerType;
import ch.swisssmp.utils.Position;
import ch.swisssmp.waypoints.editor.WaypointSlot;

public class RespawnSlot extends WaypointSlot {
private final TravelStationEditor view;
	
	public RespawnSlot(TravelStationEditor view, int slot) {
		super(view, slot);
		this.view = view;
	}

	@Override
	protected void applyWaypoint(Position position) {
		this.view.getStation().setRespawn(position);
	}

	@Override
	protected MarkerType getMarkerType() {
		return MarkerType.BLUE;
	}

	@Override
	protected Position getWaypoint() {
		return this.view.getStation().getRespawn();
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Respawn";
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList("Respawn in", "der Reisewelt");
	}

	@Override
	protected World getAttachedWorld() {
		return view.getStation().getWorld();
	}
}
