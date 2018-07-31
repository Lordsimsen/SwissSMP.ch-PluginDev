package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import ch.swisssmp.utils.VectorUtil;

public class GeneratorUtil {
	public static int getDistanceToStart(GenerationPart... neighbours){
		ArrayList<Integer> distances = new ArrayList<Integer>();
		for(GenerationPart neighbour : neighbours){
			if(neighbour==null || neighbour.getDistanceToStart()<0) continue;
			distances.add(neighbour.getDistanceToStart());
		}
		return Collections.min(distances)+1;
	}

	public static Location getTargetLocation(Entity template, World world, BlockVector templatePivot, BlockVector targetPivot, int rotation){
		Location templateLocation = template.getLocation();
		Location templateDelta = GeneratorUtil.getLocationDelta(templateLocation, templatePivot);
		Vector rotatedRelativeVector = VectorUtil.rotate(templateDelta.toVector(),rotation);
		return GeneratorUtil.getResultLocation(template, targetPivot, rotatedRelativeVector, rotation);
	}
	
	public static void cloneRotatedHangingSettings(Hanging template, Hanging target, int rotation){
		BlockFace[] rotationOrder = new BlockFace[]{BlockFace.NORTH,BlockFace.EAST,BlockFace.SOUTH,BlockFace.WEST};
		int previousRotation = Arrays.asList(rotationOrder).indexOf(template.getFacing());
		int targetRotation = (previousRotation+rotation) %4;
		target.setFacingDirection(rotationOrder[targetRotation], true);
	}
	
	public static void cloneItemFrameSettings(ItemFrame template, ItemFrame target){
		target.setRotation(template.getRotation());
	}
	
	private static Location getLocationDelta(Location globalLocation, BlockVector referenceVector){
		Location templateRelativeLocation = (globalLocation.clone()).subtract(referenceVector);
		templateRelativeLocation.subtract(0.5,0.0,0.5);//rotation pivot is the in XZ center of the block
		return templateRelativeLocation;
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
