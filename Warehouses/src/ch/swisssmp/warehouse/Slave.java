package ch.swisssmp.warehouse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.warehouse.filters.Filter;
import ch.swisssmp.warehouse.filters.FilterSetting;
import ch.swisssmp.warehouse.filters.FilterSettings;

public class Slave extends ChestItem {

	private FilterSettings settings;
	private List<Filter> filters = new ArrayList<Filter>();
	
	public FilterSettings getFilterSettings(){
		return settings;
	}
	
	public Collection<Filter> getFilters(){
		return new ArrayList<Filter>(this.filters);
	}
	
	public void setFilters(List<Filter> filters){
		this.filters = filters;
	}
	
	public boolean match(ItemStack itemStack, FilterSettings settings){
		if(settings.damage==FilterSetting.Exclude && itemStack.getItemMeta() instanceof Damageable && ((Damageable)itemStack.getItemMeta()).hasDamage()){
			return false;
		}
		else if(settings.damage==FilterSetting.Include && itemStack.getItemMeta() instanceof Damageable && !((Damageable)itemStack.getItemMeta()).hasDamage()){
			return false;
		}
		if(settings.enchantments==FilterSetting.Exclude && (itemStack.getItemMeta().hasEnchants() || itemStack.getType()==Material.ENCHANTED_BOOK)) return false;
		else if(settings.enchantments==FilterSetting.Include && !itemStack.getItemMeta().hasEnchants() && itemStack.getType()!=Material.ENCHANTED_BOOK) return false;
		if(filters.size()==0){
			return true;
		}
		for(Filter filter : this.filters){
			if(filter.match(itemStack, settings)) return true;
		}
		return false;
	}
	
	@Override
	protected void saveData(ConfigurationSection dataSection) {
		this.settings.save(dataSection);
		ConfigurationSection filtersSection = dataSection.createSection("filters");
		int index = 0;
		for(Filter filter : this.filters){
			ConfigurationSection filterSection = filtersSection.createSection("filter_"+index);
			filterSection.set("slot", filter.getSlot());
			filterSection.set("item", filter.getTemplateStack());
			index++;
		}
	}

	@Override
	protected void loadData(ConfigurationSection dataSection) {
		this.settings = new FilterSettings();
		this.settings.load(dataSection);
		ConfigurationSection filtersSection = dataSection.getConfigurationSection("filters");
		for(String key : filtersSection.getKeys(false)){
			ConfigurationSection filterSection = filtersSection.getConfigurationSection(key);
			int slot = filterSection.getInt("slot");
			ItemStack item = filterSection.getItemStack("item");
			Filter filter = new Filter(slot, item);
			this.filters.add(filter);
		}
	}
	
	public void highlightChests(){
		World world = getCollection().getWorld();
		Inventory inventory = this.getInventory();
		int size = inventory.getSize();
		int free = 0;
		for(int i = 0; i < size; i++){
			ItemStack item = inventory.getItem(i);
			if(item==null || item.getType()==Material.AIR) free++;
		}
		float space = free/(float)size;
		int red = Mathf.roundToInt(255*(1-space));
		int green = free>0 ? Mathf.roundToInt(255*space) : 0;
		int blue = free==0 ? 100 : 0;
		DustOptions slaveColor = new DustOptions(Color.fromRGB(red,green,blue),1);
		for(BlockVector chest : this.getChests()){
			world.spawnParticle(Particle.REDSTONE, new Location(world,chest.getX()+0.5,chest.getY()+1.5,chest.getZ()+0.5), 1, slaveColor);
		}
	}
	
	public void openEditor(Player player){
		SlaveFilterView.open(player, this);
	}
	
	public Inventory getInventory(){
		BlockVector vector = this.getChests().stream().findFirst().orElse(null);
		if(vector==null){
			return null;
		}
		Block block = getCollection().getWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
		if(!(block.getState() instanceof Chest)) return null;
		Chest chest = (Chest) block.getState();
		return chest.getInventory();
	}
	
	@Override
	public boolean addChest(BlockVector vector){
		if(!super.addChest(vector)) return false;
		for(Master master : Master.getBySlave(this)){
			if(!master.inRange(this)){
				master.removeSlave(this);
			}
		}
		return true;
	}
	
	@Override
	public void remove(BlockVector lastChest){
		World world = getCollection().getWorld();
		if(lastChest!=null){
			Location location = new Location(world,lastChest.getX()+0.5,lastChest.getY()+0.5,lastChest.getZ()+0.5);
			for(Filter filter : this.filters){
				world.dropItem(location, filter.getTemplateStack());
			}
		}
		super.remove(lastChest);
		for(Master master : Master.getBySlave(this)){
			master.removeSlave(this);
		}
	}

	public void reset(){
		World world = this.getCollection().getWorld();
		Vector vector = this.getChests().iterator().next().add(new Vector(0.5,1.5,0.5));
		Location location = new Location(world,vector.getX(),vector.getY(),vector.getZ());
		for(Filter filter : this.filters){
			world.dropItem(location, filter.getTemplateStack());
		}
		this.filters.clear();
		this.settings = new FilterSettings();
	}
	
	public static Slave get(Block block){
		ChestCollection<Slave> collection = SlaveCollections.getCollection(block.getWorld());
		if(collection==null) return null;
		return collection.getItem(new BlockVector(block.getX(),block.getY(),block.getZ()));
	}
	
	public static Slave create(Block block){
		ChestCollection<Slave> collection = SlaveCollections.getCollection(block.getWorld());
		if(collection==null) return null;
		Slave slave = new Slave();
		slave.setId(UUID.randomUUID());
		slave.settings = new FilterSettings();
		slave.setCollection(collection);
		collection.add(slave);
		slave.addChest(block);
		return slave;
	}
}
