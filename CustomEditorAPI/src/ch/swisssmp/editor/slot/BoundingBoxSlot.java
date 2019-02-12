package ch.swisssmp.editor.slot;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.utils.Position;
import ch.swisssmp.utils.SwissSMPler;

public abstract class BoundingBoxSlot extends EditorSlot {
	
	public BoundingBoxSlot(CustomEditorView view, int slot) {
		super(view, slot);
	}

	@Override
	public void createItem() {
		this.setItem(this.createSlot());
	}

	@Override
	public boolean onClick(ClickType clickType) {
		if(clickType==ClickType.LEFT){
			//set the bounding box
			this.updatePositions();
			this.setItem(this.createSlot());
		}
		else if(this.isComplete()){
			//select the bounding box
			this.selectBoundingBox(this.getBoundingBoxMin(), this.getBoundingBoxMax());
			this.getView().closeLater();
			SwissSMPler.get(this.getView().getPlayer().getUniqueId()).sendActionBar(ChatColor.GREEN+"Zone ausgewählt.");
		}
		return true;
	}
	
	private void updatePositions(){
		Position min = this.getNewBoundingBoxMin();
		Position max = this.getNewBoundingBoxMax();
		if(min!=null && max!=null){
			this.applyBoundingBox(min, max);
		}
	}
	
	@Override
	protected List<String> getValueDisplay(){
		Position min = this.getBoundingBoxMin();
		Position max = this.getBoundingBoxMax();
		
		List<String> result = new ArrayList<String>();
		
		if(min!=null){
			result.add("Von: "+min.getBlockX()+", "+min.getBlockY()+", "+min.getBlockZ());
		}
		if(max!=null){
			result.add("Bis: "+max.getBlockX()+", "+max.getBlockY()+", "+max.getBlockZ());
		}
		return result;
	}
	
	protected abstract Position getBoundingBoxMin();
	protected abstract Position getBoundingBoxMax();
	
	protected abstract Position getNewBoundingBoxMin();
	protected abstract Position getNewBoundingBoxMax();
	
	protected abstract void applyBoundingBox(Position min, Position max);

	protected abstract void selectBoundingBox(Position min, Position max);
	
	@Override
	protected CustomItemBuilder createSlotBase(){
		return CustomItems.getCustomItemBuilder("BOUNDING_BOX");
	}
}
