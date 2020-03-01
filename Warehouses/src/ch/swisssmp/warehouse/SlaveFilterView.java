package ch.swisssmp.warehouse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.warehouse.editor.ColorSettingSlot;
import ch.swisssmp.warehouse.editor.DamageSettingSlot;
import ch.swisssmp.warehouse.editor.EnchantsSettingSlot;
import ch.swisssmp.warehouse.editor.PotionSettingSlot;
import ch.swisssmp.warehouse.editor.RemoveSlaveSlot;
import ch.swisssmp.warehouse.filters.Filter;

public class SlaveFilterView extends CustomEditorView {

	private final Slave slave;
	
	protected SlaveFilterView(Player player, Slave slave) {
		super(player);
		this.slave = slave;
	}

	@Override
	protected void createItems() {
		super.createItems();
		Inventory inventory = this.getTopInventory();
		for(Filter filter : slave.getFilters()){
			inventory.setItem(filter.getSlot(), filter.getTemplateStack());
		}
	}

	@Override
	protected Collection<EditorSlot> initializeEditor() {
		Collection<EditorSlot> slots = new ArrayList<EditorSlot>();
		slots.add(new RemoveSlaveSlot(this,8,slave));
		slots.add(new EnchantsSettingSlot(this,17,slave.getFilterSettings()));
		slots.add(new PotionSettingSlot(this,26,slave.getFilterSettings()));
		slots.add(new DamageSettingSlot(this,35,slave.getFilterSettings()));
		slots.add(new ColorSettingSlot(this,44,slave.getFilterSettings()));
		return slots;
	}
	
	@Override
	protected boolean allowEmptySlotInteraction(){
		return true;
	}
	
	@Override
	public void onInventoryClosed(InventoryCloseEvent event){
		this.applyFilters();
	}
	
	public void applyFilters(){
		List<Filter> filters = new ArrayList<Filter>();
		for(int x = 0; x < 8; x++){
			for(int y = 0; y < 6; y++){
				int slot = x + (y*9);
				ItemStack item = this.getTopInventory().getItem(slot);
				if(item==null || item.getType()==Material.AIR) continue;
				filters.add(new Filter(slot,item));
			}
		}
		slave.setFilters(filters);
	}
	
	public void clear(){
		this.getTopInventory().clear();
	}

	public static SlaveFilterView open(Player player, Slave slave){
		SlaveFilterView result = new SlaveFilterView(player, slave);
		result.open();
		return result;
	}

	@Override
	public String getTitle() {
		return "Filter";
	}

	@Override
	protected int getInventorySize() {
		return 54;
	}
}
