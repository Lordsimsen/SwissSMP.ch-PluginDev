package ch.swisssmp.travel.editor;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;

import ch.swisssmp.travel.TravelStationEditor;
import ch.swisssmp.waypoints.MarkerType;
import ch.swisssmp.utils.Position;
import ch.swisssmp.waypoints.editor.WaypointSlot;

public class OutsideAnchorSlot extends WaypointSlot {

	private final TravelStationEditor view;
	
	public OutsideAnchorSlot(TravelStationEditor view, int slot) {
		super(view, slot);
		this.view = view;
	}

	@Override
	protected void applyWaypoint(Position position) {
		this.view.getStation().setOutsideAnchor(position);
	}

	@Override
	protected MarkerType getMarkerType() {
		return MarkerType.RED;
	}

	@Override
	protected Position getWaypoint() {
		return this.view.getStation().getOutsideAnchor();
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Link A";
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList("Referenzpunkt in", "der Startwelt");
	}

	@Override
	protected World getAttachedWorld() {
		return view.getStation().getWorld();
	}

}
