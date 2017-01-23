package ch.swisssmp.craftmmo.mmoitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoattribute.MmoAttribute;
import ch.swisssmp.craftmmo.mmoattribute.MmoElement;
import ch.swisssmp.craftmmo.mmoshop.MmoShop;
import ch.swisssmp.craftmmo.util.MmoResourceManager;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.NBTTagDouble;
import net.minecraft.server.v1_11_R1.NBTTagInt;
import net.minecraft.server.v1_11_R1.NBTTagList;
import net.minecraft.server.v1_11_R1.NBTTagString;

public class MmoItem {
	protected static HashMap<Integer, Class<? extends MmoItem>> itemclasses = new HashMap<Integer, Class<? extends MmoItem>>();
	public static HashMap<String, Integer> mmoItemIDs;
	public static HashMap<Integer, MmoItem> templates;
	public HashMap<String, Recipe> recipes;
	//technical information for item generation
	public final int mmo_item_id;
	public final int mmo_itemclass_id;
	public final short mc_durability;
	public final Material mc_enum;
	public final ConfigurationSection dataSection;
	//visible information
	public final String displayName;
	public final Integer rarity;
	private final List<String> lore;
	
	public final int maxStackSize;

	public final HashMap<MmoElement, Integer> elements = new HashMap<MmoElement, Integer>();
	public final HashMap<MmoMiningType, MmoMiningQuality> mining = new HashMap<MmoMiningType, MmoMiningQuality>();
	
	protected boolean hideAttributes = true;
	
	public MmoItem(ConfigurationSection dataSection) throws Exception{
		if(dataSection==null){
			throw new Exception("Cannot create item with empty construction data!");
		}
		//technical information for item generation
		this.dataSection = dataSection;
		this.mmo_item_id = Integer.parseInt(dataSection.getName());
		this.mmo_itemclass_id = dataSection.getInt("itemclass");
		displayName = dataSection.getString("name");
		Float damageValue = ((float) dataSection.getInt("mc_durability"))*(float)Math.pow(10, -5);
		int maxDurability = dataSection.getInt("max_durability");
		String mc_enum = dataSection.getString("mc_enum").toUpperCase();
		this.mc_enum = Material.getMaterial(mc_enum);
		this.mc_durability = getMcDurability(maxDurability, damageValue);
		if(mc_enum==null){
			throw new Exception("Material "+mc_enum+" invalid.");
		}
		//visible information
		if(dataSection.contains("configuration")){
			ConfigurationSection configurationSection = dataSection.getConfigurationSection("configuration");
			ConfigurationSection elementsSection = configurationSection.getConfigurationSection("elements");
			if(elementsSection!=null){
				for(String elementName : elementsSection.getKeys(false)){
					MmoElement element = MmoElement.get(elementName);
					int element_strength = elementsSection.getInt(elementName);
					this.elements.put(element, element_strength);
				}
			}
			ConfigurationSection miningsSection = configurationSection.getConfigurationSection("mining");
			if(miningsSection!=null){
				for(String key : miningsSection.getKeys(false)){
					ConfigurationSection miningSection = miningsSection.getConfigurationSection(key);
					String miningTypeName = miningSection.getString("type");
					if(miningTypeName==null){
						Main.debug("No mining type defined in section "+key+" in mmo_item_id "+this.mmo_item_id);
						continue;
					}
					MmoMiningType miningType = MmoMiningType.valueOf(miningTypeName);
					MmoMiningQuality miningQuality = MmoMiningQuality.valueOf(miningSection.getString("quality"));
					this.mining.put(miningType, miningQuality);
				}
			}
		}
		this.maxStackSize = dataSection.getInt("max_stack_size");
		rarity = dataSection.getInt("rarity");
		lore = dataSection.getStringList("lore");
		templates.put(mmo_item_id, this);
	}
	
	public static final NBTTagCompound getSaveData(ItemStack itemStack){
		net.minecraft.server.v1_11_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
		if(nmsItem==null){
			return null;
		}
		if(!nmsItem.hasTag()){
			return null;
		}
		return nmsItem.getTag();
	}
	
	public static final void applySaveData(NBTTagCompound nbttagcompound, ItemStack itemStack){
		MmoItem mmoItem = MmoItem.get(itemStack);
		if(mmoItem==null){
			return;
		}
		net.minecraft.server.v1_11_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
		nmsItem.setTag(nbttagcompound);
		ItemMeta itemMeta = CraftItemStack.getItemMeta(nmsItem);
		mmoItem.applyItemMeta(itemStack, itemMeta);
	}
	
	public static final void update(ItemStack itemStack){
		MmoItem mmoItem = MmoItem.get(itemStack);
		if(mmoItem==null){
			return;
		}
		mmoItem.applyItemMeta(itemStack, itemStack.getItemMeta());
		mmoItem.applyCustomData(itemStack, false);
	}
	
	private NBTTagCompound applyDefaultSaveData(NBTTagCompound nbttagcompound){
		nbttagcompound.setInt("mmo_item_id", mmo_item_id);
		if(this.mining.size()>0){
			NBTTagList destroyData = MmoMining.getBreakable(mining);
			if(destroyData!=null){
				nbttagcompound.set("CanDestroy", destroyData);
			}
		}
		else if(nbttagcompound.hasKey("CanDestroy")){
			nbttagcompound.remove("CanDestroy");
		}
		nbttagcompound = applyCustomSaveData(nbttagcompound);
		return nbttagcompound;
	}
	
	public void applyItemMeta(ItemStack itemStack, ItemMeta itemMeta){
		itemStack.setType(this.mc_enum);
		itemMeta.setDisplayName(getDisplayName());
		itemMeta.setLore(getLore(itemStack));
		itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		if(this.hideAttributes){
			itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		}
		itemStack.setItemMeta(itemMeta);
	}
	
	protected NBTTagCompound applyCustomSaveData(NBTTagCompound nbttagcompound){
		return nbttagcompound;
	}
	
	public ItemStack toItemStack(){
		ItemStack itemStack;
		if(mc_durability>0){
			itemStack = new ItemStack(mc_enum, 1, mc_durability);
		}
		else{
			itemStack = new ItemStack(mc_enum);
		}
		net.minecraft.server.v1_11_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound = applyDefaultSaveData(nbttagcompound);
		nmsItem.setTag(nbttagcompound);
		ItemMeta itemMeta = CraftItemStack.getItemMeta(nmsItem);
		applyItemMeta(itemStack, itemMeta);
		return applyCustomData(itemStack, true);
	}
	
	public ItemStack applyCustomData(ItemStack itemStack, boolean forceAll){
		//itemStack.setAmount(this.maxStackSize);
		return itemStack;
	}
	//this is expected to be overridden
	protected List<String> getItemclassData(ItemStack itemStack){
		List<String> result = new ArrayList<String>();
		return result;
	}
	protected final List<String> getLore(ItemStack itemStack){
		List<String> result = new ArrayList<String>();
		result.addAll(getItemclassData(itemStack));
		List<String> formattedLore = formatLore();
		if(formattedLore.size()>0){
			//result.add(loreHeader("Beschreibung"));
			result.addAll(formattedLore);
		}
		return result;
	}
	protected final String loreHeader(String text){
		return loreHeader(text, 1);
	}
	protected final String loreHeader(String text, int layer){
		switch(layer){
		case 1:
			return ""+ChatColor.GRAY+ChatColor.UNDERLINE+text+ChatColor.RESET;
		case 2:
			return ""+ChatColor.GRAY+ChatColor.ITALIC+text+ChatColor.RESET;
		default:
			return ""+ChatColor.GRAY+text+ChatColor.RESET;
		}
	}
	protected final List<String> formatLore(){
		List<String> result = new ArrayList<String>();
		for(String line : lore){
			result.add(ChatColor.DARK_GRAY+""+ChatColor.ITALIC+line);
		}
		return result;
	}
	protected String getDisplayName(){
		ChatColor nameColor;
		switch(rarity){
		case 1:
			nameColor = ChatColor.GRAY;
			break;
		case 2:
			nameColor = ChatColor.GREEN;
			break;
		case 3:
			nameColor = ChatColor.BLUE;
			break;
		case 4:
			nameColor = ChatColor.YELLOW;
			break;
		case 5:
			nameColor = ChatColor.GOLD;
			break;
		case 6:
			nameColor = ChatColor.RED;
			break;
		case 7:
			nameColor = ChatColor.LIGHT_PURPLE;
			break;
		default:
			nameColor = ChatColor.WHITE;
		}
		String result = nameColor+displayName;
		String elementData = getElementData();
		if(!elementData.equals("")){
			result+=elementData;
		}
		return result;
	}
	
	public MmoItemclass getItemclass(){
		return MmoItemclass.get(mmo_itemclass_id);
	}
	
	public double getDamage(){
		return 1;
	}
	
	public double getSpeed(){
		return 1;
	}
	
	//--------------------static stuff---------------------------------
	public static NBTTagCompound createAttribute(Attribute attribute, double amount, EquipmentSlot slot){
		String attributeName = MmoAttribute.getAttributeName(attribute);
		if(attributeName.isEmpty()) return null;
		String slotName = slot.toString().toLowerCase();
		if(slotName.equals("hand")) slotName = "mainhand";
		NBTTagCompound damageCompound = new NBTTagCompound();
		damageCompound.set("AttributeName", new NBTTagString(attributeName));
		damageCompound.set("Name", new NBTTagString(attributeName));
		damageCompound.set("Amount", new NBTTagDouble(amount));
		damageCompound.set("Operation", new NBTTagInt(MmoAttribute.getOperation(attribute)));
		switch(attribute){
		case GENERIC_ATTACK_SPEED:
			damageCompound.set("UUIDLeast", new NBTTagInt(1));
			damageCompound.set("UUIDMost", new NBTTagInt(1));
			break;
		case GENERIC_ATTACK_DAMAGE:
			damageCompound.set("UUIDLeast", new NBTTagInt(2));
			damageCompound.set("UUIDMost", new NBTTagInt(2));
			break;
		default:
			damageCompound.set("UUIDLeast", new NBTTagInt(894654));
			damageCompound.set("UUIDMost", new NBTTagInt(2872));
			break;
		}
		damageCompound.set("Slot", new NBTTagString(slotName));
		return damageCompound;
	}
	public static void setAttributes(ItemStack itemStack, NBTTagList modifiers){
		net.minecraft.server.v1_11_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
		compound.set("AttributeModifiers", modifiers);
		nmsStack.setTag(compound);
		ItemMeta itemMeta = CraftItemStack.getItemMeta(nmsStack);
		itemStack.setItemMeta(itemMeta);
	}
	protected short getMcDurability(int maxDurability, Float damageValue){
		if(damageValue==0 && maxDurability==0) return 0;
		return (short)(Math.max(1, Math.ceil(maxDurability*damageValue)));
	}
	public static int getID(ItemStack itemStack){
		if(itemStack==null)
			return -1;
		NBTTagCompound saveData = getSaveData(itemStack);
		if(saveData==null){
			return -1;
		}
		return saveData.getInt("mmo_item_id");
	}
	public static MmoItem get(ItemStack itemStack){
		Integer mmo_item_id = getID(itemStack);
		if(mmo_item_id<0)
			return null;
		return templates.get(mmo_item_id);
	}
	public static MmoItem get(int mmo_item_id){
		return templates.get(mmo_item_id);
	}
	protected String getElementData(){
		List<String> result = new ArrayList<String>();
		MmoElement[] elementsOrder = {MmoElement.NEUTRAL, MmoElement.AIR, MmoElement.EARTH, MmoElement.FIRE, MmoElement.WATER, MmoElement.LIGHT, MmoElement.DARKNESS};
		if(elements!=null){
			for(MmoElement element : elementsOrder){
				if(!elements.containsKey(element)){
					continue;
				}
				Integer element_strength = elements.get(element);
				if(element_strength>0){
					String line = "";
					switch(element){
					case AIR:
						line = ChatColor.GRAY+"W";
						break;
					case EARTH:
						line = ChatColor.GREEN+"E";
						break;
					case FIRE:
						line = ChatColor.RED+"F";
						break;
					case WATER:
						line = ChatColor.BLUE+"A";
						break;
					case LIGHT:
						line = ChatColor.YELLOW+"L";
						break;
					case DARKNESS:
						line = ChatColor.DARK_PURPLE+"S";
						break;
					case NEUTRAL:
						line = ChatColor.WHITE+"N";
						break;
					default:
						line = ChatColor.DARK_GRAY+element.toString();
						break;
					}
					result.add(line+element_strength);
				}
			}
		}
		return " "+String.join("", result);
	}
	
	public void registerRecipes(){
		for(Recipe recipe : recipes.values()){
			Bukkit.addRecipe(recipe);
		}
	}
	
	public static void loadItems(boolean initial) throws Exception{
		templates = new HashMap<Integer, MmoItem>();
		
		YamlConfiguration mmoItemsConfiguration = MmoResourceManager.getYamlResponse("items.php");
		for(String itemIDstring : mmoItemsConfiguration.getKeys(false)){
			ConfigurationSection dataSection = mmoItemsConfiguration.getConfigurationSection(itemIDstring);
			int mmo_itemclass_id = dataSection.getInt("itemclass");
			MmoItemclass itemclass = MmoItemclass.get(mmo_itemclass_id);
			if(itemclass!=null && dataSection.contains("configuration.class")) itemclass.createItem(dataSection);
			else Main.debug("Item "+dataSection.getInt("mmo_item_id")+" contains invalid itemclass "+mmo_itemclass_id);
		}
		MmoItemManager.loadCraftingRecipes();
		MmoShop.loadShops();
	}
}
