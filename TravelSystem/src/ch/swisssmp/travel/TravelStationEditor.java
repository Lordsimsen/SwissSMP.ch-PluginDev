package ch.swisssmp.travel;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.travel.editor.CargoSlot;
import ch.swisssmp.travel.editor.DeleteSlot;
import ch.swisssmp.travel.editor.EditTemplateSlot;
import ch.swisssmp.travel.editor.InsideAnchorSlot;
import ch.swisssmp.travel.editor.OutsideAnchorSlot;
import ch.swisssmp.travel.editor.RespawnSlot;
import ch.swisssmp.travel.editor.SaveTemplateSlot;
import ch.swisssmp.travel.editor.TravelGuideSlot;
import ch.swisssmp.travel.editor.TravelStationTypeSlot;
import ch.swisssmp.travel.editor.TravelWaypointSlot;
import ch.swisssmp.travel.editor.TravelWorldNameSlot;

public class TravelStationEditor extends CustomEditorView {

	private final TravelStation travelStation;
	
	private TravelStationEditor(Player player, TravelStation travelStation){
		super(player);
		this.travelStation = travelStation;
	}
	
	@Override
	protected Collection<EditorSlot> initializeEditor(){
		Collection<EditorSlot> result = new ArrayList<EditorSlot>();
		World world = this.getPlayer().getWorld();
		result.add(new TravelStationTypeSlot(this,0));
		result.add(new TravelWaypointSlot(this,1));
		result.add(new RespawnSlot(this,2));
		result.add(new OutsideAnchorSlot(this,3));
		result.add(new InsideAnchorSlot(this,4));
		if(world.getName().equals(this.travelStation.getTravelWorldName())){
			result.add(new SaveTemplateSlot(this,8));
		}
		else{
			result.add(new TravelGuideSlot(this,6));
			result.add(new TravelWorldNameSlot(this,7));
			result.add(new EditTemplateSlot(this,8));
			result.add(new CargoSlot(this,9));
		}
		result.add(new DeleteSlot(this, 17, this.travelStation));
		return result;
	}
	
	public TravelStation getStation(){
		return travelStation;
	}
	
	public static TravelStationEditor open(Player player, TravelStation travelStation){
		TravelStationEditor editor = new TravelStationEditor(player,travelStation);
		editor.open();
		return editor;
	}

	@Override
	protected void onInventoryClicked(InventoryClickEvent arg0) {
		this.travelStation.updateTokens();
	}

	@Override
	public String getTitle() {
		return this.travelStation.getName();
	}

	@Override
	protected int getInventorySize() {
		return 18;
	}
}
