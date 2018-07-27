package ch.swisssmp.loot.populator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.Random;

public abstract class InventoryPopulator {
	protected final Inventory inventory;
	protected final ItemStack[] templateStacks;
	protected final Random random;
	
	protected final List<ItemStack> itemStacks = new ArrayList<ItemStack>();
	
	protected InventoryPopulator(Inventory inventory, ItemStack[] templateStacks, String seed){
		this.inventory = inventory;
		this.templateStacks = templateStacks;
		this.random = new Random();
		this.random.setSeed(seed);
	}
	
	private void generateItems(){
		ItemStack itemStack;
		for(ItemStack template : this.templateStacks){
			if(template==null) continue;
			itemStack = this.buildItemStack(template);
			if(itemStack!=null) this.itemStacks.add(itemStack);
		}
	}
	
	private void generateItems(int rolls){
		List<ItemStack> rolledItems = new ArrayList<ItemStack>();
		HashMap<ItemStack,Double> weightMap;
		ItemStack itemStack;
		double weight;
		double defaultWeight = 1/(float)this.templateStacks.length;
		double randomValue;
		double totalWeight;
		for(int i = 0; i < rolls; i++){
			weightMap = new HashMap<ItemStack,Double>();
			totalWeight = 0;
			for(ItemStack template : this.templateStacks){
				if(template==null) continue;
				weight = RandomItemHandler.getItemChance(template, defaultWeight);
				weightMap.put(template, weight);
				totalWeight+=weight;
			}
			totalWeight = Math.max(totalWeight, 1);
			randomValue = this.random.nextDouble()*totalWeight;
			for(Entry<ItemStack,Double> entry : weightMap.entrySet()){
				if(entry.getValue()<randomValue) randomValue-=entry.getValue();
				rolledItems.add(entry.getKey().clone());
				break;
			}
		}
		for(ItemStack rolledItem : rolledItems){
			itemStack = this.buildItemStack(rolledItem, 1);
			if(itemStack!=null) this.itemStacks.add(itemStack);
		}
	}
	
	protected abstract void fillContainer();
	
	private ItemStack buildItemStack(ItemStack template){
		return this.buildItemStack(template,-1);
	}
	private ItemStack buildItemStack(ItemStack template, double chanceOverride){
		return RandomItemHandler.buildItemStack(template,this.random, chanceOverride);
	}
	
	public static void populate(Inventory inventory, ItemStack[] itemStacks, int min_rolls, int max_rolls, String seed){
		InventoryPopulator result;
		switch(inventory.getType()){
		case CHEST:
			result = new GenericPopulator(inventory, itemStacks, seed); break;
		case BREWING:
			if(inventory instanceof BrewerInventory) result = new BrewingPopulator((BrewerInventory)inventory, itemStacks, seed);
			else result = new GenericPopulator(inventory, itemStacks, seed);
			break;
		case DROPPER:
		case DISPENSER:
			result = new DispenserPopulator(inventory, itemStacks, seed); break;
		case FURNACE:
			if(inventory instanceof FurnaceInventory) result = new FurnacePopulator((FurnaceInventory)inventory, itemStacks, seed);
			else result = new GenericPopulator(inventory, itemStacks, seed);
			break;
		case HOPPER:
			result = new HopperPopulator(inventory, itemStacks, seed); break;
		case SHULKER_BOX:
			result = new ShulkerPopulator(inventory, itemStacks, seed); break;
		default:
			result = new GenericPopulator(inventory, itemStacks, seed); break;
		}
		if(min_rolls>0 || max_rolls>0){
			min_rolls = Math.max(min_rolls, 0);
			max_rolls = Math.max(min_rolls, max_rolls);
			Random random = new Random();
			random.setSeed(seed);
			int rolls = random.nextInt(max_rolls-min_rolls);
			result.generateItems(rolls);
		}
		else{
			result.generateItems();
		}
		result.fillContainer();
	}
}
