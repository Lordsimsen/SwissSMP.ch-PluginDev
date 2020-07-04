package ch.swisssmp.customitems;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;

public class CustomItemTemplate implements IBuilderTemplate {
	
	private final String customEnum;
	
	private JsonObject templateData;
	
	protected CustomItemTemplate(JsonObject json){
		this.customEnum = JsonUtil.getString("custom_enum", json);
		this.templateData = json;
	}
	
	public String getCustomEnum(){
		return this.customEnum;
	}
	
	public static CustomItemTemplate get(String customEnum){
		return CustomItemTemplates.templates.get(customEnum.toLowerCase());
	}

	@Override
	public JsonObject getData() {
		return templateData;
	}
}
