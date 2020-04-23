package ch.swisssmp.knightstournament.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.slot.PickItemSlot;
import ch.swisssmp.knightstournament.KnightsArenaEditor;
import ch.swisssmp.utils.ItemUtil;
import net.md_5.bungee.api.ChatColor;

public class TournamentOrganizerSlot extends PickItemSlot{
	
	private final KnightsArenaEditor view;

	public TournamentOrganizerSlot(KnightsArenaEditor view, int slot) {
		super(view, slot);
		this.view = view;
	}

	@Override
	protected ItemStack createPick() {
		CustomItemBuilder itemBuilder = new CustomItemBuilder();
		itemBuilder.setMaterial(Material.LEAD);
		itemBuilder.setAmount(1);
		itemBuilder.setDisplayName(this.getName());
		List<String> description = new ArrayList<String>();
		description.add(this.getDescriptionColor() + "NPC für Turnierorganisator");
		description.add(this.getSuggestActionColor() + "Rechtsklick auf NPC");
		itemBuilder.setLore(description);		
		ItemStack itemStack = itemBuilder.build();
		ItemUtil.setString(itemStack, "link_knightsarena", this.view.getArena().getId().toString());
		return itemStack;
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder itemBuilder = new CustomItemBuilder();
		itemBuilder.setMaterial(Material.LEAD);
		return itemBuilder;
	}

	@Override
	public String getName() {
		return ChatColor.AQUA + "Turnierorganisator";
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList("Turnierorganisator zuweisen");
	}

	@Override
	protected boolean isComplete() {
		return true;
	}


}
