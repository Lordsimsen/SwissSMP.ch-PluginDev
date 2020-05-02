package ch.swisssmp.zvierigame.editorslots;

import ch.swisssmp.utils.Position;
import ch.swisssmp.waypoints.MarkerType;
import ch.swisssmp.waypoints.editor.WaypointSlot;
import ch.swisssmp.zvierigame.ZvieriArenaEditor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CounterWaypointSlot extends WaypointSlot{
	
	private final ZvieriArenaEditor view;

	public CounterWaypointSlot(ZvieriArenaEditor view, int slot) {
		super(view, slot);
		this.view = view;
	}

	@Override
	protected void applyWaypoint(Position position) {
		view.getArena().addCounter(position);
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
		try {
		return view.getArena().getCounter(0); //Only gets you the Position of the first counter currently
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	@Override
	protected List<String> getValueDisplay(){
		List<String> result = new ArrayList<String>();
		result.add(this.view.getArena().getCurrentCounters() + "/" + this.view.getArena().getMaxCounters() + " Theken markiert");
		return result;
	}

	@Override
	public String getName() {
		return ChatColor.AQUA + "Waypoint Theke (bis zu " + view.getArena().getMaxCounters() + ")";
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList("Kunden spawnen hier, wenn",
				"sie an der Reihe sind");
	}

}
