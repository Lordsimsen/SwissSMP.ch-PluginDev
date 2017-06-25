package ch.swisssmp.adventuredungeons.mmoitem;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import ch.swisssmp.utils.ConfigurationSection;

public class MmoItemManager {
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
		return MmoItemManager.getMaterialString(itemStack, false);
	}
	@SuppressWarnings("deprecation")
	public static String getMaterialString(ItemStack itemStack, boolean matchData){
		if(itemStack==null)
			return "";
		return MmoItemManager.getMaterialString(itemStack.getType(), itemStack.getData().getData(), matchData);
	}
	public static String getMaterialString(Material material){
		return MmoItemManager.getMaterialString(material, (byte) 0, false);
	}
	public static String getMaterialString(Material material, byte b){
		return MmoItemManager.getMaterialString(material, (byte) b, true);
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
	public static ItemStack getItemFromHybridID(ConfigurationSection dataSection){
		ItemStack itemStack;
		if(dataSection.contains("mmo_item_id")){
			return null;
		}
		else if(dataSection.contains("mc_id")){
			try{
				String enumData = dataSection.getString("mc_id");
				String[] enumSplit = enumData.split(":");
				String enumString = enumSplit[0];
				Material material = Material.valueOf(enumString);
				if(enumSplit.length>1){
					itemStack = new ItemStack(material, 1, (short) Short.parseShort(enumSplit[1]));
				}
				else{
					itemStack = new ItemStack(material);
				}
				if(dataSection.contains("name")){
					ItemMeta itemMeta = itemStack.getItemMeta();
					itemMeta.setDisplayName(dataSection.getString("name"));
					itemStack.setItemMeta(itemMeta);
				}
			}
			catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
		else{
			return null;
		}
		int amount = 1;
		if(dataSection.contains("amount")){
			amount = dataSection.getInt("amount");
		}
		if(dataSection.contains("max") && amount<1){
			int max = dataSection.getInt("max");
			if(max<1){
				return null;
			}
			int min = 1;
			if(dataSection.contains("min")){
				min = dataSection.getInt("min");
			}
			Random random = new Random();
			amount = random.nextInt(max-min+1)+min;
		}
		itemStack.setAmount(amount);
		if(itemStack.getAmount()<1)
			return null;
		return itemStack;
	}
}