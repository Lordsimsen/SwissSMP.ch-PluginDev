package ch.swisssmp.craftmmo.mmoitem;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class MmoItemFood extends MmoItem{

	public final int nutrition;
	public final float saturation;
	
	public MmoItemFood(ConfigurationSection dataSection) throws Exception {
		super(dataSection);
		//configurationSection cannot be null because this class is only used when there is a classSection in it
		ConfigurationSection configurationSection = dataSection.getConfigurationSection("configuration");
		ConfigurationSection classSection = configurationSection.getConfigurationSection("class");
		this.nutrition = classSection.getInt("nutrition");
		this.saturation = (float)classSection.getDouble("saturation");
	}
	public static int getVanillaHunger(ItemStack itemStack){
		if(itemStack==null) return 0;
		switch(itemStack.getType()){
		case APPLE:
			return 4;
		case BAKED_POTATO:
			return 5;
		case BEETROOT:
			return 1;
		case BEETROOT_SOUP:
			return 6;
		case BREAD:
			return 5;
		case CAKE:
			return 2;
		case CAKE_BLOCK:
			return 14;
		case CARROT:
			return 3;
		case CHORUS_FRUIT:
			return 4;
		case COOKED_CHICKEN:
			return 6;
		case COOKED_FISH:
			return 5;
		case COOKED_MUTTON:
			return 6;
		case GRILLED_PORK:
			return 8;
		case COOKED_RABBIT:
			return 5;
		case COOKIE:
			return 2;
		case GOLDEN_APPLE:
			return 4;
		case GOLDEN_CARROT:
			return 6;
		case MELON:
			return 2;
		case MUSHROOM_SOUP:
			return 6;
		case POISONOUS_POTATO:
			return 2;
		case POTATO:
			return 1;
		case PUMPKIN_PIE:
			return 8;
		case RABBIT_STEW:
			return 10;
		case RAW_BEEF:
			return 3;
		case RAW_CHICKEN:
			return 2;
		case RAW_FISH:
			return 2;
		case MUTTON:
			return 2;
		case PORK:
			return 3;
		case RABBIT:
			return 3;
		case ROTTEN_FLESH:
			return 4;
		case SPIDER_EYE:
			return 2;
		case COOKED_BEEF:
			return 8;
		default:
			return 0;
		}
	}
	public static double getVanillaSaturation(ItemStack itemStack){
		if(itemStack==null) return 0;
		switch(itemStack.getType()){
		case APPLE:
			return 2.4;
		case BAKED_POTATO:
			return 6;
		case BEETROOT:
			return 1.2;
		case BEETROOT_SOUP:
			return 7.2;
		case BREAD:
			return 6;
		case CAKE:
			return 0.4;
		case CAKE_BLOCK:
			return 2.8;
		case CARROT:
			return 3.6;
		case CHORUS_FRUIT:
			return 2.4;
		case COOKED_CHICKEN:
			return 7.2;
		case COOKED_FISH:
			return 6;
		case COOKED_MUTTON:
			return 9.6;
		case GRILLED_PORK:
			return 12.8;
		case COOKED_RABBIT:
			return 6;
		case COOKIE:
			return 0.4;
		case GOLDEN_APPLE:
			return 9.6;
		case GOLDEN_CARROT:
			return 14.4;
		case MELON:
			return 1.2;
		case MUSHROOM_SOUP:
			return 7.2;
		case POISONOUS_POTATO:
			return 1.2;
		case POTATO:
			return 0.6;
		case PUMPKIN_PIE:
			return 4.8;
		case RABBIT_STEW:
			return 12;
		case RAW_BEEF:
			return 1.8;
		case RAW_CHICKEN:
			return 1.2;
		case RAW_FISH:
			return 0.4;
		case MUTTON:
			return 1.2;
		case PORK:
			return 1.8;
		case RABBIT:
			return 1.8;
		case ROTTEN_FLESH:
			return 0.8;
		case SPIDER_EYE:
			return 3.2;
		case COOKED_BEEF:
			return 12.8;
		default:
			return 0;
		}
	}
}
