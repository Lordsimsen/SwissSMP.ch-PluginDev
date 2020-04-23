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

public class EndSoundSlot extends ValueSlot{
	
	private final KnightsArenaEditor view;

	public EndSoundSlot(KnightsArenaEditor view, int slot) {
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
		this.view.getArena().setEndSound(name);
		KnightsArena.save(this.view.getArena().getWorld());
		return true;
	}

	@Override
	protected ItemStack createPick() {
		CustomItemBuilder itemBuilder = new CustomItemBuilder();
		String arenaEndSound = this.view.getArena().getEndSound();
		if (arenaEndSound != null) {
			itemBuilder.setDisplayName(arenaEndSound);
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
		return ChatColor.AQUA + "Arena endSound nennen";
	}

	@Override protected List<String> getValueDisplay(){
		return Arrays.asList(this.view.getArena().getEndSound());
	}
	
	@Override
	protected List<String> getNormalDescription() {
		List<String> result = new ArrayList<String>();
		result.add("Namensschild mit");
		result.add("endSound Namen einsetzen");
		return result;
	}
	
	@Override
	protected List<String> getIncompleteDescription(){
		return Arrays.asList("Namensschild mit", "endSound Namen einsetzen");
	}

	@Override
	protected boolean isComplete() {
		String endSoundName = this.view.getArena().getEndSound();
		return (endSoundName != null) && (!endSoundName.isEmpty());
	}


}
