package ch.swisssmp.travel.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.slot.PickItemSlot;
import ch.swisssmp.travel.TravelStationEditor;
import ch.swisssmp.utils.ItemUtil;

public class TravelGuideSlot extends PickItemSlot {

	private final TravelStationEditor view;
	
	public TravelGuideSlot(TravelStationEditor view, int slot) {
		super(view, slot);
		this.view = view;
	}

	@Override
	protected ItemStack createPick() {
		CustomItemBuilder itemBuilder = new CustomItemBuilder();
		itemBuilder.setMaterial(Material.LEAD);
		itemBuilder.setAmount(1);
		itemBuilder.setDisplayName(this.getName());
		List<String> description = new ArrayList<String>();
		description.add(this.getDescriptionColor()+"NPC für Zielauswahl");
		description.add(this.getSuggestActionColor()+"Rechtsklick auf NPC");
		itemBuilder.setLore(description);
		ItemStack itemStack = itemBuilder.build();
		ItemUtil.setString(itemStack, "link_travelstation", this.view.getStation().getId().toString());
		return itemStack;
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder itemBuilder = new CustomItemBuilder();
		itemBuilder.setMaterial(Material.LEAD);
		return itemBuilder;
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Reiseführer";
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList("NPC für Zielauswahl");
	}

	@Override
	protected boolean isComplete() {
		return true;
	}

}
