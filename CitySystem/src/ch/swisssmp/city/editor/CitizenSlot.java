package ch.swisssmp.city.editor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.city.Citizen;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.InfoSlot;

public class CitizenSlot extends InfoSlot {

	private final Citizen citizen;
	
	public CitizenSlot(CustomEditorView view, int slot, Citizen citizen) {
		super(view, slot);
		this.citizen = citizen;
	}

	@Override
	protected ItemStack createSlot() {
		ItemStack result = citizen.getHead();
		ItemMeta itemMeta = result.getItemMeta();
		itemMeta.setDisplayName(this.getName());
		itemMeta.setLore(this.getDescription());
		result.setItemMeta(itemMeta);
		return result;
	}

	@Override
	public String getName() {
		return ChatColor.YELLOW+ citizen.getDisplayName();
	}

	@Override
	protected List<String> getNormalDescription() {
		List<String> result = new ArrayList<String>();
		result.add(citizen.getRank().getDisplayName());
		String role = citizen.getRole();
		if(!role.isEmpty() && !role.equals(citizen.getRank().getDisplayName())) result.add(ChatColor.LIGHT_PURPLE+role);
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
