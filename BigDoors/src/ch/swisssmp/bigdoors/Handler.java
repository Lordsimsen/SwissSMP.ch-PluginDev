package ch.swisssmp.bigdoors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public enum Handler {
LEVER,STONE_BUTTON,WOOD_BUTTON,STONE_PLATE,WOOD_PLATE,GOLD_PLATE,IRON_PLATE,TRIPWIRE_HOOK;
	@Override
	public String toString(){
		switch(this){
		case LEVER:
			return "LEVER";
		case STONE_BUTTON:
			return "STONE_BUTTON";
		case WOOD_BUTTON:
			return "WOOD_BUTTON";
		case STONE_PLATE:
			return "STONE_PLATE";
		case WOOD_PLATE:
			return "WOOD_PLATE";
		case GOLD_PLATE:
			return "GOLD_PLATE";
		case IRON_PLATE:
			return "IRON_PLATE";
		case TRIPWIRE_HOOK:
			return "TRIPWIRE_HOOK";
		default: return"";
		}
	}
	public static Handler get(Material material){
		switch(material){
		case LEVER:
			return LEVER;
		case STONE_BUTTON:
			return STONE_BUTTON;
		case WOOD_BUTTON:
			return WOOD_BUTTON;
		case STONE_PLATE:
			return STONE_PLATE;
		case WOOD_PLATE:
			return WOOD_PLATE;
		case GOLD_PLATE:
			return GOLD_PLATE;
		case IRON_PLATE:
			return IRON_PLATE;
		case TRIPWIRE_HOOK:
			return TRIPWIRE_HOOK;
		default: return null;
		}
	}
	public static String getName(Block block){
		return getName(block, block.getType());
	}
    public static String getName(Block block, ItemStack itemStack){
		return getName(block, itemStack.getType());
    }
    public static String getName(Block block, Material material){
		Handler handler = Handler.get(material);
		if(handler==null)
			return "";
		Location location = block.getLocation();
    	if(location==null)
    		return "";
    	World world = location.getWorld();
    	Vector vector = location.toVector();
    	return handler.toString()+"_"+world.getName()+"_"+(int)vector.getX()+"_"+(int)vector.getY()+"_"+(int)vector.getZ();
    }
}
