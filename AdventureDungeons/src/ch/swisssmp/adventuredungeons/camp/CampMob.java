package ch.swisssmp.adventuredungeons.camp;

import org.bukkit.entity.EntityType;

import ch.swisssmp.utils.ConfigurationSection;

public class CampMob {
	
	private final EntityType entityType;
	private final String customName;
	private final int amount;
	
	private CampMob(EntityType entityType, String customName, int amount){
		this.entityType = entityType;
		this.customName = customName;
		this.amount = amount;
	}
	
	public EntityType getEntityType(){
		return this.entityType;
	}
	
	public boolean hasCustomName(){
		return (this.customName!=null && !this.customName.isEmpty());
	}
	
	public String getCustomName(){
		return this.customName;
	}
	
	public int getAmount(){
		return this.amount;
	}
	
	protected static CampMob get(ConfigurationSection dataSection){
		try{
			EntityType entityType = EntityType.valueOf(dataSection.getString("entity_type"));
			String customName = dataSection.getString("custom_name");
			int amount = dataSection.getInt("amount");
			return new CampMob(entityType, customName, amount);
		}
		catch(Exception e){
			return null;
		}
	}
}
