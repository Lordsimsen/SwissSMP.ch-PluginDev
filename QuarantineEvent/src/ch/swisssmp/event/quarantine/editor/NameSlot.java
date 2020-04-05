package ch.swisssmp.event.quarantine.editor;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ValueSlot;
import ch.swisssmp.event.quarantine.QuarantineArena;

public class NameSlot extends ValueSlot {

	private final QuarantineArena arena;
	
	public NameSlot(CustomEditorView view, int slot, QuarantineArena arena) {
		super(view, slot);
		this.arena = arena;
	}

	@Override
	protected boolean applyValue(ItemStack itemStack) {
		if(itemStack==null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) return false;
		String displayName = itemStack.getItemMeta().getDisplayName();
		arena.setName(displayName);
		arena.getContainer().save();
		return true;
	}

	@Override
	protected ItemStack createPick() {
		CustomItemBuilder itemBuilder = new CustomItemBuilder();
		itemBuilder.setAmount(1);
		itemBuilder.setMaterial(Material.NAME_TAG);
		if(arena.getName()!=null) itemBuilder.setDisplayName(arena.getName());
		return itemBuilder.build();
	}
	
	@Override
	protected CustomItemBuilder createSlotBase(){
		CustomItemBuilder itemBuilder = new CustomItemBuilder();
		itemBuilder.setMaterial(Material.NAME_TAG);
		return itemBuilder;
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Name";
	}

	@Override
	protected List<String> getValueDisplay() {
		return Arrays.asList(arena.getName()!=null ? arena.getName() : "");
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList("Benanntes Namensschild","einsetzen");
	}

	@Override
	protected boolean isComplete() {
		return arena.getName()!=null && !arena.getName().isEmpty();
	}

}