package ch.swisssmp.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
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
	
	public ShapedRecipe getShapedRecipe(String arg0, ItemStack resultStack){
		return this.getConfigurationSection(arg0).getShapedRecipe(resultStack);
	}
	
	public ShapedRecipe getShapedRecipe(ItemStack resultStack){
		ShapedRecipe result = new ShapedRecipe(new NamespacedKey(SwissSMPUtils.plugin, resultStack.getItemMeta().getDisplayName()), resultStack);
		List<String> shape = this.getStringList("shape");
		String[] shapeArray = new String[shape.size()];
		result.shape(shape.toArray(shapeArray));
		ConfigurationSection ingredientsSection = this.getConfigurationSection("ingredients");
		for(String key : ingredientsSection.getKeys(false)){
			MaterialData material = ingredientsSection.getMaterialData(key);
			result.setIngredient(key.toCharArray()[0], material);
		}
		return result;
	}
	
	public ShapelessRecipe getShapelessRecipe(String arg0, ItemStack resultStack){
		return this.getConfigurationSection(arg0).getShapelessRecipe(resultStack);
		
	}

	public ShapelessRecipe getShapelessRecipe(ItemStack resultStack){
		ShapelessRecipe result = new ShapelessRecipe(new NamespacedKey(SwissSMPUtils.plugin, resultStack.getItemMeta().getDisplayName()), resultStack);
		ConfigurationSection ingredientsSection = this.getConfigurationSection("ingredients");
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
		if(this.isConfigurationSection(arg0)){
			org.bukkit.configuration.ConfigurationSection dataSection = configurationSection.getConfigurationSection(arg0);
			return ThreadLocalRandom.current().nextDouble(dataSection.getDouble("min"),dataSection.getDouble("max"));
		}
		else{
			return configurationSection.getDouble(arg0);
		}
	}
	
	public List<Double> getDoubleList(String arg0) {
		return configurationSection.getDoubleList(arg0);
	}

	
	public List<Float> getFloatList(String arg0) {
		return configurationSection.getFloatList(arg0);
	}

	public int getInt(String arg0) {
		if(this.isConfigurationSection(arg0)){
			org.bukkit.configuration.ConfigurationSection dataSection = configurationSection.getConfigurationSection(arg0);
			return ThreadLocalRandom.current().nextInt(dataSection.getInt("min"),dataSection.getInt("max")+1);
		}
		else{
			return configurationSection.getInt(arg0);
		}
	}
	
	public List<Integer> getIntegerList(String arg0) {
		return configurationSection.getIntegerList(arg0);
	}

	public PotionData getPotionData(String arg0){
		return this.getConfigurationSection(arg0).getPotionData();
	}
	
	public PotionData getPotionData(){
		String typeName = this.getString("type");
		try{
			PotionType type = PotionType.valueOf(typeName);
			boolean extended = this.getInt("extended")==1;
			boolean upgraded = this.getInt("upgraded")==1;
			return new PotionData(type, extended, upgraded);
		}
		catch(Exception e){
			WebCore.debug("Unkown potion type "+typeName);
			return null;
		}
	}
	
	public PotionEffect getPotionEffect(String arg0){
		return this.getConfigurationSection(arg0).getPotionEffect();
	}
	
	public PotionEffect getPotionEffect(){
		String typeName = this.getString("type");
		try{
			PotionEffectType type = PotionEffectType.getByName(typeName);
			int duration = this.getInt("duration");
			int amplifier = this.getInt("amplifier");
			boolean ambient = this.getInt("ambient")==1;
			boolean particles = this.getInt("particles")==1;
			Color color = this.getColor("color");
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
		return this.getConfigurationSection(arg0).getLocation();
	}
	
	public Location getLocation(String arg0, World world){
		return this.getConfigurationSection(arg0).getLocation(world);
	}
	
	public Location getLocation(){
		String worldName = this.getString("world");
		World world = Bukkit.getWorld(worldName);
		return this.getLocation(world);
	}
	
	public Location getLocation(World world){
		if(world==null) return null;
		double x = this.getDouble("x");
		double y = this.getDouble("y");
		double z = this.getDouble("z");
		float yaw = (float)this.getDouble("yaw");
		float pitch = (float)this.getDouble("pitch");
		return new Location(world, x, y, z, yaw, pitch);
	}

	public long getLong(String arg0) {
		if(this.isConfigurationSection(arg0)){
			org.bukkit.configuration.ConfigurationSection dataSection = configurationSection.getConfigurationSection(arg0);
			return ThreadLocalRandom.current().nextLong(dataSection.getLong("min"),dataSection.getLong("max")+1);
		}
		else{
			return configurationSection.getLong(arg0);
		}
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
		return this.getConfigurationSection(arg0).getRandomizedLocation();
	}
	
	public RandomizedLocation getRandomizedLocation(){
		String worldName = this.getString("world");
		double x = this.getDouble("x");
		double y = this.getDouble("y");
		double z = this.getDouble("z");
		float pitch = (float)this.getDouble("pitch");
		float yaw = (float)this.getDouble("yaw");
		double range = this.getDouble("range");
		World world = Bukkit.getWorld(worldName);
		return new RandomizedLocation(world, x, y, z, yaw, pitch, range);
	}
	
	public Enchantment getEnchantment(String arg0){
		String enchantString = this.getString(arg0);
		if(enchantString.equals("ANY")){
			return Enchantment.values()[ThreadLocalRandom.current().nextInt(Enchantment.values().length)];
		}
		return Enchantment.getByName(enchantString);
	}
	
	public EnchantmentData getEnchantmentData(String arg0){
		return this.getConfigurationSection(arg0).getEnchantmentData();
	}
	
	public EnchantmentData getEnchantmentData(){
		Enchantment enchantment = this.getEnchantment("enchantment");
		if(enchantment==null) return null;
		int level = this.getInt("level");
		boolean ignoreLevelRestriction = this.getBoolean("ignore_level_restriction");
		if(!ignoreLevelRestriction) level = Math.min(level, enchantment.getMaxLevel());
		return new EnchantmentData(enchantment, level, ignoreLevelRestriction);
	}
	
	public ItemFlag getItemFlag(String arg0){
		try{
			ItemFlag result = ItemFlag.valueOf(this.getString(arg0));
			return result;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
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
		return this.getConfigurationSection(arg0).getVector();
	}
	
	public Vector getVector(){
		return new Vector(this.getDouble("x"), this.getDouble("y"), this.getDouble("z"));
	}

	public VectorKey getVectorKey(String arg0) {
		return new VectorKey(this.getConfigurationSection(arg0).getVector());
	}
	
	public VectorKey getVectorKey(){
		return new VectorKey(new Vector(this.getDouble("x"), this.getDouble("y"), this.getDouble("z")));
	}
	
	public ItemStack getItemStack(String arg0){
		org.bukkit.configuration.file.YamlConfiguration yamlConfiguration = new YamlConfiguration();
		try {
			yamlConfiguration.loadFromString(new String(Base64.decodeBase64(this.getString(arg0))));
			return yamlConfiguration.getItemStack("item");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void set(String arg0, Object arg1) {
		configurationSection.set(arg0, arg1);
	}

	public boolean isConfigurationSection(String arg0){
		if(!this.contains(arg0)) return false;
		return this.configurationSection.get(arg0) instanceof org.bukkit.configuration.ConfigurationSection;
	}
}
