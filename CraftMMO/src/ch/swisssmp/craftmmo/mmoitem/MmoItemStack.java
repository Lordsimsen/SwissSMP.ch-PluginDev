package ch.swisssmp.craftmmo.mmoitem;

import org.bukkit.inventory.ItemStack;

public class MmoItemStack {
	public MmoItem mmoItem;
	public ItemStack itemStack;
	
	private MmoItemStack(MmoItem mmoItem, ItemStack itemStack){
		this.mmoItem = mmoItem;
		this.itemStack = itemStack;
	}
	
	public static MmoItemStack get(ItemStack itemStack){
		if(itemStack==null){
			return null;
		}
		MmoItem mmoItem = MmoItem.get(itemStack);
		if(mmoItem==null){
			return null;
		}
		return new MmoItemStack(mmoItem, itemStack);
	}
	
	public static MmoItemStack get(MmoItem mmoItem, ItemStack itemStack){
		if(itemStack==null || mmoItem==null){
			return null;
		}
		return new MmoItemStack(mmoItem, itemStack);
	}
	
	public static MmoItemStack createEmpty(){
		return new MmoItemStack(null, null);
	}
}
