package ch.swisssmp.loot.populator;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GenericPopulator extends InventoryPopulator{
	
	public GenericPopulator(Inventory inventory, ItemStack[] templateStacks, String seed){
		super(inventory, templateStacks, seed);
	}
	
	@Override
	protected void fillContainer(){
		inventory.clear();
		HashMap<Integer,ItemStack> overflow;
		int slot;
		for(ItemStack itemStack : this.itemStacks){
			int remainingAmount = itemStack.getAmount();
			while(remainingAmount>0){
				itemStack.setAmount(Math.min(remainingAmount, itemStack.getMaxStackSize()));
				remainingAmount-=itemStack.getAmount();
				slot = this.random.nextInt(this.inventory.getSize());
				if(this.inventory.getItem(slot)==null) this.inventory.setItem(slot, itemStack);
				else{
					overflow = inventory.addItem(itemStack.clone());
					if(overflow.size()>0) return;
				}
			}
		}
		this.spreadItems();
	}
	
	private void spreadItems(){
		ArrayList<Integer> emptySlots = new ArrayList<Integer>();
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for(int i = 0; i < this.inventory.getSize(); i++){
			if(this.inventory.getItem(i)!=null){
				items.add(this.inventory.getItem(i));
			}
			else{
				emptySlots.add(i);
			}
		}
		int spreadSlot;
		int spreadSlotIndex;
		int spreadLoops;
		ItemStack spreadStack;
		for(ItemStack itemStack : items){
			if(itemStack.getAmount()>1 && random.nextDouble()>0.3f){
				spreadLoops = Math.min(random.nextInt(Math.min(itemStack.getAmount()/2, 8)), emptySlots.size());
				for(int i = 0; i < spreadLoops ; i++){
					spreadSlotIndex = random.nextInt(emptySlots.size());
					spreadSlot = emptySlots.get(spreadSlotIndex);
					emptySlots.remove(spreadSlotIndex);
					spreadStack = itemStack.clone();
					spreadStack.setAmount(1);
					itemStack.setAmount(itemStack.getAmount()-1);
					this.inventory.setItem(spreadSlot, spreadStack);
				}
				if(emptySlots.size()==0) break;
			}
		}
	}
}
