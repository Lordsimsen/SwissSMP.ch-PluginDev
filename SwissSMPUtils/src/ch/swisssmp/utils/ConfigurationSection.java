package ch.swisssmp.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import ch.swisssmp.webcore.WebCore;

public class ConfigurationSection{

	protected org.bukkit.configuration.ConfigurationSection configurationSection;
	
	protected ConfigurationSection(){
		
	}
	
	protected ConfigurationSection(org.bukkit.configuration.ConfigurationSection configurationSection){
		this.configurationSection = configurationSection;
	}
	
	public void addDefault(String arg0, Object arg1) {
		configurationSection.addDefault(arg0, arg1);
	}

	
	public boolean contains(String arg0) {
		return configurationSection.contains(arg0);
	}

	
	public boolean contains(String arg0, boolean arg1) {
		return configurationSection.contains(arg0, arg1);
	}

	
	public ConfigurationSection createSection(String arg0) {
		return new ConfigurationSection(configurationSection.createSection(arg0));
	}

	
	public ConfigurationSection createSection(String arg0, Map<?, ?> arg1) {
		return new ConfigurationSection(configurationSection.createSection(arg0, arg1));
	}

	
	public Object get(String arg0) {
		return configurationSection.get(arg0);
	}

	
	public boolean getBoolean(String arg0) {
		return configurationSection.getBoolean(arg0);
	}

	
	public boolean getBoolean(String arg0, boolean arg1) {
		return configurationSection.getBoolean(arg0, arg1);
	}

	
	public List<Boolean> getBooleanList(String arg0) {
		return configurationSection.getBooleanList(arg0);
	}

	
	public List<Byte> getByteList(String arg0) {
		return configurationSection.getByteList(arg0);
	}

	
	public List<Character> getCharacterList(String arg0) {
		return configurationSection.getCharacterList(arg0);
	}

	
	public Color getColor(String arg0) {
		return configurationSection.getColor(arg0);
	}
	
	public ShapedRecipe getShapedRecipe(String arg0){
		ConfigurationSection recipeSection = this.getConfigurationSection(arg0);
		ItemStack resultStack = recipeSection.getItemStack("result");
		ShapedRecipe result = new ShapedRecipe(new NamespacedKey(SwissSMPUtils.plugin, resultStack.getItemMeta().getDisplayName()), resultStack);
		List<String> shape = recipeSection.getStringList("shape");
		String[] shapeArray = new String[shape.size()];
		result.shape(shape.toArray(shapeArray));
		ConfigurationSection ingredientsSection = recipeSection.getConfigurationSection("ingredients");
		for(String key : ingredientsSection.getKeys(false)){
			MaterialData material = ingredientsSection.getMaterialData(key);
			result.setIngredient(key.toCharArray()[0], material);
		}
		return result;
	}

	public ShapelessRecipe getShapelessRecipe(String arg0){
		ConfigurationSection recipeSection = this.getConfigurationSection(arg0);
		ItemStack resultStack = recipeSection.getItemStack("result");
		ShapelessRecipe result = new ShapelessRecipe(new NamespacedKey(SwissSMPUtils.plugin, resultStack.getItemMeta().getDisplayName()), resultStack);
		ConfigurationSection ingredientsSection = recipeSection.getConfigurationSection("ingredients");
		for(String key : ingredientsSection.getKeys(false)){
			ConfigurationSection ingredientSection = ingredientsSection.getConfigurationSection(key);
			int count = ingredientSection.getInt("count");
			MaterialData material = ingredientSection.getMaterialData(key);
			result.addIngredient(count, material);
		}
		return result;
	}
	
	public ConfigurationSection getConfigurationSection(String arg0) {
		if(!configurationSection.contains(arg0)) return null;
		return new ConfigurationSection(configurationSection.getConfigurationSection(arg0));
	}

	
	public double getDouble(String arg0) {
		return configurationSection.getDouble(arg0);
	}

	
	public double getDouble(String arg0, double arg1) {
		return configurationSection.getDouble(arg0,arg1);
	}

	
	public List<Double> getDoubleList(String arg0) {
		return configurationSection.getDoubleList(arg0);
	}

	
	public List<Float> getFloatList(String arg0) {
		return configurationSection.getFloatList(arg0);
	}

	
	public int getInt(String arg0) {
		return configurationSection.getInt(arg0);
	}

	
	public int getInt(String arg0, int arg1) {
		return configurationSection.getInt(arg0, arg1);
	}

	
	public List<Integer> getIntegerList(String arg0) {
		return configurationSection.getIntegerList(arg0);
	}

	
	public ItemStack getItemStack(String arg0) {
		ConfigurationSection configurationSection = this.getConfigurationSection(arg0);
		MaterialData material = configurationSection.getMaterialData("material");
		int amount = configurationSection.getInt("amount");
		@SuppressWarnings("deprecation")
		ItemStack result = new ItemStack(material.getItemType(), amount, material.getData());
		if(configurationSection.contains("durability")){
			result.setDurability((short) configurationSection.getInt("durability"));
		}
		ItemMeta itemMeta = result.getItemMeta();
		if(configurationSection.contains("name")){
			String name = configurationSection.getString("name");
			itemMeta.setDisplayName(name);
		}
		if(configurationSection.contains("lore")){
			List<String> lore = configurationSection.getStringList("lore");
			itemMeta.setLore(lore);
		}
		if(configurationSection.contains("unbreakable")){
			itemMeta.setUnbreakable(configurationSection.getInt("unbreakable")==1);
		}
		if(configurationSection.contains("enchantments")){
			ConfigurationSection enchantmentsSection = configurationSection.getConfigurationSection("enchantments");
			for(String key : enchantmentsSection.getKeys(false)){
				ConfigurationSection enchantmentSection = enchantmentsSection.getConfigurationSection(key);
				String enchantmentName = enchantmentSection.getString("enchantment");
				try{
					Enchantment enchantment = Enchantment.getByName(enchantmentName);
					int level = enchantmentSection.getInt("level");
					itemMeta.addEnchant(enchantment, level, true);
				}
				catch(Exception e){
					WebCore.debug("Unkown enchantment "+enchantmentName);
				}
			}
		}
		if(configurationSection.contains("flags")){
			ConfigurationSection flagsSection = configurationSection.getConfigurationSection("flags");
			for(String flag : flagsSection.getKeys(false)){
				try{
					itemMeta.addItemFlags(ItemFlag.valueOf(flag));
				}
				catch(Exception e){
					WebCore.debug("Unkown item flag "+flag);
				}
			}
		}
		if(configurationSection.contains("potion") && itemMeta instanceof PotionMeta){
			ConfigurationSection potionSection = configurationSection.getConfigurationSection("potion");
			PotionMeta potionMeta = (PotionMeta) itemMeta;
			PotionData base = potionSection.getPotionData("base");
			if(base!=null){
				potionMeta.setBasePotionData(base);
			}
			if(potionSection.contains("color")){
				Color color = potionSection.getColor("color");
				potionMeta.setColor(color);
			}
			if(potionSection.contains("custom")){
				ConfigurationSection customsSection = potionSection.getConfigurationSection("custom");
				for(String key : customsSection.getKeys(false)){
					PotionEffect potionEffect = customsSection.getPotionEffect(key);
					potionMeta.addCustomEffect(potionEffect, true);
				}
			}
		}
		result.setItemMeta(itemMeta);
		return result;
	}

	public PotionData getPotionData(String arg0){
		ConfigurationSection potionSection = this.getConfigurationSection(arg0);
		String typeName = potionSection.getString("type");
		try{
			PotionType type = PotionType.valueOf(typeName);
			boolean extended = potionSection.getInt("extended")==1;
			boolean upgraded = potionSection.getInt("upgraded")==1;
			return new PotionData(type, extended, upgraded);
		}
		catch(Exception e){
			WebCore.debug("Unkown potion type "+typeName);
			return null;
		}
	}
	
	public PotionEffect getPotionEffect(String arg0){
		ConfigurationSection potionSection = this.getConfigurationSection(arg0);
		String typeName = potionSection.getString("type");
		try{
			PotionEffectType type = PotionEffectType.getByName(typeName);
			int duration = potionSection.getInt("duration");
			int amplifier = potionSection.getInt("amplifier");
			boolean ambient = potionSection.getInt("ambient")==1;
			boolean particles = potionSection.getInt("particles")==1;
			Color color = potionSection.getColor("color");
			return new PotionEffect(type, duration, amplifier, ambient, particles, color);
		}
		catch(Exception e){
			WebCore.debug("Unkown potion type "+typeName);
			return null;
		}
	}
	
	public Set<String> getKeys(boolean arg0) {
		return configurationSection.getKeys(arg0);
	}

	
	public List<?> getList(String arg0) {
		return configurationSection.getList(arg0);
	}
	
	public Location getLocation(String arg0){
		ConfigurationSection configurationSection = this.getConfigurationSection(arg0);
		String worldName = configurationSection.getString("world");
		double x = configurationSection.getDouble("x");
		double y = configurationSection.getDouble("y");
		double z = configurationSection.getDouble("z");
		World world = Bukkit.getWorld(worldName);
		if(world==null) return null;
		return new Location(world, x, y, z);
	}
	
	public long getLong(String arg0) {
		return configurationSection.getLong(arg0);
	}

	
	public List<Long> getLongList(String arg0) {
		return configurationSection.getLongList(arg0);
	}

	public Material getMaterial(String arg0){
		try{
			return Material.valueOf(configurationSection.getString(arg0));
		}
		catch(Exception e){
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	public MaterialData getMaterialData(String arg0){
		try{
			String[] parts = this.configurationSection.getString(arg0).split(":");
			Material material = Material.valueOf(parts[0]);
			byte data = 0;
			if(parts.length>1) data = Byte.valueOf(parts[1]);
			return new MaterialData(material, data);
		}
		catch(Exception e){
			return null;
		}
	}
	
	public String getName() {
		return configurationSection.getName();
	}
	
	public RandomizedLocation getRandomizedLocation(String arg0){
		ConfigurationSection configurationSection = this.getConfigurationSection(arg0);
		String worldName = configurationSection.getString("world");
		double x = configurationSection.getDouble("x");
		double y = configurationSection.getDouble("y");
		double z = configurationSection.getDouble("z");
		double range = configurationSection.getDouble("range");
		World world = Bukkit.getWorld(worldName);
		if(world==null) return null;
		return new RandomizedLocation(new Location(world, x, y, z), range);
	}

	
	public List<Short> getShortList(String arg0) {
		return configurationSection.getShortList(arg0);
	}

	
	public String getString(String arg0) {
		return configurationSection.getString(arg0);
	}

	
	public List<String> getStringList(String arg0) {
		return configurationSection.getStringList(arg0);
	}

	
	public Map<String, Object> getValues(boolean arg0) {
		return configurationSection.getValues(arg0);
	}

	
	public Vector getVector(String arg0) {
		return configurationSection.getVector(arg0);
	}

	
	public void set(String arg0, Object arg1) {
		configurationSection.set(arg0, arg1);
	}

}
