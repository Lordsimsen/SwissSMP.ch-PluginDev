package ch.swisssmp.zvieriplausch.editorslots;

import ch.swisssmp.utils.Position;
import ch.swisssmp.waypoints.MarkerType;
import ch.swisssmp.waypoints.editor.WaypointSlot;
import ch.swisssmp.zvieriplausch.ZvieriArenaEditor;
import org.bukkit.ChatColor;
import org.bukkit.World;

import java.util.Arrays;
import java.util.List;

public class KitchenWaypointSlot extends WaypointSlot{
	
	private final ZvieriArenaEditor view;

	public KitchenWaypointSlot(ZvieriArenaEditor view, int slot) {
		super(view, slot);
		this.view = view;
	}

	@Override
	protected void applyWaypoint(Position position) {
		view.getArena().setKitchen(position);
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
		return view.getArena().getKitchen();
	}

	@Override
	public String getName() {
		return ChatColor.AQUA + "Waypoint K�che (Spawn)";
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList("Spieler spawnen hier am",
				"Anfang des Spiels");
	}

}
