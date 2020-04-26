package ch.swisssmp.customitems;

import org.bukkit.Material;

import ch.swisssmp.utils.ConfigurationSection;

public class CustomMaterialTemplate implements IBuilderTemplate {

	private final String customEnum;
	private final Material material;
	private final short durability;
	private final int customModelId;
	private final boolean useCustomModelDataProperty;
	
	private ConfigurationSection templateData;
	
	protected CustomMaterialTemplate(ConfigurationSection dataSection){
		this.customEnum = dataSection.getString("custom_enum");
		this.material = dataSection.getMaterial("material");
		this.durability = (short) dataSection.getInt("durability");
		this.customModelId = dataSection.getInt("custom_model_id");
		this.useCustomModelDataProperty = dataSection.getBoolean("use_custom_model_data_property", false);
		this.templateData = dataSection;
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
	public ConfigurationSection getData() {
		return templateData;
	}
}
