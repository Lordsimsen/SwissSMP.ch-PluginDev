package ch.swisssmp.zvierigame.editorslots;

import ch.swisssmp.utils.Position;
import ch.swisssmp.waypoints.MarkerType;
import ch.swisssmp.waypoints.editor.WaypointSlot;
import ch.swisssmp.zvierigame.ZvieriArenaEditor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;

import java.util.Arrays;
import java.util.List;

public class QueueWaypointSlot extends WaypointSlot{

	private final ZvieriArenaEditor view;

	public QueueWaypointSlot(ZvieriArenaEditor view, int slot) {
		super(view, slot);
		this.view = view;
	}

	@Override
	protected void applyWaypoint(Position position) {
		view.getArena().setQueue(position);
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
		return view.getArena().getQueue();
	}

	@Override
	public String getName() {
		return ChatColor.AQUA + "Wegpunkt Schlange";
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList("Kunden spawnen hier");
	}
}
