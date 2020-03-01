package ch.swisssmp.warehouse.filters;

import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.warehouse.EnchantmentUtil;
import ch.swisssmp.warehouse.MaterialGroup;
import ch.swisssmp.warehouse.MaterialUtil;
import ch.swisssmp.warehouse.PotionDataUtil;

public class Filter {
	
	private final int slot;
	private final ItemStack templateStack;
	private final String custom_enum;
	
	public Filter(int slot, ItemStack templateStack){
		this.slot = slot;
		this.templateStack = templateStack;
		this.custom_enum = CustomItems.getCustomEnum(templateStack);
	}
	
	public int getSlot(){
		return slot;
	}
	
	public ItemStack getTemplateStack(){
		return templateStack;
	}
	
	public boolean match(ItemStack itemStack, FilterSettings settings){
		if(itemStack==null) return false;
		if(settings.color==FilterSetting.Include || settings.color==FilterSetting.Default){
			DyeColor a = MaterialUtil.getColor(templateStack.getType());
			if(a!=null){
				if(a!=MaterialUtil.getColor(itemStack.getType())) return false;
				if(MaterialUtil.getGroup(templateStack.getType())==MaterialUtil.getGroup(itemStack.getType())) return true;
			}
		}
		else if(settings.color==FilterSetting.Any){
			MaterialGroup a = MaterialUtil.getGroup(templateStack.getType());
			if(a!=null && a==MaterialUtil.getGroup(itemStack.getType())) return true;
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		ItemMeta templateMeta = templateStack.getItemMeta();
		if(settings.enchantments==FilterSetting.Exact){
			if(!EnchantmentUtil.compare(templateMeta, itemMeta)) return false;
		}
		if(settings.potion==FilterSetting.Exact){
			if(itemMeta instanceof PotionMeta && templateMeta instanceof PotionMeta){
				PotionMeta templatePotionMeta = (PotionMeta) templateMeta;
				PotionMeta itemPotionMeta = (PotionMeta) itemMeta;
				if(!PotionDataUtil.compare(templatePotionMeta, itemPotionMeta)) return false;
			}
			DyeColor a = MaterialUtil.getColor(templateStack.getType());
			if(a!=null){
				if(a!=MaterialUtil.getColor(itemStack.getType())) return false;
				if(MaterialUtil.getGroup(templateStack.getType())==MaterialUtil.getGroup(itemStack.getType())) return true;
			}
		}
		if(templateStack.getType()!=itemStack.getType()) return false;
		if(this.custom_enum!=null){
			String custom_enum = CustomItems.getCustomEnum(itemStack);
			if(custom_enum==null || !custom_enum.equals(this.custom_enum)) return false;
		}
		return true;
	}
}
