package ch.swisssmp.utils;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import ch.swisssmp.webcore.RequestMethod;

public class CurrencyInfo {
	private static final HashMap<String,CurrencyInfo> loadedCurrencies = new HashMap<String,CurrencyInfo>();
	
	private final String name;
	private final String currency_enum;
	private final String description;
	private final CustomItemBuilder itemBuilder;
	
	private CurrencyInfo(ConfigurationSection dataSection){
		this.name = dataSection.getString("name");
		this.currency_enum = dataSection.getString("currency_enum");
		this.description = dataSection.getString("description");
		this.itemBuilder = CustomItems.getCustomItemBuilder(this.currency_enum);
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getCurrencyType(){
		return this.currency_enum;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public ItemStack getItem(int amount){
		if(this.itemBuilder==null) return null;
		this.itemBuilder.setAmount(amount);
		return this.itemBuilder.build();
	}
	
	public static CurrencyInfo get(ItemStack itemStack) {
		String customEnum = CustomItems.getCustomEnum(itemStack);
		return customEnum!=null ? get(customEnum) : null;
	}
	
	protected static CurrencyInfo get(String currencyType){
		if(currencyType==null) return null;
		for(String key : loadedCurrencies.keySet()){
			if(key.toLowerCase().startsWith(currencyType.toLowerCase())) return loadedCurrencies.get(key);
		}
		HTTPRequest request = DataSource.getResponse(EventPoints.getInstance(), "get_currency_info.php", new String[]{
				"currency="+URLEncoder.encode(currencyType)
		}, RequestMethod.POST_SYNC);
		YamlConfiguration yamlConfiguration = request.getYamlResponse();
		if(yamlConfiguration==null || !yamlConfiguration.contains("info")) return null;
		CurrencyInfo result = new CurrencyInfo(yamlConfiguration.getConfigurationSection("info"));
		loadedCurrencies.put(result.name.toLowerCase(), result);
		loadedCurrencies.put(result.currency_enum.toLowerCase(), result);
		return result;
	}
	
	protected static void clear() {
		loadedCurrencies.clear();
	}

	public static Collection<CurrencyInfo> getAll(){
		return loadedCurrencies.values();
	}
}
