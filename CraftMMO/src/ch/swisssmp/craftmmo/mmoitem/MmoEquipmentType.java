package ch.swisssmp.craftmmo.mmoitem;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum MmoEquipmentType {
HELMET,CHESTPLATE,LEGGINGS,BOOTS,WEAPON,HAND;
	
	public static MmoEquipmentType getType(ItemStack itemStack){
		Material material = itemStack.getType();
		String materialName = material.name().toLowerCase();
		if(materialName.contains("helmet")||materialName.contains("cap")){
			return HELMET;
		}
		else if(materialName.contains("chestplate")||materialName.contains("elytra")||materialName.contains("tunic")){
			return CHESTPLATE;
		}
		else if(materialName.contains("leggings")||materialName.contains("pants")){
			return LEGGINGS;
		}
		else if(materialName.contains("boots")){
			return BOOTS;
		}
		else if(materialName.contains("sword") || materialName.contains("bow") || materialName.contains("axe") || materialName.contains("pickaxe") || materialName.contains("hoe") || materialName.contains("shovel")){
			return WEAPON;
		}
		else return HAND;
	}
}
