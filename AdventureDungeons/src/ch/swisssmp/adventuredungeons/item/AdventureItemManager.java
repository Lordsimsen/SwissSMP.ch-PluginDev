package ch.swisssmp.adventuredungeons.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class AdventureItemManager {
	@SuppressWarnings("deprecation")
	public static MaterialData getMaterialData(String materialString){
		if(materialString==null) return null;
		else if(materialString.isEmpty()) return null;
		if(materialString.contains(":")){
			String[] parts = materialString.split(":");
			try{
				return new MaterialData(Material.valueOf(parts[0]), Byte.parseByte(parts[1]));
			}
			catch(Exception e){
				return null;
			}
		}
		else{
			return new MaterialData(Material.valueOf(materialString));
		}
	}
	public static String getMaterialString(ItemStack itemStack){
		return AdventureItemManager.getMaterialString(itemStack, false);
	}
	@SuppressWarnings("deprecation")
	public static String getMaterialString(ItemStack itemStack, boolean matchData){
		if(itemStack==null)
			return "";
		return AdventureItemManager.getMaterialString(itemStack.getType(), itemStack.getData().getData(), matchData);
	}
	public static String getMaterialString(Material material){
		return AdventureItemManager.getMaterialString(material, (byte) 0, false);
	}
	public static String getMaterialString(Material material, byte b){
		return AdventureItemManager.getMaterialString(material, (byte) b, true);
	}
	public static String getMaterialString(Material material, byte b, boolean matchData){
		if(material==null)
			return "";
		String result = material.toString();
		if(matchData){
			return result+":"+b;
		}
		else return result;
	}
}