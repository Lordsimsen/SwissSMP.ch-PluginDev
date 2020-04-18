package ch.swisssmp.zones.editor.slots;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zones.ItemManager;
import ch.swisssmp.zones.editor.ZoneEditor;
import ch.swisssmp.zones.zoneinfos.ZoneInfo;

public class ConfirmZoneEditorSlot extends ButtonSlot {

	private final ZoneInfo zoneInfo;
	private final ZoneEditor editor;
	
	public ConfirmZoneEditorSlot(CustomEditorView view, int slot, ZoneInfo zoneInfo, ZoneEditor editor) {
		super(view, slot);
		this.zoneInfo = zoneInfo;
		this.editor = editor;
	}

	@Override
	protected void triggerOnClick(ClickType arg0) {
		ProtectedRegion region = editor.complete();
		if(region!=null){
			zoneInfo.setRegion(editor.getWorld(), editor.complete());
			SwissSMPler.get((Player) this.getView().getPlayer()).sendActionBar(ChatColor.GREEN+"Zone festgelegt.");
		}
		else{
			SwissSMPler.get((Player) this.getView().getPlayer()).sendActionBar(ChatColor.RED+"Zone konnte nicht festgelegt werden.");
		}
		ItemManager.updateItems(zoneInfo);
		this.getView().closeLater();
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = CustomItems.getCustomItemBuilder("checkmark");
		result.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		return result;
	}

	@Override
	public String getName() {
		return ChatColor.GREEN+"Zone bestätigen";
	}

	@Override
	protected List<String> getNormalDescription() {
		return null;
	}

	@Override
	protected List<String> getIncompleteDescription() {
		return Arrays.asList("Beendet den", "Bearbeitungsmodus");
	}

	@Override
	protected boolean isComplete() {
		return false;
	}

}
