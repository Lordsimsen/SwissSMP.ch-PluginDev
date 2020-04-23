package ch.swisssmp.knightstournament.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.slot.ValueSlot;
import ch.swisssmp.knightstournament.KnightsArena;
import ch.swisssmp.knightstournament.KnightsArenaEditor;

public class BeginSoundSlot extends ValueSlot {
	
	private final KnightsArenaEditor view;

	public BeginSoundSlot(KnightsArenaEditor view, int slot) {
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
		this.view.getArena().setBeginSound(name);
		KnightsArena.save(this.view.getArena().getWorld());
		return true;
	}

	@Override
	protected ItemStack createPick() {
		CustomItemBuilder itemBuilder = new CustomItemBuilder();
		String arenaBeginSound = this.view.getArena().getBeginSound();
		if (arenaBeginSound != null) {
			itemBuilder.setDisplayName(arenaBeginSound);
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
		return ChatColor.AQUA + "Arena beginSound nennen";
	}

	@Override protected List<String> getValueDisplay(){
		return Arrays.asList(this.view.getArena().getBeginSound());
	}
	
	@Override
	protected List<String> getNormalDescription() {
		List<String> result = new ArrayList<String>();
		result.add("Namensschild mit");
		result.add("beginSound Namen einsetzen");
		return result;
	}
	
	@Override
	protected List<String> getIncompleteDescription(){
		return Arrays.asList("Namensschild mit", "beginSound Namen einsetzen");
	}

	@Override
	protected boolean isComplete() {
		String beginSoundName = this.view.getArena().getBeginSound();
		return (beginSoundName != null) && (!beginSoundName.isEmpty());
	}


}
