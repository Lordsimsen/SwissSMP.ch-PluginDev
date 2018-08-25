package ch.swisssmp.adventuredungeons;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class ItemManager {
	/**
	 * Gets the Dungeon ID from the ItemStacks NBT data
	 * @param itemStack
	 * @return A Dungeon ID if found;
	 * 		   otherwise <code>-1</code>
	 */
	public static int getDungeonId(ItemStack itemStack){
		if(itemStack==null) return -1;
		net.minecraft.server.v1_12_R1.ItemStack nbtStack = CraftItemStack.asNMSCopy(itemStack);
		if(!nbtStack.hasTag()) return -1;
		NBTTagCompound nbtTag = nbtStack.getTag();
		if(!nbtTag.hasKey("dungeon_id")) return -1;
		return nbtTag.getInt("dungeon_id");
	}
	public static ItemStack getDungeonToken(int dungeon_id, String dungeon_name, String custom_enum){
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder(custom_enum);
		itemBuilder.setDisplayName(dungeon_name);
		itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ItemStack result = itemBuilder.build();
		net.minecraft.server.v1_12_R1.ItemStack nbtStack = CraftItemStack.asNMSCopy(result);
		NBTTagCompound nbtTag = nbtStack.getTag();
		nbtTag.setInt("dungeon_id", dungeon_id);
		nbtStack.setTag(nbtTag);
		result.setItemMeta(CraftItemStack.getItemMeta(nbtStack));
		return result;
	}
}
