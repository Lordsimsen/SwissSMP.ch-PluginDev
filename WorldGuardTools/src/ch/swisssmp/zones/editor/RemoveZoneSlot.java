package ch.swisssmp.zones.editor;

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
import ch.swisssmp.zones.zoneinfos.ZoneInfo;

public class RemoveZoneSlot extends ButtonSlot {

	private final ZoneInfo zoneInfo;
	
	private boolean confirmed = false;
	
	public RemoveZoneSlot(CustomEditorView view, int slot, ZoneInfo zoneInfo) {
		super(view, slot);
		this.zoneInfo = zoneInfo;
	}

	@Override
	protected void triggerOnClick(ClickType arg0) {
		if(!confirmed){
			confirmed = true;
			this.setItem(this.createSlot());
			return;
		}
		this.getView().closeLater();
		zoneInfo.remove();

		SwissSMPler.get((Player) this.getView().getPlayer()).sendActionBar(ChatColor.RED+"Zone entfernt");
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
			return ChatColor.RED+"Zone entfernen";
		}
		else{
			return ChatColor.RED+"Zone wirklich entfernen?";
		}
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList("Alle Zonenpläne dieser", "Zone werden geleert.");
	}

	@Override
	protected boolean isComplete() {
		return true;
	}
}
