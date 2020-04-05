package ch.swisssmp.npc.editor.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ValueSlot;
import ch.swisssmp.npc.NPCInstance;

public class DialogSlot extends ValueSlot {

	private final NPCInstance npc;
	private List<String> value = new ArrayList<String>();
	
	public DialogSlot(CustomEditorView view, int slot, NPCInstance npc) {
		super(view, slot);
		this.npc = npc;
		List<String> dialog = npc.getDialog();
		if(dialog!=null) {
			this.value.addAll(dialog);
		}
	}

	@Override
	protected boolean applyValue(ItemStack itemStack) {
		if(itemStack==null || itemStack.getType()!=Material.WRITABLE_BOOK) return false;
		List<String> value = ((BookMeta) itemStack.getItemMeta()).getPages();
		this.value.clear();
		this.value.addAll(value);
		npc.setDialog(value);
		this.setItem(this.createSlot());
		return true;
	}

	@Override
	protected ItemStack createPick() {
		ItemStack itemStack = new ItemStack(Material.WRITABLE_BOOK);
		BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
		bookMeta.setPages(value);
		itemStack.setItemMeta(bookMeta);
		return itemStack;
	}
	
	@Override
	protected CustomItemBuilder createSlotBase(){
		CustomItemBuilder itemBuilder = new CustomItemBuilder();
		itemBuilder.setMaterial(this.value.size() > 0 ? Material.WRITTEN_BOOK : Material.WRITABLE_BOOK);
		return itemBuilder;
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Dialog";
	}

	@Override
	protected List<String> getValueDisplay() {
		return Arrays.asList(value.size()+" Textblöcke");
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList("Geschriebenes Buch","einsetzen",ChatColor.ITALIC+"1 Textblock pro Seite");
	}

	@Override
	protected boolean isComplete() {
		return true;
	}

}