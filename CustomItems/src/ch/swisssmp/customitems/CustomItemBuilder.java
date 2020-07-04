package ch.swisssmp.customitems;

import java.text.SimpleDateFormat;
import java.util.*;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
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
	public Material getMaterial(){
		return material;
	}
	public CustomItemBuilder setAmount(int amount){
		this.amount = amount;
		return this;
	}
	public int getAmount(){
		return amount;
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
	public short getDurability(){
		return durability;
	}
	public CustomItemBuilder setMaxCustomDurability(int maxCustomDurability){
		this.useNMS = true;
		this.maxCustomDurability = maxCustomDurability;
		return this;
	}
	public int getMaxCustomDurability(){
		return maxCustomDurability;
	}
	public CustomItemBuilder setCustomDurability(int customDurability){
		this.customDurability = customDurability;
		return this;
	}
	public int getCustomDurability(){
		return customDurability;
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
	public boolean getUseCustomModelDataProperty(){
		return useCustomModelDataProperty;
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
	public Collection<EnchantmentData> getEnchantments(){
		return enchantments;
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
	public Collection<ItemFlag> getItemFlags(){
		return itemFlags;
	}
	public CustomItemBuilder setDisplayName(String displayName){
		this.displayName = displayName;
		return this;
	}
	public String getDisplayName(){
		return displayName;
	}
	public CustomItemBuilder setLocalizedName(String localizedName){
		this.localizedName = localizedName;
		return this;
	}
	public String getLocalizedName(){
		return localizedName;
	}
	public CustomItemBuilder setLore(List<String> lore){
		this.lore = lore;
		return this;
	}
	public List<String> getLore(){
		return lore;
	}
	public CustomItemBuilder setUnbreakable(boolean unbreakable){
		this.unbreakable = unbreakable;
		return this;
	}
	public boolean getUnbreakable(){
		return unbreakable;
	}
	public CustomItemBuilder setSkullOwner(UUID owner){
		this.skullOwner = owner;
		return this;
	}
	public UUID getSkullOwner(){
		return skullOwner;
	}
	public CustomItemBuilder addComponent(CustomItemBuilderModifier component) {
		this.components.add(component);
		return this;
	}
	public Collection<CustomItemBuilderModifier> getModifiers(){
		return components;
	}
	/**
	 * Setzt wann der ItemStack aus Inventaren entfernt werden soll
	 * @param expirationDate - Timestamp in Sekunden
	 */
	public CustomItemBuilder setExpirationDate(long expirationDate){
		this.expirationDate = expirationDate;
		this.useNMS = useNMS || expirationDate>0;
		return this;
	}
	public long getExpirationDate(){
		return expirationDate;
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

	public String getCustomEnum(){
		return customEnum;
	}

	public CustomItemBuilder setAttackDamage(double attackDamage){
		this.useNMS = true;
		this.attackDamage = attackDamage;
		return this;
	}
	public double getAttackDamage(){
		return attackDamage;
	}
	public CustomItemBuilder setAttackSpeed(double attackSpeed){
		this.useNMS = true;
		this.attackSpeed = attackSpeed;
		return this;
	}
	public double getAttackSpeed(){
		return attackSpeed;
	}
	public CustomItemBuilder setMaxHealth(double maxHealth){
		this.useNMS = true;
		this.maxHealth = maxHealth;
		return this;
	}
	public double getMaxHealth(){
		return maxHealth;
	}
	public CustomItemBuilder setArmor(double armor){
		this.useNMS = true;
		this.armor = armor;
		return this;
	}
	public double getArmor(){
		return armor;
	}
	public CustomItemBuilder setMovementSpeed(double movementSpeed){
		this.useNMS = true;
		this.movementSpeed = movementSpeed;
		return this;
	}
	public double getMovementSpeed(){
		return movementSpeed;
	}
	public CustomItemBuilder setLuck(double luck){
		this.useNMS = true;
		this.luck = luck;
		return this;
	}
	public double getLuck(){
		return luck;
	}
	public CustomItemBuilder setCustomPotionColor(int customPotionColor){
		this.useNMS = true;
		this.customPotionColor = customPotionColor;
		return this;
	}
	public int getCustomPotionColor(){
		return customPotionColor;
	}
	public CustomItemBuilder setMaxStackSize(int maxStackSize){
		this.maxStackSize = maxStackSize;
		return this;
	}
	public int getMaxStackSize(){
		return maxStackSize;
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
	private CompoundTag buildNBTAttributeBase(String name){
		CompoundTag base = new CompoundTag();
		base.putString("AttributeName", name);
		base.putString("Name", name);
		base.putInt("UUIDLeast", 894654);
		base.putInt("UUIDMost", 2872);
		base.putString("Slot", this.slot);
		return base;
	}
	private ListTag<?> buildAttributeModifiers(){
		ListTag<CompoundTag> modifiers = new ListTag<>(CompoundTag.class);
		if(this.attackDamage>=0){
			CompoundTag tag = buildNBTAttributeBase("generic.attackDamage");
			tag.putDouble("Amount", this.attackDamage);
			modifiers.add(tag);
		}
		if(this.attackSpeed>=0f){
			CompoundTag tag = buildNBTAttributeBase("generic.attackSpeed");
			tag.putDouble("Amount", this.attackSpeed);
			modifiers.add(tag);
		}
		if(this.maxHealth>=0f){
			CompoundTag tag = buildNBTAttributeBase("generic.maxHealth");
			tag.putDouble("Amount", this.maxHealth);
			modifiers.add(tag);
		}
		if(this.armor>=0f){
			CompoundTag tag = buildNBTAttributeBase("generic.armor");
			tag.putDouble("Amount", this.armor);
			modifiers.add(tag);
		}
		if(this.movementSpeed>=0f){
			CompoundTag tag = buildNBTAttributeBase("generic.movementSpeed");
			tag.putDouble("Amount", this.movementSpeed);
			modifiers.add(tag);
		}
		if(this.luck>=0f){
			CompoundTag tag = buildNBTAttributeBase("generic.luck");
			tag.putDouble("Amount", this.luck);
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
			CompoundTag nbtTag = ItemUtil.getData(itemStack);
			if(nbtTag==null) nbtTag = new CompoundTag();
			
			if(!this.customEnum.isEmpty()){
				nbtTag.putString("customEnum", this.customEnum);
			}
			
			if(this.maxCustomDurability>0){
				nbtTag.putInt("maxCustomDurability", this.maxCustomDurability);
				if(!nbtTag.containsKey("customDurability")){
					nbtTag.putInt("customDurability", this.customDurability);
				}
				else if(nbtTag.getInt("customDurability")>this.maxCustomDurability){
					nbtTag.putInt("customDurability", this.maxCustomDurability);
				}
			}
			
			if(this.customPotionColor>0){
				nbtTag.putInt("CustomPotionColor", this.customPotionColor);
			}
			
			if(this.colorMap>0){
				nbtTag.putInt("ColorMap", this.colorMap);
			}
			
			if(this.expirationDate>0){
				nbtTag.putLong("expirationDate", this.expirationDate);
			}
			
			if(this.maxStackSize>0){
				nbtTag.putInt("maxStackSize", this.maxStackSize);
			}
			
			ListTag<?> attributeModifiers = this.buildAttributeModifiers();
			if(attributeModifiers.size()>0){
				nbtTag.put("AttributeModifiers", attributeModifiers);
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
