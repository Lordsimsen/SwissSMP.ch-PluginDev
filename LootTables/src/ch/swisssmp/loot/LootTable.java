package ch.swisssmp.loot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.loot.populator.InventoryPopulator;
import ch.swisssmp.random.RandomItemUtil;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.SwissSMPUtils;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class LootTable {
	private static HashMap<String,LootTable> loadedTables = new HashMap<String,LootTable>();
	
	private int loot_table_id;
	private String name;
	private LootType lootType;
	private double chance;
	private int min_rolls;
	private int max_rolls;
	private ItemStack[] items;
	
	private LootTable(ConfigurationSection dataSection){
		this.loot_table_id = dataSection.getInt("id");
		this.name = dataSection.getString("name");
		this.lootType = LootType.valueOf(dataSection.getString("loot_type"));
		this.chance = dataSection.getDouble("chance");
		this.min_rolls = dataSection.getInt("min_rolls");
		this.max_rolls = dataSection.getInt("max_rolls");
		this.items = dataSection.getItemStacks("items", dataSection.getInt("slots"));
		loadedTables.put(this.name.toLowerCase(), this);
	}
	
	public void setName(String name){
		loadedTables.remove(this.name);
		name = name.replace(" ", "_");
		this.name = name;
		loadedTables.put(this.name,this);
		DataSource.getResponse("loot/edit_table_name.php", new String[]{
				"id="+this.loot_table_id,
				"name="+URLEncoder.encode(this.name)
		});
		this.updateTokens();
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getDisplayName(){
		return this.lootType.getColor()+this.getName();
	}
	
	public void setLootType(LootType lootType){
		this.lootType = lootType;
		DataSource.getResponse("loot/edit_table_loot_type.php", new String[]{
				"id="+this.loot_table_id,
				"loot_type="+URLEncoder.encode(this.lootType.toString())
		});
		this.updateTokens();
	}
	
	public void setChance(double chance){
		this.chance = Mathf.clamp01(chance);
		DataSource.getResponse("loot/edit_table_chance.php", new String[]{
				"id="+this.loot_table_id,
				"chance="+this.chance
		});
		this.updateTokens();
	}
	
	public double getChance(){
		return this.chance;
	}
	
	public void setRolls(int min_rolls, int max_rolls){
		if(min_rolls>0 || min_rolls>0){
			this.min_rolls = Math.max(1,min_rolls);
			this.max_rolls = Math.max(this.min_rolls,max_rolls);
		}
		else{
			this.min_rolls = -1;
			this.max_rolls = -1;
		}
		DataSource.getResponse("loot/edit_table_rolls.php", new String[]{
				"id="+this.loot_table_id,
				"min_rolls="+this.min_rolls,
				"max_rolls="+this.max_rolls
		});
		this.updateTokens();
	}
	
	public int getMinRolls(){
		return this.min_rolls;
	}
	
	public int getMaxRolls(){
		return this.max_rolls;
	}
	
	public ItemStack getInventoryToken(int amount){
		String displayName = this.getDisplayName();
		if(this.chance<1){
			displayName+= ChatColor.DARK_GRAY+" - "+(this.chance*100)+"%";
		}
		if(this.min_rolls>0 || this.max_rolls>0){
			String rollsString;
			if(this.min_rolls==this.max_rolls){
				rollsString = "("+this.min_rolls+" Items)";
			}
			else{
				rollsString = "("+this.min_rolls+"-"+this.max_rolls+" Items)";
			}
			displayName+= ChatColor.WHITE+" "+rollsString;
		}
		CustomItemBuilder customItemBuilder = CustomItems.getCustomItemBuilder(this.lootType.getCustomEnum());
		customItemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		customItemBuilder.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		customItemBuilder.setUnbreakable(true);
		customItemBuilder.setAmount(amount);
		customItemBuilder.setDisplayName(displayName);
		customItemBuilder.setLore(this.getItemInfo());
		ItemStack result = customItemBuilder.build();
		ItemUtil.setInt(result, "loot_table", this.loot_table_id);
		return result;
	}
	
	public List<String> getItemInfo(){
		ArrayList<String> result = new ArrayList<String>();
		int totalCount = 0;
		if(this.items!=null){
			Inventory countInventory = Bukkit.createInventory(null, this.items.length);
			for(ItemStack itemStack : this.items){
				if(itemStack==null) continue;
				countInventory.addItem(itemStack);
			}
			String line;
			for(ItemStack itemStack : countInventory){
				if(itemStack==null) continue;
				if(result.size()<5){
					line = RandomItemUtil.getDescriptiveItemString(itemStack);
					result.add(line);
				}
				totalCount++;
			}
			if(result.size()<totalCount){
				result.add(ChatColor.GRAY+""+ChatColor.ITALIC+"Und "+(totalCount-result.size())+" mehr...");
			}
		}
		if(result.size()==0){
			result.add(ChatColor.WHITE+"Leere Beutetabelle");
		}
		return result;
	}
	
	public void setItems(ItemStack[] items){
		this.items = items;
		ArrayList<String> arguments = new ArrayList<String>();
		for(int i = 0; i < this.items.length; i++){
			if(this.items[i]==null) continue;
			arguments.add("items["+i+"][slot]="+i);
			arguments.add("items["+i+"][item]="+SwissSMPUtils.encodeItemStack(this.items[i]));
		}
		DataSource.getResponse("loot/edit_table_items.php", new String[]{
			"id="+this.loot_table_id,
			"slots="+this.items.length,
			StringUtils.join(arguments, "&")
		});
		this.updateTokens();
	}
	
	public ItemStack[] getItems(){
		return this.items;
	}
	
	public void populate(Inventory inventory){
		this.populate(inventory,this.makeSeed(inventory));
	}
	
	public void populate(Inventory inventory, String seed){
		InventoryPopulator.populate(inventory, this.getItems(), this.min_rolls, this.max_rolls, seed);
	}
	
	public void openEditor(Player player){
		LootTableEditor.open(player, this);
	}
	
	public void remove(){
		loadedTables.remove(this.name.toLowerCase());
		DataSource.getResponse("loot/remove_table.php", new String[]{
				"id="+this.loot_table_id
		});
	}
	
	public void updateToken(ItemStack itemStack){
		ItemStack templateToken = this.getInventoryToken(1);
		itemStack.setItemMeta(templateToken.getItemMeta());
		itemStack.setDurability(templateToken.getDurability());
	}
	
	public void updateTokens(){
		ItemStack tokenStack = this.getInventoryToken(1);
		for(Player player : Bukkit.getOnlinePlayers()){
			for(ItemStack itemStack : player.getInventory()){
				if(ItemUtil.getInt(itemStack, "loot_table")!=this.loot_table_id) continue;
				itemStack.setItemMeta(tokenStack.getItemMeta());
				itemStack.setDurability(tokenStack.getDurability());
			}
		}
	}
	
	public String makeSeed(Inventory inventory){
		return this.loot_table_id+"-"+LootTable.makeStaticSeed(inventory);
	}
	
	public static String makeStaticSeed(Inventory inventory){
		InventoryHolder holder = inventory.getHolder();
		if(holder instanceof BlockState){
			Location location = ((BlockState)holder).getBlock().getLocation();
			return location.getBlockX()+","+location.getBlockY()+","+location.getBlockZ();
		}
		else{
			return UUID.randomUUID().toString();
		}
	}
	
	public static LootTable get(String name, boolean createIfMissing){
		if(name==null || name.isEmpty()) return null;
		if(loadedTables.containsKey(name.toLowerCase())) return loadedTables.get(name.toLowerCase());
		YamlConfiguration yamlConfiguration;
		yamlConfiguration = DataSource.getYamlResponse("loot/get_table.php", new String[]{
			"name="+URLEncoder.encode(name),
			"create_missing="+(createIfMissing?1:0)
		});
		if(yamlConfiguration==null || !yamlConfiguration.contains("loot_table")) return null;
		return new LootTable(yamlConfiguration.getConfigurationSection("loot_table"));
	}
	
	public static LootTable get(int loot_table_id){
		for(LootTable lootTable : loadedTables.values()){
			if(lootTable.loot_table_id==loot_table_id) return lootTable;
		}
		YamlConfiguration yamlConfiguration;
		yamlConfiguration = DataSource.getYamlResponse("loot/get_table.php", new String[]{
			"id="+loot_table_id,
			"create_missing=0"
		});
		if(yamlConfiguration==null || !yamlConfiguration.contains("loot_table")) return null;
		return new LootTable(yamlConfiguration.getConfigurationSection("loot_table"));
	}
	
	public static LootTable get(ItemStack tokenStack){
		return LootTable.get(ItemUtil.getInt(tokenStack, "loot_table"));
	}
}
