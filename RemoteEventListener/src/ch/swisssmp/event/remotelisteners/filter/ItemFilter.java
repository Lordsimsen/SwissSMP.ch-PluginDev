package ch.swisssmp.event.remotelisteners.filter;

import ch.swisssmp.utils.ItemUtil;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.ConfigurationSection;

public interface ItemFilter {

	public default boolean checkItem(ConfigurationSection dataSection, ItemStack itemStack){
		boolean result = true;
		if(itemStack==null) return false;
		if(dataSection.contains("material")){
			result = dataSection.getMaterial("material") == itemStack.getType();
		}
		if(dataSection.contains("item_id")){
			result &= ItemUtil.getInt(itemStack, "item_id")==dataSection.getInt("item_id");
		}
		return result;
	}
}
