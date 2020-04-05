package ch.swisssmp.ageofempires.editor;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.util.Vector;

import ch.swisssmp.ageofempires.TauntEntry;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;

public class TriggerTauntSlot extends ButtonSlot {

	private final TauntEntry taunt;
	
	public TriggerTauntSlot(CustomEditorView view, int slot, TauntEntry taunt) {
		super(view, slot);
		this.taunt = taunt;
	}

	@Override
	protected void triggerOnClick(ClickType clickType) {
		CustomEditorView view = this.getView();
		Player player = view.getPlayer();
		if(clickType==ClickType.RIGHT) {
			player.playSound(player.getEyeLocation().add(new Vector(0,2,0)), taunt.getAudio(), SoundCategory.VOICE, 2f, 1);
			return;
		}
		view.closeLater();
		player.chat(taunt.getKey());
	}

	@Override
	protected boolean isComplete() {
		return true;
	}

	@Override
	public String getName() {
		return ChatColor.YELLOW+taunt.getDisplay();
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList(ChatColor.GRAY+taunt.getKey(), ChatColor.AQUA+"Linksklick: Auslösen", ChatColor.AQUA+"Rechtsklick: Anhören");
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		result.setMaterial(Material.BOOK);
		result.setAmount(1);
		return result;
	}

}
