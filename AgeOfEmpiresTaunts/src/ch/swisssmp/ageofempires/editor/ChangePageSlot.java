package ch.swisssmp.ageofempires.editor;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import ch.swisssmp.ageofempires.TauntsView;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.slot.ButtonSlot;

public class ChangePageSlot extends ButtonSlot {

	private final TauntsView view;
	private final boolean pageDown;
	
	public ChangePageSlot(TauntsView view, int slot, boolean pageDown) {
		super(view, slot);
		this.view = view;
		this.pageDown = pageDown;
	}

	@Override
	protected void triggerOnClick(ClickType clickType) {
		if(pageDown) {
			view.pageDown();
		}
		else {
			view.pageUp();
		}
	}

	@Override
	protected boolean isComplete() {
		return true;
	}

	@Override
	public String getName() {
		return pageDown ? "Nächste Seite" : "Vorherige Seite";
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList();
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		result.setMaterial(Material.PAPER);
		result.setAmount(1);
		return result;
	}

}
