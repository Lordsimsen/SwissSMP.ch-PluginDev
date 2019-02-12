package ch.swisssmp.warehouse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.warehouse.filters.FilterSettings;

public class Master extends ChestItem {

	private UUID player_uuid;
	private FilterSettings settings;
	private final List<Slave> slaves =  new ArrayList<Slave>();
	
	public Master(){
		
	}
	
	public Master(UUID player_uuid){
		this.player_uuid = player_uuid;
	}
	
	public UUID getPlayerId(){
		return player_uuid;
	}
	
	public FilterSettings getFilterSettings(){
		return settings;
	}
	
	public Collection<Slave> getSlaves(){
		return new ArrayList<Slave>(slaves);
	}
	
	public void run(boolean animate){
		World world = this.getCollection().getWorld();
		List<ItemStack> items = new ArrayList<ItemStack>();
		for(BlockVector vector : this.getChests()){
			Block block = world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
			if(!(block.getState() instanceof Chest)){
				Bukkit.getLogger().info(WarehousesPlugin.getPrefix()+vector.getBlockX()+","+vector.getBlockY()+","+vector.getBlockZ()+" is not a Chest.");
				continue;
			}
			Chest chest = (Chest) block.getState();
			for(ItemStack item : chest.getBlockInventory()){
				if(item==null) continue;
				items.add(item);
			}
		}
		this.run(items, animate);
	}
	
	private void run(List<ItemStack> items, boolean animate){
		ChestCollection<?> collection = getCollection();
		World world = collection.getWorld();
		ChestCollection<Slave> slavesCollection = SlaveCollections.getCollection(world);
		if(slavesCollection==null){
			Bukkit.getLogger().info(WarehousesPlugin.getPrefix()+"SlaveCollection for World "+world.getName()+" not found!");
			return;
		}
		for(Slave slave : this.slaves){
			Inventory inventory = slave.getInventory();
			FilterSettings filterSettings = FilterSettings.combine(settings, slave.getFilterSettings());
			boolean chestChanged = false;
			for(ItemStack itemStack : items){
				if(itemStack==null || itemStack.getAmount()==0 || itemStack.getType()==Material.AIR || !slave.match(itemStack, filterSettings)){
					continue;
				}
				HashMap<Integer,ItemStack> overflow = inventory.addItem(itemStack.clone());
				int prevAmount = itemStack.getAmount();
				itemStack.setAmount(overflow.size()>0 ? overflow.get(0).getAmount() : 0);
				chestChanged = chestChanged || prevAmount!=itemStack.getAmount();
			}
			if(chestChanged && animate){
				long delay = Mathf.roundToInt(Math.sqrt(getDistance(slave))*1.5f);
				Bukkit.getScheduler().runTaskLater(WarehousesPlugin.getInstance(), ()->{
					SlaveChestsAnimationRoutine.addSlave(slave);
				}, delay);
			}
		}
	}
	
	public boolean inRange(Slave slave){
		double distance = getDistance(slave);
		return distance>=0 && distance<=400;
	}
	
	public double getDistance(Slave slave){
		World world = this.getCollection().getWorld();
		if(slave.getCollection().getWorld()!=world) return -1;
		BlockVector slaveVector = slave.getChests().stream().findFirst().orElse(null);
		BlockVector masterVector = this.getChests().stream().findFirst().orElse(null);
		Block slaveBlock = world.getBlockAt(slaveVector.getBlockX(), slaveVector.getBlockY(), slaveVector.getBlockZ());
		Block masterBlock = world.getBlockAt(masterVector.getBlockX(), masterVector.getBlockY(), masterVector.getBlockZ());
		return slaveBlock.getLocation().distanceSquared(masterBlock.getLocation());
	}
	
	public boolean addSlave(Slave slave){
		if(!inRange(slave)) return false;
		slaves.add(slave);
		ItemManager.updateWarehouseTools();
		return true;
	}
	
	public void removeSlave(Slave slave){
		slaves.remove(slave);
		ItemManager.updateWarehouseTools();
	}
	
	@Override
	protected void saveData(ConfigurationSection dataSection) {
		dataSection.set("player", player_uuid.toString());
		this.settings.save(dataSection);
		List<String> slaves = new ArrayList<String>();
		for(Slave slave : this.slaves){
			slaves.add(slave.getId().toString());
		}
		dataSection.set("slaves", slaves);
	}

	@Override
	protected void loadData(ConfigurationSection dataSection) {
		this.player_uuid = UUID.fromString(dataSection.getString("player"));
		this.settings = new FilterSettings();
		this.settings.load(dataSection);
		List<String> slavesList = dataSection.getStringList("slaves");
		if(slavesList==null) return;
		World world = getCollection().getWorld();
		ChestCollection<Slave> slavesCollection = SlaveCollections.getCollection(world);
		if(slavesCollection==null) return;
		for(String entry : slavesList){
			UUID slaveId = UUID.fromString(entry);
			Slave slave = slavesCollection.getItem(slaveId);
			if(slave==null) continue;
			this.slaves.add(slave);
		}
	}
	
	@Override
	public void remove(BlockVector lastChest){
		super.remove(lastChest);
		ItemManager.updateWarehouseTools();
	}
	
	public void highlightChests(){
		World world = getCollection().getWorld();
		DustOptions masterColor = new DustOptions(Color.BLUE,1);
		for(BlockVector chest : this.getChests()){
			world.spawnParticle(Particle.REDSTONE, new Location(world,chest.getX()+0.5,chest.getY()+1.5,chest.getZ()+0.5), 1, masterColor);
		}
		for(Slave slave : this.getSlaves()){
			slave.highlightChests();
		}
	}
	
	public static Master get(World world, UUID master_id){
		ChestCollection<Master> collection = MasterCollections.getCollection(world);
		if(collection!=null) collection.getItem(master_id);
		for(World other : Bukkit.getWorlds()){
			if(other==world) continue;
			ChestCollection<Master> otherCollection = MasterCollections.getCollection(other);
			if(otherCollection==null) continue;
			Master result = otherCollection.getItem(master_id);
			if(result==null) continue;
			return result;
		}
		return null;
	}
	
	public static Master get(Block block){
		ChestCollection<Master> collection = MasterCollections.getCollection(block.getWorld());
		if(collection==null){
			Bukkit.getLogger().info(WarehousesPlugin.getPrefix()+"ChestCollection für Welt "+block.getWorld().getName()+" nicht gefunden.");
			return null;
		}
		return collection.getItem(new BlockVector(block.getX(),block.getY(),block.getZ()));
	}
	
	public static Collection<Master> getBySlave(Slave slave){
		ChestCollection<Master> collection = MasterCollections.getCollection(slave.getCollection().getWorld());
		if(collection==null){
			return null;
		}
		Collection<Master> result = new ArrayList<Master>();
		for(Master master : collection.getItems()){
			if(!master.slaves.contains(slave)) continue;
			result.add(master);
		}
		return result;
	}
	
	public static Master get(UUID master_id){
		for(World world : Bukkit.getWorlds()){
			ChestCollection<Master> collection = MasterCollections.getCollection(world);
			if(collection==null) continue;
			Master result = collection.getItem(master_id);
			if(result==null) continue;
			return result;
		}
		return null;
	}
	
	public static Master create(Player player, Block block){
		ChestCollection<Master> collection = MasterCollections.getCollection(block.getWorld());
		if(collection==null){
			Bukkit.getLogger().info(WarehousesPlugin.getPrefix()+"ChestCollection für Welt "+block.getWorld().getName()+" nicht gefunden.");
			return null;
		}
		Master master = new Master(player.getUniqueId());
		master.setId(UUID.randomUUID());
		master.settings = new FilterSettings();
		master.setCollection(collection);
		collection.add(master);
		master.addChest(block);
		return master;
	}
}
