package ch.swisssmp.world;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.Random;
import ch.swisssmp.utils.YamlConfiguration;

public class WorldEditor extends InventoryView implements Listener{
	private static Random random = new Random();
	
	private final String worldName;
	private final Player player;
	private final Inventory inventory;
	
	private Environment environment;
	private boolean generate_structures;
	private WorldType worldType;
	
	private String seed;
	
	private WorldEditor(String worldName, Player player){
		this.worldName = worldName;
		this.player = player;
		this.inventory = Bukkit.createInventory(null, 9, worldName);
		this.loadSettings();
		this.updateSeedItem();
		this.updateCheckmarkItem();
	}
	
	public void setSeed(String seed){
		this.seed = seed;
		this.updateSeedItem();
	}
	
	private void loadSettings(){
		YamlConfiguration yamlConfiguration = WorldManager.getWorldSettings(this.worldName);
		if(yamlConfiguration!=null && yamlConfiguration.contains("world")){
			ConfigurationSection dataSection = yamlConfiguration.getConfigurationSection("world");
			this.environment = Environment.valueOf(dataSection.getString("environment"));
			this.generate_structures = dataSection.getBoolean("generate_structures");
			this.worldType = WorldType.valueOf(dataSection.getString("world_type"));
			this.seed = dataSection.getString("seed");
		}
		else{
			this.environment = Environment.NORMAL;
			this.generate_structures = true;
			this.worldType = WorldType.NORMAL;
			this.seed = String.valueOf(random.nextLong());
		}
		this.updateEnvironmentItem();
		this.updateStructuresItem();
		this.updateWorldTypeItem();
	}
	
	private void generateWorld(){
		Bukkit.createWorld(new WorldCreator(this.worldName)
				.environment(this.environment)
				.generateStructures(this.generate_structures)
				.type(this.worldType)
				.seed(StringUtils.isNumeric(this.seed) ? Long.valueOf(this.seed) : this.seed.hashCode()));
		player.sendMessage("[WorldManager] "+ChatColor.GREEN+"Welt "+this.worldName+" generiert.");
	}
	
	private void closeNextTick(){
		Bukkit.getScheduler().runTaskLater(WorldManager.plugin, new Runnable(){public void run(){close();}}, 1L);
	}
	
	@EventHandler
	private void onInventoryClick(InventoryClickEvent event){
		if(event.getView()!=this) return;
		event.setCancelled(true);
		if(Bukkit.getWorld(this.worldName)!=null || event.getInventory()!=this.inventory){
			if(event.getSlot()==8) this.closeNextTick();
			return; //Do not allow editing when World is loaded
		}
		switch(event.getSlot()){
		case 0: this.cycleEnvironment(); break;
		case 1: this.toggleStructures(); break;
		case 2: this.cycleWorldType(); break;
		case 8: this.generateWorld(); this.closeNextTick(); break;
		default: break;
		}
	}
	
	@EventHandler
	private void onInventoryDrag(InventoryDragEvent event){
		if(event.getView()!=this) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	private void onInventoryClose(InventoryCloseEvent event){
		if(event.getInventory()!=this.inventory) return;
		HandlerList.unregisterAll(this);
	}
	
	@Override
	public Inventory getBottomInventory() {
		return this.player.getInventory();
	}

	@Override
	public HumanEntity getPlayer() {
		return this.player;
	}

	@Override
	public Inventory getTopInventory() {
		return this.inventory;
	}

	@Override
	public InventoryType getType() {
		return InventoryType.CHEST;
	}
	
	private void cycleEnvironment(){
		switch(this.environment){
		case NORMAL: this.environment = Environment.NETHER; break;
		case NETHER: this.environment = Environment.THE_END; break;
		case THE_END: this.environment = Environment.NORMAL; break;
		}
		this.updateEnvironmentItem();
	}
	
	private void toggleStructures(){
		this.generate_structures = !this.generate_structures;
		this.updateStructuresItem();
	}
	
	private void cycleWorldType(){
		switch(this.worldType){
		case NORMAL: this.worldType = WorldType.LARGE_BIOMES; break;
		case LARGE_BIOMES: this.worldType = WorldType.AMPLIFIED; break;
		case AMPLIFIED: this.worldType = WorldType.FLAT; break;
		case FLAT: this.worldType = WorldType.NORMAL; break;
		default: this.worldType = WorldType.NORMAL; break;
		}
		this.updateWorldTypeItem();
	}
	
	private void updateEnvironmentItem(){
		CustomItemBuilder itemBuilder;
		switch(this.environment){
		case NORMAL: itemBuilder = CustomItems.getCustomItemBuilder("WORLD_OVERWORLD"); itemBuilder.setDisplayName(ChatColor.GREEN+"Normale Welt"); break;
		case NETHER: itemBuilder = CustomItems.getCustomItemBuilder("WORLD_NETHER"); itemBuilder.setDisplayName(ChatColor.RED+"Nether"); break;
		case THE_END: itemBuilder = CustomItems.getCustomItemBuilder("WORLD_THE_END"); itemBuilder.setDisplayName(ChatColor.YELLOW+"The End"); break;
		default: return;
		}
		itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		this.inventory.setItem(0, itemBuilder.build());
	}
	
	private void updateStructuresItem(){
		ItemStack itemStack;
		String displayName;
		if(this.generate_structures){
			itemStack = new ItemStack(Material.SMOOTH_BRICK);
			displayName = ChatColor.GREEN+"Generiere Strukturen";
		}
		else{
			itemStack = new ItemStack(Material.BARRIER);
			displayName = ChatColor.RED+"Generiere keine Strukturen";
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(displayName);
		itemStack.setItemMeta(itemMeta);
		this.inventory.setItem(1, itemStack);
	}
	
	private void updateWorldTypeItem(){
		ItemStack itemStack;
		String displayName;
		switch(this.worldType){
		case NORMAL:
			itemStack = new ItemStack(Material.GRASS);
			displayName = ChatColor.GREEN+"Normal";
			break;
		case LARGE_BIOMES:
			itemStack = new ItemStack(Material.LEAVES);
			displayName = ChatColor.LIGHT_PURPLE+"Grosse Biome";
			break;
		case AMPLIFIED:
			itemStack = new ItemStack(Material.COBBLESTONE_STAIRS);
			displayName = ChatColor.GREEN+"Zerklüftet";
			break;
		case FLAT:
			itemStack = new ItemStack(Material.CARPET);
			displayName = ChatColor.GREEN+"Flach";
			break;
		default:
			return;
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(displayName);
		itemStack.setItemMeta(itemMeta);
		this.inventory.setItem(2, itemStack);
	}
	
	private void updateSeedItem(){
		CustomItemBuilder customItemBuilder = new CustomItemBuilder();
		customItemBuilder.setDisplayName(ChatColor.WHITE+""+this.seed);
		customItemBuilder.setAmount(1);
		customItemBuilder.setMaterial(Material.SEEDS);
		customItemBuilder.setLore(Arrays.asList("Der Seed bestimmt die","Form der Landschaft.","Zwei Welten mit dem gleichen","Seed sehen genau gleich aus."));
		this.inventory.setItem(7, customItemBuilder.build());
	}
	
	private void updateCheckmarkItem(){
		if(Bukkit.getWorld(this.worldName)==null){
			CustomItemBuilder customItemBuilder = CustomItems.getCustomItemBuilder("CHECKMARK");
			customItemBuilder.setDisplayName(ChatColor.WHITE+"Welt jetzt generieren");
			customItemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			this.inventory.setItem(8, customItemBuilder.build());
		}
		else{
			CustomItemBuilder customItemBuilder = new CustomItemBuilder();
			customItemBuilder.setMaterial(Material.BARRIER);
			customItemBuilder.setDisplayName(ChatColor.WHITE+"Schliessen");
			customItemBuilder.setAmount(1);
			this.inventory.setItem(8, customItemBuilder.build());
		}
	}
	
	protected static WorldEditor open(String worldName, Player player){
		WorldEditor result = new WorldEditor(worldName, player);
		Bukkit.getPluginManager().registerEvents(result, WorldManager.plugin);
		player.openInventory(result);
		return result;
	}
}
