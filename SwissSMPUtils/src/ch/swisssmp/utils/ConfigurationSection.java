package ch.swisssmp.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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
			Material material = ingredientsSection.getMaterial(key);
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
			Material material = ingredientSection.getMaterial(key);
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
			Bukkit.getLogger().info("Unkown potion type "+typeName);
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
			boolean icon = this.getInt("icon")==1;
			return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
		}
		catch(Exception e){
			Bukkit.getLogger().info("Unkown potion type "+typeName);
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
		if(!this.configurationSection.contains(arg0)) return null;
		return this.getConfigurationSection(arg0).getLocation();
	}
	
	public Location getLocation(String arg0, World world){
		if(!this.configurationSection.contains(arg0)) return null;
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
	
	public Position getPosition(String arg0){
		if(!this.contains(arg0)) return null;
		return this.getConfigurationSection(arg0).getPosition();
	}
	
	public Position getPosition(){
		double x = this.getDouble("x");
		double y = this.getDouble("y");
		double z = this.getDouble("z");
		float yaw = (float)this.getDouble("yaw");
		float pitch = (float)this.getDouble("pitch");
		return new Position(x, y, z, yaw, pitch);
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
		String material_name = configurationSection.getString(arg0);
		try{
			return Material.valueOf(material_name);
		}
		catch(Exception e){
			return Material.matchMaterial(material_name);
		}
	}
	
	public String getName() {
		return configurationSection.getName();
	}
	
	public Enchantment getEnchantment(String arg0){
		String enchantString = this.getString(arg0);
		if(enchantString.equals("ANY")){
			return Enchantment.values()[ThreadLocalRandom.current().nextInt(Enchantment.values().length)];
		}
		//TODO check if this actually works
		return Enchantment.getByKey(NamespacedKey.minecraft(enchantString.toLowerCase()));
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
		return this.configurationSection.getVector(arg0);
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
		if(Base64.isBase64(this.getString(arg0))){
			return ItemUtil.deserialize(this.getString(arg0));
		}
		else return this.configurationSection.getItemStack(arg0);
	}
	
	public ItemStack[] getItemStacks(String arg0, int size){
		ConfigurationSection itemsSection = this.getConfigurationSection(arg0);
		if(itemsSection==null) return null;
		return itemsSection.getItemStacks(size);
	}
	
	public ItemStack[] getItemStacks(int size){
		ItemStack[] result = new ItemStack[size];
		ConfigurationSection itemSection;
		int slot;
		ItemStack itemStack;
		try{
			for(String key : this.getKeys(false)){
				itemSection = this.getConfigurationSection(key);
				slot = itemSection.getInt("slot");
				itemStack = itemSection.getItemStack("item");
				if(itemStack==null) continue;
				if(slot>=result.length) continue;
				result[slot] = itemStack;
			}
		}
		catch(Exception e){
			return null;
		}
		return result;
	}
	
	public void set(String arg0, ItemStack[] arg1){
		ConfigurationSection itemsSection = this.createSection(arg0);
		itemsSection.set("size", arg1.length);
		ConfigurationSection itemSection;
		for(int i = 0; i < arg1.length; i++){
			if(arg1[i]==null)continue;
			itemSection = itemsSection.createSection("item_"+i);
			itemSection.set("slot", i);
			itemSection.set("item", arg1[i]);
		}
	}
	
	public void set(String arg0, ItemStack arg1){
		String itemStackString = ItemUtil.serialize(arg1);
		this.set(arg0, itemStackString);
	}

	public void set(String arg0, Object arg1) {
		configurationSection.set(arg0, arg1);
	}

	public boolean isConfigurationSection(String arg0){
		if(!this.contains(arg0)) return false;
		return this.configurationSection.get(arg0) instanceof org.bukkit.configuration.ConfigurationSection;
	}
	
	public void remove(String arg0){
		this.configurationSection.set(arg0, null);
	}
	public JsonElement toJson() {
		if(this.configurationSection==null) return null;
		JsonObject result = new JsonObject();
		for(String key : this.configurationSection.getKeys(false)) {
			Object value = this.configurationSection.get(key);
			if(value instanceof ConfigurationSection) {
				JsonElement sectionJson = this.getConfigurationSection(key).toJson();
				if(sectionJson==null) continue;
				result.add(key, sectionJson);
				continue;
			}
			if(value instanceof String) {
				result.add(key, new JsonPrimitive((String) value));
				continue;
			}
			if(value instanceof Double) {
				result.add(key, new JsonPrimitive((Double) value));
				continue;
			}
			if(value instanceof Integer) {
				result.add(key, new JsonPrimitive((Integer) value));
				continue;
			}
			if(value instanceof Float) {
				result.add(key, new JsonPrimitive((Float) value));
				continue;
			}
			if(value instanceof Short) {
				result.add(key, new JsonPrimitive((Short) value));
				continue;
			}
			if(value instanceof Long) {
				result.add(key, new JsonPrimitive((Long) value));
				continue;
			}
			if(value instanceof Byte) {
				result.add(key, new JsonPrimitive((Byte) value));
				continue;
			}
			if(value instanceof Boolean) {
				result.add(key, new JsonPrimitive((Boolean) value));
				continue;
			}
			if(value instanceof Character) {
				result.add(key, new JsonPrimitive((Character) value));
				continue;
			}
			if(value instanceof Collection<?>) {
				List<String> stringList = ((Collection<?>) value).stream().map(e->e.toString()).collect(Collectors.toList());
				JsonArray jsonArray = new JsonArray();
				for(String s : stringList) {
					jsonArray.add(s);
				}
				result.add(key, jsonArray);
				continue;
			}
			result.add(key, new JsonPrimitive(value.toString()));
			
		}
		return result;
	}
}
