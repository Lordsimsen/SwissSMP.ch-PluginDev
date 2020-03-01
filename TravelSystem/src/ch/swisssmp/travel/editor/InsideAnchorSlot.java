package ch.swisssmp.travel.editor;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import ch.swisssmp.travel.TravelStationEditor;
import ch.swisssmp.waypoints.MarkerType;
import ch.swisssmp.utils.Position;
import ch.swisssmp.waypoints.editor.WaypointSlot;

public class InsideAnchorSlot extends WaypointSlot {

	private final TravelStationEditor view;
	
	public InsideAnchorSlot(TravelStationEditor view, int slot) {
		super(view, slot);
		this.view = view;
	}

	@Override
	protected void applyWaypoint(Position position) {
		this.view.getStation().setInsideAnchor(position);
	}

	@Override
	protected MarkerType getMarkerType() {
		return MarkerType.BLUE;
	}

	@Override
	protected Position getWaypoint() {
		return this.view.getStation().getInsideAnchor();
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Link B";
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList("Referenzpunkt in", "der Reisewelt");
	}

	@Override
	protected World getAttachedWorld() {
		String worldName = this.view.getStation().getTravelWorldName();
		return Bukkit.getWorld(worldName);
	}
}
