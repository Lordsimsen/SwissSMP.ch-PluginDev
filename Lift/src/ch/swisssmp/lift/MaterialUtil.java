package ch.swisssmp.lift;

import org.bukkit.Material;

public class MaterialUtil {
	public static boolean isGlassBlock(Material material){
		switch(material){
		case GLASS:
		case BLACK_STAINED_GLASS:
		case BLUE_STAINED_GLASS:
		case BROWN_STAINED_GLASS:
		case CYAN_STAINED_GLASS:
		case GRAY_STAINED_GLASS:
		case GREEN_STAINED_GLASS:
		case LIGHT_BLUE_STAINED_GLASS:
		case LIGHT_GRAY_STAINED_GLASS:
		case LIME_STAINED_GLASS:
		case MAGENTA_STAINED_GLASS:
		case ORANGE_STAINED_GLASS:
		case PINK_STAINED_GLASS:
		case PURPLE_STAINED_GLASS:
		case RED_STAINED_GLASS:
		case WHITE_STAINED_GLASS:
		case YELLOW_STAINED_GLASS:
			return true;
		default: return false;
		}
	}
	public static boolean isSign(Material material){
		switch(material){
		case ACACIA_WALL_SIGN:
		case BIRCH_WALL_SIGN:
		case DARK_OAK_WALL_SIGN:
		case JUNGLE_WALL_SIGN:
		case OAK_WALL_SIGN:
		case SPRUCE_WALL_SIGN:
			return true;
		default:
			return false;
		}
	}
	public static boolean isButton(Material material){
		switch(material){
		case ACACIA_BUTTON:
		case BIRCH_BUTTON:
		case DARK_OAK_BUTTON:
		case JUNGLE_BUTTON:
		case OAK_BUTTON:
		case SPRUCE_BUTTON:
		case STONE_BUTTON:
			return true;
		default:
			return false;
		}
	}
	public static boolean isAllowedInShaft(Material material){
		if(MaterialUtil.isAir(material)) return true;
		if(MaterialUtil.isButton(material)) return true;
		if(MaterialUtil.isSign(material)) return true;
		if(MaterialUtil.isGlassBlock(material)) return true;
		if(MaterialUtil.isTrapdoor(material)) return true;
		
		switch(material){
		case TORCH:
		case WALL_TORCH:
		case REDSTONE_TORCH:
		case REDSTONE_WALL_TORCH:
		case WATER:
		case LADDER:
			return true;
		default:
			return false;
		}
	}
	public static boolean isAir(Material material){
		switch(material){
		case AIR: return true;
		case CAVE_AIR: return true;
		case VOID_AIR: return true;
		default: return false;
		}
	}
	public static boolean isTrapdoor(Material material){
		switch(material){
		case ACACIA_TRAPDOOR:
		case BIRCH_TRAPDOOR:
		case DARK_OAK_TRAPDOOR:
		case IRON_TRAPDOOR:
		case JUNGLE_TRAPDOOR:
		case OAK_TRAPDOOR:
		case SPRUCE_TRAPDOOR: return true;
		default: return false;
		}
	}
	public static double getLiftSpeed(Material material){
		switch(material){
		case DIAMOND_BLOCK: return 8.0;
		case GOLD_BLOCK: return 7.2;
		case EMERALD_BLOCK: return 6.4;
		case REDSTONE_BLOCK: return 5.6;
		case IRON_BLOCK: return 4.8;
		case LAPIS_BLOCK: return 4.0;
		case OBSIDIAN: return 3.2;
		default: return -1;
		}
	}
	public static boolean isIntermediateFloor(Material material){
		return MaterialUtil.isGlassBlock(material);
	}
	public static boolean isGroundFloor(Material material){
		return getLiftSpeed(material)>0;
	}
}