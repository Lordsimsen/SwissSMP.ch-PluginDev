package ch.swisssmp.city.npcs.guides.editor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import ch.swisssmp.city.npcs.guides.AddonGuide;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;
import ch.swisssmp.utils.SwissSMPler;

public class RemoveGuideSlot extends ButtonSlot {

	private final AddonGuide guide;
	
	private boolean confirmed = false;
	
	public RemoveGuideSlot(CustomEditorView view, int slot, AddonGuide guide) {
		super(view, slot);
		this.guide = guide;
	}

	@Override
	protected void triggerOnClick(ClickType arg0) {
		if(!confirmed){
			confirmed = true;
			this.setItem(this.createSlot());
			return;
		}
		this.getView().closeLater();
		guide.remove();

		SwissSMPler.get(this.getView().getPlayer()).sendActionBar(ChatColor.RED+"Addon Guide entfernt");
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
			return ChatColor.RED+"Addon Guide entfernen";
		}
		else{
			return ChatColor.RED+"Bist du sicher?";
		}
	}

	@Override
	protected List<String> getNormalDescription() {
		List<String> result = new ArrayList<String>();
		result.add("Entfernt den NPC. Diese");
		result.add("Aktion hat keinen Ein-");
		result.add("fluss auf die Funktion");
		result.add("des Addons.");
		return result;
	}

	@Override
	protected boolean isComplete() {
		return true;
	}
}
