package ch.swisssmp.addonabnahme;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import ch.swisssmp.addonabnahme.editor.AddonSlot;
import ch.swisssmp.addonabnahme.editor.AddonStateSlot;
import ch.swisssmp.addonabnahme.editor.RemoveGuideSlot;
import ch.swisssmp.addonabnahme.event.GuideViewEvent;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.npc.NPCInstance;

public class AddonInstanceGuideView extends CustomEditorView {

	private final NPCInstance npc;
	private final AddonInstanceInfo addon;
	
	private List<EditorSlot> slots;
	
	protected AddonInstanceGuideView(Player player, NPCInstance npc, AddonInstanceInfo addon) {
		super(player);
		this.npc = npc;
		this.addon = addon;
	}
	
	public NPCInstance getNPC(){
		return npc;
	}
	
	public AddonInstanceInfo getAddon(){
		return addon;
	}
	
	public List<EditorSlot> getSlots(){
		return slots;
	}

	@Override
	protected Inventory createInventory() {
		return Bukkit.createInventory(null, 9, addon.getAddonInfo().getName());
	}

	@Override
	protected Collection<EditorSlot> createSlots() {
		return this.slots;
	}
	
	private void prepareSlots(){
		List<EditorSlot> slots = new ArrayList<EditorSlot>();
		slots.add(new AddonSlot(this,0,this.addon));
		slots.add(new AddonStateSlot(this,1,addon.getState(),addon.getAddonStateReason()));
		slots.add(new RemoveGuideSlot(this,8,npc,addon));
		this.slots = slots;
	}

	public static AddonInstanceGuideView open(Player player, NPCInstance npc, AddonInstanceInfo instance){
		AddonInstanceGuideView result = new AddonInstanceGuideView(player,npc, instance);
		result.prepareSlots();
		GuideViewEvent event = new GuideViewEvent(result);
		Bukkit.getPluginManager().callEvent(event);
		result.open();
		return result;
	}
}
