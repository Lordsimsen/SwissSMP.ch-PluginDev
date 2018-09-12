package ch.swisssmp.deluminator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import ch.swisssmp.utils.ItemUtil;

public class Deluminator {
	private static HashMap<String,Deluminator> deluminators = new HashMap<String,Deluminator>();
	
	private final List<Block> blocks = new ArrayList<Block>();
	
	protected void add(Block block){
		this.blocks.add(block);
	}
	
	protected void igniteAll(Player player, int range){
		for(Block block : new ArrayList<Block>(this.blocks)){
			if(block.getWorld()!=player.getWorld()) continue;
			if(block.getLocation().distanceSquared(player.getLocation())>10000) continue; //100 blocks range
			this.blocks.remove(block);
			Deluminator.ignite(player, block);
		}
	}
	
	protected void igniteAll(Player player){
		int count = 0;
		for(Block block : this.blocks){
			if(!Deluminator.ignite(player, block)) continue;
			count++;
		}
		System.out.println("[Deluminator] "+count+" Lampen angezündet.");
		this.blocks.clear();
	}
	
	protected void extinguish(Player player, Block block, BlockFace face){
		if(block.getType()!=Material.REDSTONE_LAMP_ON) return;
		this.blocks.add(block);
		DeluminatorPlugin.extinguishLamp(block);
		LightParticles.spawn(Deluminator.getBlockFaceLocation(block, face), new Targetable(player));
	}
	
	protected static boolean ignite(Player player, Block block){
		if(block.getType()!=Material.REDSTONE_LAMP_OFF) return false;
		LightParticles lightParticles = LightParticles.spawn(player.getEyeLocation().add(0, -0.5, 0), new Targetable(Deluminator.getFreeBlockFaceLocation(block, player.getEyeLocation())));
		lightParticles.addOnHitListener(()->{
			DeluminatorPlugin.igniteLamp(block);
		});
		return true;
	}
	
	protected static Deluminator get(ItemStack itemStack){
		String deluminator_id = ItemUtil.getString(itemStack, "deluminator_id");
		if(deluminator_id==null) return null;
		Deluminator result = deluminators.get(deluminator_id);
		if(result==null){
			result = new Deluminator();
			deluminators.put(deluminator_id, result);
		}
		return result;
	}
	
	/**
	 * Reignite all Blocks in the World instantly (usually used when it is unloaded)
	 * @param world - The World to look for
	 */
	protected static void resetBlocks(World world){
		for(Deluminator deluminator : deluminators.values()){
			for(Block block : new ArrayList<Block>(deluminator.blocks)){
				if(block.getWorld()!=world) continue;
				if(block.getType()==Material.REDSTONE_LAMP_OFF){
					DeluminatorPlugin.igniteLamp(block);
				}
				deluminator.blocks.remove(block);
			}
		}
	}
	
	protected static boolean hasDeactivated(Block block){
		if(block.getType()!=Material.REDSTONE_LAMP_OFF) return false;
		for(Deluminator deluminator : deluminators.values()){
			if(deluminator.blocks.contains(block)) return true;
		}
		return false;
	}
	
	protected static BlockFace getClosestBlockFace(Block block, Location target){
		List<BlockFace> freeFaces = new ArrayList<BlockFace>();
		for(BlockFace face : BlockFace.values()){
			if(block.getRelative(face).getType().isSolid()) continue;
			freeFaces.add(face);
		}
		if(freeFaces.size()==0) return BlockFace.UP;
		BlockFace closest = freeFaces.get(0);
		Location closestLocation = getBlockFaceLocation(block, freeFaces.get(0));
		double closestDistance = target.distanceSquared(closestLocation);
		Location currentLocation;
		double currentDistance;
		for(int i = 1; i < freeFaces.size(); i++){
			currentLocation = getBlockFaceLocation(block, freeFaces.get(i));
			currentDistance = currentLocation.distanceSquared(target);
			if(currentDistance<closestDistance){
				closest = freeFaces.get(i);
				closestLocation = currentLocation;
				closestDistance = currentDistance;
			}
		}
		return closest;
	}
	
	private static Location getFreeBlockFaceLocation(Block block, Location target){
		return getBlockFaceLocation(block, getClosestBlockFace(block, target));
	}
	
	private static Location getBlockFaceLocation(Block block, BlockFace face){
		Location result;
		Vector offset;
		Vector normal;
		switch(face){
		case UP:{
			offset = new Vector(0.5, 1, 0.5);
			normal = new Vector(0,1,0);
			break;
		}
		case DOWN:{
			offset = new Vector(0.5, 0, 0.5);
			normal = new Vector(0, -1, 0);
			break;
		}
		case NORTH:{
			offset = new Vector(0.5, 0.5, 0);
			normal = new Vector(0, 0, -1);
			break;
		}
		case EAST:{
			offset = new Vector(1, 0.5, 0.5);
			normal = new Vector(1, 0, 0);
			break;
		}
		case SOUTH:{
			offset = new Vector(0.5, 0.5, 1);
			normal = new Vector(0, 0, 1);
			break;
		}
		case WEST:{
			offset = new Vector(0, 0.5, 0.5);
			normal = new Vector(1, 0, 0);
			break;
		}
		default:{
			return block.getLocation().add(0.5,0.5,0.5);
		}
		}
		result = block.getLocation().add(offset);
		result.setDirection(normal);
		return result;
	}
}
