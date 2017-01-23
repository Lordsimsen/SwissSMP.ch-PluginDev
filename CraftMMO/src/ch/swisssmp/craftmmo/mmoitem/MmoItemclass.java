package ch.swisssmp.craftmmo.mmoitem;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.util.MmoResourceManager;

public class MmoItemclass {
	private static HashMap<Integer, MmoItemclass> itemclasses = new HashMap<Integer, MmoItemclass>();
	
	public final int mmo_itemclass_id;
	private Class<? extends MmoItem> classReference;
	public final String name;
	
	private final HashMap<String, MmoItemSubclass> subclasses = new HashMap<String, MmoItemSubclass>();
	
	@SuppressWarnings("unchecked")
	public MmoItemclass(ConfigurationSection dataSection){
		this.mmo_itemclass_id = dataSection.getInt("mmo_itemclass_id");
		this.name = dataSection.getString("name");
		try {
			this.classReference = (Class<? extends MmoItem>) Class.forName(dataSection.getString("class"));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		ConfigurationSection subclassesSection = dataSection.getConfigurationSection("subclasses");
		if(subclassesSection!=null){
			for(String key : subclassesSection.getKeys(false)){
				ConfigurationSection subclassSection = subclassesSection.getConfigurationSection(key);
				MmoItemSubclass subclass = new MmoItemSubclass(subclassSection);
				subclasses.put(subclass.subclass_enum, subclass);
			}
		}
		itemclasses.put(mmo_itemclass_id, this);
	}
	public MmoItemSubclass getSubclass(String subclass_enum){
		MmoItemSubclass result = subclasses.get(subclass_enum);
		if(result==null) return null;
		return result;
	}
	public MmoItem createItem(ConfigurationSection dataSection){
		if(classReference!=null){
			try {
				return classReference.getConstructor(ConfigurationSection.class).newInstance(dataSection);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
		else{
			Main.debug("No class reference in itemclass "+this.name);
		}
		return null;
	}
	public synchronized static void loadClasses(boolean fullload) throws Exception{
		itemclasses = new HashMap<Integer, MmoItemclass>();
		
		YamlConfiguration yamlConfiguration = MmoResourceManager.getYamlResponse("itemclasses.php");
		for(String key : yamlConfiguration.getKeys(false)){
			ConfigurationSection dataSection = yamlConfiguration.getConfigurationSection(key);
			new MmoItemclass(dataSection);
		}
		MmoItem.loadItems(fullload);
	}
	public static MmoItemclass get(int mmo_itemclass_id){
		return itemclasses.get(mmo_itemclass_id);
	}
}