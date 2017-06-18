package ch.swisssmp.craftmmo.mmoitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.craftmmo.mmoattribute.IDurable;
import ch.swisssmp.craftmmo.mmoattribute.MmoDurability;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;

public abstract class MmoPhysicalWeapon extends MmoItem implements IDurable{
	public final String subclass_enum;
	public final int level;
	public final Double damage;
	public final Double speed;
	public final HashMap<MmoDurability, Integer> durabilityMap;

	public MmoPhysicalWeapon(ConfigurationSection dataSection) throws Exception {
		super(dataSection);
		this.hideAttributes = false;
		//configurationSection cannot be null because this class is only used when there is a classSection in it
		ConfigurationSection configurationSection = dataSection.getConfigurationSection("configuration");
		ConfigurationSection durabilitySection = configurationSection.getConfigurationSection("durability");
		this.durabilityMap = new HashMap<MmoDurability, Integer>();
		if(durabilitySection!=null){
			for(String sharpnessLevel : durabilitySection.getKeys(false)){
				MmoDurability durabilityLevel = MmoDurability.valueOf(sharpnessLevel);
				int sharpness_durability = durabilitySection.getInt(sharpnessLevel);
				this.durabilityMap.put(durabilityLevel, sharpness_durability);
			}
		}
		ConfigurationSection classSection = configurationSection.getConfigurationSection("class");
		this.subclass_enum = classSection.getString("type");
		this.level = classSection.getInt("level");
		this.damage = classSection.getDouble("strength");
		this.speed = classSection.getDouble("speed");
	}
	
	
	@Override
	protected List<String> getItemclassData(ItemStack itemStack){
		List<String> result = new ArrayList<String>();
		MmoItemSubclass subclass = this.getItemclass().getSubclass(this.subclass_enum);
		if(subclass!=null){
			result.add(loreHeader(subclass.subclass_name));
			result.add(ChatColor.WHITE+"Schaden: "+this.getDamage());
			result.add(ChatColor.WHITE+"Tempo: "+this.getSpeed());
		}
		result.add(IDurable.getDurabilityBar(itemStack));
		return result;
	}
	
	public MmoItemSubclass getSubclass(){
		MmoItemclass itemclass = this.getItemclass();
		if(itemclass==null) return null;
		return itemclass.getSubclass(subclass_enum);
	}
	
	@Override
	public double getDamage(){
		MmoItemSubclass subclass = this.getSubclass();
		if(subclass==null) return 0;
		return this.damage*subclass.subclass_strength;
	}

	@Override
	public double getSpeed(){
		MmoItemSubclass subclass = this.getSubclass();
		if(subclass==null) return 0;
		return subclass.subclass_speed;
	}
	
	//overrides applyCustomData of MmoItem, adds Durability Bar and Weapon Stats
	@Override
	public ItemStack applyCustomData(ItemStack itemStack, boolean forceAll){
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.spigot().setUnbreakable(true);
		if(!itemMeta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)){
			itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		}
		itemStack.setItemMeta(itemMeta);
		if(forceAll){
			IDurable.setDurability(itemStack, this.getTotalDurability());
		}
		double damage = this.getDamage();
		double speed = this.getSpeed();
		NBTTagCompound damageCompound = createAttribute(Attribute.GENERIC_ATTACK_DAMAGE, damage, EquipmentSlot.HAND);
		NBTTagCompound speedCompound = createAttribute(Attribute.GENERIC_ATTACK_SPEED, speed, EquipmentSlot.HAND);
		NBTTagList attributes = new NBTTagList();
		attributes.add(damageCompound);
		attributes.add(speedCompound);
		setAttributes(itemStack, attributes);
		return itemStack;
	}
	
	public int getTotalDurability(){
		int result = 0;
		for(Integer durability : durabilityMap.values()){
			result+=durability;
		}
		return result;
	}

	//overrides applyCustomSaveData of MmoItem; Adds an NBT tag to the compound
	@Override
	protected NBTTagCompound applyCustomSaveData(NBTTagCompound nbttagcompound){
		nbttagcompound.setInt("durability", getTotalDurability());
		return nbttagcompound;
	}
	
	public static MmoPhysicalWeapon get(ItemStack itemStack){
		MmoItem mmoItem = MmoItem.get(itemStack);
		if(mmoItem==null)
			return null;
		if(!(mmoItem instanceof MmoPhysicalWeapon)){
			return null;
		}
		return (MmoPhysicalWeapon)mmoItem;
	}
	@Override
	public HashMap<MmoDurability, Integer> getDurabilityMap() {
		return this.durabilityMap;
	}
	@Override
	public String getDurabilityLabel() {
		return "Schärfe";
	}
}
