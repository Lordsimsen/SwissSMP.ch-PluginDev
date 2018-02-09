package ch.swisssmp.adventuredungeons.block;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.item.AdventureItemManager;
import ch.swisssmp.utils.ConfigurationSection;

public class AdventureBlockUtil {	
	@SuppressWarnings("deprecation")
	public static void set(Block block, MaterialData targetData, UUID player_uuid){
		byte data = targetData.getData();
		if(block.getState().getData() instanceof Directional){
			Directional directional = (Directional) block.getState().getData();
			data = AdventureBlockUtil.getBlockFaceData(directional.getFacing());
		}
		block.setTypeIdAndData(targetData.getItemTypeId(), data, false);
		AdventureDungeons.info("Block updated at "+block.getX()+", "+block.getY()+", "+block.getZ()+" in world "+block.getWorld().getName());
	}
	
	public static String getMaterialString(Location location){
		return getMaterialString(location, false);
	}
	public static String getMaterialString(Location location, boolean matchData){
		if(location==null)return"";
		Block block = location.getBlock();
		return getMaterialString(block, matchData);
	}
	public static String getMaterialString(Block block){
		return getMaterialString(block, false);
	}
	@SuppressWarnings("deprecation")
	public static String getMaterialString(Block block, boolean matchData){
		if(block==null)
			return "";
		return AdventureItemManager.getMaterialString(block.getType(), block.getData(), matchData);
	}
	public static String getMaterialString(MaterialData materialData){
		return getMaterialString(materialData, false);
	}
	@SuppressWarnings("deprecation")
	public static String getMaterialString(MaterialData materialData, boolean matchData){
		if(materialData==null)
			return "";
		return AdventureItemManager.getMaterialString(materialData.getItemType(), materialData.getData(), matchData);
	}
	
	public static Block get(ConfigurationSection dataSection, World world){
		if(world!=null){
			int x = dataSection.getInt("x");
			int y = dataSection.getInt("y");
			int z = dataSection.getInt("z");
			return world.getBlockAt(x,y,z);
		}
		else return null;
	}
	
	public static Block get(ConfigurationSection dataSection){
		String worldName = dataSection.getString("world");
		World world = Bukkit.getWorld(worldName);
		if(world!=null){
			int x = dataSection.getInt("x");
			int y = dataSection.getInt("y");
			int z = dataSection.getInt("z");
			return world.getBlockAt(x,y,z);
		}
		else{
			AdventureDungeons.debug("The world "+worldName+" could not be found!");
			return null;
		}
	}
	
	public static Location getLocation(ConfigurationSection dataSection){
		return get(dataSection).getLocation();
	}
	public static MaterialData getMaterialData(String materialString){
		return AdventureItemManager.getMaterialData(materialString);
	}

	public static String asHash(Block block){
		return block.getWorld().getName()+":"+block.getX()+","+block.getY()+","+block.getZ();
	}
	
	public static byte getBlockFaceData(BlockFace face){
		byte data;
        switch (face) {
        case DOWN:
        	data = 0x0;
        	break;
        case EAST:
            data = 0x1;
            break;

        case WEST:
            data = 0x2;
            break;

        case SOUTH:
            data = 0x3;
            break;

        case NORTH:
            data = 0x4;
            break;

        case UP:
        default:
            data = 0x5;
        }
        return data;
	}
}
