package ch.swisssmp.trophies;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;


public class TrophyPedestal {
	
	private final UUID uuid;
	private final Color color;
	private final Block block;
	private final ArmorStand armorStand;
	private Item item;
	
	private TrophyPedestal(UUID uuid, Color color, Block block, ArmorStand armorStand, Item item) {
		this.uuid = uuid;
		this.color = color;
		this.block = block;
		this.armorStand = armorStand;
		this.item = item;
	}
	
	private TrophyPedestal(UUID uuid, Color color, Block block, ArmorStand armorStand) {
		this(uuid, color, block, armorStand, null);
	}
	
	public UUID getUniqueId() {
		return uuid;
	}
	
	public Color getColor() {
		return color;
	}
	
	public Block getBlock() {
		return block;
	}
	
	public ArmorStand getArmorStand() {
		return armorStand;
	}
	
	public Item getItem() {
		return item;
	}
	
	public void setItemStack(ItemStack itemStack) {
		if(itemStack==null) {
			if(item!=null) {
				item.remove();
				item = null;
			}
			return;
		}
		
		if(item==null) {
			Location location = block.getLocation().add(0.5,1.1,0.5);
			Item spawned = EntityUtility.spawnStaticItem(uuid, location, itemStack);
			UUID spawnedUuid = spawned.getUniqueId();
			item = block.getWorld().getEntities().stream().filter(e->e.getUniqueId().equals(spawnedUuid)).map(e->(Item) e).findFirst().get();
			EntityUtility.setTrophyPedestalId(item, uuid);
			
		}
		else {
			item.setItemStack(itemStack);
		}
		
		ItemMeta itemMeta = itemStack.getItemMeta();
		if(itemMeta.hasDisplayName()) {
			item.setCustomName(itemMeta.getDisplayName());
		}
	}
	
	public ItemStack getItemStack() {
		return item!=null ? item.getItemStack() : null;
	}
	
	public void remove() {
		block.setType(Material.AIR);
		armorStand.remove();
		if(item!=null) {
			item.getWorld().dropItem(item.getLocation(), item.getItemStack());
			item.remove();
		}
	}
	
	public void breakNaturally() {
		World world = block.getWorld();
		Location blockCenter = block.getLocation().add(0.5,0.5,0.5);
		ItemStack pedestalStack = armorStand.getEquipment().getHelmet();
		world.dropItem(blockCenter, pedestalStack);
		
		remove();
		
		world.spawnParticle(Particle.BLOCK_CRACK, blockCenter, 10, Bukkit.createBlockData(Material.STONE));
		world.playSound(blockCenter, Sound.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1, 1);
	}
	
	public static TrophyPedestal create(Block block, Color color) {
		UUID uuid = UUID.randomUUID();
		NamespacedKey key = EntityUtility.getTrophyPedestalIdKey();
		
		Location blockCenter = block.getLocation().add(0.5,0.5,0.5);
		ArmorStand armorStand = EntityUtility.spawnInvisibleArmorStand(block.getLocation().add(0.5,0,0.5));
		armorStand.setSilent(true);
		armorStand.setVisible(false);
		armorStand.setSmall(true);
		armorStand.setGravity(false);
		armorStand.setMarker(true);
		armorStand.setAI(false);
		Bukkit.getScheduler().runTaskLater(TrophyPedestalsPlugin.getInstance(), ()->{
			armorStand.setHeadPose(new EulerAngle(0,0,0));
			armorStand.setBodyPose(new EulerAngle(0,0,0));
		}, 2L);
		armorStand.setRotation(0, 0);
		armorStand.getPersistentDataContainer().set(key, PersistentDataType.STRING, uuid.toString());
		
		EntityEquipment equipment = armorStand.getEquipment();
		ItemStack itemStack = color.getItemStack();
		equipment.setHelmet(itemStack);
		block.setType(Material.BARRIER);
		
		block.getWorld().playSound(blockCenter, Sound.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1, 1);
		
		return new TrophyPedestal(uuid, color, block, armorStand);
	}
	
	public static Optional<TrophyPedestal> get(Block block) {
		Optional<ArmorStand> armorStand = EntityUtility.getArmorStand(block);
		if(!armorStand.isPresent()) {
			return Optional.empty();
		}
		Optional<UUID> uuid = EntityUtility.getTrophyPedestalId(armorStand.get());
		if(!uuid.isPresent()) {
			return Optional.empty();
		}
		Color color = Color.of(armorStand.get());
		return Optional.of(get(uuid.get(), color, block, armorStand.get()));
	}
	
	public static Optional<TrophyPedestal> get(ArmorStand armorStand) {
		Optional<UUID> uuid = EntityUtility.getTrophyPedestalId(armorStand);
		if(!uuid.isPresent()) return Optional.empty();
		Color color = Color.of(armorStand);
		Optional<Block> block = EntityUtility.getBlock(armorStand);
		return block.isPresent() ? Optional.of(get(uuid.get(), color, block.get(), armorStand)) : Optional.empty();
	}
	
	public static Optional<TrophyPedestal> get(Item item) {
		Optional<UUID> uuid = EntityUtility.getTrophyPedestalId(item);
		if(!uuid.isPresent()) return Optional.empty();
		Optional<Block> blockQuery = EntityUtility.getBlock(item);
		if(!blockQuery.isPresent()) return Optional.empty();
		Block block = blockQuery.get();
		Optional<ArmorStand> armorStandQuery = EntityUtility.getArmorStand(block);
		if(!armorStandQuery.isPresent()) {
			return Optional.empty();
		}
		ArmorStand armorStand = armorStandQuery.get();
		Color color = Color.of(armorStand);
		return Optional.of(get(uuid.get(), color, block, armorStand));
	}
	
	private static TrophyPedestal get(UUID uuid, Color color, Block block, ArmorStand armorStand) {
		Optional<Item> item = EntityUtility.getItem(block, uuid);
		return new TrophyPedestal(uuid, color, block, armorStand, item.isPresent() ? item.get() : null);
	}
}
