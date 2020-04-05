package ch.swisssmp.event.quarantine;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.entity.Player;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.event.quarantine.editor.ArenaBoundingBoxSlot;
import ch.swisssmp.event.quarantine.editor.NameSlot;
import ch.swisssmp.event.quarantine.editor.RemoveArenaSlot;
import ch.swisssmp.event.quarantine.editor.RespawnSlot;
import ch.swisssmp.event.quarantine.editor.SpawnrateSlot;
import ch.swisssmp.event.quarantine.editor.SurvivorStartSlot;

public class QuarantineArenaView extends CustomEditorView {

	private final QuarantineArena arena;
	
	protected QuarantineArenaView(Player player, QuarantineArena arena) {
		super(player);
		this.arena = arena;
	}

	@Override
	protected int getInventorySize() {
		return 36;
	}

	@Override
	protected Collection<EditorSlot> initializeEditor() {
		return Arrays.asList(
				new NameSlot(this, 0, arena),
				new SurvivorStartSlot(this, 1, arena),
				new RespawnSlot(this, 2, arena),
				new ArenaBoundingBoxSlot(this, 9, arena),
				new RemoveArenaSlot(this, 8, arena),
				
				new SpawnrateSlot(this, 18, arena, QuarantineMaterial.WATER_BOTTLE),
				new SpawnrateSlot(this, 19, arena, QuarantineMaterial.HONEY_BOTTLE),
				new SpawnrateSlot(this, 20, arena, QuarantineMaterial.DRAGON_BREATH),
				
				new SpawnrateSlot(this, 21, arena, QuarantineMaterial.SWEET_BERRIES),
				new SpawnrateSlot(this, 22, arena, QuarantineMaterial.ENDER_PEARL),
				new SpawnrateSlot(this, 23, arena, QuarantineMaterial.COCOA),
				
				new SpawnrateSlot(this, 24, arena, QuarantineMaterial.REDSTONE),
				new SpawnrateSlot(this, 25, arena, QuarantineMaterial.GLOWSTONE_DUST),
				new SpawnrateSlot(this, 26, arena, QuarantineMaterial.SUGAR),
				
				new SpawnrateSlot(this, 27, arena, QuarantineMaterial.FOOD),
				new SpawnrateSlot(this, 28, arena, QuarantineMaterial.TROPHY)
		);
	}

	@Override
	public String getTitle() {
		String name = arena.getName();
		if(name==null || name.isEmpty()) name = "Unbenannte Arena";
		return name;
	}

	public static QuarantineArenaView open(Player player, QuarantineArena arena) {
		QuarantineArenaView result = new QuarantineArenaView(player, arena);
		result.open();
		return result;
	}
}
