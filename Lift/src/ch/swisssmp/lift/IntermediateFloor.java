package ch.swisssmp.lift;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class IntermediateFloor extends LiftFloor {

	public IntermediateFloor(int floorIndex, List<Block> blocks, Block floorSign, Block button, Block nameSign) {
		super(floorIndex, blocks, floorSign, button, nameSign);

	}

	public static IntermediateFloor get(int floorIndex, Block block){
		List<Block> floor = BlockUtil.getFloor(block);
		return IntermediateFloor.get(floorIndex, floor);
	}

	public static IntermediateFloor get(int floorIndex, List<Block> floor){
		Block button = BlockUtil.getButtonBlock(floor);
		if(button == null) return null;
		Block floorSign = button.getRelative(BlockFace.UP);
		Block nameSign = button.getRelative(BlockFace.DOWN);
		return new IntermediateFloor(floorIndex, floor, floorSign, button, nameSign);
	}
}
