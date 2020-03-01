package ch.swisssmp.zones.editor;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;
import ch.swisssmp.zones.ZoneEditor;
import ch.swisssmp.zones.zoneinfos.ZoneInfo;

public class LaunchZoneEditorSlot extends ButtonSlot {

	private final ZoneInfo zoneInfo;
	private final ItemStack itemStack;
	
	public LaunchZoneEditorSlot(CustomEditorView view, int slot, ItemStack itemStack, ZoneInfo zoneInfo) {
		super(view, slot);
		this.zoneInfo = zoneInfo;
		this.itemStack = itemStack;
	}

	@Override
	protected void triggerOnClick(ClickType arg0) {
		ZoneEditor.start((Player) this.getView().getPlayer(), itemStack, zoneInfo);
		this.getView().closeLater();
		System.out.println("Launch Editor!");
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		result.setMaterial(Material.FEATHER);
		return result;
	}

	@Override
	public String getName() {
		return isComplete() ? ChatColor.AQUA+"Zone bearbeiten" : ChatColor.YELLOW+"Zone auswählen";
	}

	@Override
	protected List<String> getNormalDescription() {
		return null;
	}

	@Override
	protected boolean isComplete() {
		return zoneInfo!=null;
	}

}
