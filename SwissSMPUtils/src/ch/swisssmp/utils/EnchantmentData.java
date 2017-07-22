package ch.swisssmp.utils;

import org.bukkit.enchantments.Enchantment;

public class EnchantmentData {
	private final Enchantment enchantment;
	private final int level;
	private final boolean ignoreLevelRestriction;
	public EnchantmentData(Enchantment enchantment, int level, boolean ignoreLevelRestriction){
		this.enchantment = enchantment;
		this.level = level;
		this.ignoreLevelRestriction = ignoreLevelRestriction;
	}
	public Enchantment getEnchantment(){
		return this.enchantment;
	}
	public int getLevel(){
		return this.level;
	}
	public boolean getIgnoreLevelRestriction(){
		return this.ignoreLevelRestriction;
	}
}
