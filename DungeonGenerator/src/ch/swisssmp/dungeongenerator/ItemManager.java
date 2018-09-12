package ch.swisssmp.dungeongenerator;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;

public class ItemManager {
	protected static ItemStack getInventoryToken(DungeonGenerator generator, int amount){
		String displayName = ChatColor.LIGHT_PURPLE+generator.getName();
		CustomItemBuilder customItemBuilder = CustomItems.getCustomItemBuilder("DUNGEON_GENERATOR");
		customItemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		customItemBuilder.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		customItemBuilder.setUnbreakable(true);
		customItemBuilder.setAmount(amount);
		customItemBuilder.setDisplayName(displayName);
		List<String> lore = generator.getInfo();
		lore.add(0, "Linksklick ("+generator.getBoundingBoxMaterial().name()+"): Vorlage auswählen");
		lore.add(1, "Linksklick ("+generator.getGenerationBoxMaterial().name()+"): Start markieren");
		customItemBuilder.setLore(lore);
		ItemStack result = customItemBuilder.build();
		ItemUtil.setInt(result, "generator_id", generator.getId());
		return result;
	}
}
