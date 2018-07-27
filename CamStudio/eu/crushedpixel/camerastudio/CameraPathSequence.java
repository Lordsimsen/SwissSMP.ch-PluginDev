package eu.crushedpixel.camerastudio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class CameraPathSequence {
	private int sequence_id;
	private String name;
	private HashMap<Integer,Integer> sequence = new HashMap<Integer,Integer>();
	private List<CameraPath> paths = new ArrayList<CameraPath>();
	
	private CameraPathSequence(ConfigurationSection dataSection, World world){
		this.sequence_id = dataSection.getInt("id");
		this.name = dataSection.getString("name");
		ConfigurationSection pathsSection = dataSection.getConfigurationSection("sequence");
		ConfigurationSection pathSection;
		for(String key : pathsSection.getKeys(false)){
			pathSection = pathsSection.getConfigurationSection(key);
			int path_id = pathSection.getInt("path_id");
			int duration = pathSection.getInt("duration");
			sequence.put(path_id, duration);
		}
		CameraPath cameraPath;
		for(Integer path_id : sequence.keySet()){
			if(path_id==null)continue;
			cameraPath = CameraPath.load(path_id, world);
			if(cameraPath==null)continue;
			paths.add(cameraPath);
		}
	}
	
	public int getSequenceId(){
		return this.sequence_id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void runSequence(Player player, Runnable callback){
		if(player==null) return;
		GameMode originalGameMode = player.getGameMode();
		Location startLocation = player.getLocation();
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("commands/schedule_player_join.php", new String[]{
			"player_uuid="+player.getUniqueId(),
			"commands[]=tp "+player.getName()+" "+startLocation.getBlockX()+" "+startLocation.getBlockY()+" "+startLocation.getBlockZ(),
			"commands[]=gamemode "+originalGameMode.name()+" "+player.getName()
		});
		if(yamlConfiguration==null || !yamlConfiguration.contains("schedule_id")){
			Bukkit.getLogger().info("[CamStudio] Could not start sequence "+this.name+" for player "+player.getName()+" because fallback commands could not be scheduled.");
			return;
		}
		int schedule_id = yamlConfiguration.getInt("schedule_id");
		player.setGameMode(GameMode.SPECTATOR);
		this.runSequence(player,0, new Runnable(){
			public void run(){
				DataSource.getResponse("commands/remove_player_join.php", new String[]{
					"schedule_id="+schedule_id	
				});
				player.setGameMode(originalGameMode);
				player.teleport(startLocation);
				CameraStudio.loadChunkArea(player,startLocation.getBlockX()>>4,startLocation.getBlockZ()>>4,10);
				if(callback!=null)callback.run();
			}
		});
	}
	
	private void runSequence(Player player, int path_index, Runnable callback){
		if(path_index>=this.paths.size()){
			if(callback!=null) callback.run();
			return;
		}
		CameraPath cameraPath = paths.get(path_index);
		int duration = sequence.get(cameraPath.getPathId());
		CameraStudio.travelSimple(player, cameraPath.getPoints(), duration*20, new Runnable(){
			public void run(){
				runSequence(player, path_index+1, callback);
			}
		});
	}
	
	private static CameraPathSequence create(ConfigurationSection dataSection){
		World world = Bukkit.getWorld(dataSection.getString("mc_world"));
		if(world==null) return null;
		return new CameraPathSequence(dataSection, world);
	}
	
	public static CameraPathSequence load(int sequence_id){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("camera_studio/load_sequence.php", new String[]{
				"sequence="+sequence_id
		});
		if(yamlConfiguration==null || !yamlConfiguration.contains("sequence")) return null;
		return CameraPathSequence.create(yamlConfiguration.getConfigurationSection("sequence"));
	}
	
	public static CameraPathSequence load(String name){
		YamlConfiguration yamlConfiguration;
		yamlConfiguration = DataSource.getYamlResponse("camera_studio/load_sequence.php", new String[]{
				"name="+URLEncoder.encode(name)
		});
		if(yamlConfiguration==null || !yamlConfiguration.contains("sequence")) return null;
		return CameraPathSequence.create(yamlConfiguration.getConfigurationSection("sequence"));
	}
}
