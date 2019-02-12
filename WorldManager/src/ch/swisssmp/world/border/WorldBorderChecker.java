package ch.swisssmp.world.border;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

class WorldBorderChecker extends BukkitRunnable{

	String worldName;
	WorldBorder worldBorder;
	Location location;
	boolean outOfBounds;
	int center_x;
	int center_z;
	int radius;
	int margin;
	int new_x;
	int new_z;
	boolean north;
	boolean east;
	boolean south;
	boolean west;
	Block oldBlock;
	Block block;
	
	@Override
	public void run() {
		for(Player player : Bukkit.getOnlinePlayers()){
			worldName = player.getWorld().getName();
			worldBorder = WorldBorderManager.getWorldBorder(worldName);
			if(worldBorder==null || !worldBorder.doWrap()) continue;
			outOfBounds = false;
			north = false;
			east = false;
			south = false;
			west = false;
			location = player.getLocation();
			center_x = worldBorder.getCenterX();
			center_z = worldBorder.getCenterZ();
			radius = worldBorder.getRadius();
			margin = worldBorder.getMargin();
			if(location.getX()-center_x>radius){
				outOfBounds = true;
				west = true;
				new_x = center_x-radius+margin;
			}
			else if(center_x-location.getX()>radius){
				outOfBounds = true;
				east = true;
				new_x = center_x+radius-margin;
			}
			else{
				new_x = location.getBlockX();
			}
			if(location.getZ()-center_z>radius){
				outOfBounds = true;
				north = true;
				new_z = center_z-radius+margin;
			}
			else if(center_z-location.getZ()>radius){
				outOfBounds = true;
				south = true;
				new_z = center_z+radius-margin;
			}
			else{
				new_z = location.getBlockZ();
			}
			if(outOfBounds){
				block = player.getWorld().getBlockAt(new_x, location.getBlockY(), new_z);
				if(!player.isGliding()){
					oldBlock = block;
					while(block.getY()>1 && !block.getRelative(BlockFace.DOWN).getType().isSolid()){
						block = block.getRelative(BlockFace.DOWN);
					}
					if(!block.getRelative(BlockFace.DOWN).getType().isSolid()){
						block = oldBlock;
					}
				}
				while(block.getY()<block.getWorld().getMaxHeight()-1 && (block.getType()!=Material.AIR || block.getRelative(BlockFace.UP).getType()!=Material.AIR)){
					if(block.getType()==Material.LAVA){
						if(worldBorder.doWrap()){
							if(north){
								block = block.getRelative(BlockFace.SOUTH);
							}
							else if(south){
								block = block.getRelative(BlockFace.NORTH);
							}
							if(west){
								block = block.getRelative(BlockFace.EAST);
							}
							else if(east){
								block = block.getRelative(BlockFace.WEST);
							}
						}
						else{
							if(north){
								block = block.getRelative(BlockFace.NORTH);
							}
							else if(south){
								block = block.getRelative(BlockFace.SOUTH);
							}
							if(west){
								block = block.getRelative(BlockFace.WEST);
							}
							else if(east){
								block = block.getRelative(BlockFace.EAST);
							}
						}
						continue;
					}
					block = block.getRelative(BlockFace.UP);
				}
				location = block.getLocation().add(0.5, 0.2, 0.5);
				location.setDirection(player.getLocation().getDirection());
				if(player.getVehicle()!=null){
					Entity vehicle = player.getVehicle();
					vehicle.eject();
					player.teleport(location);
					vehicle.teleport(location);
				}
				else{
					player.teleport(location);
				}
				player.sendMessage("§cDu hast das Ende der Welt erreicht.");
			}
		}
		worldBorder = null;
		location = null;
		oldBlock = null;
		block = null;
	}
}
