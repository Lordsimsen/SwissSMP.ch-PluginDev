package ch.swisssmp.waypoints.editor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ValueSlot;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.Position;
import ch.swisssmp.waypoints.MarkerType;
import ch.swisssmp.waypoints.WaypointAPI;

public abstract class WaypointSlot extends ValueSlot {

	public WaypointSlot(CustomEditorView view, int slot) {
		super(view, slot);
	}

	@Override
	public void createItem() {
		this.setItem(this.createSlot());
	}
	
	@Override
	protected boolean applyValue(ItemStack itemStack){
		Position position = ItemUtil.getPosition(itemStack, "waypoint");
		if(position==null) return false;
		this.applyWaypoint(position);
		return true;
	}
	
	@Override
	protected ItemStack createPick(){
		if(!this.isComplete()) return null;
		ItemStack result = WaypointAPI.getItem(this.getWaypoint(), this.getMarkerType());
		World attachedWorld = this.getAttachedWorld();
		if(attachedWorld!=null){
			WaypointAPI.setAttachedWorld(result, attachedWorld);
		}
		return result;
	}
	
	@Override
	protected CustomItemBuilder createSlotBase(){
		return CustomItems.getCustomItemBuilder("MARKER_"+(this.isComplete() ? this.getMarkerType() : MarkerType.MISSING));
	}
	
	@Override
	protected List<String> getValueDisplay(){
		Position current = this.getWaypoint();
		List<String> result = new ArrayList<String>();
		if(current!=null) result.add(this.getValueColor()+""+current.getBlockX()+", "+current.getBlockY()+", "+current.getBlockZ());
		return result;
	}
	
	@Override
	protected boolean isComplete(){
		return this.getWaypoint()!=null;
	}
	
	@Override
	protected List<String> getIncompleteDescription(){
		return WaypointAPI.getWaypointMissingLore();
	}
	
	protected abstract World getAttachedWorld();
	protected abstract MarkerType getMarkerType();
	protected abstract Position getWaypoint();
	protected abstract void applyWaypoint(Position position);
}
