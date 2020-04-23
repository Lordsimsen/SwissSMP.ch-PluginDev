package ch.swisssmp.knightstournament.editor;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import ch.swisssmp.knightstournament.KnightsArena;
import ch.swisssmp.knightstournament.KnightsArenaEditor;
import ch.swisssmp.utils.Position;
import ch.swisssmp.waypoints.MarkerType;
import ch.swisssmp.waypoints.editor.WaypointSlot;

public class PosOneWaypointSlot extends WaypointSlot{
	
	KnightsArenaEditor view;

	public PosOneWaypointSlot(KnightsArenaEditor view, int slot) {
		super(view, slot);
		this.view = view;
	}

	@Override
	protected World getAttachedWorld() {
		return this.view.getArena().getWorld();
	}

	@Override
	protected MarkerType getMarkerType() {
		return MarkerType.RED;
	}

	@Override
	protected Position getWaypoint() {
		Location location = view.getArena().getPosOne();
		return location!=null ? new Position(location) : null;
	}

	@Override
	protected void applyWaypoint(Position position) {
		view.getArena().setPosOne(position.getLocation(view.getArena().getWorld()));
		KnightsArena.save(this.view.getArena().getWorld());		
	}

	@Override
	public String getName() {
		return ChatColor.AQUA + "Waypoint Position Eins";
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList("Startposition für Spieler 1");
	}

}
