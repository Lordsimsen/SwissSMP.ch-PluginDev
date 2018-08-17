package ch.swisssmp.adventuredungeons.sound;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.world.Dungeon;
import ch.swisssmp.adventuredungeons.world.DungeonInstance;

public class MusicLoop implements Runnable{
	private static HashMap<UUID, MusicLoop> musicLoops = new HashMap<UUID, MusicLoop>();
	
	private final Player player;
	private int music_id;
	
	private BukkitTask task;
	
	private MusicLoop(Player player, int music_id){
		this.player = player;
		this.music_id = music_id;
	}
	
	@Override
	public void run(){
		if(!player.isOnline()){
			musicLoops.remove(this.player.getUniqueId());
			this.task.cancel();
			musicLoops.remove(this.player.getUniqueId());
		}
		player.playSound(player.getLocation(), String.valueOf(music_id), 500f, 1);
	}
	
	public void cancel(){
		player.stopSound(String.valueOf(this.music_id));
		this.task.cancel();
	}
	
	private static MusicLoop run(Player player, int music_id, long looptime){
		MusicLoop previous = musicLoops.get(player.getUniqueId());
		if(previous!=null){
			previous.cancel();
		}
		MusicLoop result = new MusicLoop(player, music_id);
		result.task = Bukkit.getScheduler().runTaskTimer(AdventureDungeons.getInstance(), result, 0, looptime);
		musicLoops.put(player.getUniqueId(), result);
		return result;
	}
	
	public static void update(Player player){
    	if(musicLoops.containsKey(player.getUniqueId())){
    		return;
    	}
    	Dungeon dungeon = Dungeon.get(player);
    	if(dungeon!=null){
    		DungeonInstance instance = Dungeon.getInstance(player);
    		if(instance.isRunning() && dungeon.background_music>0){
    			MusicLoop.run(player, dungeon.background_music, dungeon.looptime);
    		}
    	}
	}
}
