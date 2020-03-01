package ch.swisssmp.warehouse;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.entity.Player;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.warehouse.editor.ColorSettingSlot;
import ch.swisssmp.warehouse.editor.DamageSettingSlot;
import ch.swisssmp.warehouse.editor.EnchantsSettingSlot;
import ch.swisssmp.warehouse.editor.RemoveWarehouseSlot;

public class MasterFilterView extends CustomEditorView {

	private final Master master;
	
	protected MasterFilterView(Player player, Master master) {
		super(player);
		this.master = master;
	}

	@Override
	protected Collection<EditorSlot> initializeEditor() {
		Collection<EditorSlot> slots = new ArrayList<EditorSlot>();
		slots.add(new EnchantsSettingSlot(this,0,master.getFilterSettings()));
		slots.add(new DamageSettingSlot(this,1,master.getFilterSettings()));
		slots.add(new ColorSettingSlot(this,2,master.getFilterSettings()));
		slots.add(new RemoveWarehouseSlot(this,8,master));
		return slots;
	}

	public static MasterFilterView open(Player player, Master master){
		MasterFilterView result = new MasterFilterView(player, master);
		result.open();
		return result;
	}

	@Override
	public String getTitle() {
		return "Verteilertruhe";
	}

	@Override
	protected int getInventorySize() {
		return 9;
	}
}
