package ch.swisssmp.travel.editor;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;

import ch.swisssmp.travel.TravelStationEditor;
import ch.swisssmp.utils.MarkerType;
import ch.swisssmp.utils.Position;
import ch.swisssmp.utils.editor.WaypointSlot;

public class TravelWaypointSlot extends WaypointSlot {

	private final TravelStationEditor view;
	
	public TravelWaypointSlot(TravelStationEditor view, int slot) {
		super(view, slot);
		this.view = view;
	}

	@Override
	protected void applyWaypoint(Position position) {
		view.getStation().setWaypoint(position);
	}

	@Override
	protected MarkerType getMarkerType() {
		return MarkerType.RED;
	}

	@Override
	protected Position getWaypoint() {
		return view.getStation().getWaypoint();
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Wegpunkt";
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList(
				"Spieler spawnen hier am",
				"Ende einer Reise"
				);
	}

	@Override
	protected World getAttachedWorld() {
		return view.getStation().getWorld();
	}

}
