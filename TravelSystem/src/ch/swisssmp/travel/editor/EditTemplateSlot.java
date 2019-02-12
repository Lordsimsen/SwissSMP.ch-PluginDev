package ch.swisssmp.travel.editor;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.slot.ButtonSlot;
import ch.swisssmp.travel.TravelStationEditor;
import ch.swisssmp.travel.TravelSystem;

public class EditTemplateSlot extends ButtonSlot {

	private final TravelStationEditor view;
	
	public EditTemplateSlot(TravelStationEditor view, int slot) {
		super(view, slot);
		this.view = view;
	}

	@Override
	protected void triggerOnClick(ClickType arg0) {
		this.view.closeLater();
		String worldName = this.view.getStation().getTravelWorldName();
		Bukkit.getScheduler().runTaskLater(TravelSystem.getInstance(), ()->{
			Bukkit.dispatchCommand(this.view.getPlayer(), "travelworld edit "+worldName);
		}, 5L);
	}
	
	@Override
	protected CustomItemBuilder createSlotBase(){
		CustomItemBuilder itemBuilder = new CustomItemBuilder();
		itemBuilder.setMaterial(Material.FEATHER);
		return itemBuilder;
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Reisewelt bearbeiten";
	}

	@Override
	protected List<String> getNormalDescription() {
		return null;
	}

	@Override
	protected List<String> getIncompleteDescription() {
		return Arrays.asList(
				"Zuerst Name festlegen");
	}

	@Override
	protected boolean isComplete() {
		String worldName = this.view.getStation().getTravelWorldName();
		return worldName!=null && !worldName.isEmpty();
	}

}
