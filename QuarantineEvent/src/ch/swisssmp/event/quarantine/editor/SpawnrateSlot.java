package ch.swisssmp.event.quarantine.editor;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ValueRangeSlot;
import ch.swisssmp.event.quarantine.QuarantineArena;
import ch.swisssmp.event.quarantine.QuarantineMaterial;

public class SpawnrateSlot extends ValueRangeSlot {

	private final QuarantineArena arena;
	private final QuarantineMaterial material;
	private final ItemStack exampleStack;
	
	public SpawnrateSlot(CustomEditorView view, int slot, QuarantineArena arena, QuarantineMaterial material) {
		super(view, slot);
		this.arena = arena;
		this.material = material;
		this.exampleStack = material.getItemStack();
	}

	@Override
	protected float getInitialValue() {
		return arena.getSpawnrate(material);
	}

	@Override
	protected float getMinValue() {
		return 0;
	}

	@Override
	protected float getMaxValue() {
		return Float.MAX_VALUE;
	}

	@Override
	protected float getStep() {
		return 0.1f;
	}

	@Override
	protected float getBigStep() {
		return 1;
	}

	@Override
	protected void onValueChanged(float value) {
		arena.setSpawnrate(material, value);
		arena.getContainer().save();
	}

	@Override
	protected boolean isComplete() {
		return true;
	}

	@Override
	public String getName() {
		return "Spawnrate "+(exampleStack!=null ? exampleStack.getItemMeta().getDisplayName() : material.toString());
	}

	@Override
	protected List<String> getNormalDescription() {
		return exampleStack!=null ? exampleStack.getItemMeta().getLore() : Arrays.asList();
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		result.setMaterial(exampleStack!=null ? exampleStack.getType() : Material.BOOK);
		return result;
	}

}
