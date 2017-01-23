package ch.swisssmp.craftmmo.mmoitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;

import org.bukkit.inventory.ItemStack;

public class MmoChanceItem {
	private static Random globalRandom = new Random();
	private final ItemStack itemStack;
	private final int min;
	private final int max;
	private final Random random;
	public MmoChanceItem(ItemStack itemStack, int min_amount, int max_amount){
		this.itemStack = itemStack;
		this.min = min_amount;
		this.max = max_amount;
		this.random = new Random();
	}
	public ItemStack unpack(){
		if(itemStack==null)
			return null;
		int amount = random.nextInt((max - min) + 1) + min;
		if(amount<=0)
			return null;
		itemStack.setAmount(amount);
		return itemStack;
	}
	public static List<MmoChanceItem> getRandomItems(HashMap<MmoChanceItem, Double> items){
		List<MmoChanceItem> result = new ArrayList<MmoChanceItem>();
        for(Entry<MmoChanceItem, Double> entry : items.entrySet()){
        	double chance = entry.getValue();
        	if(globalRandom.nextDouble()*100<=chance){
        		result.add(entry.getKey());
        	}
        }
        return result;
	}
}
