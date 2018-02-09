package ch.swisssmp.event.remotelisteners.filter;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.ConfigurationSection;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public interface ItemFilter {

	public default boolean checkItem(ConfigurationSection dataSection, ItemStack itemStack){
		boolean result = true;
		if(itemStack==null) return false;
		if(dataSection.contains("material")){
			result &= dataSection.getMaterial("material")==itemStack.getType();
		}
		if(dataSection.contains("durability")){
			result &= dataSection.getInt("durability")==itemStack.getDurability();
		}
		if(dataSection.contains("item_id")){
			net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
			if(!nmsItemStack.hasTag()) return false;
			NBTTagCompound nbtTag = nmsItemStack.getTag();
			if(!nbtTag.hasKey("item_id")) return false;
			result &= dataSection.getInt("item_id")==nbtTag.getInt("item_id");
		}
		return result;
	}
}
