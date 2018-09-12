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
		LightParticles.spawn(this.getBlockFaceLocation(block, face), new Targetable(player));
	}
	
	protected static boolean ignite(Player player, Block block){
		if(block.getType()!=Material.REDSTONE_LAMP_OFF) return false;
		DeluminatorPlugin.igniteLamp(block);
		LightParticles.spawn(player.getEyeLocation().add(0, -0.5, 0), new Targetable(block.getLocation().add(0.5,0.5,0.5)));
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
	
	private Location getBlockFaceLocation(Block block, BlockFace face){
		switch(face){
		case UP: return block.getLocation().add(0.5, 1, 0.5);
		case DOWN: return block.getLocation().add(0.5,0,0.5);
		case NORTH: return block.getLocation().add(0.5,0.5,0);
		case EAST: return block.getLocation().add(1,0.5,0.5);
		case SOUTH: return block.getLocation().add(0.5,0.5,1);
		case WEST: return block.getLocation().add(0,0.5,0.5);
		default: return block.getLocation().add(0.5,0.5,0.5);
		}
	}
}
