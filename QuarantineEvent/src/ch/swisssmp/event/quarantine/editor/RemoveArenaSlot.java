package ch.swisssmp.event.quarantine.editor;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;
import ch.swisssmp.event.quarantine.QuarantineArena;
import ch.swisssmp.utils.SwissSMPler;

public class RemoveArenaSlot extends ButtonSlot {

	private final QuarantineArena arena;
	
	private boolean confirmed = false;
	
	public RemoveArenaSlot(CustomEditorView view, int slot, QuarantineArena arena) {
		super(view, slot);
		this.arena = arena;
	}

	@Override
	protected void triggerOnClick(ClickType arg0) {
		if(!confirmed){
			confirmed = true;
			this.setItem(this.createSlot());
			return;
		}
		this.getView().closeLater();
		arena.remove();
		arena.getContainer().save();

		SwissSMPler.get((Player) this.getView().getPlayer()).sendActionBar(ChatColor.RED+"Arena entfernt");
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
			return ChatColor.RED+"Arena entfernen";
		}
		else{
			return ChatColor.RED+"Arena wirklich entfernen?";
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
