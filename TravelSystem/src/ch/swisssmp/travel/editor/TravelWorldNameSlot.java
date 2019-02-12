package ch.swisssmp.travel.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.slot.ValueSlot;
import ch.swisssmp.travel.TravelStationEditor;

public class TravelWorldNameSlot extends ValueSlot {

	private final TravelStationEditor view;
	
	public TravelWorldNameSlot(TravelStationEditor view, int slot) {
		super(view, slot);
		this.view = view;
	}

	@Override
	protected boolean applyValue(ItemStack itemStack) {
		if(!itemStack.hasItemMeta() || itemStack.getType()!=Material.NAME_TAG) return false;
		ItemMeta itemMeta = itemStack.getItemMeta();
		if(!itemMeta.hasDisplayName()) return false;
		String name = itemMeta.getDisplayName();
		this.view.getStation().setTravelWorldName(name);
		return true;
	}

	@Override
	protected ItemStack createPick() {
		CustomItemBuilder itemBuilder = new CustomItemBuilder();
		String worldName = this.view.getStation().getTravelWorldName();
		if(worldName!=null) itemBuilder.setDisplayName(worldName);
		itemBuilder.setAmount(1);
		itemBuilder.setMaterial(Material.NAME_TAG);
		return itemBuilder.build();
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		result.setMaterial(Material.NAME_TAG);
		return result;
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Vorlage Reisewelt";
	}

	@Override
	protected List<String> getValueDisplay(){
		return Arrays.asList(this.view.getStation().getTravelWorldName());
	}
	
	@Override
	protected List<String> getNormalDescription() {
		List<String> result = new ArrayList<String>();
		result.add("Namensschild mit");
		result.add("Weltname einsetzen");
		return result;
	}

	@Override
	protected List<String> getIncompleteDescription() {
		return Arrays.asList("Namensschild mit", "Weltname einsetzen");
	}

	@Override
	protected boolean isComplete() {
		String worldName = this.view.getStation().getTravelWorldName();
		return worldName!=null && !worldName.isEmpty();
	}

}
