package ch.swisssmp.vanillaportals;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;
import org.bukkit.util.BlockVector;

import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.Random;

public class NetherPortalAgent {
	
	private static Random random = new Random();
	
	public static Location getTargetLocation(Location location, int portalSearchRadius, int spaceSearchRadius, BlockVector firstSpaceSearch, BlockVector secondSpaceSearch, boolean allowCreation) {
		World world = location.getWorld();
		int maxHeight = Math.min(world.getMaxHeight(), world.getEnvironment()!=Environment.NETHER ? Integer.MAX_VALUE : 128);
		Location portalSearchMin = new Location(world, location.getX() - portalSearchRadius, 0, location.getZ() - portalSearchRadius);
		Location portalSearchMax = new Location(world, location.getX() + portalSearchRadius, maxHeight-1, location.getZ() + portalSearchRadius);
		Block closestPortalBlock = getClosestPortalBlock(location, portalSearchMin, portalSearchMax);
		if(closestPortalBlock!=null) {
			// Bukkit.getLogger().info("[NetherPortalFixer] Portal gefunden");
			return closestPortalBlock.getLocation();
		}

		Location spaceSearchMin = new Location(world, location.getX() - spaceSearchRadius, 0, location.getZ() - spaceSearchRadius);
		Location spaceSearchMax = new Location(world, location.getX() + spaceSearchRadius, maxHeight-1, location.getZ() + spaceSearchRadius);
		AbstractMap.SimpleEntry<Block,BlockFace> emptySpaceBlock = getEmptySpaceBlock(location, spaceSearchMin, spaceSearchMax, firstSpaceSearch, secondSpaceSearch);
		if(emptySpaceBlock!=null) {
			Bukkit.getLogger().info("[NetherPortalFixer] Freie Fläche gefunden");
			if(allowCreation) {
				// Bukkit.getLogger().info("[NetherPortalFixer] Generiere Portal");
				createPortal(emptySpaceBlock.getKey(), emptySpaceBlock.getValue());
			}
			else {

				// Bukkit.getLogger().info("[NetherPortalFixer] Teleportiere ohne Portal");
			}
			return emptySpaceBlock.getKey().getLocation();
		}

		if(!allowCreation) {
			// Bukkit.getLogger().info("[NetherPortalFixer] Suche freie Fläche");
			return getFreeSpace(location.getBlock()).getLocation();
		}

		// Bukkit.getLogger().info("[NetherPortalFixer] Erzwinge Portal");
		AbstractMap.SimpleEntry<Block,BlockFace> forcedSpaceBlock = getForcedSpaceBlock(location, spaceSearchMin, spaceSearchMax);
		createPortal(forcedSpaceBlock.getKey(), forcedSpaceBlock.getValue());
		return forcedSpaceBlock.getKey().getLocation();
	}
	
	/**
	 * Copy from Minecraft Wiki: 
	 * Starting at the destination coordinates, the game looks for the closest active portal. It searches a bounding area of (128) horizontal blocks from the player, and the full map height. 
	 * This gives a search area of 257 blocks by 257 by 256 blocks.
	 * An active portal for this purpose is defined as a portal block that does not have another portal block below it; thus, only the lowest portal blocks in the obsidian frame are considered. 
	 * A single portal block generated in and placed using server commands would be a valid location.
	 * If a candidate portal is found, then the portal teleports the player to the closest one as determined by the distance in the new coordinate system (including the Y coordinate, which can 
	 * cause seemingly more distant portals to be selected). Note that this is Euclidean distance, not taxicab distance. The distance computation between portals in the range is a straight-line 
	 * distance calculation, and the shortest path is chosen, counting the Y difference. 
	 */
	private static Block getClosestPortalBlock(Location destination, Location min, Location max) {
		World world = destination.getWorld();
		double closestDistance = Double.MAX_VALUE;
		Block closest = null;
		double posX = destination.getX();
		double posY = destination.getY();
		double posZ = destination.getZ();
		for(int y = min.getBlockY(); y <= max.getBlockY(); y++) {
			for(int x = min.getBlockX(); x <= max.getBlockX(); x++) {
				for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
					Block current = world.getBlockAt(x, y, z);
					if(current.getType()!=Material.NETHER_PORTAL || current.getRelative(BlockFace.DOWN).getType()!=Material.OBSIDIAN) {
						continue;
					}
					double distance = Math.pow(x-posX, 2) + Math.abs(y-posY) + Math.pow(z-posZ, 2);
					if(distance>closestDistance) {
						continue;
					}
					closest = current;
					closestDistance = distance;
				}
			}
		}
		return closest;
	}
	
	/**
	 * Copy from Minecraft Wiki: 
	 * If no portals exist in the search region, the game creates one, by looking for the closest suitable location to place a portal, within 16 blocks horizontally (but any distance vertically) 
	 * of the player's destination coordinates. A valid location is 3×4 buildable blocks with air 4 high above all 12 blocks. When enough space is available, the orientation of the portal is random. 
	 * The closest valid position in the 3D distance is always picked.
	 * A valid location exactly 3 wide in the shorter dimension may sometimes not be found, as the check for a point fails if the first tried orientation wants that dimension to be 4 wide. 
	 * This is likely a bug (in Vanilla).
	 * If the first check for valid locations fails entirely, the check is redone looking for a 1×4 expanse of buildable blocks with air 4 high above each. 
	 */
	private static AbstractMap.SimpleEntry<Block,BlockFace> getEmptySpaceBlock(Location location, Location min, Location max, BlockVector firstSearchVolume, BlockVector secondSearchVolume) {
		World world = location.getWorld();
		List<Block> candidates = new ArrayList<Block>();
		// Find all available ground blocks with enough space above
		for(int y = min.getBlockY(); y <= max.getBlockY(); y++) {
			for(int x = min.getBlockX(); x <= max.getBlockX(); x++) {
				for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
					Block current = world.getBlockAt(x, y, z);
					if(current.getType()!=Material.AIR) {
						continue;
					}
					Material below = current.getRelative(BlockFace.DOWN).getType();
					if(!below.isSolid() || below==Material.BEDROCK) {
						continue;
					}
					boolean isCandidate = true;
					for(int relY = 1; relY < firstSearchVolume.getBlockY(); relY++) {
						Block relative = current.getRelative(BlockFace.UP, relY);
						if(relative.getType()==Material.AIR) continue;
						isCandidate = false;
						break;
					}
					if(!isCandidate) {
						continue;
					}
					candidates.add(current);
				}
			}
		}
		
		if(candidates.size()==0) return null;
		
		// Find all available ground blocks with enough volume above (first check)
		List<Block> availableNS = new ArrayList<Block>();
		List<Block> availableEW = new ArrayList<Block>();
		for(Block block : candidates) {
			boolean candidateNS = checkVolume(block, firstSearchVolume.getBlockX(), firstSearchVolume.getBlockY(), firstSearchVolume.getBlockZ());
			boolean candidateEW = checkVolume(block, firstSearchVolume.getBlockZ(), firstSearchVolume.getBlockY(), firstSearchVolume.getBlockX());
			if(candidateNS) availableNS.add(block);
			if(candidateEW) availableEW.add(block);
		}
		
		if(availableNS.size()==0 && availableEW.size()==0) {
			// Find all available ground blocks with enough volume above (second check)
			for(Block block : candidates) {
				boolean candidateNS = checkVolume(block, secondSearchVolume.getBlockX(), secondSearchVolume.getBlockY(), secondSearchVolume.getBlockZ());
				boolean candidateEW = checkVolume(block, secondSearchVolume.getBlockZ(), secondSearchVolume.getBlockY(), secondSearchVolume.getBlockX());
				if(candidateNS) availableNS.add(block);
				if(candidateEW) availableEW.add(block);
			}
		}

		if(availableNS.size()==0 && availableEW.size()==0) {
			return null;
		}
		
		// Choose direction and dump rest
		BlockFace direction = chooseDirection(availableNS, availableEW);
		List<Block> available = direction==BlockFace.NORTH || direction==BlockFace.SOUTH ? availableNS : availableEW;
		
		// Choose closest candidate
		Block closest = getClosest(location, available);
		return new AbstractMap.SimpleEntry<Block,BlockFace>(closest,direction);
	}
	
	/**
	 * If that fails, too, a portal is forced at the target coordinates, but with Y constrained to be between 70 and 10 less than the world height (i.e. 118 for the Nether or 246 for the Overworld). 
	 * When a portal is forced in this way, a 2×3 platform of obsidian with air 3 high above is created at the target location, overwriting whatever might be there. This provides air space underground 
	 * or a small platform if high in the air. In Bedrock Edition, these obsidian blocks are flanked by 4 more blocks of netherrack on each side, resulting in 12 blocks of platform.
	 * Once coordinates are chosen, a portal (always 4×5 and including the corners) including portal blocks is constructed at the target coordinates, replacing anything in the way.
	 */
	private static AbstractMap.SimpleEntry<Block,BlockFace> getForcedSpaceBlock(Location location, Location min, Location max) {
		World world = location.getWorld();
		Block clamped = world.getBlockAt(location.getBlockX(), Mathf.roundToInt(Mathf.clamp(location.getY(), 70, world.getMaxHeight()-10)), location.getBlockZ());
		BlockFace direction = random.nextBoolean() ? BlockFace.SOUTH : BlockFace.EAST;
		Block origin = clamped.getRelative(BlockFace.DOWN).getRelative(direction==BlockFace.SOUTH ? BlockFace.WEST : BlockFace.SOUTH); // origin of the forced space
		BlockData platformData = Bukkit.createBlockData(Material.OBSIDIAN);
		int originX = origin.getX();
		int originY = origin.getY();
		int originZ = origin.getZ();
		for(int y = 0; y < 4; y++) {
			for(int x = 0; x < 2; x++) {
				for(int z = 0; z < 3; z++) {
					Block b = world.getBlockAt(originX+x,originY+y,originZ+z);
					if(y==0) {
						b.setBlockData(platformData);
						continue;
					}
					b.setType(Material.AIR);
				}
			}
		}
		return new AbstractMap.SimpleEntry<Block,BlockFace>(clamped, direction);
	}
	
	private static Block getFreeSpace(Block block) {
		while(block.getType()!=Material.AIR || block.getRelative(BlockFace.UP).getType()!=Material.AIR) {
			block = block.getRelative(BlockFace.UP);
		}
		return block;
	}
	
	private static Block getClosest(Location location, Collection<Block> blocks) {
		Block closest = null;
		double closestDistance = Double.MAX_VALUE;
		for(Block b : blocks) {
			double distance = b.getLocation().distanceSquared(location);
			if(distance>closestDistance) continue;
			closest = b;
			closestDistance = distance;
		}
		return closest;
	}
	
	private static boolean checkVolume(Block origin, int sizeX, int sizeY, int sizeZ) {
		World world = origin.getWorld();
		int originX = origin.getX();
		int originY = origin.getY();
		int originZ = origin.getZ();
		// Check that all ground blocks are solid
		for(int x = 0; x < sizeX; x++) {
			for(int z = 0; z < sizeZ; z++) {
				Block current = world.getBlockAt(originX + x, originY-1, originZ+z);
				if(!current.getType().isSolid()) return false;
			}
		}
		// Check that all volume blocks are air
		for(int y = 0; y < sizeY; y++) {
			for(int x = 0; x < sizeX; x++) {
				for(int z = 0; z < sizeZ; z++) {
					Block current = world.getBlockAt(originX+x, originY+y, originZ+z);
					if(current.getType()!=Material.AIR) return false;
				}
			}
		}
		return true;
	}
	
	private static BlockFace chooseDirection(List<Block> availableNS, List<Block> availableEW) {
		if(availableNS.size()>0) {
			if(availableEW.size()>0) {
				return random.nextBoolean() 
						? BlockFace.SOUTH
						: BlockFace.EAST;
			}
			return BlockFace.SOUTH;
		}
		return BlockFace.EAST;
	}
	
	private static void createPortal(Block block, BlockFace direction) {
		Block origin = block.getRelative(BlockFace.DOWN);
		BlockData frameData = Bukkit.createBlockData(Material.OBSIDIAN);
		BlockData portalData = Bukkit.createBlockData(Material.NETHER_PORTAL);
		Orientable directional = (Orientable) portalData;
		directional.setAxis(direction==BlockFace.SOUTH ? Axis.Z : Axis.X);
		for(int y = 0; y < 5; y++) {
			for(int x = 0; x < 4; x++) {
				boolean isFrame = (x==0 || x==3 || y==0 || y == 4);
				if(!isFrame) continue;
				origin.getRelative(direction, x).getRelative(BlockFace.UP, y).setBlockData(frameData);
			}
		}
		for(int y = 0; y < 3; y++) {
			for(int x = 0; x < 2; x++) {
				origin.getRelative(direction, x+1).getRelative(BlockFace.UP, y+1).setBlockData(portalData);
			}
		}
	}
}
