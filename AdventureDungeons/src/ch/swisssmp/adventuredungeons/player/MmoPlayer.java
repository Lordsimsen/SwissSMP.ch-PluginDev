package ch.swisssmp.adventuredungeons.player;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.sound.MmoSound;
import ch.swisssmp.adventuredungeons.world.Dungeon;
import ch.swisssmp.adventuredungeons.world.DungeonInstance;

public abstract class MmoPlayer{
	
	public static void login(Player player){
		DungeonInstance dungeonInstance = Dungeon.getInstance(player);
		if(dungeonInstance==null) {
			String worldName = player.getWorld().getName();
			if(worldName.contains("dungeon_instance")){
				int instance_id = Integer.parseInt(worldName.split("_")[2]);
				DungeonInstance instance = Dungeon.getInstance(instance_id);
				if(instance==null){
					player.teleport(Bukkit.getWorld(AdventureDungeons.config.getString("default_world")).getSpawnLocation());
				}
				else{
					int dungeon_id = instance.dungeon_id;
					Dungeon mmoDungeon = Dungeon.get(dungeon_id);
					if(mmoDungeon!=null){
						player.teleport(mmoDungeon.getLeavePoint());
					}
					else{
						player.teleport(Bukkit.getWorld(AdventureDungeons.config.getString("default_world")).getSpawnLocation());
					}
				}
			}
			if(player.getGameMode()==GameMode.ADVENTURE) player.setGameMode(GameMode.SURVIVAL);
		}
		Bukkit.getScheduler().runTaskLater(AdventureDungeons.plugin, new Runnable(){
			public void run(){
				MmoPlayer.updateMusic(player);
			}
		}, 60L);
	}
    public static void updateMusic(Player player){
    	if(MmoSound.musicLoops.containsKey(player.getUniqueId())){
    		return;
    	}
    	Dungeon dungeon = Dungeon.get(player);
    	if(dungeon!=null){
    		DungeonInstance instance = Dungeon.getInstance(player);
    		if(instance.running && dungeon.background_music>0){
    			MmoSound.playMusic(player, dungeon.background_music, dungeon.looptime);
    		}
    	}
    }
}
