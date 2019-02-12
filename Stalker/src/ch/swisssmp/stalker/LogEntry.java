package ch.swisssmp.stalker;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;

import com.google.gson.JsonObject;

import ch.swisssmp.utils.URLEncoder;

public class LogEntry {
	
	private final String who;
	private String what;
	private String world;
	private int x;
	private int y;
	private int z;
	private String currentType;
	private String currentData;
	private String previousType;
	private String previousData;
	
	private JsonObject extraData;
	
	public LogEntry(String who){
		this.who = who;
	}
	
	public LogEntry(Entity who){
		this.who = Stalker.getIdentifier(who);
	}
	
	public LogEntry(Block who){
		this.who = who.getType().toString();
	}
	
	public void setWhat(String what){
		this.what = what;
	}
	
	public void setWhere(Block where){
		this.world = where.getWorld().getName();
		this.x = where.getX();
		this.y = where.getY();
		this.z = where.getZ();
	}
	
	public void setCurrent(Block block){
		this.setCurrent(block.getState());
	}
	
	public void setCurrent(BlockState blockState){
		this.setCurrent(blockState.getType(),blockState.getBlockData());
	}
	
	public void setCurrent(Material currentType, BlockData currentData){
		this.currentType = currentType.toString();
		this.currentData = currentData.getAsString();
	}
	
	public void setPrevious(Block block){
		this.setPrevious(block.getState());
	}
	
	public void setPrevious(BlockState blockState){
		this.setPrevious(blockState.getType(), blockState.getBlockData());
	}
	
	public void setPrevious(Material previousType, BlockData previousData){
		this.previousType = previousType.toString();
		this.previousData = previousData.getAsString();
	}
	
	public void setExtraData(JsonObject extraData){
		this.extraData = extraData;
	}
	
	public boolean hasExtraData(){
		return extraData!=null;
	}
	
	public String[] getData(String prefix) {
		if(what==null || what.isEmpty()) return new String[]{};
		
		return new String[]{
				prefix+"[who]="+URLEncoder.encode(who != null ? who : "Unkown"),
				prefix+"[what]="+URLEncoder.encode(what),
				prefix+"[world]="+URLEncoder.encode(world != null ? world : ""),
				prefix+"[x]="+x,
				prefix+"[y]="+y,
				prefix+"[z]="+z,
				prefix+"[ct]="+URLEncoder.encode(currentType != null ? currentType : ""),
				prefix+"[cd]="+URLEncoder.encode(currentData != null ? currentData : ""),
				prefix+"[pt]="+URLEncoder.encode(previousType != null ? previousType : ""),
				prefix+"[pd]="+URLEncoder.encode(previousData != null ? previousData : "")
		};
	}
	
	public String[] getExtraData(String prefix, int mainId){
		return new String[]{
				prefix+"[main]="+mainId,
				prefix+"[d]="+URLEncoder.encode(extraData.toString())
		};
	}
}
