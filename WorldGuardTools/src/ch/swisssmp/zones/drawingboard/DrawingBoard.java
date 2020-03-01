package ch.swisssmp.zones.drawingboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.zones.ZonesPlugin;

public class DrawingBoard {
	
	private final Collection<Block> blocks;
	private final ArmorStand displayStand;
	
	private DrawingBoard(Collection<Block> blocks, ArmorStand displayStand){
		this.blocks = blocks;
		this.displayStand = displayStand;
	}
	
	public Collection<Block> getBlocks(){
		return blocks;
	}
	
	public ArmorStand getDisplayStand(){
		return displayStand;
	}
	
	public void breakNaturally(){
		World world = displayStand.getWorld();
		Location location = displayStand.getEyeLocation();
		ItemStack itemStack = displayStand.getHelmet();
		Bukkit.getScheduler().runTaskLater(ZonesPlugin.getInstance(), ()->{
			world.dropItem(location, itemStack);
		}, 1L);
		
		remove();
	}
	
	public void remove(){
		World world = displayStand.getWorld();
		for(Block block : blocks){
			block.setType(Material.AIR);
			world.spawnParticle(Particle.BLOCK_CRACK, block.getLocation().add(0.5,0.5,0.5), 4, Material.OAK_PLANKS.createBlockData());
		}
		world.playSound(displayStand.getEyeLocation(), Sound.BLOCK_WOOD_BREAK, 4, 1);
		displayStand.remove();
	}
	
	public static DrawingBoard create(Location location, ItemStack itemStack){
		Block block = location.getBlock();
		BlockFace forward = getFacing(location.getYaw()).getOppositeFace();
		BlockFace left = getLeft(forward);
		//System.out.println("Forward: "+forward+", Left: "+left);
		Block lowerLeft = block.getRelative(left);
		Block upperLeft = lowerLeft.getRelative(BlockFace.UP);
		if(lowerLeft.getType()!=Material.AIR || upperLeft.getType()!=Material.AIR){
			block = block.getRelative(left.getOppositeFace());
			lowerLeft = block.getRelative(left);
			upperLeft = lowerLeft.getRelative(BlockFace.UP);
		}
		Block lowerRight = block;
		Block upperRight = lowerRight.getRelative(BlockFace.UP);
		if(lowerLeft.getType()!=Material.AIR || 
				upperLeft.getType()!=Material.AIR || 
				lowerRight.getType()!=Material.AIR || 
				upperRight.getType()!=Material.AIR) return null;
		List<Block> blocks = new ArrayList<Block>();
		blocks.add(lowerLeft);
		blocks.add(lowerRight);
		blocks.add(upperLeft);
		blocks.add(upperRight);
		for(Block part : blocks){
			part.setType(Material.BARRIER);
		}
		Location displayLocation = lowerLeft.getLocation().add(getDisplayOffset(forward.getOppositeFace()));
		displayLocation.setYaw(Math.round((location.getYaw()+180)/90)*90);
		ArmorStand displayStand = createDisplayStand(displayLocation, itemStack);
		displayStand.getWorld().playSound(displayStand.getEyeLocation(), Sound.BLOCK_WOOD_PLACE, 4, 1);
		return new DrawingBoard(blocks, displayStand);
	}
	
	public static DrawingBoard get(Block block){
		if(block==null || block.getType()!=Material.BARRIER) return null;
		//System.out.println("Attempt to find the display stand");
		ArmorStand displayStand = findDisplayStand(block);
		if(displayStand==null){
			//System.out.println("Display Stand not found");
			return null;
		}
		Collection<Block> blocks = getBlocks(displayStand);
		return new DrawingBoard(blocks, displayStand);
	}
	
	private static ArmorStand createDisplayStand(Location location, ItemStack itemStack){
		Entity entity = location.getWorld().spawnEntity(location.clone().add(0, -2, 0), EntityType.ARMOR_STAND);
		ArmorStand result = (ArmorStand) entity;
		result.setInvulnerable(true);
		result.setVisible(false);
		result.setGravity(false);
		result.setSmall(true);
		result.setCustomName(ChatColor.RESET+"drawing_board");
		result.setHelmet(itemStack);
		Bukkit.getScheduler().runTaskLater(ZonesPlugin.getInstance(), ()->{
			entity.teleport(location);
			entity.setSilent(true);
		}, 2L);
		return result;
	}
	
	private static ArmorStand findDisplayStand(Block block){
		World world = block.getWorld();
		Location center = block.getLocation().add(0.5, -0.5, 0.5);
		Collection<Entity> entities = world.getNearbyEntities(center, 0.6, 0.6, 0.6, (entity)->
			entity.getType()==EntityType.ARMOR_STAND && 
			entity.isInvulnerable() && 
			!((ArmorStand)entity).isVisible() &&
			((ArmorStand)entity).getHelmet()!=null);
		if(entities==null || entities.size()==0){
			//System.out.println("No armor stands near "+center.getX()+", "+center.getY()+", "+center.getZ());
			return null;
		}
		ArmorStand result = (ArmorStand) entities.iterator().next();
		ItemStack helmet = result.getHelmet();
		String customEnum = CustomItems.getCustomEnum(helmet);
		if(customEnum==null || !customEnum.equals("DRAWING_BOARD")){
			//System.out.println("ArmorStand is not a drawing board");
			return null;
		}
		return result;
	}
	
	private static Collection<Block> getBlocks(ArmorStand displayStand){
		Location location = displayStand.getLocation();
		BlockFace forward = getFacing(location.getYaw());
		BlockFace leftFace = getLeft(forward);
		BlockFace rightFace = getRight(forward);
		Block lowerLeft = location.clone().add(getBlockOffset(leftFace)).getBlock();
		Block lowerRight = location.clone().add(getBlockOffset(rightFace)).getBlock();
		Block upperLeft = lowerLeft.getRelative(BlockFace.UP);
		Block upperRight = lowerRight.getRelative(BlockFace.UP);
		List<Block> result = new ArrayList<Block>();
		result.add(lowerLeft);
		result.add(lowerRight);
		result.add(upperLeft);
		result.add(upperRight);
		return result;
	}
	
	private static BlockFace getFacing(float yaw){
		while(yaw>180) yaw-=360;
		while(yaw<-180) yaw+=360;
		if(yaw<-135 || yaw>135) return BlockFace.NORTH;
		if(yaw<-45) return BlockFace.EAST;
		if(yaw>45) return BlockFace.WEST;
		return BlockFace.SOUTH;
	}
	
	private static BlockFace getLeft(BlockFace forward){
		switch(forward){
		case SOUTH: return BlockFace.WEST;
		case EAST: return BlockFace.SOUTH;
		case NORTH: return BlockFace.EAST;
		case WEST: return BlockFace.NORTH;
		default: return forward;
		}
	}
	
	private static BlockFace getRight(BlockFace forward){
		switch(forward){
		case SOUTH: return BlockFace.EAST;
		case EAST: return BlockFace.NORTH;
		case NORTH: return BlockFace.WEST;
		case WEST: return BlockFace.SOUTH;
		default: return forward;
		}
	}
	
	private static Vector getBlockOffset(BlockFace face){
		switch(face){
		case NORTH: return new Vector(0,0,-0.5);
		case EAST: return new Vector(0.5,0,0);
		case SOUTH: return new Vector(0,0,0.5);
		case WEST: return new Vector(-0.5,0,0);
		default: return new Vector();
		}
	}
	
	private static Vector getDisplayOffset(BlockFace face){
		switch(face){
		case NORTH: return new Vector(1,0,0.5);
		case EAST: return new Vector(0.5,0,1);
		case SOUTH: return new Vector(0,0,0.5);
		case WEST: return new Vector(0.5,0,0);
		default: return new Vector();
		}
	}
}
