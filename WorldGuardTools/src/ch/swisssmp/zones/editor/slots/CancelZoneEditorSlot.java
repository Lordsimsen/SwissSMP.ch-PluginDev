package ch.swisssmp.zones.editor.slots;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zones.editor.ZoneEditor;

public class CancelZoneEditorSlot extends ButtonSlot {

	private final ZoneEditor editor;
	
	private boolean confirmed = false;
	
	public CancelZoneEditorSlot(CustomEditorView view, int slot, ZoneEditor editor) {
		super(view, slot);
		this.editor = editor;
	}

	@Override
	protected void triggerOnClick(ClickType arg0) {
		if(!confirmed){
			confirmed = true;
			this.setItem(this.createSlot());
			return;
		}
		this.getView().closeLater();
		editor.cancel();

		SwissSMPler.get((Player) this.getView().getPlayer()).sendActionBar(ChatColor.RED+"Bearbeiten abgebrochen.");
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
			return ChatColor.RED+"Bearbeiten abbrechen";
		}
		else{
			return ChatColor.RED+"Wirklich abbrechen?";
		}
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList("Die Eckpunkte werden", "dadurch zurückgesetzt.");
	}

	@Override
	protected boolean isComplete() {
		return true;
	}
}
