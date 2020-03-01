package ch.swisssmp.warehouse.filters;

import ch.swisssmp.utils.ConfigurationSection;

public class FilterSettings {
	public FilterSetting enchantments = FilterSetting.Default;
	public FilterSetting potion = FilterSetting.Default;
	public FilterSetting color = FilterSetting.Default;
	public FilterSetting damage = FilterSetting.Default;
	
	public void save(ConfigurationSection dataSection){
		dataSection.set("enchants", enchantments.toString());
		dataSection.set("potion", potion.toString());
		dataSection.set("color", color.toString());
		dataSection.set("damage", damage.toString());
	}
	
	public void load(ConfigurationSection dataSection){
		this.enchantments = FilterSetting.get(dataSection.getString("enchants"));
		this.potion = FilterSetting.get(dataSection.getString("potion"));
		this.color = FilterSetting.get(dataSection.getString("color"));
		this.damage = FilterSetting.get(dataSection.getString("damage"));
	}
	
	public static FilterSettings combine(FilterSettings parent, FilterSettings child){
		FilterSettings result = new FilterSettings();
		result.enchantments = child.enchantments==FilterSetting.Default ? parent.enchantments : child.enchantments;
		result.potion = child.potion==FilterSetting.Default ? parent.potion : child.potion;
		result.color = child.color==FilterSetting.Default ? parent.color : child.color;
		result.damage = child.damage==FilterSetting.Default ? parent.damage : child.damage;
		return result;
	}
}
