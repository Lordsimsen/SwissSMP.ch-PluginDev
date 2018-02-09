package ch.swisssmp.customitems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import ch.swisssmp.utils.EnchantmentData;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;

public class CustomItemBuilder {
	//itemStack
	private Material material;
	private int amount;
	private short durability;
	
	//itemMeta
	private String displayName = "";
	private String localizedName = "";
	private ArrayList<EnchantmentData> enchantments = new ArrayList<EnchantmentData>();
	private ArrayList<ItemFlag> itemFlags = new ArrayList<ItemFlag>();
	private List<String> lore = new ArrayList<String>();
	private boolean unbreakable = false;
	private String skullOwner = "";
	
	//nms stuff
	private boolean useNMS = false;
	
	private String customEnum = "";
	private int item_id = 0;
	private int maxCustomDurability = 0;
	private int customDurability = 0;
	
	private double attackDamage = -1;
	private double attackSpeed = -1f;
	private double maxHealth = -1f;
	private double armor = -1f;
	private double movementSpeed = -1f;
	private double luck = -1f;
	
	private int customPotionColor = -1;
	private int colorMap = -1;
	
	private String slot = "mainhand";
	
	public CustomItemBuilder(){
	}
	public void setMaterial(Material material){
		this.material = material;
	}
	public void setAmount(int amount){
		this.amount = amount;
	}
	public void setDurability(short durability){
		this.durability = durability;
	}
	public void setMaxCustomDurability(int maxCustomDurability){
		this.useNMS = true;
		this.maxCustomDurability = maxCustomDurability;
	}
	public void setCustomDurability(int customDurability){
		this.customDurability = customDurability;
	}
	public void addEnchantments(List<EnchantmentData> enchantments){
		this.enchantments.addAll(enchantments);
	}
	public void addEnchantment(EnchantmentData enchantmentData){
		this.enchantments.add(enchantmentData);
	}
	public void addEnchantment(Enchantment enchantment, int level, boolean ignoreLevelRestriction){
		enchantments.add(new EnchantmentData(enchantment, level, ignoreLevelRestriction));
	}
	public void addItemFlags(List<ItemFlag> itemFlags){
		itemFlags.addAll(itemFlags);
	}
	public void addItemFlags(ItemFlag... itemFlags){
		for(int i = 0; i < itemFlags.length; i++){
			this.itemFlags.add(itemFlags[i]);
		}
	}
	public void setDisplayName(String displayName){
		this.displayName = displayName;
	}
	public void setLocalizedName(String localizedName){
		this.localizedName = localizedName;
	}
	public void setLore(List<String> lore){
		this.lore = lore;
	}
	public void setUnbreakable(boolean unbreakable){
		this.unbreakable = unbreakable;
	}
	public void setSkullOwner(String owner){
		this.skullOwner = owner;
	}
	public void setCustomEnum(String customEnum){
		this.useNMS = true;
		this.customEnum = customEnum;
		if(!this.itemFlags.contains(ItemFlag.HIDE_UNBREAKABLE)){
			this.itemFlags.add(ItemFlag.HIDE_UNBREAKABLE);
		}
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("items/material_builder.php", new String[]{
				"enum="+this.customEnum
		});
		if(yamlConfiguration!=null){
			Material material = yamlConfiguration.getMaterial("material");
			if(material!=null) this.setMaterial(material);
			this.setDurability((short)yamlConfiguration.getInt("durability"));
		}
		this.unbreakable = true;
	}
	public void setItemId(int item_id){
		this.useNMS = true;
		this.item_id = item_id;
	}
	public void setAttackDamage(double attackDamage){
		this.useNMS = true;
		this.attackDamage = attackDamage;
	}
	public void setAttackSpeed(double attackSpeed){
		this.useNMS = true;
		this.attackSpeed = attackSpeed;
	}
	public void setMaxHealth(double maxHealth){
		this.useNMS = true;
		this.maxHealth = maxHealth;
	}
	public void setArmor(double armor){
		this.useNMS = true;
		this.armor = armor;
	}
	public void setMovementSpeed(double movementSpeed){
		this.useNMS = true;
		this.movementSpeed = movementSpeed;
	}
	public void setLuck(double luck){
		this.useNMS = true;
		this.luck = luck;
	}
	public void setCustomPotionColor(int customPotionColor){
		this.useNMS = true;
		this.customPotionColor = customPotionColor;
	}
	/**
	 * To check validity
	 * @return
	 */
	public Material getMaterial(){
		return this.material;
	}
	private ItemMeta buildItemMeta(ItemStack itemStack){
		ItemMeta itemMeta = itemStack.getItemMeta();
		if(itemMeta instanceof EnchantmentStorageMeta){
			EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) itemMeta;
			for(EnchantmentData enchantmentData : this.enchantments){
				if(enchantmentStorageMeta.hasStoredEnchant(enchantmentData.getEnchantment())){
					enchantmentStorageMeta.removeStoredEnchant(enchantmentData.getEnchantment());
				}
				enchantmentStorageMeta.addStoredEnchant(enchantmentData.getEnchantment(), enchantmentData.getLevel(), enchantmentData.getIgnoreLevelRestriction());
			}
		}
		else{
			for(EnchantmentData enchantmentData : this.enchantments){
				if(itemMeta.hasConflictingEnchant(enchantmentData.getEnchantment())){
					continue;
				}
				if(itemMeta.hasEnchant(enchantmentData.getEnchantment())){
					itemMeta.removeEnchant(enchantmentData.getEnchantment());
				}
				Bukkit.getLogger().info("[CustomItems] Füge Enchantment "+enchantmentData.getEnchantment().getName()+" hinzu.");
				itemMeta.addEnchant(enchantmentData.getEnchantment(), enchantmentData.getLevel(), enchantmentData.getIgnoreLevelRestriction());
			}
		}
		if(itemMeta instanceof SkullMeta){
			SkullMeta skullMeta = (SkullMeta) itemMeta;
			if(!this.skullOwner.isEmpty()){
				skullMeta.setOwner(this.skullOwner);
			}
		}
		for(ItemFlag itemFlag : this.itemFlags){
			if(itemMeta.hasItemFlag(itemFlag)) continue;
			itemMeta.addItemFlags(itemFlag);
		}
		if(!this.displayName.isEmpty()){
			itemMeta.setDisplayName(this.displayName);
		}
		if(!this.localizedName.isEmpty()){
			itemMeta.setLocalizedName(this.localizedName);
		}
		if(this.lore.size()>0){
			itemMeta.setLore(lore);
		}
		itemMeta.setUnbreakable(this.unbreakable);
		return itemMeta;
	}
	private NBTTagCompound buildNBTAttributeBase(String name){
		NBTTagCompound base = new NBTTagCompound();
		base.setString("AttributeName", name);
		base.setString("Name", name);
		base.setInt("UUIDLeast", 894654);
		base.setInt("UUIDMost", 2872);
		base.setString("Slot", this.slot);
		return base;
	}
	private NBTTagList buildAttributeModifiers(){
		NBTTagList modifiers = new NBTTagList();
		if(this.attackDamage>=0){
			NBTTagCompound tag = buildNBTAttributeBase("generic.attackDamage");
			tag.setDouble("Amount", this.attackDamage);
			modifiers.add(tag);
		}
		if(this.attackSpeed>=0f){
			NBTTagCompound tag = buildNBTAttributeBase("generic.attackSpeed");
			tag.setDouble("Amount", this.attackSpeed);
			modifiers.add(tag);
		}
		if(this.maxHealth>=0f){
			NBTTagCompound tag = buildNBTAttributeBase("generic.maxHealth");
			tag.setDouble("Amount", this.maxHealth);
			modifiers.add(tag);
		}
		if(this.armor>=0f){
			NBTTagCompound tag = buildNBTAttributeBase("generic.armor");
			tag.setDouble("Amount", this.armor);
			modifiers.add(tag);
		}
		if(this.movementSpeed>=0f){
			NBTTagCompound tag = buildNBTAttributeBase("generic.movementSpeed");
			tag.setDouble("Amount", this.movementSpeed);
			modifiers.add(tag);
		}
		if(this.luck>=0f){
			NBTTagCompound tag = buildNBTAttributeBase("generic.luck");
			tag.setDouble("Amount", this.luck);
			modifiers.add(tag);
		}
		return modifiers;
	}
	public ItemStack build(){
		if(material==null){
			Bukkit.getLogger().info("[CustomItems] ItemStack konnte nicht kreiiert werden, da kein Material angegeben wurde.");
			return null;
		}
		ItemStack result = new ItemStack(material);
		result.setAmount(amount);
		result.setDurability(durability);
		this.update(result);
		return result;
	}
	public void update(ItemStack itemStack){
		if(useNMS){
			net.minecraft.server.v1_12_R1.ItemStack craftItemStack = CraftItemStack.asNMSCopy(itemStack);
			NBTTagCompound nbtTags;
			if(craftItemStack.hasTag())
				nbtTags = craftItemStack.getTag();
			else
				nbtTags = new NBTTagCompound();
			
			if(!this.customEnum.isEmpty()){
				nbtTags.setString("customEnum", this.customEnum);
			}
			if(this.item_id>0){
				nbtTags.setInt("item_id", item_id);
			}
			if(this.maxCustomDurability>0){
				nbtTags.setInt("maxCustomDurability", this.maxCustomDurability);
				if(!nbtTags.hasKey("customDurability")){
					nbtTags.setInt("customDurability", this.customDurability);
				}
				else if(nbtTags.getInt("customDurability")>this.maxCustomDurability){
					nbtTags.setInt("customDurability", this.maxCustomDurability);
				}
			}
			if(this.customPotionColor>0){
				nbtTags.setInt("CustomPotionColor", this.customPotionColor);
			}
			if(this.colorMap>0){
				nbtTags.setInt("ColorMap", this.colorMap);
			}
			nbtTags.set("AttributeModifiers", buildAttributeModifiers());
			craftItemStack.setTag(nbtTags);
			itemStack.setItemMeta(CraftItemStack.getItemMeta(craftItemStack));
		}
		itemStack.setItemMeta(buildItemMeta(itemStack));
	}
}
