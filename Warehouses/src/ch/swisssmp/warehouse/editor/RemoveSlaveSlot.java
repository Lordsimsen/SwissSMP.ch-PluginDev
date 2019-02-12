package ch.swisssmp.warehouse.editor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.editor.slot.ButtonSlot;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.warehouse.Slave;
import ch.swisssmp.warehouse.SlaveFilterView;

public class RemoveSlaveSlot extends ButtonSlot {

	private final SlaveFilterView view;
	private final Slave slave;
	private boolean confirmed = false;
	
	public RemoveSlaveSlot(SlaveFilterView view, int slot, Slave slave) {
		super(view, slot);
		this.view = view;
		this.slave = slave;
	}

	@Override
	protected void triggerOnClick(ClickType arg0) {
		if(!confirmed){
			confirmed = true;
			this.setItem(this.createSlot());
			return;
		}
		this.getView().closeLater();
		view.applyFilters(); //ensure changes are applied to the slave
		view.clear(); //ensure the view is wiped so it does not reapply the filters when closing
		slave.reset(); //make slave drop all filters and clear them afterwards

		SwissSMPler.get((Player) this.getView().getPlayer()).sendActionBar(ChatColor.RED+"Truhenfilter entfernt");
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
			return ChatColor.RED+"Filter entfernen";
		}
		else{
			return ChatColor.RED+"Bist du sicher?";
		}
	}

	@Override
	protected List<String> getNormalDescription() {
		List<String> result = new ArrayList<String>();
		result.add("Entfernt den Filter.");
		result.add("Die Kiste bleibt");
		result.add("zugewiesen.");
		return result;
	}

	@Override
	protected boolean isComplete() {
		return true;
	}
}
