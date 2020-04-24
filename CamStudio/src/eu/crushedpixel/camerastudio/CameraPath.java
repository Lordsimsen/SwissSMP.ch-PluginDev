package eu.crushedpixel.camerastudio;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.Location;
import org.bukkit.World;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class CameraPath {
	private final int path_id;
	private final String name;
	private final List<Location> points;
	
	protected CameraPath(World world, ConfigurationSection dataSection){
		this.path_id = dataSection.getInt("path_id");
		this.name = dataSection.getString("name");
		this.points = new ArrayList<Location>();
		if(dataSection.contains("points")){
			ConfigurationSection pointsSection = dataSection.getConfigurationSection("points");
			Location point;
			for(String key : pointsSection.getKeys(false)){
				point = pointsSection.getLocation(key, world);
				this.points.add(point);
			}
		}
	}
	
	public int getPathId(){
		return this.path_id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public List<Location> getPoints(){
		return this.points;
	}
	
	public static void load(int path_id,World world, Consumer<CameraPath> callback){
		HTTPRequest request = DataSource.getResponse(CameraStudio.getInstance(), "load_path.php", new String[]{
				"path="+path_id
		});
		request.onFinish(()->{
			YamlConfiguration yamlConfiguration = request.getYamlResponse();
			if(yamlConfiguration==null || !yamlConfiguration.contains("path")){
				callback.accept(null);
				return;
			}
			callback.accept(new CameraPath(world, yamlConfiguration.getConfigurationSection("path")));
		});
	}
}
