package ch.swisssmp.craftmmo.mmoitem;

import org.bukkit.Material;

public class MmoVanillaItemInfo {
	public static double getBaseDamage(Material material){
		switch(material){
		case WOOD_SWORD:
		case GOLD_SWORD:
			return 4;
		case STONE_SWORD:
			return 5;
		case IRON_SWORD:
			return 6;
		case DIAMOND_SWORD:
			return 7;
		case WOOD_PICKAXE:
		case GOLD_PICKAXE:
			return 2;
		case STONE_PICKAXE:
			return 3;
		case IRON_PICKAXE:
			return 4;
		case DIAMOND_PICKAXE:
			return 5;
		case WOOD_AXE:
		case GOLD_AXE:
			return 7;
		case STONE_AXE:
			return 9;
		case IRON_AXE:
			return 9;
		case DIAMOND_AXE:
			return 9;
		case WOOD_SPADE:
		case GOLD_SPADE:
			return 2.5;
		case STONE_SPADE:
			return 3.5;
		case IRON_SPADE:
			return 4.5;
		case DIAMOND_SPADE:
			return 5.5;
		case WOOD_HOE:
		case STONE_HOE:
		case IRON_HOE:
		case GOLD_HOE:
		case DIAMOND_HOE:
			return 1;
		default:
			return 1;
		}
	}
	public static double getBaseSpeed(Material material){
		switch(material){
		case WOOD_SWORD:
		case STONE_SWORD:
		case IRON_SWORD:
		case GOLD_SWORD:
		case DIAMOND_SWORD:
			return 1.6;
		case WOOD_PICKAXE:
		case STONE_PICKAXE:
		case IRON_PICKAXE:
		case GOLD_PICKAXE:
		case DIAMOND_PICKAXE:
			return 1.2;
		case WOOD_AXE:
		case STONE_AXE:
			return 0.8;
		case IRON_AXE:
			return 0.9;
		case GOLD_AXE:
			return 1;
		case DIAMOND_AXE:
			return 1;
		case WOOD_SPADE:
		case STONE_SPADE:
		case IRON_SPADE:
		case GOLD_SPADE:
		case DIAMOND_SPADE:
			return 1;
		case WOOD_HOE:
			return 1;
		case STONE_HOE:
			return 2;
		case IRON_HOE:
			return 3;
		case GOLD_HOE:
			return 1;
		case DIAMOND_HOE:
			return 4;
		default:
			return 4;
		}
	}
}
