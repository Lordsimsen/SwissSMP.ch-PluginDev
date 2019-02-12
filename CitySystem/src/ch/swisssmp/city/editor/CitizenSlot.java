package ch.swisssmp.city.editor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.city.CitizenInfo;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.InfoSlot;

public class CitizenSlot extends InfoSlot {

	private final CitizenInfo citizenInfo;
	
	public CitizenSlot(CustomEditorView view, int slot, CitizenInfo citizenInfo) {
		super(view, slot);
		this.citizenInfo = citizenInfo;
	}

	@Override
	protected ItemStack createSlot() {
		ItemStack result = citizenInfo.getHead();
		ItemMeta itemMeta = result.getItemMeta();
		itemMeta.setDisplayName(this.getName());
		itemMeta.setLore(this.getDescription());
		result.setItemMeta(itemMeta);
		return result;
	}

	@Override
	public String getName() {
		return ChatColor.YELLOW+citizenInfo.getDisplayName();
	}

	@Override
	protected List<String> getNormalDescription() {
		List<String> result = new ArrayList<String>();
		result.add(citizenInfo.getRank().getDisplayName());
		String role = citizenInfo.getRole();
		if(!role.isEmpty() && !role.equals(citizenInfo.getRank().getDisplayName())) result.add(ChatColor.LIGHT_PURPLE+role);
		return result;
	}

	@Override
	protected boolean isComplete() {
		return true;
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		return null;
	}

}
