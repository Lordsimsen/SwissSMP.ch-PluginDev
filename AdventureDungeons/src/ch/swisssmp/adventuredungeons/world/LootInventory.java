package ch.swisssmp.adventuredungeons.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.block.AdventureBlockUtil;
import ch.swisssmp.adventuredungeons.util.AdventureResourceManager;

public class LootInventory implements Runnable{
	
	private final DungeonInstance dungeonInstance;
	private final Player player;
	private final String action;
	private final Block block;
	private final Inventory inventory;
	private final ArrayList<ItemStack> original_items;
	private final boolean global;
	
	private int task_id;
	
	LootInventory(DungeonInstance dungeonInstance, Player player, String action, boolean global, Block block, Inventory inventory){
		this.dungeonInstance = dungeonInstance;
		this.player = player;
		this.action = action;
		this.block = block;
		this.inventory = inventory;
		original_items = new ArrayList<ItemStack>();
		for(ItemStack itemStack : inventory){
			if(itemStack==null)
				continue;
			original_items.add(itemStack);
		}
		this.global = global;
	}
	@Override
	public void run() {
		if(block.getWorld()!=null){
			close();
		}
	}
	public void close(){
		this.dungeonInstance.closeLootInventory(this.block, this);
		World world = block.getWorld();
		for(ItemStack itemStack : inventory){
			if(itemStack!=null && !original_items.contains(itemStack)){
				world.dropItem(block.getLocation().add(0.5, 1.5, 0.5), itemStack);
			}
		}
		Bukkit.getScheduler().cancelTask(task_id);
	}
	
	public void setTaskId(int task_id){
		this.task_id = task_id;
	}
	
	public Inventory getInventory(){
		return this.inventory;
	}
	
	public Player getPlayer(){
		return this.player;
	}
	
	public boolean isGlobal(){
		return this.global;
	}
	
	public String getAction(){
		return this.action;
	}
	
	public static boolean open(Player player, String action, Block block){
		DungeonInstance dungeonInstance = Dungeon.getInstance(player);
		if(dungeonInstance==null) return false;
		LootInventory lootInventory = dungeonInstance.getLootInventory(player, "oninteract", block);
		if(lootInventory==null){
			ApplicableRegionSet regions = WorldGuardPlugin.inst().getRegionManager(block.getWorld()).getApplicableRegions(block.getLocation());
			List<String> regionNames = new ArrayList<String>();
			for(ProtectedRegion region : regions){
				regionNames.add("regions[]="+region.getId());
			}
			return AdventureResourceManager.processYamlResponse(player.getUniqueId(), "adventure/treasure.php", new String[]{
					"player="+player.getUniqueId().toString(),
					"mc_enum="+AdventureBlockUtil.getMaterialString(block, true),
					"action=oninteract",
					"x="+block.getX(),
					"y="+block.getY(),
					"z="+block.getZ(),
					"dungeon="+dungeonInstance.getDungeonId(),
					"world="+dungeonInstance.getWorldName(),
					String.join("&", regionNames)
					}, "loot");
		}
		else{
			AdventureDungeons.info("opening LootInventory from Cache");
			player.openInventory(lootInventory.inventory);
			return true;
		}
	}
}
