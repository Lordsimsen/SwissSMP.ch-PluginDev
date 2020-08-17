package ch.swisssmp.customitems;

import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.Material;

import ch.swisssmp.utils.ConfigurationSection;
import org.bukkit.NamespacedKey;

import java.util.Optional;

public class CustomMaterialTemplate implements IBuilderTemplate {

	private final NamespacedKey key;
	private Material material;
	private short durability;
	private int customModelId;
	private boolean useCustomModelDataProperty;
	
	private JsonObject templateData;
	
	private CustomMaterialTemplate(NamespacedKey key){
		this.key = key;
	}
	
	public NamespacedKey getKey(){
		return this.key;
	}
	
	public Material getMaterial(){
		return this.material;
	}
	
	public short getDurability(){
		return durability;
	}
	
	public int getCustomModelId() {
		return customModelId;
	}
	
	public boolean useCustomModelDataProperty() {
		return this.useCustomModelDataProperty;
	}
	
	protected static Optional<CustomMaterialTemplate> get(NamespacedKey key){
		return CustomMaterialTemplates.getTemplate(key);
	}

	@Override
	public JsonObject getData() {
		return templateData;
	}

	private void loadData(JsonObject json){
		this.material = JsonUtil.getMaterial("material", json);
		this.durability = JsonUtil.getShort("durability", json);
		this.customModelId = JsonUtil.getInt("custom_model_id", json);
		this.useCustomModelDataProperty = JsonUtil.getBool("use_custom_model_data_property", json);
		this.templateData = json;
	}

	protected static Optional<CustomMaterialTemplate> load(JsonObject json){
		String customEnum = JsonUtil.getString("custom_enum", json);
		String source = JsonUtil.getString("source", json);
		if(customEnum==null || customEnum.isEmpty()) return Optional.empty();
		//noinspection deprecation
		NamespacedKey key = source!=null && !source.isEmpty()
				? new NamespacedKey(source.toLowerCase(), customEnum.toLowerCase())
				: NamespacedKey.minecraft(customEnum.toLowerCase());
		CustomMaterialTemplate result = new CustomMaterialTemplate(key);
		result.loadData(json);
		return Optional.of(result);
	}
}
