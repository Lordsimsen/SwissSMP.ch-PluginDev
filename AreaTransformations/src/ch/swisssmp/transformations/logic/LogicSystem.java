package ch.swisssmp.transformations.logic;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import ch.swisssmp.transformations.TransformationState;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.block.Block;

public class LogicSystem {

	private boolean isOn = false;
	private Consumer<Boolean> onStateChange;

	private final Set<LogicNode> nodes = new HashSet<>();

	public LogicSystem onStateChange(Consumer<Boolean> callback){
		this.onStateChange = callback;
		return this;
	}

	protected void setState(boolean isOn){
		if(this.isOn == isOn) return;
		this.isOn = isOn;
		if(this.onStateChange!=null) this.onStateChange.accept(isOn);
	}
	
	public Integer getCurrent(Block block){
		return block.getBlockPower();
	}

	public JsonObject save(){
		JsonObject json = new JsonObject();
		JsonArray nodesArray = new JsonArray();
		for(LogicNode node : nodes){
			nodesArray.add(node.save());
		}
		json.add("nodes", nodesArray);
		return json;
	}

	public void load(JsonObject json){
		nodes.clear();
		if(json.has("nodes")){
			for(JsonElement element : json.getAsJsonArray("nodes")){
				if(!element.isJsonObject()) continue;
				LogicNode node = LogicNode.load(element.getAsJsonObject()).orElse(null);
				if(node==null) continue;
				nodes.add(node);
			}
		}
	}
}
