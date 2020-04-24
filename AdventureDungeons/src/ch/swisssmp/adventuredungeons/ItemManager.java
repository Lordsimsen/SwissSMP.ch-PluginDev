package ch.swisssmp.adventuredungeons;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.Position;

public class ItemManager {
	/**
	 * Gets the Dungeon ID from the ItemStacks NBT data
	 * @param itemStack
	 * @return A Dungeon ID if found;
	 * 		   otherwise <code>-1</code>
	 */
	public static int getDungeonId(ItemStack itemStack){
		if(itemStack==null) return -1;
		return ItemUtil.getInt(itemStack, "dugeon_id");
	}
	
	/**
	 * 
	 * @param dungeon_id - The ID written into the ItemStacks NBT data
	 * @param dungeon_name - The name for the ItemStack
	 * @param custom_enum - The custom item enum to be used for this token
	 * @return An <code>ItemStack</code> containing the <code>dungeon_id</code> in the NBT data
	 */
	public static ItemStack getDungeonToken(int dungeon_id, String dungeon_name, String custom_enum){
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder(custom_enum);
		itemBuilder.setDisplayName(ChatColor.LIGHT_PURPLE+dungeon_name);
		itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ItemStack result = itemBuilder.build();
		ItemUtil.setInt(result, "dungeon_id", dungeon_id);
		ItemUtil.setString(result, "dungeon_tool", "DUNGEON");
		return result;
	}
	
	/**
	 * @param position - The position to be displayed
	 * @return A description Text for Position Token ItemStacks containing instructions and its coordinates
	 */
	protected static List<String> getPositionTokenLore(Position position){
		List<String> result = new ArrayList<String>();
		result.add(ChatColor.GRAY+"X: "+Mathf.round(position.getX(),1)+", Y: "+Mathf.round(position.getY(),1)+", Z: "+Mathf.round(position.getZ(),1));
		result.add(ChatColor.GRAY+"Linksklick: Teleport");
		result.add(ChatColor.GRAY+"Rechtsklick: Punkt setzen");
		return result;
	}
}
