package ch.swisssmp.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

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

	
	public Color getColor(String arg0, Color arg1) {
		return configurationSection.getColor(arg0, arg1);
	}

	
	public ConfigurationSection getConfigurationSection(String arg0) {
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
		return configurationSection.getItemStack(arg0);
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
