package ch.swisssmp.lift;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

public class GroundFloor extends LiftFloor {

	private final double speed;
	
	public GroundFloor(int floorIndex, List<Block> blocks, Block floorSign, Block button, Block nameSign) {
		super(floorIndex, blocks, floorSign, button, nameSign);
		speed = MaterialUtil.getLiftSpeed(blocks.get(0).getType());
	}
	
	public double getSpeed(){
		return speed;
	}

	public static GroundFloor get(Block block){
		List<Block> floor = BlockUtil.getGroundFloor(block);
		if(floor == null){
			Debug.Log("GroundFloor not detected");
			return null;
		}
		Block button = BlockUtil.getButtonBlock(floor);
		if(button == null){
			Debug.Log("GroundFloor has no button");
			return null;
		}
		Block floorSign = button.getRelative(BlockFace.UP);
		Block nameSign = button.getRelative(BlockFace.DOWN);

		int floorIndex = 0;
		if(nameSign.getState() instanceof Sign){
			Sign sign = (Sign) nameSign.getState();
			try{
				floorIndex = Integer.parseInt(sign.getLine(2));
			}
			catch(Exception e){ }
		}
		
		return new GroundFloor(floorIndex, floor, floorSign, button, nameSign);
	}
}