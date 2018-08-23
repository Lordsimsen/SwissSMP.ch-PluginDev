package ch.swisssmp.adventuredungeons.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.adventuredungeons.AdventureDungeons;

public class DungeonLoadSequence implements Runnable{

	private final Dungeon dungeon;
	private final Player player;
	private final DungeonMode mode;
	
	private World world;
	private BukkitTask task;
	
	private DungeonLoadSequence(Dungeon dungeon, Player player, DungeonMode mode){
		this.dungeon = dungeon;
		this.player = player;
		this.mode = mode;
	}
	
	@Override
	public void run() {
		if(world!=null){
			this.player.teleport(dungeon.getSpawnPoint(this.world));
			this.task.cancel();
		}
	}
	
	protected static DungeonLoadSequence run(Dungeon dungeon, Player player, DungeonMode mode){
		DungeonLoadSequence result = new DungeonLoadSequence(dungeon,player,mode);
		result.task = Bukkit.getScheduler().runTaskTimer(AdventureDungeons.getInstance(), result, 0, 1);
		return result;
	}
}
