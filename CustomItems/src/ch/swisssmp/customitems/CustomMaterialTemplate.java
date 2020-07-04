package ch.swisssmp.customitems;

import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.Material;

import ch.swisssmp.utils.ConfigurationSection;

public class CustomMaterialTemplate implements IBuilderTemplate {

	private final String customEnum;
	private final Material material;
	private final short durability;
	private final int customModelId;
	private final boolean useCustomModelDataProperty;
	
	private final JsonObject templateData;
	
	protected CustomMaterialTemplate(JsonObject json){
		this.customEnum = JsonUtil.getString("custom_enum", json);
		this.material = JsonUtil.getMaterial("material", json);
		this.durability = JsonUtil.getShort("durability", json);
		this.customModelId = JsonUtil.getInt("custom_model_id", json);
		this.useCustomModelDataProperty = JsonUtil.getBool("use_custom_model_data_property", json);
		this.templateData = json;
	}
	
	public String getCustomEnum(){
		return this.customEnum;
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
	
	public static CustomMaterialTemplate get(String customEnum){
		return CustomMaterialTemplates.templates.get(customEnum.toLowerCase());
	}

	@Override
	public JsonObject getData() {
		return templateData;
	}
}
