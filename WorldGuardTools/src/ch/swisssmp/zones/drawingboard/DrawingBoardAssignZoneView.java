package ch.swisssmp.zones.drawingboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import ch.swisssmp.zones.ZoneContainer;
import ch.swisssmp.zones.ZoneType;
import ch.swisssmp.zones.zoneinfos.ZoneInfo;

public class DrawingBoardAssignZoneView {
	
	public static InventoryView open(Player player){
		Merchant merchant = Bukkit.createMerchant("Zeichenbrett");
		List<MerchantRecipe> recipes = createRecipes(player);
		if(recipes!=null) merchant.setRecipes(recipes);
		InventoryView result = player.openMerchant(merchant, true);
		return result;
	}
	
	private static List<MerchantRecipe> createRecipes(Player player){
		List<ZoneInfo> zones = getAvailableZones(player.getWorld(), player);
		List<MerchantRecipe> recipes = zones!=null ? zones.stream().map(z->getAssignZoneRecipe(z)).collect(Collectors.toList()) : new ArrayList<MerchantRecipe>();
		recipes.add(getCreateZoneRecipe(ZoneType.PROJECT));
		recipes.add(getCreateZoneRecipe(ZoneType.NO_CREEPER));
		recipes.add(getCreateZoneRecipe(ZoneType.NO_HOSTILE));
		recipes.add(getCreateZoneRecipe(ZoneType.ALLOW_SPAWN));
		return recipes;
	}
	
	private static MerchantRecipe getCreateZoneRecipe(ZoneType zoneType){
		ZoneInfo zoneInfo = zoneType.createZoneInfo();
		MerchantRecipe recipe = new MerchantRecipe(zoneInfo.createItemStack(), 0, Integer.MAX_VALUE, false, 0, 1);
		recipe.setIngredients(Arrays.asList(ZoneType.GENERIC.toItemStack(), zoneType.getCost()));
		return recipe;
	}
	
	private static MerchantRecipe getAssignZoneRecipe(ZoneInfo zoneInfo){
		MerchantRecipe recipe = new MerchantRecipe(zoneInfo.createItemStack(), 0, Integer.MAX_VALUE, false, 0, 1);
		recipe.setIngredients(Arrays.asList(ZoneType.GENERIC.toItemStack(), new ItemStack(Material.EMERALD,8)));
		return recipe;
	}
	
	private static List<ZoneInfo> getAvailableZones(World world, Player player){
		ZoneContainer container = ZoneContainer.get(world);
		if(container==null) return null;
		return container.getZones(player);
	}
}
