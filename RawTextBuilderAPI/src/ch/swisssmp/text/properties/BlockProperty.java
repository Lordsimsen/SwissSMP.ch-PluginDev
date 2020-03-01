package ch.swisssmp.text.properties;

import org.bukkit.util.BlockVector;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * A string specifying the coordinates of the block entity from which the NBT value is obtained. The coordinates can be absolute or relative. Useless if  nbt is absent.
 */
public class BlockProperty extends AbstractStringProperty implements IOptionalProperty {

	private BlockProperty(BlockProperty template) {
		super(template);
	}
	
	public BlockProperty(String s) {
		super(s);
	}
	
	public BlockProperty(BlockVector vector) {
		super(vector.getBlockX()+","+vector.getBlockY()+","+vector.getBlockZ());
	}

	@Override
	public String getKey() {
		return "block";
	}

	@Override
	public IProperty duplicate() {
		return new BlockProperty(this);
	}

	@Override
	public void applySpigotValues(BaseComponent component) {
		System.out.println("BlockProperty cannot be applied to chat components.");
	}
}
