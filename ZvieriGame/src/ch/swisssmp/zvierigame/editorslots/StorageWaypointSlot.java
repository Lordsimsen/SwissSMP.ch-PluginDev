package ch.swisssmp.zvierigame.editorslots;

import ch.swisssmp.utils.Position;
import ch.swisssmp.waypoints.MarkerType;
import ch.swisssmp.waypoints.editor.WaypointSlot;
import ch.swisssmp.zvierigame.ZvieriArenaEditor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;

import java.util.Arrays;
import java.util.List;

public class StorageWaypointSlot extends WaypointSlot{
	
	private final ZvieriArenaEditor view;

	public StorageWaypointSlot(ZvieriArenaEditor view, int slot) {
		super(view, slot);
		this.view = view;
	}

	@Override
	protected void applyWaypoint(Position position) {
//		view.getArena().setStorage(position);
		return;
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
//		return view.getArena().getStorage();
		return null;
	}

	@Override
	public String getName() {
		return ChatColor.AQUA + "Wegpunkt Lager(kiste)";
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList("Kiste für Lager markieren");
	}

}
