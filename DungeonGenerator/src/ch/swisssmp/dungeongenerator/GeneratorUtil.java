package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import ch.swisssmp.utils.VectorUtil;

public class GeneratorUtil {
	protected static int getDistance(PartType partType, GenerationPart... neighbours){
		ArrayList<Integer> distances = new ArrayList<Integer>();
		for(GenerationPart neighbour : neighbours){
			if(neighbour==null || neighbour.getDistance(partType)<0 || neighbour instanceof ObstructedGenerationPart) continue;
			distances.add(neighbour.getDistance(partType));
		}
		if(distances.size()==0) return 0;
		return Collections.min(distances)+1;
	}

	protected static Location getTargetLocation(Entity template, World world, BlockVector templatePivot, BlockVector targetPivot, int rotation){
		Location templateLocation = template.getLocation();
		Location templateDelta = GeneratorUtil.getLocationDelta(templateLocation, templatePivot);
		Vector rotatedRelativeVector = VectorUtil.rotate(templateDelta.toVector(),rotation);
		return GeneratorUtil.getResultLocation(template, targetPivot, rotatedRelativeVector, rotation);
	}
	
	protected static void cloneRotatedHangingSettings(Hanging template, Hanging target, int rotation){
		BlockFace[] rotationOrder = new BlockFace[]{BlockFace.NORTH,BlockFace.EAST,BlockFace.SOUTH,BlockFace.WEST};
		int previousRotation = Arrays.asList(rotationOrder).indexOf(template.getFacing());
		int targetRotation = (previousRotation+rotation) %4;
		target.setFacingDirection(rotationOrder[targetRotation], true);
	}
	
	protected static void cloneItemFrameSettings(ItemFrame template, ItemFrame target){
		target.setRotation(template.getRotation());
	}
	
	protected static Location getLocationDelta(Location globalLocation, BlockVector referenceVector){
		Location templateRelativeLocation = (globalLocation.clone()).subtract(referenceVector);
		templateRelativeLocation.subtract(0.5,0.0,0.5);//rotation pivot is the in XZ center of the block
		return templateRelativeLocation;
	}
	
	/**
	 * Looks for the block with the lowest XYZ coordinates still belonging to the template grid
	 * @param block - A block somewhere on the template grid
	 * @param boundingBoxMaterial - The Material of the Bounding Box in this template grid
	 * @return The origin block of the template if found;
	 * 		   <code>null</code> otherwise
	 */
	protected static Block getGridOrigin(Block block, Material boundingBoxMaterial, int boundingBoxSizeXZ, int boundingBoxSizeY){
		if(block.getType()!=boundingBoxMaterial) return null;
		Block lowest = BlockUtil.getFurthestValidBlock(block, BlockFace.DOWN, boundingBoxMaterial, boundingBoxSizeY+2); //negative Y axis
		Block furthestNorth = BlockUtil.getFurthestValidBlock(lowest, BlockFace.NORTH, boundingBoxMaterial, boundingBoxSizeXZ*2); //negative Z axis
		Block furthestWest = BlockUtil.getFurthestValidBlock(furthestNorth, BlockFace.WEST, boundingBoxMaterial, boundingBoxSizeXZ*2); //negative X axis
		return furthestWest;
	}
	
	protected static boolean isVolumeEmpty(Block block, int partSizeXZ, int partSizeY, Material... ignore){
		World world = block.getWorld();
		Block current;
		for(int z = 0; z < partSizeXZ; z++){
			for(int x = 0; x < partSizeXZ; x++){
				for(int y = 0; y < partSizeY; y++){
					current = world.getBlockAt(block.getX()+x, block.getY()+y, block.getZ()+z);
					if(current.getType()!=Material.AIR && !GeneratorUtil.inArray(ignore,current.getType())){
						return false;
					}
				}
			}
		}
		return true;
	}
	
	protected static BlockVector getWorldPosition(BlockVector worldCenterPosition, BlockVector gridPosition, int partSizeXZ, int partSizeY){
		int x = worldCenterPosition.getBlockX()+gridPosition.getBlockX()*partSizeXZ;
		int y = worldCenterPosition.getBlockY()+gridPosition.getBlockY()*partSizeY;
		int z = worldCenterPosition.getBlockZ()+gridPosition.getBlockZ()*partSizeXZ;
		return new BlockVector(x, y, z);
	}
	
	private static boolean inArray(Material[] materials, Material material){
		for(int i = 0; i < materials.length; i++){
			if(materials[i]==material) return true;
		}
		return false;
	}
	
	private static Location getResultLocation(Entity template, BlockVector targetPivot, Vector targetDelta, int rotation){
		Location location = new Location(template.getWorld(),targetPivot.getBlockX(),targetPivot.getBlockY(),targetPivot.getBlockZ());
		location.add(targetDelta);
		location.add(0.5,0.0,0.5);
		location.setYaw(template.getLocation().getYaw()+rotation*90);
		location.setPitch(template.getLocation().getPitch());
		return location;
	}
}
