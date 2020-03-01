package ch.swisssmp.lift;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import ch.swisssmp.utils.SwissSMPler;

public class EventListener implements Listener {

	@EventHandler
	private void onWorldUnload(WorldUnloadEvent event){
		LiftInstances.remove(event.getWorld());
	}
	
	/**
	 * Verhindert Veränderung eines aktiven Lifts
	 */
	@EventHandler
	private void onBlockPlace(BlockPlaceEvent event){
		LiftInstance instance = LiftInstance.get(event.getBlock());
		if(instance==null) return;
		LiftTravel travel = LiftTravel.get(instance);
		if(travel!=null){
			event.setCancelled(true);
			SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.RED+"Der Lift wird gerade verwendet.");
			return;
		}
		instance.release();
	}
	
	/**
	 * Verhindert Veränderung eines aktiven Lifts
	 */
	@EventHandler
	private void onBlockBreak(BlockBreakEvent event){
		LiftInstance instance = LiftInstance.get(event.getBlock());
		if(instance==null) return;
		LiftTravel travel = LiftTravel.get(instance);
		if(travel!=null){
			event.setCancelled(true);
			SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.RED+"Der Lift wird gerade verwendet.");
			return;
		}
		instance.release();
	}
	
	@EventHandler
	private void onBlockInteract(PlayerInteractEvent event){
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK && event.getAction()!=Action.LEFT_CLICK_BLOCK) return;
		if(event.getPlayer().getGameMode()==GameMode.SPECTATOR) return;
		Block block = event.getClickedBlock();
		if(MaterialUtil.isButton(block.getType())){
			this.onButtonInteract(event.getPlayer(), block);
			return;
		}
		if(MaterialUtil.isSign(block.getType())){
			this.onSignInteract(event.getPlayer(), block, event.getAction());
			return;
		}
	}
	
	@EventHandler
	private void onChunkUnload(ChunkUnloadEvent event){
		World world = event.getWorld();
		Chunk chunk = event.getChunk();
		for(LiftInstance lift : new ArrayList<LiftInstance>(LiftInstances.getAll())){
			if(lift.getWorld()!=event.getWorld()) continue;
			BoundingBox box = lift.getBoundingBox();
			Vector center = box.getCenter();
			Block block = world.getBlockAt(center.getBlockX(), center.getBlockY(), center.getBlockZ());
			if(!block.getChunk().equals(chunk)) continue;
			lift.release();
		}
	}
	
	private void onButtonInteract(Player player, Block block){
		//System.out.println("Button Interact");
		LiftInstance liftInstance = LiftInstance.get(block);
		if(liftInstance==null) return;
		LiftTravel travel = LiftTravel.get(liftInstance);
		if(travel!=null){
			SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Der Lift wird gerade verwendet.");
			return;
		}
		LiftFloor floor = liftInstance.getFloor(block);
		if(floor==null){
			//System.out.println("Floor not found");
			return;
		}
		//System.out.println("Floor found");
		LiftFloor target = liftInstance.getFloor(floor.getTargetFloor());
		LiftTravel.start(floor, target);
		//System.out.println("Travel initiated");
	}
	
	private void onSignInteract(Player player, Block block, Action action){
		//System.out.println("Sign Interact");
		LiftInstance liftInstance = LiftInstance.get(block);
		if(liftInstance==null) return;
		//System.out.println("LiftInstance found");
		LiftFloor floor = liftInstance.getFloor(block);
		if(floor==null || !floor.getFloorSign().equals(block)) return;
		int current = liftInstance.getFloorPosition(floor);
		//System.out.println("Floor found");
		int target = floor.getTargetFloor();
		if(target==0 && action==Action.LEFT_CLICK_BLOCK) target = liftInstance.getFloorCount()-1;
		else if(target==liftInstance.getFloorCount()-1 && action==Action.RIGHT_CLICK_BLOCK) target = 0;
		else target += action==Action.RIGHT_CLICK_BLOCK ? 1 : -1;
		
		if(target >= liftInstance.getFloorCount() || (action==Action.RIGHT_CLICK_BLOCK && target == current && floor == liftInstance.getTopFloor())){
			target = 0;
		}
		else if(target<0 || (action==Action.LEFT_CLICK_BLOCK && target==current && floor == liftInstance.getGroundFloor())){
			target = liftInstance.getFloorCount()-1;
		}
		if(target==current) target += action==Action.RIGHT_CLICK_BLOCK ? 1 : -1;
		//System.out.println(target);
		floor.setTargetFloor(target);
		//System.out.println("Target updated");
		liftInstance.getWorld().playSound(block.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 8, 1);
	}
}
