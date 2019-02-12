package ch.swisssmp.city;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import ch.swisssmp.utils.ItemUtil;

public class CityToolChoice implements RecipeChoice {

	private final ItemStack itemStack;
	private final String city_tool;
	
	protected CityToolChoice(ItemStack itemStack){
		this.itemStack = itemStack;
		this.city_tool = ItemUtil.getString(itemStack, "city_tool");
	}

	@Override
	public ItemStack getItemStack() {
		return itemStack;
	}

	@Override
	public boolean test(ItemStack t) {
		System.out.println("Check City Tool!");
		if(t==null || t.getType()!=itemStack.getType()) return false;
		String city_tool = ItemUtil.getString(t, "city_tool");
		return city_tool!=null && city_tool.equals(this.city_tool);
	}

	@Override
	public RecipeChoice clone() {
		return new CityToolChoice(itemStack.clone());
	}

}
