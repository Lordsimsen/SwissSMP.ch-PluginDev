package ch.swisssmp.event.quarantine;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.Mathf;

public class MaterialSpawner {
	private final Block block;
	private final QuarantineMaterial material;
	private final long spawnrate;
	
	private long respawnTimeout = 0;
	private boolean filled = true;
	
	protected MaterialSpawner(Block block, QuarantineMaterial material, float spawnrate) {
		this.block = block;
		this.material = material;
		this.spawnrate = Mathf.roundToInt(spawnrate * 20);
	}
	
	public long getSpawnrate() {
		return spawnrate;
	}
	
	public ItemStack collect() {
		if(!filled) return null;
		block.setType(QuarantineMaterial.EMPTY_SHELF.getShelfMaterial());
		respawnTimeout = spawnrate;
		filled = false;
		return material.getItemStack();
	}
	
	public void run() {
		if(filled) return;
		respawnTimeout--;
		if(respawnTimeout>0) return;
		reset();
	}
	
	public void reset() {
		filled = true;
		block.setType(material.getShelfMaterial());
		respawnTimeout = spawnrate;
	}
	
	public void setTimeout(long timeout) {
		if(timeout>0) {
			this.collect();
			this.respawnTimeout = timeout;
		}
		else {
			this.reset();
		}
	}
	
	public boolean isFilled() {
		return filled;
	}
	
	public boolean isAt(Block block) {
		return this.block.getWorld()==block.getWorld() && this.block.getX()==block.getX() && this.block.getY()==block.getY() && this.block.getZ()==block.getZ();
	}
}
