package ch.swisssmp.craftmmo.mmoitem;

import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.craftmmo.Main;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.NBTTagList;

public class MmoArmor extends MmoItem{

	//public final float armorFactor;
	public final float baseArmor;
	public final EquipmentSlot equipmentSlot;
	
	protected MmoArmor(ConfigurationSection dataSection) throws Exception {
		super(dataSection);
		ConfigurationSection configurationSection = dataSection.getConfigurationSection("configuration");
		if(configurationSection!=null){
			ConfigurationSection classSection = configurationSection.getConfigurationSection("class");
			//this.armorFactor = (float)classSection.getDouble("factor");
			this.baseArmor = (float)classSection.getDouble("strength");
			this.equipmentSlot = getEquipmentSlot(classSection.getString("part"));
			
		}
		else{
			this.baseArmor = 0;
			this.equipmentSlot = null;
			Main.debug("Warning: armor with mmo_item_id "+this.mmo_item_id+" has invalid armor configuration.");
		}
	}
	
	//overrides applyCustomData of MmoItem; Sets the armor attribute
	@Override
	public ItemStack applyCustomData(ItemStack itemStack, boolean forceAll){
		NBTTagCompound damageCompound = createAttribute(Attribute.GENERIC_ARMOR, this.baseArmor, this.equipmentSlot);
		NBTTagList attributes = new NBTTagList();
		attributes.add(damageCompound);
		setAttributes(itemStack, attributes);
		return itemStack;
	}
	private static EquipmentSlot getEquipmentSlot(String string){
		switch(string.toUpperCase()){
		case "HELMET":
		case "HEAD":
			return EquipmentSlot.HEAD;
		case "CHESTPLATE":
		case "CHEST":
			return EquipmentSlot.CHEST;
		case "LEGGINGS":
		case "LEGS":
			return EquipmentSlot.LEGS;
		case "BOOTS":
		case "FEET":
			return EquipmentSlot.FEET;
		default:
			return null;
		}
	}
}
