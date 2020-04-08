package ch.swisssmp.travel.editor;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;
import ch.swisssmp.travel.TravelStation;
import ch.swisssmp.utils.SwissSMPler;

public class DeleteSlot extends ButtonSlot {

	private final TravelStation station;
	
	private boolean confirmed = false;
	
	public DeleteSlot(CustomEditorView view, int slot, TravelStation station) {
		super(view, slot);
		this.station = station;
	}

	@Override
	protected void triggerOnClick(ClickType arg0) {
		if(!confirmed){
			confirmed = true;
			this.setItem(this.createSlot());
			return;
		}
		this.getView().closeLater();
		station.remove();

		SwissSMPler.get((Player) this.getView().getPlayer()).sendActionBar(ChatColor.RED+"Station entfernt");
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		if(!this.confirmed){
			CustomItemBuilder result = new CustomItemBuilder();
			result.setMaterial(Material.BARRIER);
			return result;
		}
		return CustomItems.getCustomItemBuilder("CHECKMARK");
	}

	@Override
	public String getName() {
		if(!confirmed){
			return ChatColor.RED+"Station entfernen";
		}
		else{
			return ChatColor.RED+"Station wirklich entfernen?";
		}
	}

	@Override
	protected List<String> getNormalDescription() {
		return null;
	}

	@Override
	protected boolean isComplete() {
		return true;
	}
}
