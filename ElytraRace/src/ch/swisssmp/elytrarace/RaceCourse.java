package ch.swisssmp.elytrarace;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.managers.RegionManager;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import eu.crushedpixel.camerastudio.CameraPath;
import eu.crushedpixel.camerastudio.CameraStudio;

public class RaceCourse {
	private static HashMap<Integer,RaceCourse> courses = new HashMap<Integer,RaceCourse>();
	private final int course_id;
	private final List<Integer> cameraPath;
	private final int timePerPath;
	private final String name;
	private final World world;
	private final WorldType worldType;
	private final Environment environment;
	private final String soundtrack;
	
	private int checkpointCount;
	
	private RaceCourse(ConfigurationSection dataSection){
		this.course_id = dataSection.getInt("course_id");
		this.name = dataSection.getString("name");
		this.worldType = WorldType.valueOf(dataSection.getString("world_type"));
		this.environment = Environment.valueOf(dataSection.getString("environment"));
		this.cameraPath = dataSection.getIntegerList("camera_path");
		this.timePerPath = dataSection.getInt("time_per_path");
		this.soundtrack = dataSection.getString("soundtrack");
		World world = Bukkit.getWorld(dataSection.getString("world"));
		if(world==null){
			WorldCreator worldCreator = new WorldCreator(dataSection.getString("world"));
			worldCreator.type(this.worldType);
			worldCreator.environment(this.environment);
			world = Bukkit.createWorld(worldCreator);
		}
		this.world = world;
		this.updateCheckpointCount();
	}
	
	public int getCourseId(){
		return this.course_id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public World getWorld(){
		return this.world;
	}
	
	public int getCheckpointCount(){
		return this.checkpointCount;
	}
	
	public String getSoundtrack(){
		return this.soundtrack;
	}
	
	public void updateCheckpointCount(){
		if(this.world==null)return;
		int checkpointCount = 0;
		RegionManager regionManager = ElytraRace.worldGuardPlugin.getRegionManager(this.world);
		for(String key : regionManager.getRegions().keySet()){
			if(key.contains("checkpoint_")){
				checkpointCount++;
			}
		}
		this.checkpointCount = checkpointCount;
	}
	
	public void showIntroduction(Player player){
		if(this.cameraPath==null || player.getGameMode()==GameMode.CREATIVE)return;
		player.setGameMode(GameMode.SPECTATOR);
		SwissSMPler.get(player).sendTitle(this.name, ElytraRace.getContestName());
		CameraPath path;
		int cuts = 0;
		for(int i = 0; i < this.cameraPath.size(); i++){
			final int path_id = cameraPath.get(i);
			path = CameraPath.load(path_id, this.getWorld());
			if(path==null)break;
			CameraStudio.loadChunkArea(player,path.getPoints().get(0).getBlockX()>>4,path.getPoints().get(0).getBlockZ()>>4,10);
			Bukkit.getScheduler().runTaskLater(ElytraRace.plugin, new Runnable(){
				public void run(){
					if(!player.isOnline()) return;
					CameraStudio.travel(player, path_id, timePerPath);
				}
			}, timePerPath*20*i);
			cuts++;
		}
		Bukkit.getScheduler().runTaskLater(ElytraRace.plugin, new Runnable(){
			public void run(){
				ElytraRace.preparePlayerPlay(player);
			}
		}, timePerPath*20*cuts+2);
	}
	
	public static RaceCourse get(World world){
		for(RaceCourse raceCourse : courses.values()){
			if(raceCourse.world==world) return raceCourse;
		}
		return null;
	}
	
	public static RaceCourse get(int course_id){
		return courses.get(course_id);
	}
	
	public static RaceCourse[] getCourses(){
		RaceCourse[] result = new RaceCourse[courses.size()];
		courses.values().toArray(result);
		return result;
	}
	
	protected static void loadCourses(){
		courses.clear();
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("elytra_race/courses.php");
		if(yamlConfiguration.contains("courses")){
			ConfigurationSection coursesSection = yamlConfiguration.getConfigurationSection("courses"); 
			ConfigurationSection dataSection;
			for(String key : coursesSection.getKeys(false)){
				dataSection = coursesSection.getConfigurationSection(key);
				courses.put(dataSection.getInt("course_id"), new RaceCourse(dataSection));
			}
		}
	}
}
