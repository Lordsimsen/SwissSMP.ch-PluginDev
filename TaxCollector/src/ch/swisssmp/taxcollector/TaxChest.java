package ch.swisssmp.taxcollector;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TaxChest {
	
	private final int city_id;
	private final String worldName;
	private final int x;
	private final int y;
	private final int z;
	
	public TaxChest(int city_id, String worldName, int x, int y, int z){
		this.city_id = city_id;
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getCityId(){
		return this.city_id;
	}
	
	public Block getBlock(){
		World world = Bukkit.getWorld(this.worldName);
		if(world==null) return null;
		return world.getBlockAt(x,y,z);
	}
	
	public Chest getChest(){
		Block block = this.getBlock();
		if(block==null) return null;
		if(!(block.getState() instanceof Chest)) return null;
		return (Chest)block.getState();
	}
	
	public String getWorldName(){
		return this.worldName;
	}
	
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
	
	public int getZ(){
		return this.z;
	}
	
	public void addItem(ItemStack itemStack){
		Chest chest = this.getChest();
		if(chest==null){
			Bukkit.getLogger().info("[TaxCollector] Konnte ItemStack "+itemStack.getAmount()+" "+itemStack.getType().toString()+" nicht hinzufügen.");
			Bukkit.getLogger().info("[TaxCollector] Kiste "+worldName+" "+x+","+y+","+z+" nicht gefunden.");
			return;
		}
		Bukkit.getLogger().info("[TaxCollector] ItemStack "+itemStack.getAmount()+" "+itemStack.getType().toString()+" zu "+this.city_id+" hinzugefügt.");
		chest.getInventory().addItem(itemStack);
	}
	
	public ItemStack[] removeContents(){
		Chest chest = this.getChest();
		if(chest==null){
			Bukkit.getLogger().info("[TaxCollector] Kiste "+worldName+" "+x+","+y+","+z+" nicht gefunden.");
			return new ItemStack[0];
		}
		Inventory inventory = chest.getInventory();
		List<ItemStack> result = new ArrayList<ItemStack>();
		for(int i = 0; i < inventory.getSize(); i++){
			if(inventory.getItem(i)==null) continue;
			result.add(inventory.getItem(i).clone());
		}
		this.clear(inventory);
		return result.toArray(new ItemStack[result.size()]);
	}
	
	private void clear(Inventory inventory){
		if(inventory.getHolder() instanceof DoubleChest){
			Bukkit.getLogger().info("[TaxCollector] Clearing the fucking DoubleChest inventory.");
			DoubleChest doubleChest = (DoubleChest) inventory.getHolder();
			Chest left = (Chest)doubleChest.getLeftSide();
			Chest right = (Chest)doubleChest.getRightSide();
			left.getInventory().clear();
			left.getBlockInventory().clear();
			left.update();
			right.getInventory().clear();
			right.getBlockInventory().clear();
			right.update();
		}
		else if(inventory.getHolder() instanceof Chest){
			Bukkit.getLogger().info("[TaxCollector] Clearing the fucking Chest inventory.");
			Chest theFuckingAssholeChest = (Chest)inventory.getHolder();
			theFuckingAssholeChest.getInventory();
			theFuckingAssholeChest.getBlockInventory().clear();
			theFuckingAssholeChest.update();
		}
	}
}
