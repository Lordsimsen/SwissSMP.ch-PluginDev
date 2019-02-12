package ch.swisssmp.customitems;

import ch.swisssmp.utils.ConfigurationSection;

public class CustomItemTemplate implements IBuilderTemplate {
	
	private final String customEnum;
	
	private ConfigurationSection templateData;
	
	protected CustomItemTemplate(ConfigurationSection dataSection){
		this.customEnum = dataSection.getString("custom_enum");
		this.templateData = dataSection;
	}
	
	public String getCustomEnum(){
		return this.customEnum;
	}
	
	public static CustomItemTemplate get(String customEnum){
		return CustomItemTemplates.templates.get(customEnum.toLowerCase());
	}

	@Override
	public ConfigurationSection getData() {
		return templateData;
	}
}
