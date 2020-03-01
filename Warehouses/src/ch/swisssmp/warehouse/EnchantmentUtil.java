package ch.swisssmp.warehouse;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantmentUtil {
	public static boolean compare(ItemMeta a, ItemMeta b){
		if(a instanceof EnchantmentStorageMeta && b instanceof EnchantmentStorageMeta){
			if(!compare((EnchantmentStorageMeta)a, (EnchantmentStorageMeta)b)) return false;
		}
		if(a.hasEnchants()!=b.hasEnchants()) return false;
		return compare(a.getEnchants(), b.getEnchants());
	}
	public static boolean compare(EnchantmentStorageMeta a, EnchantmentStorageMeta b){
		return compare(a.getStoredEnchants(), b.getStoredEnchants());
	}
	public static boolean compare(Map<Enchantment,Integer> a, Map<Enchantment,Integer> b){
		if(a.size()!=b.size()) return false;
		for(Entry<Enchantment,Integer> entry : a.entrySet()){
			if(!b.containsKey(entry.getKey())) return false;
			if(entry.getValue()!=b.get(entry.getKey())) return false;
		}
		return true;
	}
}
