package ch.swisssmp.trophies;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;

public enum Color {
	WHITE(Material.WHITE_WOOL),
	ORANGE(Material.ORANGE_WOOL),
	MAGENTA(Material.MAGENTA_WOOL),
	LIGHT_BLUE(Material.LIGHT_BLUE_WOOL),
	YELLOW(Material.YELLOW_WOOL),
	LIME(Material.LIME_WOOL),
	PINK(Material.PINK_WOOL),
	GRAY(Material.GRAY_WOOL),
	LIGHT_GRAY(Material.LIGHT_GRAY_WOOL),
	CYAN(Material.CYAN_WOOL),
	BLUE(Material.BLUE_WOOL),
	PURPLE(Material.PURPLE_WOOL),
	GREEN(Material.GREEN_WOOL),
	BROWN(Material.BROWN_WOOL),
	RED(Material.RED_WOOL),
	BLACK(Material.BLACK_WOOL)
	;
	
	private final Material material;
	
	private Color(Material material) {
		this.material = material;
	}
	
	public String getCustomItemEnum() {
		return toString()+"_TROPHY_PEDESTAL";
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public ItemStack getItemStack() {
		String prefix = TrophyPedestalsPlugin.getPrefix();
		String customEnum = getCustomItemEnum();
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder(customEnum);
		if(itemBuilder==null) {
			Bukkit.getLogger().info(prefix+ChatColor.RED+" CustomItem für "+customEnum.toLowerCase()+" nicht gefunden!");
			return null;
		}
		if(itemBuilder.getMaterial()==null) {
			Bukkit.getLogger().info(prefix+ChatColor.RED+" CustomItem für "+customEnum.toLowerCase()+" hat kein Material!");
			return null;
		}
		itemBuilder.setAmount(1);
		ItemStack result = itemBuilder.build();
		ItemUtil.setString(result, "trophy_pedestal_color", this.toString());
		return result;
	}
	
	public static Color of(ArmorStand armorStand) {
		if(armorStand.getEquipment().getHelmet()==null) return null;
		return of(armorStand.getEquipment().getHelmet());
	}
	
	public static Color of(ItemStack itemStack) {
		String colorString = ItemUtil.getString(itemStack, "trophy_pedestal_color");
		try {
			return colorString!=null ? Color.valueOf(colorString) : null;
		}
		catch(Exception e) {
			return null;
		}
	}
}
