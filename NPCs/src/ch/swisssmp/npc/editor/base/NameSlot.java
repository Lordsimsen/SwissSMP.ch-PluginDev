package ch.swisssmp.npc.editor.base;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ValueSlot;
import ch.swisssmp.npc.NPCInstance;

public class NameSlot extends ValueSlot {

	private final NPCInstance npc;
	
	public NameSlot(CustomEditorView view, int slot, NPCInstance npc) {
		super(view, slot);
		this.npc = npc;
	}

	@Override
	protected boolean applyValue(ItemStack itemStack) {
		if(itemStack==null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) return false;
		String displayName = itemStack.getItemMeta().getDisplayName();
		npc.setName(displayName);
		return true;
	}

	@Override
	protected ItemStack createPick() {
		CustomItemBuilder itemBuilder = new CustomItemBuilder();
		itemBuilder.setAmount(1);
		itemBuilder.setMaterial(Material.NAME_TAG);
		itemBuilder.setDisplayName(npc.getName());
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
		return Arrays.asList(this.npc.getName());
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList("Benanntes Namensschild","einsetzen");
	}

	@Override
	protected boolean isComplete() {
		return true;
	}

}
