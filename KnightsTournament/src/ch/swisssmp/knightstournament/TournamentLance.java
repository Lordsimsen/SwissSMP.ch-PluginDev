package ch.swisssmp.knightstournament;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

public class TournamentLance {
	
	protected static final String customBaseEnum = "TOURNAMENT_LANCE";
	protected static final String bareCustomEnum = "BARE_TOURNAMENT_LANCE";
	protected static final String dataProperty = "TournamentLance";
	protected static final String primaryColorProperty = "PrimaryColor";
	protected static final String secondaryColorProperty = "SecondaryColor";
	protected static final String PLACEHOLDER_ITEM = "FAKE_ARROW_SHIELD";

	public static boolean isLance(ItemStack itemStack){
		if(itemStack==null) return false;
		NBTTagCompound nbt = ItemUtil.getData(itemStack);
		return nbt!=null && nbt.hasKey(dataProperty);
	}

	protected static void registerCraftingRecipe(){
		CustomItemBuilder lanceBuilder = CustomItems.getCustomItemBuilder(bareCustomEnum);
		if(lanceBuilder==null){
			Bukkit.getLogger().info("[KnightsTournament] Turnierlanze konnte nicht geladen werden.");
			return;
		}
		lanceBuilder.setAmount(1);
		lanceBuilder.setAttackDamage(1);
		lanceBuilder.setAttackSpeed(0.1f);
		ItemStack lance = lanceBuilder.build();
		ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(KnightsTournamentPlugin.plugin, "bare_tournament_lance"), lance);
		recipe.shape(
				" bw",
				"lwb",
				"il "
		);
		recipe.setIngredient('b', Material.HONEY_BOTTLE);
		recipe.setIngredient('i', Material.IRON_INGOT);
		recipe.setIngredient('l', Material.LEATHER);
		recipe.setIngredient('w', new RecipeChoice.MaterialChoice(Material.OAK_PLANKS, Material.ACACIA_PLANKS,
				Material.BIRCH_PLANKS, Material.DARK_OAK_PLANKS, Material.JUNGLE_PLANKS, Material.SPRUCE_PLANKS));
		Bukkit.getServer().addRecipe(recipe);
	}

	protected static void updateLegacyLances(){
		for(Player player : Bukkit.getOnlinePlayers()){
			updateLegacyLances(player.getInventory());
		}
	}

	protected static void updateLegacyLances(Inventory inventory){
		for(ItemStack itemStack : inventory){
			if(itemStack==null) continue;
			if(itemStack.getType()!=Material.DIAMOND_SWORD) continue;
			String customEnum = CustomItems.getCustomEnum(itemStack);
			if(customEnum==null || !customEnum.equalsIgnoreCase("TOURNAMENT_LANCE")) continue;
			CustomItemBuilder customItemBuilder = CustomItems.getCustomItemBuilder(TournamentLance.bareCustomEnum);
			customItemBuilder.update(itemStack);
			NBTTagCompound nbt = ItemUtil.getData(itemStack);
			nbt.remove("AttributeModifiers");
			nbt.remove("HideFlags");
			ItemUtil.setData(itemStack, nbt);
		}
	}
}
