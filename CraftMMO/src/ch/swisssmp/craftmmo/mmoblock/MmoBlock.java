package ch.swisssmp.craftmmo.mmoblock;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoevent.MmoBlockChangeEvent;
import ch.swisssmp.craftmmo.mmoitem.MmoItemManager;

public class MmoBlock {
	public static HashMap<Material, Material> minedTypes = new HashMap<Material, Material>();
	
	public static void initialize(){
		minedTypes.put(Material.COAL_ORE, Material.STONE);
		minedTypes.put(Material.IRON_ORE, Material.STONE);
		minedTypes.put(Material.GOLD_ORE, Material.STONE);
		minedTypes.put(Material.LAPIS_ORE, Material.STONE);
		minedTypes.put(Material.DIAMOND_ORE, Material.STONE);
		minedTypes.put(Material.REDSTONE_ORE, Material.STONE);
		minedTypes.put(Material.EMERALD_ORE, Material.STONE);
		minedTypes.put(Material.FLOWER_POT, Material.AIR);
		minedTypes.put(Material.RED_ROSE, Material.AIR);
		minedTypes.put(Material.YELLOW_FLOWER, Material.AIR);
	}
	
	@SuppressWarnings("deprecation")
	public static void set(Block block, MaterialData targetData, UUID player_uuid){
		MmoBlockChangeEvent event = new MmoBlockChangeEvent(block, targetData, player_uuid);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) return;
		byte data = targetData.getData();
		if(block.getState().getData() instanceof Directional){
			Directional directional = (Directional) block.getState().getData();
			data = MmoBlock.getBlockFaceData(directional.getFacing());
		}
		block.setTypeIdAndData(targetData.getItemTypeId(), data, false);
		Main.info("Block updated at "+block.getX()+", "+block.getY()+", "+block.getZ()+" in world "+block.getWorld().getName());
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
		return MmoItemManager.getMaterialString(block.getType(), block.getData(), matchData);
	}
	public static String getMaterialString(MaterialData materialData){
		return getMaterialString(materialData, false);
	}
	@SuppressWarnings("deprecation")
	public static String getMaterialString(MaterialData materialData, boolean matchData){
		if(materialData==null)
			return "";
		return MmoItemManager.getMaterialString(materialData.getItemType(), materialData.getData(), matchData);
	}
	
	public static MaterialData getMinedType(MaterialData mined){
		Material material = minedTypes.get(mined.getItemType());
		MaterialData materialData;
		if(material==null){
			materialData = new MaterialData(Material.AIR);
		}
		else{
			materialData = new MaterialData(material);
		}
		return materialData;
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
			Main.debug("The world "+worldName+" could not be found!");
			return null;
		}
	}
	
	public static Location getLocation(ConfigurationSection dataSection){
		return get(dataSection).getLocation();
	}
	public static MaterialData getMaterialData(String materialString){
		return MmoItemManager.getMaterialData(materialString);
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
