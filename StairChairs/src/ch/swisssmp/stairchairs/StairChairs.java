package ch.swisssmp.stairchairs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StairChairs{

	private static final boolean DebugOutput = false;

	public static Optional<ChairInstance> sit(Player player, Block block){
		if(BlockChecker.isObstructed(block.getRelative(BlockFace.UP))) {
			debug("Obstructed");
			return Optional.empty();
		}
		if(!BlockChecker.isChair(block)) {
			debug("Not a chair");
			return Optional.empty();
		}
		if(ChairInstances.getInstance(block).isPresent()) {
			debug("Occupied");
			return Optional.empty();
		}
		if(player.getLocation().distanceSquared(block.getLocation().add(0.5, 0, 0.5))>1.75){
			StairChairs.debug("Out of range");
			return Optional.empty();
		}
		Location sittingLocation = ChairScanner.getSittingLocation(block).orElse(null);
		if(sittingLocation==null) return Optional.empty();
		return Optional.of(ChairInstance.create(player, block, sittingLocation, player.getLocation()));
	}

	protected static void unload(){
		for(ChairInstance instance : new ArrayList<>(ChairInstances.getAll())){
			instance.unsit();
		}
	}

	protected static void removeUnusedArmorStands(List<Entity> entities){
		List<ArmorStand> toRemove = new ArrayList<ArmorStand>();
		ArmorStand armorStand;
		for(Entity entity : entities){
			if(!(entity instanceof ArmorStand)){
				continue;
			}
			armorStand = (ArmorStand) entity;
			if(armorStand.getCustomName()==null){
				continue;
			}
			if(!armorStand.getCustomName().equals("§cStairChair")){
				return;
			}
			if(armorStand.getPassengers().size()==0)
			{
				toRemove.add(armorStand);
			}
		}
		for(ArmorStand armorStandToRemove : toRemove){
			armorStandToRemove.remove();
		}
	}

	protected static void debug(String s) {
		if(DebugOutput) Bukkit.getLogger().info(StairChairsPlugin.getPrefix()+" "+s);
	}
}
