package ch.swisssmp.stalker.listeners.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.block.SpongeAbsorbEvent;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ch.swisssmp.stalker.LogEntry;
import ch.swisssmp.stalker.Stalker;
import ch.swisssmp.utils.SwissSMPUtils;

public class BlockEventListener implements Listener {
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onBlockBreak(BlockBreakEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("BLOCK_BREAK");
		logEntry.setWhere(event.getBlock());
		logEntry.setCurrent(event.getBlock());
		Stalker.log(logEntry);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onBlockBurn(BlockBurnEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry("Environment");
		logEntry.setWhat("BLOCK_BURN");
		logEntry.setWhere(event.getBlock());
		logEntry.setCurrent(event.getBlock());
		Stalker.log(logEntry);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onBlockDispenseArmor(BlockDispenseArmorEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getBlock());
		logEntry.setWhat("BLOCK_DISPENSE_ARMOR");
		logEntry.setWhere(event.getBlock());
		logEntry.setCurrent(event.getBlock());
		JsonObject extraData = new JsonObject();
		extraData.addProperty("item", SwissSMPUtils.encodeItemStack(event.getItem()));
		extraData.addProperty("target", Stalker.getIdentifier(event.getTargetEntity()));
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onBlockDispense(BlockDispenseEvent event){
		if(event instanceof BlockDispenseArmorEvent) return;
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getBlock());
		logEntry.setWhat("BLOCK_DISPENSE");
		logEntry.setWhere(event.getBlock());
		logEntry.setCurrent(event.getBlock());
		JsonObject extraData = new JsonObject();
		extraData.addProperty("item", SwissSMPUtils.encodeItemStack(event.getItem()));
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onBlockExplode(BlockExplodeEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getBlock());
		logEntry.setWhat("BLOCK_EXPLODE");
		logEntry.setWhere(event.getBlock());
		logEntry.setCurrent(event.getBlock());
		Stalker.log(logEntry);
	}
	
	/*
	@EventHandler(priority=EventPriority.MONITOR)
	private void onBlockFadeEvent(BlockFadeEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getBlock());
		logEntry.setWhat("BLOCK_FADE");
		logEntry.setWhere(event.getBlock());
		logEntry.setCurrent(event.getBlock());
		Stalker.log(logEntry);
	}
	*/
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onBlockFertilize(BlockFertilizeEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("BLOCK_FERTILIZE");
		logEntry.setWhere(event.getBlock());
		logEntry.setCurrent(event.getBlock());
		Stalker.log(logEntry);
	}
	
	/*
	@EventHandler(priority=EventPriority.MONITOR)
	private void onBlockForm(BlockFormEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry("Environment");
		logEntry.setWhat("BLOCK_FORM");
		logEntry.setWhere(event.getBlock());
		logEntry.setPrevious(event.getBlock());
		logEntry.setCurrent(event.getNewState());
		Stalker.log(logEntry);
	}
	*/
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onBlockFromTo(BlockFromToEvent event){
		if(event.isCancelled()) return;
		//TODO implement Listener
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onBlockGrow(BlockGrowEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getBlock());
		logEntry.setWhat("BLOCK_GROW");
		logEntry.setWhere(event.getBlock());
		logEntry.setPrevious(event.getBlock());
		logEntry.setCurrent(event.getNewState());
		Stalker.log(logEntry);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onBlockIgnite(BlockIgniteEvent event){
		if(event.isCancelled()) return;
		Player player = event.getPlayer();
		Entity entity = event.getIgnitingEntity();
		Block block = event.getIgnitingBlock();
		LogEntry logEntry = (player!=null ? new LogEntry(player) : (entity!=null ? new LogEntry(entity) : new LogEntry(block)));
		logEntry.setWhat("BLOCK_IGNITE");
		logEntry.setWhere(event.getBlock());
		logEntry.setPrevious(event.getBlock());
		Stalker.log(logEntry);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onBlockMultiPlace(BlockMultiPlaceEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("BLOCK_MULTI_PLACE");
		logEntry.setWhere(event.getBlock());
		logEntry.setPrevious(event.getBlock());
		logEntry.setCurrent(event.getBlockReplacedState());
		Stalker.log(logEntry);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onBlockPistonExtend(BlockPistonExtendEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getBlock());
		logEntry.setWhat("BLOCK_PISTON_EXTEND");
		logEntry.setWhere(event.getBlock());
		logEntry.setCurrent(event.getBlock());
		Stalker.log(logEntry);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onBlockPistonRetract(BlockPistonRetractEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getBlock());
		logEntry.setWhat("BLOCK_PISTON_RETRACT");
		logEntry.setWhere(event.getBlock());
		logEntry.setCurrent(event.getBlock());
		Stalker.log(logEntry);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onBlockPlace(BlockPlaceEvent event){
		if(event instanceof BlockMultiPlaceEvent) return;
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("BLOCK_PLACE");
		logEntry.setWhere(event.getBlock());
		logEntry.setPrevious(event.getBlock());
		logEntry.setCurrent(event.getBlockReplacedState());
		Stalker.log(logEntry);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onSignChange(SignChangeEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("SIGN_CHANGE");
		logEntry.setWhere(event.getBlock());
		logEntry.setCurrent(event.getBlock());
		JsonObject extraData = new JsonObject();
		JsonArray linesArray = new JsonArray();
		for(String line : event.getLines()){
			linesArray.add(line);
		}
		extraData.add("lines", linesArray);
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onSpongeAbsorb(SpongeAbsorbEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getBlock());
		logEntry.setWhat("SPONGE_ABSORB");
		logEntry.setWhere(event.getBlock());
		logEntry.setCurrent(event.getBlock());
		Stalker.log(logEntry);
	}
}
