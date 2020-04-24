package ch.swisssmp.blocks;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;

import ch.swisssmp.utils.ItemUtil;

public class CustomBlock {
	
	private final UUID uuid;
	private final Block block;
	private ArmorStand armorStand;
	private ItemStack blockStack;
	
	private CustomBlock(UUID uuid, Block block) {
		this.uuid = uuid;
		this.block = block;
	}
	
	private CustomBlock(UUID uuid, Block block, ArmorStand armorStand) {
		this.uuid = uuid;
		this.block = block;
		this.armorStand = armorStand;
		this.blockStack = armorStand.getEquipment().getChestplate();
	}
	
	public UUID getUniqueId() {
		return uuid;
	}
	
	public void setType(ItemStack itemStack) {
		this.blockStack = itemStack;
		armorStand.getEquipment().setHelmet(itemStack);
	}

	public void remove() {
		remove(false);
	}
	
	public void remove(boolean breakEffect) {
		this.linkArmorStand();
		armorStand.remove();
	}
	
	private void linkArmorStand() {
		Chunk chunk = block.getChunk();
		if(!chunk.isLoaded()) {
			chunk.load();
		}
		World world = chunk.getWorld();
		Optional<Entity> entity = world.getNearbyEntities(BoundingBox.of(block)).stream().filter(e->e instanceof ArmorStand && uuid.equals(getUniqueBlockId((ArmorStand)e))).findAny();
		if(!entity.isPresent()) return;
		this.armorStand = (ArmorStand) entity.get();
	}
	
	private static void setUniqueBlockId(ArmorStand armorStand, UUID uuid) {
		ItemStack stack = new ItemStack(Material.NAME_TAG);
		ItemUtil.setString(stack, "custom_block_uuid", uuid.toString());
		armorStand.getEquipment().setChestplate(stack);
	}
	
	private static UUID getUniqueBlockId(ArmorStand armorStand) {
		ItemStack stack = armorStand.getEquipment().getChestplate();
		if(stack==null) return null;
		String uuidString = ItemUtil.getString(stack, "custom_block_uuid");
		try {
			return UUID.fromString(uuidString);
		}
		catch(Exception e){
			return null;
		}
	}
	
	public static Optional<CustomBlock> get(Block block) {
		World world = block.getWorld();
		Collection<ArmorStand> entities = world.getNearbyEntities(BoundingBox.of(block)).stream().filter(e->e instanceof ArmorStand).map(e->(ArmorStand)e).collect(Collectors.toList());
		UUID blockUid = null;
		ArmorStand armorStand = null;
		for(ArmorStand entity : entities) {
			blockUid = getUniqueBlockId(entity);
			if(blockUid!=null) break;
			armorStand = entity;
		}
		
		return blockUid!=null ? Optional.of(new CustomBlock(blockUid, block, armorStand)) : Optional.empty();
	}
}
