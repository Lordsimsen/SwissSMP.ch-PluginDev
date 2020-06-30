package ch.swisssmp.lift;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

public abstract class LiftFloor {
	
	private LiftInstance instance;
	
	private final int floorIndex;
	private final List<Block> blocks;
	private final int y;
	private final Block floorSign;
	private final Block button;
	private final Block nameSign;
	
	private int targetFloor;
	
	public LiftFloor(int floorIndex, List<Block> blocks, Block floorSign, Block button, Block nameSign){
		this.floorIndex = floorIndex;
		this.blocks = blocks;
		this.y = blocks.get(0).getY();
		this.floorSign = floorSign;
		this.button = button;
		this.nameSign = nameSign;
		
		if(floorSign.getState() instanceof Sign){
			Sign sign = (Sign) floorSign.getState();
			int targetFloor;
			try{
				Debug.Log(sign.getLine(2));
				String keyString = sign.getLine(2).replace(String.valueOf(ChatColor.COLOR_CHAR), "").replace("Ziel:", "").trim();
				Debug.Log(keyString);
				targetFloor = Integer.parseInt(keyString);
			}
			catch(Exception e){
				targetFloor = 0;
			}
			this.targetFloor = targetFloor;
		}
	}
	
	protected void initialize(LiftInstance instance){
		this.instance = instance;
		this.updateFloorSign();
	}
	
	public LiftInstance getLift(){
		return instance;
	}
	
	public int getFloorIndex(){
		return floorIndex;
	}
	
	public int getY(){
		return y;
	}
	
	public String getName(){
		if(nameSign.getState() instanceof Sign){
			Sign sign = (Sign) nameSign.getState();
			String name = sign.getLine(1);
			if(!name.isEmpty()) return name;
		}
		return floorIndex==0 ? "EG" : (floorIndex > 0 ? floorIndex+". Stock" : Math.abs(floorIndex)+". UG");
	}
	
	public List<Block> getBlocks(){
		return blocks;
	}
	
	public Block getFloorSign(){
		return floorSign;
	}
	
	public Block getButton(){
		return button;
	}
	
	public Block getNameSign(){
		return nameSign;
	}
	
	public int getTargetFloor(){
		return targetFloor;
	}
	
	public void updateFloorSign(){
		BlockState floorSignState = floorSign.getState();
		if(!(floorSignState instanceof Sign)){
			return;
		}
		LiftFloor targetFloor = instance.getFloor(this.targetFloor);
		
		Sign sign = (Sign) floorSignState;
		sign.setEditable(true);
		sign.setLine(0, getName());
		sign.setLine(1, "--*--");
		sign.setLine(2, "Ziel:" + makeNumberInvisible(this.targetFloor)+" ");
		sign.setLine(3, targetFloor.getName());
		sign.update(false,false);
	}
	
	public void setTargetFloor(int targetFloor){
		this.targetFloor = targetFloor;
		this.updateFloorSign();
	}
	
	private String makeNumberInvisible(int number){
		String result = (String.valueOf(number)).replaceAll("([0-9])", ChatColor.COLOR_CHAR+"$1");
		Debug.Log("Invisible number: "+result);
		return result;
	}
}
