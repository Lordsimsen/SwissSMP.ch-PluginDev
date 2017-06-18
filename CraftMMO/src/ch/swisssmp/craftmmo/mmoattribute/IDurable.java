package ch.swisssmp.craftmmo.mmoattribute;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.inventory.ItemStack;

import ch.swisssmp.craftmmo.mmoitem.MmoItem;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public interface IDurable {
	public int getTotalDurability();
	public HashMap<MmoDurability, Integer> getDurabilityMap();
	public String getDurabilityLabel();
	public static void setDurability(ItemStack itemStack, int durability) {
		if(itemStack==null){
			return;
		}
		NBTTagCompound nbttagcompound = MmoItem.getSaveData(itemStack);
		nbttagcompound.setInt("durability", Math.max(0, durability));
		MmoItem.applySaveData(nbttagcompound, itemStack);
	}
	public static void changeDurability(ItemStack itemStack, int durability) {
		if(itemStack==null){
			return;
		}
		NBTTagCompound nbttagcompound = MmoItem.getSaveData(itemStack);
		if(!nbttagcompound.hasKey("durability")){
			return;
		}
		int old_durability = nbttagcompound.getInt("durability");
		int new_durability = Math.max(0, old_durability+durability);
		nbttagcompound.setInt("durability", new_durability);
		MmoItem.applySaveData(nbttagcompound, itemStack);
	}
	public static String getDurabilityBar(ItemStack itemStack){
		IDurable iDurable = IDurable.get(itemStack);
		if(iDurable==null && itemStack!=null){
			return "";
		}
		String durabilityLabel = iDurable.getDurabilityLabel();
		String result = ChatColor.WHITE+durabilityLabel+": ";
		MmoDurability[] durabilityOrder = MmoDurability.getOrder();
		int durability = IDurable.getDurability(itemStack);
		int totalDurability = iDurable.getTotalDurability();
		HashMap<MmoDurability, Integer> durabilityMap = iDurable.getDurabilityMap();
		if(durabilityMap!=null && durability>0){
			double progress = 0;
			for(MmoDurability durabilityPart : durabilityOrder){
				if(!durabilityMap.containsKey(durabilityPart)){
					continue;
				}
				Integer value = durabilityMap.get(durabilityPart);
				Float percentage = (float)value/totalDurability;
				Integer length = Math.round(percentage*20);
				result+=durabilityPart.getColor();
				for(int i = 0; i < length && progress<=durability; i++){
					result+="I";
					progress+=(totalDurability/20d);
				}
			}
		}
		result+=ChatColor.RESET;
		return result;
	}
	public static IDurable get(ItemStack itemStack){
		MmoItem mmoItem = MmoItem.get(itemStack);
		if(mmoItem==null){
			return null;
		}
		else if(!(mmoItem instanceof IDurable)){
			return null;
		}
		else return (IDurable)mmoItem;
	}
	public static int getDurability(ItemStack itemStack){
		NBTTagCompound saveData = MmoItem.getSaveData(itemStack);
		if(saveData==null){
			return -1;
		}
		else return saveData.getInt("durability");
	}
	public static int getTotalDurability(ItemStack itemStack){
		IDurable iDurable = IDurable.get(itemStack);
		if(iDurable==null){
			return -1;
		}
		else return iDurable.getTotalDurability();
	}
	public static HashMap<MmoDurability, Integer> getDurabilityMap(ItemStack itemStack){
		IDurable iDurable = IDurable.get(itemStack);
		if(iDurable==null){
			return null;
		}
		else return iDurable.getDurabilityMap();
	}
	public static String getDurabilityLabel(ItemStack itemStack){
		IDurable iDurable = IDurable.get(itemStack);
		if(iDurable==null){
			return "";
		}
		else return iDurable.getDurabilityLabel();
	}
	public static MmoDurability getDurabilityLevel(ItemStack itemStack){
		IDurable iDurable = IDurable.get(itemStack);
		return IDurable.getDurabilityLevel(iDurable, itemStack);
	}
	public static MmoDurability getDurabilityLevel(IDurable iDurable, ItemStack itemStack){
		if(iDurable==null){
			return null;
		}
		int durability = IDurable.getDurability(itemStack);
		if(durability==0){
			return null;
		}
		MmoDurability lastChecked = null;
		for(Entry<MmoDurability, Integer> entry : iDurable.getDurabilityMap().entrySet()){
			durability -= entry.getValue();
			lastChecked = entry.getKey();
			if(durability<=0){
				return entry.getKey();
			}
		}
		return lastChecked;
	}
}
