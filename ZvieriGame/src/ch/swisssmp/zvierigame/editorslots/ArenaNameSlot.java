package ch.swisssmp.zvierigame.editorslots;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.slot.ValueSlot;
import ch.swisssmp.zvierigame.ZvieriArenaEditor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArenaNameSlot extends ValueSlot{
	
	private final ZvieriArenaEditor view;

	public ArenaNameSlot(ZvieriArenaEditor view, int slot) {
		super(view, slot);
		this.view = view;
	}

	@Override
	protected boolean applyValue(ItemStack itemStack) {
		if(!itemStack.hasItemMeta() || (itemStack.getType() != Material.NAME_TAG)) {
			return false;
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		if(!itemMeta.hasDisplayName()) {
			return false;
		}
		String name = itemMeta.getDisplayName();
		this.view.getArena().setName(name);
		this.view.getArena().updateTokens();
		return true;
	}

	@Override
	protected ItemStack createPick() {
		CustomItemBuilder itemBuilder = new CustomItemBuilder();
		String arenaName = this.view.getArena().getName();
		if (arenaName != null) {
			itemBuilder.setDisplayName(arenaName);
		}
		itemBuilder.setAmount(1);
		itemBuilder.setMaterial(Material.NAME_TAG);	
		return itemBuilder.build();
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		result.setMaterial(Material.NAME_TAG);
		return result;
	}

	@Override
	public String getName() {
		return ChatColor.AQUA + "Arena benennen";
	}

	@Override protected List<String> getValueDisplay(){
		return Arrays.asList(this.view.getArena().getName());
	}
	
	@Override
	protected List<String> getNormalDescription() {
		List<String> result = new ArrayList<String>();
		result.add("Namensschild mit");
		result.add("Arenanamen einsetzen");
		return result;
	}
	
	@Override
	protected List<String> getIncompleteDescription(){
		return Arrays.asList("Namensschild mit", "Arenanamen einsetzen");
	}

	@Override
	protected boolean isComplete() {
		String arenaName = this.view.getArena().getName();
		return (arenaName != null) && (!arenaName.isEmpty());
	}

}
