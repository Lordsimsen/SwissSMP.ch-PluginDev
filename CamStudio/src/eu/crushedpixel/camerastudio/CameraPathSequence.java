package eu.crushedpixel.camerastudio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import ch.swisssmp.webcore.HTTPRequest;
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
	private final int sequence_id;
	private final String name;
	private final World world;
	private final HashMap<Integer,Integer> sequence = new HashMap<Integer,Integer>();
	private final List<CameraPath> paths = new ArrayList<CameraPath>();

	private CameraPathSequence(ConfigurationSection dataSection, World world){
		this.sequence_id = dataSection.getInt("id");
		this.name = dataSection.getString("name");
		this.world = world;
		ConfigurationSection pathsSection = dataSection.getConfigurationSection("sequence");
		ConfigurationSection pathSection;
		for(String key : pathsSection.getKeys(false)){
			pathSection = pathsSection.getConfigurationSection(key);
			int path_id = pathSection.getInt("path_id");
			int duration = pathSection.getInt("duration");
			sequence.put(path_id, duration);
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
		/*
		CommandScheduler.schedulePlayerJoin(player.getUniqueId(), new String[]{
				"tp "+player.getName()+" "+startLocation.getBlockX()+" "+startLocation.getBlockY()+" "+startLocation.getBlockZ(),
				"gamemode "+originalGameMode.name()+" "+player.getName()
			});
		if(schedule_id<0){
			Bukkit.getLogger().info("[CamStudio] Could not start sequence "+this.name+" for player "+player.getName()+" because fallback commands could not be scheduled.");
			return;
		}*/
		player.setGameMode(GameMode.SPECTATOR);
		this.runSequence(player,0, new Runnable(){
			public void run(){
				// CommandScheduler.removePlayerJoin(schedule_id);
				player.setGameMode(originalGameMode);
				player.teleport(startLocation);
				CameraStudio.loadChunkArea(player,startLocation.getBlockX()>>4,startLocation.getBlockZ()>>4,10);
				if(callback!=null)callback.run();
			}
		});
	}

	private void loadPaths(Runnable callback){
		World world = this.world;
		int index = 0;
		List<Boolean> done = new ArrayList<Boolean>();
		for(Integer path_id : sequence.keySet()){
			if(path_id==null)continue;
			paths.add(null);
			done.add(false);
			final int pathIndex = index;
			CameraPath.load(path_id, world, (path)->{
				paths.set(pathIndex, path);
				done.set(pathIndex, true);
				if(done.stream().noneMatch(b->!b)){
					callback.run();
				}
			});
			index++;
		}
	}
	
	private void runSequence(Player player, int path_index, Runnable callback){
		if(this.paths.size()==0 && this.sequence.size()>0){
			this.loadPaths(()->{
				this.runSequence(player, path_index, callback);
			});
			return;
		}
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
	
	public static void load(int sequence_id, Consumer<CameraPathSequence> callback){
		HTTPRequest request = DataSource.getResponse(CameraStudio.getInstance(), "load_sequence.php", new String[]{
				"sequence="+sequence_id
		});
		request.onFinish(()->{
			YamlConfiguration yamlConfiguration = request.getYamlResponse();
			if(yamlConfiguration==null || !yamlConfiguration.contains("sequence")){
				callback.accept(null);
				return;
			}
			callback.accept(CameraPathSequence.create(yamlConfiguration.getConfigurationSection("sequence")));
		});
	}
	
	public static void load(String name, Consumer<CameraPathSequence> callback){
		HTTPRequest request = DataSource.getResponse(CameraStudio.getInstance(), "load_sequence.php", new String[]{
				"name="+URLEncoder.encode(name)
		});
		request.onFinish(()->{
			YamlConfiguration yamlConfiguration = request.getYamlResponse();
			if(yamlConfiguration==null || !yamlConfiguration.contains("sequence")){
				callback.accept(null);
				return;
			}
			callback.accept(CameraPathSequence.create(yamlConfiguration.getConfigurationSection("sequence")));
		});
	}
}
