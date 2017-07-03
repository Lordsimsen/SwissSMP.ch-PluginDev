package ch.swisssmp.adventuredungeons.mmoplayer;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import ch.swisssmp.adventuredungeons.Main;
import ch.swisssmp.adventuredungeons.mmosound.MmoSound;
import ch.swisssmp.adventuredungeons.mmoworld.MmoDungeon;
import ch.swisssmp.adventuredungeons.mmoworld.MmoDungeonInstance;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorld;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorldInstance;
import ch.swisssmp.webcore.DataSource;

public abstract class MmoPlayer{
	
	private static HashMap<UUID, String> assignedResourcepacks = new HashMap<UUID, String>();
	
	public static void login(Player player){
		MmoDungeonInstance dungeonInstance = MmoDungeon.getInstance(player);
		if(dungeonInstance==null) {
			String worldName = player.getWorld().getName();
			if(worldName.contains("dungeon_instance")){
				int instance_id = Integer.parseInt(worldName.split("_")[2]);
				MmoDungeonInstance instance = MmoDungeon.getInstance(instance_id);
				if(instance==null){
					player.teleport(Bukkit.getWorld(Main.config.getString("default_world")).getSpawnLocation());
				}
				else{
					int dungeon_id = instance.mmo_dungeon_id;
					MmoDungeon mmoDungeon = MmoDungeon.get(dungeon_id);
					if(mmoDungeon!=null){
						player.teleport(mmoDungeon.getLeavePoint());
					}
					else{
						player.teleport(Bukkit.getWorld(Main.config.getString("default_world")).getSpawnLocation());
					}
				}
			}
			if(player.getGameMode()==GameMode.ADVENTURE) player.setGameMode(GameMode.SURVIVAL);
		}
		MmoPlayer.updateResourcepack(player);
		Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable(){
			public void run(){
				MmoPlayer.updateMusic(player);
			}
		}, 60L);
	}
	public static void updateResourcepack(Player player){
		Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable(){
			public void run(){
				if(player==null) return;
				try {
					MmoWorldInstance worldInstance = MmoWorld.getInstance(player);
					String worldName = player.getWorld().getName();
					if(worldInstance!=null) worldName = worldInstance.system_name;
					String result = DataSource.getResponse("resourcepack.php", new String[]{
							"player="+URLEncoder.encode(player.getUniqueId().toString(), "UTF-8"),
							"world="+URLEncoder.encode(worldName, "utf-8")
					});
					if(!result.isEmpty()){
						if(assignedResourcepacks.containsKey(player.getUniqueId())&&assignedResourcepacks.get(player.getUniqueId()).equals(result))
							return;
						assignedResourcepacks.put(player.getUniqueId(), result);
						player.setResourcePack(result);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 5L);
	}
	public static void unregisterResourcepack(Player player){
		assignedResourcepacks.remove(player.getUniqueId());
	}
	public static String getResourcepack(UUID player_uuid){
		return assignedResourcepacks.get(player_uuid);
	}
    public static void updateMusic(Player player){
    	if(MmoSound.musicLoops.containsKey(player.getUniqueId())){
    		return;
    	}
    	MmoDungeon dungeon = MmoDungeon.get(player);
    	if(dungeon!=null){
    		MmoDungeonInstance instance = MmoDungeon.getInstance(player);
    		if(instance.running && dungeon.background_music>0){
    			MmoSound.playMusic(player, dungeon.background_music, dungeon.looptime);
    		}
    	}
    }
}
