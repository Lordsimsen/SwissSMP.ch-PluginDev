package ch.swisssmp.travel.editor;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.editor.slot.ButtonSlot;
import ch.swisssmp.travel.TravelStationEditor;
import ch.swisssmp.travel.TravelSystem;

public class SaveTemplateSlot extends ButtonSlot {

	private final TravelStationEditor view;
	
	public SaveTemplateSlot(TravelStationEditor view, int slot) {
		super(view, slot);
		this.view = view;
	}

	@Override
	protected void triggerOnClick(ClickType arg0) {
		this.view.closeLater();
		Bukkit.getScheduler().runTaskLater(TravelSystem.getInstance(), ()->{
			Bukkit.dispatchCommand(this.view.getPlayer(), "travelworld endedit");
		}, 2L);
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		return CustomItems.getCustomItemBuilder("CHECKMARK");
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Speichern & Welt verlassen";
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
