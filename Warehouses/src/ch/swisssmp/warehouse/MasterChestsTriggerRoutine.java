package ch.swisssmp.warehouse;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;

public class MasterChestsTriggerRoutine implements Runnable {

	private static MasterChestsTriggerRoutine instance;
	
	private HashMap<Block,Boolean> blocks = new HashMap<Block,Boolean>();
	
	@Override
	public void run() {
		for(Entry<Block,Boolean> entry : blocks.entrySet()){
			Master master = Master.get(entry.getKey());
			if(master==null) continue;
			master.run(entry.getValue());
		}
		blocks.clear();
	}
	
	protected static void start(){
		MasterChestsTriggerRoutine result = new MasterChestsTriggerRoutine();
		instance = result;
		Bukkit.getScheduler().runTaskTimer(WarehousesPlugin.getInstance(), result, 0, 1);
	}

	public static void addBlock(Block block, boolean animate){
		if(instance.blocks.containsKey(block)) animate = instance.blocks.get(block) || animate;
		instance.blocks.put(block,animate);
	}
}
