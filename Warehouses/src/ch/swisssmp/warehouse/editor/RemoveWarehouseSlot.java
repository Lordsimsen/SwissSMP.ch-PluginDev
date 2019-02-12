package ch.swisssmp.warehouse.editor;

import java.util.ArrayList;
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
import ch.swisssmp.warehouse.Master;

public class RemoveWarehouseSlot extends ButtonSlot {

	private final Master master;
	private boolean confirmed = false;
	
	public RemoveWarehouseSlot(CustomEditorView view, int slot, Master master) {
		super(view, slot);
		this.master = master;
	}

	@Override
	protected void triggerOnClick(ClickType arg0) {
		if(!confirmed){
			confirmed = true;
			this.setItem(this.createSlot());
			return;
		}
		this.getView().closeLater();
		master.remove(master.getChests().stream().findFirst().orElse(null));

		SwissSMPler.get((Player) this.getView().getPlayer()).sendActionBar(ChatColor.RED+"Lagersystem entfernt");
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
			return ChatColor.RED+"Lagersystem entfernen";
		}
		else{
			return ChatColor.RED+"Bist du sicher?";
		}
	}

	@Override
	protected List<String> getNormalDescription() {
		List<String> result = new ArrayList<String>();
		result.add("Entfernt das System.");
		result.add("Die Filter bleiben");
		result.add("erhalten.");
		return result;
	}

	@Override
	protected boolean isComplete() {
		return true;
	}
}
