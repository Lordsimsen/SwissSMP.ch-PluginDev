package ch.swisssmp.customitems;

import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;

import java.util.Optional;

public class CustomItemTemplate implements IBuilderTemplate {
	
	private final NamespacedKey key;
	
	private JsonObject templateData;
	
	protected CustomItemTemplate(NamespacedKey key){
		this.key = key;
	}

	public NamespacedKey getKey(){
		return this.key;
	}
	
	protected static Optional<CustomItemTemplate> get(NamespacedKey key){
		return CustomItemTemplates.getTemplate(key);
	}

	@Override
	public JsonObject getData() {
		return templateData;
	}

	private void loadData(JsonObject json){
		this.templateData = json;
	}

	protected static Optional<CustomItemTemplate> load(JsonObject json){
		String customEnum = JsonUtil.getString("custom_enum", json);
		String source = JsonUtil.getString("source", json);
		if(customEnum==null || customEnum.isEmpty()) return Optional.empty();
		//noinspection deprecation
		NamespacedKey key = source!=null && !source.isEmpty()
				? new NamespacedKey(source.toLowerCase(), customEnum.toLowerCase())
				: NamespacedKey.minecraft(customEnum.toLowerCase());
		CustomItemTemplate result = new CustomItemTemplate(key);
		result.loadData(json);
		return Optional.of(result);
	}
}
