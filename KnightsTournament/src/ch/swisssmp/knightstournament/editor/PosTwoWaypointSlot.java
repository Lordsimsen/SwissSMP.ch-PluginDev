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

public class PosTwoWaypointSlot extends WaypointSlot{
	
	KnightsArenaEditor view;

	public PosTwoWaypointSlot(KnightsArenaEditor view, int slot) {
		super(view, slot);
		this.view = view;
	}

	@Override
	protected World getAttachedWorld() {
		return this.view.getArena().getWorld();
	}

	@Override
	protected MarkerType getMarkerType() {
		return MarkerType.BLUE;
	}

	@Override
	protected Position getWaypoint() {
		Location location = view.getArena().getPosTwo();
		return location!=null ? new Position(location) : null;
	}

	@Override
	protected void applyWaypoint(Position position) {
		view.getArena().setPosTwo(position.getLocation(view.getArena().getWorld()));
		KnightsArena.save(this.view.getArena().getWorld());	
	}

	@Override
	public String getName() {
		return ChatColor.AQUA + "Waypoint Position Zwo";
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList("Startposition für Spieler 2");
	}

}
