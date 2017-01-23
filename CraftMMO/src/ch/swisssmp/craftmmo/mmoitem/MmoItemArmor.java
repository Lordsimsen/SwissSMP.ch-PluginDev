package ch.swisssmp.craftmmo.mmoitem;

import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.NBTTagList;

public class MmoItemArmor extends MmoItem{

	public final EquipmentSlot equipmentSlot;
	public final int level;
	public final double strength;
	
	public MmoItemArmor(ConfigurationSection dataSection) throws Exception {
		super(dataSection);
		this.hideAttributes = false;
		//configurationSection cannot be null because this class is only used when there is a classSection in it
		ConfigurationSection configurationSection = dataSection.getConfigurationSection("configuration");
		ConfigurationSection classSection = configurationSection.getConfigurationSection("class");
		this.equipmentSlot = getEquipmentSlot(classSection.getString("part"));
		this.strength = classSection.getDouble("strength");
		this.level = classSection.getInt("level");
	}

	public double getStrength(){
		//MmoItemclass itemclass = this.getItemclass();
		return this.strength;
	}
	
	//overrides applyCustomData of MmoItem, Armor Stats
	@Override
	public ItemStack applyCustomData(ItemStack itemStack, boolean forceAll){
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.spigot().setUnbreakable(true);
		if(!itemMeta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)){
			itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		}
		itemStack.setItemMeta(itemMeta);
		double strength = this.getStrength();
		NBTTagCompound damageCompound = createAttribute(Attribute.GENERIC_ARMOR, strength, this.equipmentSlot);
		NBTTagList attributes = new NBTTagList();
		attributes.add(damageCompound);
		setAttributes(itemStack, attributes);
		return itemStack;
	}
	public static EquipmentSlot getEquipmentSlot(String string){
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
