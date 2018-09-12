package ch.swisssmp.deluminator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.ChunkUtil;

public class IgniteTask {
	private static HashMap<Player,IgniteTask> tasks = new HashMap<Player,IgniteTask>();

	private final Player player;
	private int currentTimeout = 0;
	private int steps = 0;
	
	private List<Chunk> checkedChunks = new ArrayList<Chunk>();
	
	private IgniteTask(Player player){
		this.player = player;
	}
	
	private void run() {
		if(steps>=3){
			this.stop();
			return;
		}
		if(currentTimeout>0){
			currentTimeout--;
			return;
		}
		int count = 0;
		for(Block block : this.getAllLamps(this.steps)){
			if(!Deluminator.ignite(player, block)) continue;
			count++;
		}
		this.steps++;
		System.out.println("[Deluminator] "+count+" Lampen angezündet.");
	}
	
	private Collection<Block> getAllLamps(int range){
		Collection<Block> result = new ArrayList<Block>();
		Collection<Chunk> chunks = new ArrayList<Chunk>();
		chunks = this.getChunks(range);
		for(Chunk chunk : chunks){
			result.addAll(ChunkUtil.getAll(chunk, Material.REDSTONE_LAMP_OFF));
			this.checkedChunks.add(chunk);
		}
		return result;
	}
	
	private Collection<Chunk> getChunks(int range){
		List<Chunk> result = new ArrayList<Chunk>();
		World world = this.player.getWorld();
		Chunk current = this.player.getLocation().getChunk();
		int center_x = current.getX();
		int center_z = current.getZ();
		for(Chunk chunk : ChunkUtil.getChunks(world, center_x, center_z, range)){
			if(this.checkedChunks.contains(chunk)) continue;
			result.add(chunk);
		}
		return result;
	}
	
	private void stop(){
		tasks.remove(this.player);
	}
	
	protected static void run(Player player){
		final IgniteTask result;
		if(tasks.containsKey(player)) result = tasks.get(player);
		else {
			result = new IgniteTask(player);
			Bukkit.getScheduler().runTaskLater(DeluminatorPlugin.plugin, ()->{
				result.stop();
			}, 100);
			tasks.put(player, result);
		}
		result.run();
	}
}
