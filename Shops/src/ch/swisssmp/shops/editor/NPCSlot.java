package ch.swisssmp.shops.editor;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;
import ch.swisssmp.npc.NPCEditorView;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.shops.ShopsPlugin;

public class NPCSlot extends ButtonSlot {

	private final NPCInstance npc;
	
	public NPCSlot(CustomEditorView view, int slot, NPCInstance npc) {
		super(view, slot);
		this.npc = npc;
	}

	@Override
	protected void triggerOnClick(ClickType arg0) {
		this.getView().closeLater();
		Bukkit.getScheduler().runTaskLater(ShopsPlugin.getInstance(), ()->{
			NPCEditorView.open((Player) this.getView().getPlayer(), npc);
		}, 2L);
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		result.setMaterial(Material.FEATHER);
		return result;
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"NPC bearbeiten";
	}

	@Override
	protected List<String> getNormalDescription() {
		return null;
	}

	@Override
	protected boolean isComplete() {
		return true;
	}

}
