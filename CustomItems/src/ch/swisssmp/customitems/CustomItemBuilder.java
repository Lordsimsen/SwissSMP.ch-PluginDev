package ch.swisssmp.customitems;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import ch.swisssmp.utils.EnchantmentData;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.nbt.NBTTagCompound;
import ch.swisssmp.utils.nbt.NBTTagList;

public class CustomItemBuilder {
	//itemStack
	private Material material;
	private int amount;
	private short durability = 0;
	
	//itemMeta
	private String displayName = "";
	private String localizedName = "";
	private ArrayList<EnchantmentData> enchantments = new ArrayList<EnchantmentData>();
	private ArrayList<ItemFlag> itemFlags = new ArrayList<ItemFlag>();
	private List<String> lore = new ArrayList<String>();
	private boolean unbreakable = false;
	private UUID skullOwner = null;
	
	//nms stuff
	private boolean useNMS = false;
	private boolean useCustomModelDataProperty = false;
	
	private String customEnum = "";
	private int maxCustomDurability = 0;
	private int customDurability = 0;
	private int customModelId = 0;
	private long expirationDate = 0;
	private int maxStackSize = 0;
	
	private double attackDamage = -1;
	private double attackSpeed = -1f;
	private double maxHealth = -1f;
	private double armor = -1f;
	private double movementSpeed = -1f;
	private double luck = -1f;
	
	private int customPotionColor = -1;
	private int colorMap = -1;
	
	private String slot = "mainhand";
	
	private final List<CustomItemBuilderModifier> components = new ArrayList<CustomItemBuilderModifier>();
	
	public CustomItemBuilder(){
		this.amount = 1;
	}

	public CustomItemBuilder(Material material){
		this.material = material;
		this.amount = 1;
	}

	public CustomItemBuilder(Material material, int amount){
		this.material = material;
		this.amount = amount;
	}
	
	public CustomItemBuilder setMaterial(Material material){
		this.material = material;
		return this;
	}
	public CustomItemBuilder setAmount(int amount){
		this.amount = amount;
		return this;
	}
	public CustomItemBuilder setDurability(short durability){
		this.durability = durability;
		if(!this.customEnum.isEmpty() && this.durability>=0){
			this.unbreakable = true;
			if(!this.itemFlags.contains(ItemFlag.HIDE_UNBREAKABLE)){
				this.itemFlags.add(ItemFlag.HIDE_UNBREAKABLE);
			}
		}
		return this;
	}
	public CustomItemBuilder setMaxCustomDurability(int maxCustomDurability){
		this.useNMS = true;
		this.maxCustomDurability = maxCustomDurability;
		return this;
	}
	public CustomItemBuilder setCustomDurability(int customDurability){
		this.customDurability = customDurability;
		return this;
	}
	public CustomItemBuilder setCustomModelId(int customModelId) {
		this.customModelId = customModelId;
		this.useCustomModelDataProperty = true;
		return this;
	}

	public int getCustomModelId(){
		return customModelId;
	}

	public CustomItemBuilder setUseCustomModelDataProperty(boolean use) {
		this.useCustomModelDataProperty = use;
		return this;
	}
	public CustomItemBuilder addEnchantments(List<EnchantmentData> enchantments){
		this.enchantments.addAll(enchantments);
		return this;
	}
	public CustomItemBuilder addEnchantment(EnchantmentData enchantmentData){
		this.enchantments.add(enchantmentData);
		return this;
	}
	public CustomItemBuilder addEnchantment(Enchantment enchantment, int level, boolean ignoreLevelRestriction){
		enchantments.add(new EnchantmentData(enchantment, level, ignoreLevelRestriction));
		return this;
	}
	public CustomItemBuilder addItemFlags(List<ItemFlag> itemFlags){
		itemFlags.addAll(itemFlags);
		return this;
	}
	public CustomItemBuilder addItemFlags(ItemFlag... itemFlags){
		for(int i = 0; i < itemFlags.length; i++){
			this.itemFlags.add(itemFlags[i]);
		}
		return this;
	}
	public CustomItemBuilder setDisplayName(String displayName){
		this.displayName = displayName;
		return this;
	}
	public CustomItemBuilder setLocalizedName(String localizedName){
		this.localizedName = localizedName;
		return this;
	}
	public CustomItemBuilder setLore(List<String> lore){
		this.lore = lore;
		return this;
	}
	public CustomItemBuilder setUnbreakable(boolean unbreakable){
		this.unbreakable = unbreakable;
		return this;
	}
	public CustomItemBuilder setSkullOwner(UUID owner){
		this.skullOwner = owner;
		return this;
	}
	public CustomItemBuilder addComponent(CustomItemBuilderModifier component) {
		this.components.add(component);
		return this;
	}
	/**
	 * Setzt wann der ItemStack aus Inventaren entfernt werden soll
	 * @param expirationDate - Timestamp in Sekunden
	 */
	public CustomItemBuilder setExpirationDate(int expirationDate){
		this.expirationDate = expirationDate;
		this.useNMS = useNMS || expirationDate>0;
		return this;
	}
	
	public CustomItemBuilder setCustomEnum(String customEnum){
		this.setCustomEnum(customEnum, null);
		return this;
	}
	
	protected CustomItemBuilder setCustomEnum(String customEnum, CustomMaterialTemplate template){
		this.customEnum = customEnum;
		if(this.customEnum==null) return this;
		if(this.material==null){
			if(template==null){
				template = CustomMaterialTemplate.get(customEnum);
			}
			if(template!=null){
				Material material = template.getMaterial();
				if(material!=null) this.setMaterial(material);
				this.setDurability(template.getDurability());
				this.customModelId = template.getCustomModelId();
				this.useCustomModelDataProperty = template.useCustomModelDataProperty();
			}
		}
		if(this.durability!=0 && !this.customEnum.isEmpty()){
			this.useNMS = true;
			this.unbreakable |= !this.useCustomModelDataProperty;
			if(!this.useCustomModelDataProperty && !this.itemFlags.contains(ItemFlag.HIDE_UNBREAKABLE)){
				this.itemFlags.add(ItemFlag.HIDE_UNBREAKABLE);
			}
		}
		return this;
	}

	public CustomItemBuilder setAttackDamage(double attackDamage){
		this.useNMS = true;
		this.attackDamage = attackDamage;
		return this;
	}
	public CustomItemBuilder setAttackSpeed(double attackSpeed){
		this.useNMS = true;
		this.attackSpeed = attackSpeed;
		return this;
	}
	public CustomItemBuilder setMaxHealth(double maxHealth){
		this.useNMS = true;
		this.maxHealth = maxHealth;
		return this;
	}
	public CustomItemBuilder setArmor(double armor){
		this.useNMS = true;
		this.armor = armor;
		return this;
	}
	public CustomItemBuilder setMovementSpeed(double movementSpeed){
		this.useNMS = true;
		this.movementSpeed = movementSpeed;
		return this;
	}
	public CustomItemBuilder setLuck(double luck){
		this.useNMS = true;
		this.luck = luck;
		return this;
	}
	public CustomItemBuilder setCustomPotionColor(int customPotionColor){
		this.useNMS = true;
		this.customPotionColor = customPotionColor;
		return this;
	}
	public CustomItemBuilder setMaxStackSize(int maxStackSize){
		this.maxStackSize = maxStackSize;
		return this;
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
				Bukkit.getLogger().info("[CustomItems] Füge Enchantment "+enchantmentData.getEnchantment().toString()+" hinzu.");
				itemMeta.addEnchant(enchantmentData.getEnchantment(), enchantmentData.getLevel(), enchantmentData.getIgnoreLevelRestriction());
			}
		}
		if(itemMeta instanceof SkullMeta){
			SkullMeta skullMeta = (SkullMeta) itemMeta;
			if(this.skullOwner!=null){
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(skullOwner);
				skullMeta.setOwningPlayer(offlinePlayer);
			}
		}
		for(ItemFlag itemFlag : this.itemFlags){
			if(itemMeta.hasItemFlag(itemFlag)) continue;
			itemMeta.addItemFlags(itemFlag);
		}
		if(this.displayName!=null && !this.displayName.isEmpty()){
			itemMeta.setDisplayName(this.displayName);
		}
		if(!this.localizedName.isEmpty()){
			itemMeta.setLocalizedName(this.localizedName);
		}
		if(this.lore.size()>0){
			itemMeta.setLore(lore);
		}
		if(this.expirationDate>0){
			List<String> lore = itemMeta.getLore();
			if(lore==null) lore = new ArrayList<String>();
			Date date = new Date(this.expirationDate*1000L);//Date expects millis, expirationDate is seconds
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			lore.add(ChatColor.GRAY+"Ablaufdatum: "+dateFormat.format(date));
			itemMeta.setLore(lore);
		}
		if(this.unbreakable!=itemMeta.isUnbreakable()){
			itemMeta.setUnbreakable(this.unbreakable);
		}
		if(this.useCustomModelDataProperty) {
			itemMeta.setCustomModelData(customModelId);
		}
		if(itemMeta instanceof Damageable){
			((Damageable)itemMeta).setDamage(durability);
		}
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
		this.update(result);
		return result;
	}

	/**
	 * Aktualisiert alle Eigenschaften des ItemStacks, inklusive Material, solange diese im CustomItemBuilder festgelegt
	 * sind. Die Anzahl bleibt unverändert.
	 * @param itemStack - Der zu aktualisierende ItemStack
	 * @return Derselbe CustomItemBuilder zur Verkettung von Befehlen
	 */
	public CustomItemBuilder update(ItemStack itemStack){
		if(this.material!=null) itemStack.setType(material);
		if(useNMS){
			NBTTagCompound nbtTag = ItemUtil.getData(itemStack);
			if(nbtTag==null) nbtTag = new NBTTagCompound();
			
			if(!this.customEnum.isEmpty()){
				nbtTag.setString("customEnum", this.customEnum);
			}
			
			if(this.maxCustomDurability>0){
				nbtTag.setInt("maxCustomDurability", this.maxCustomDurability);
				if(!nbtTag.hasKey("customDurability")){
					nbtTag.setInt("customDurability", this.customDurability);
				}
				else if(nbtTag.getInt("customDurability")>this.maxCustomDurability){
					nbtTag.setInt("customDurability", this.maxCustomDurability);
				}
			}
			
			if(this.customPotionColor>0){
				nbtTag.setInt("CustomPotionColor", this.customPotionColor);
			}
			
			if(this.colorMap>0){
				nbtTag.setInt("ColorMap", this.colorMap);
			}
			
			if(this.expirationDate>0){
				nbtTag.setLong("expirationDate", this.expirationDate);
			}
			
			if(this.maxStackSize>0){
				nbtTag.setInt("maxStackSize", this.maxStackSize);
			}
			
			NBTTagList attributeModifiers = this.buildAttributeModifiers();
			if(!attributeModifiers.isEmpty()){
				nbtTag.set("AttributeModifiers", attributeModifiers);
			}
			
			ItemUtil.setData(itemStack, nbtTag);
		}
		itemStack.setItemMeta(buildItemMeta(itemStack));
		
		for(CustomItemBuilderModifier c : components) {
			try {
				c.apply(itemStack);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}

		return this;
	}
}
