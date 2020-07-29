package ch.swisssmp.city.editor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.city.Citizenship;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.InfoSlot;

public class CitizenSlot extends InfoSlot {

	private final Citizenship citizenship;
	
	public CitizenSlot(CustomEditorView view, int slot, Citizenship citizenship) {
		super(view, slot);
		this.citizenship = citizenship;
	}

	@Override
	protected ItemStack createSlot() {
		ItemStack result = citizenship.getHead();
		ItemMeta itemMeta = result.getItemMeta();
		itemMeta.setDisplayName(this.getName());
		itemMeta.setLore(this.getDescription());
		result.setItemMeta(itemMeta);
		return result;
	}

	@Override
	public String getName() {
		return ChatColor.YELLOW+ citizenship.getDisplayName();
	}

	@Override
	protected List<String> getNormalDescription() {
		List<String> result = new ArrayList<String>();
		result.add(citizenship.getRank().getDisplayName());
		String role = citizenship.getRole();
		if(!role.isEmpty() && !role.equals(citizenship.getRank().getDisplayName())) result.add(ChatColor.LIGHT_PURPLE+role);
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
