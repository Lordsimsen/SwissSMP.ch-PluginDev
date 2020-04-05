package ch.swisssmp.event.quarantine;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;

public enum QuarantineMaterial {
	EMPTY_SHELF("EMPTY_SHELF", Material.ANDESITE_WALL, Material.ANDESITE_WALL, 0),
	FOOD("CANNED_RAVIOLI", Material.DIORITE_WALL, Material.COOKED_BEEF, 30),
	TROPHY("QUARANTINE_TROPHY", Material.END_STONE_BRICK_WALL, Material.PAPER, 30),
	
	WATER_BOTTLE(Material.MOSSY_STONE_BRICK_WALL, Material.WATER_BUCKET, 30),
	HONEY_BOTTLE(Material.NETHER_BRICK_WALL, Material.HONEY_BOTTLE, 30),
	DRAGON_BREATH(Material.BRICK_WALL, Material.DRAGON_BREATH, 30),
	SWEET_BERRIES(Material.RED_NETHER_BRICK_WALL, Material.SWEET_BERRIES, 30),
	ENDER_PEARL(Material.PRISMARINE_WALL, Material.ENDER_PEARL, 30),
	COCOA(Material.GRANITE_WALL, Material.COCOA, 30),
	REDSTONE(Material.STONE_BRICK_WALL, Material.REDSTONE, 30),
	GLOWSTONE_DUST(Material.RED_SANDSTONE_WALL, Material.GLOWSTONE_DUST, 30),
	SUGAR(Material.SANDSTONE_WALL, Material.SUGAR, 30)
	;
	
	private final String customEnum;
	private final Material shelfMaterial;
	private final Material itemMaterial;
	private final String name;
	private final float spawnrate;

	private QuarantineMaterial(Material shelfMaterial, Material itemMaterial, float defaultSpawnrate) {
		this(shelfMaterial, itemMaterial, null, defaultSpawnrate);
	}
	
	private QuarantineMaterial(Material shelfMaterial, Material itemMaterial, String name, float defaultSpawnrate) {
		this.customEnum = null;
		this.shelfMaterial = shelfMaterial;
		this.itemMaterial = itemMaterial;
		this.name = name;
		this.spawnrate = defaultSpawnrate;
	}
	
	private QuarantineMaterial(String customEnum, Material shelfMaterial, Material itemMaterial, float defaultSpawnrate) {
		this.customEnum = customEnum;
		this.shelfMaterial = shelfMaterial;
		this.itemMaterial = itemMaterial;
		this.name = null;
		this.spawnrate = defaultSpawnrate;
	}
	
	public String getCustomEnum() {
		return customEnum;
	}
	
	public Material getShelfMaterial() {
		return shelfMaterial;
	}
	
	public float getDefaultSpawnrate() {
		return spawnrate;
	}
	
	public static QuarantineMaterial of(Material material) {
		for(QuarantineMaterial m : QuarantineMaterial.values()) {
			if(m.shelfMaterial==material || m.itemMaterial==material) return m;
		}
		return null;
	}
	
	public ItemStack getItemStack() {
		CustomItemBuilder itemBuilder;
		switch(this) {
		case EMPTY_SHELF:return new ItemStack(this.shelfMaterial);
		case FOOD:
		case TROPHY:
			itemBuilder = CustomItems.getCustomItemBuilder(this.getCustomEnum());
			break;
		case WATER_BOTTLE:
			return new ItemStack(Material.WATER_BUCKET);
		case HONEY_BOTTLE:
			return new ItemStack(Material.HONEY_BOTTLE);
		case DRAGON_BREATH:
			return new ItemStack(Material.DRAGON_BREATH);
		case ENDER_PEARL:
			return new ItemStack(Material.ENDER_PEARL);
		case SWEET_BERRIES:
			return new ItemStack(Material.SWEET_BERRIES);
		case COCOA:
			return new ItemStack(Material.COCOA_BEANS);
		case REDSTONE:
			return new ItemStack(Material.REDSTONE);
		case GLOWSTONE_DUST:
			return new ItemStack(Material.GLOWSTONE_DUST);
		case SUGAR:
			return new ItemStack(Material.SUGAR);
			
		default: return null;
		}
		
		if(itemBuilder==null) {
			itemBuilder = new CustomItemBuilder();
			itemBuilder.setMaterial(Material.PAPER);
		}
		
		itemBuilder.setAmount(1);
		
		if(this.name!=null) itemBuilder.setDisplayName(this.name);
		return itemBuilder.build();
	}
	
	public static String getName(Material material) {
		switch(material) {
		case WATER_BUCKET: return "Wasserkübel";
		case HONEY_BOTTLE: return "Honigflasche";
		case DRAGON_BREATH: return "Drachenatem";
		case ENDER_PEARL: return "Enderperle";
		case SWEET_BERRIES: return "Süsse Beeren";
		case COCOA_BEANS: return "Kakaobohnen";
		case REDSTONE: return "Redstone";
		case GLOWSTONE_DUST: return "Glühstein";
		case SUGAR: return "Zucker";
		default: return material.toString();
		}
	}
	
	public static QuarantineMaterial fromId(String id) {
		try {
			return QuarantineMaterial.valueOf(id);
		}
		catch(Exception e){
			return null;
		}
	}
	
	public static QuarantineMaterial[] getLiquids() {
		return new QuarantineMaterial[] {
				QuarantineMaterial.WATER_BOTTLE,
				QuarantineMaterial.DRAGON_BREATH,
				QuarantineMaterial.HONEY_BOTTLE,
		};
	}
	
	public static QuarantineMaterial[] getSolids() {
		return new QuarantineMaterial[] {
				QuarantineMaterial.COCOA,
				QuarantineMaterial.ENDER_PEARL,
				QuarantineMaterial.SWEET_BERRIES,
		};
	}
	
	public static QuarantineMaterial[] getCatalysts() {
		return new QuarantineMaterial[] {
				QuarantineMaterial.REDSTONE,
				QuarantineMaterial.GLOWSTONE_DUST,
				QuarantineMaterial.SUGAR,
		};
	}
}
