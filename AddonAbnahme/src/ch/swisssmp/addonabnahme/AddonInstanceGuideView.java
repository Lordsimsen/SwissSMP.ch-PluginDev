package ch.swisssmp.addonabnahme;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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

	@Override
	protected Collection<EditorSlot> initializeEditor() {
		List<EditorSlot> slots = new ArrayList<EditorSlot>();
		slots.add(new AddonSlot(this,0,this.addon));
		slots.add(new AddonStateSlot(this,1,addon.getState(),addon.getAddonStateReason()));
		slots.add(new RemoveGuideSlot(this,8,npc,addon));
		return slots;
	}

	@Override
	public String getTitle() {
		return addon.getAddonInfo().getName();
	}

	@Override
	protected int getInventorySize() {
		return 9;
	}

	public static AddonInstanceGuideView open(Player player, NPCInstance npc, AddonInstanceInfo instance){
		AddonInstanceGuideView result = new AddonInstanceGuideView(player,npc, instance);
		GuideViewEvent event = new GuideViewEvent(result);
		Bukkit.getPluginManager().callEvent(event);
		result.open();
		return result;
	}
}
