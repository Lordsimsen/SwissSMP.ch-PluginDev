package ch.swisssmp.events.halloween;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import ch.swisssmp.utils.Random;

public class BattleArea {
	
	private final Random random = new Random();
	private final Block center;
	private final List<Block> area = new ArrayList<Block>();
	
	private BlockFace[] blockScanDirections = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
	
	protected BattleArea(Block block){
		this.center = block;
	}
	
	protected void scan(int minDistance, int requiredBattleAreaSize){
		this.area.clear();
		List<Block> pending = new ArrayList<Block>();
		Collection<Block> completed = new HashSet<Block>();
		Collection<Block> checked = new HashSet<Block>();
		pending.add(this.center);
		checked.add(this.center);
		Block current;
		int maxCycles = 5000;
		while(pending.size()>0 && completed.size()<requiredBattleAreaSize*10 && maxCycles>0){
			current = pending.get(0);
			for(Block relative : this.getValidRelatives(current, checked)){
				pending.add(relative);
			}
			completed.add(current);
			pending.remove(0);
			maxCycles--;
		}
		if(completed.size()<requiredBattleAreaSize){
			return;
		}
		int minDistanceSquared = (int)Math.pow(minDistance, 2);
		for(Block block : completed){
			if(block.getLocation().distanceSquared(center.getLocation())<minDistanceSquared) continue;
			this.area.add(block);
		}
	}
	
	protected Block getCenter(){
		return this.center;
	}
	
	protected Block getRandomBlock(){
		return area.get(random.nextInt(area.size())).getRelative(BlockFace.UP);
	}
	
	protected int getSize(){
		return this.area.size();
	}
	
	private Collection<Block> getValidRelatives(Block block, Collection<Block> checked){
		Collection<Block> result = new ArrayList<Block>();
		Block relative;
		for(BlockFace scanDirection : this.blockScanDirections){
			relative = this.getValidRelative(block, scanDirection, checked);
			if(relative==null) continue;
			result.add(relative);
		}
		return result;
	}
	
	private Block getValidRelative(Block block, BlockFace blockFace, Collection<Block> checked){
		Block directRelative = block.getRelative(blockFace);
		Block relativeDown = directRelative.getRelative(BlockFace.DOWN);
		Block relativeUp = directRelative.getRelative(BlockFace.UP);
		if(checked.contains(directRelative) || checked.contains(relativeDown) || checked.contains(relativeUp)) return null;
		checked.add(directRelative);
		checked.add(relativeDown);
		checked.add(relativeUp);
		if(isValid(directRelative)) return directRelative;
		if(isValid(relativeDown)) return relativeDown;
		if(isValid(relativeUp)) return relativeUp;
		return null;
	}
	
	private boolean isValid(Block block){
		return block.getType().isSolid() && 
				(block.getRelative(BlockFace.UP,1).getType()==Material.AIR || !block.getRelative(BlockFace.UP,1).getType().isSolid()) && 
				block.getRelative(BlockFace.UP,2).getType()==Material.AIR &&
				block.getRelative(BlockFace.UP,3).getType()==Material.AIR;
	}
}
