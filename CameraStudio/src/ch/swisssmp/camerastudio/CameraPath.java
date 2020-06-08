package ch.swisssmp.camerastudio;

import java.util.*;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class CameraPath {

	public static final String UID_PROPERTY = "cameraStudioPathId";

	private final World world;
	private final UUID pathUid;
	private final String name;
	private final List<Location> points;

	protected CameraPath(UUID pathUid, World world, String name){
		this(pathUid, world, name, new ArrayList<>());
	}

	protected CameraPath(UUID pathUid, World world, String name, List<Location> points){
		this.pathUid = pathUid;
		this.world = world;
		this.name = name;
		this.points = points;
	}
	
	public UUID getUniqueId(){
		return this.pathUid;
	}

	public World getWorld(){
		return world;
	}

	public String getName(){
		return this.name;
	}
	
	public List<Location> getPoints(){
		return this.points;
	}

	public void setPoints(Collection<Location> points){
		this.points.clear();
		this.points.addAll(points);
	}

	public ItemStack getItemStack(){
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder(CameraStudioMaterial.PATH);
		itemBuilder.setAmount(1);
		itemBuilder.setDisplayName(ChatColor.AQUA+name);
		ItemStack itemStack = itemBuilder.build();
		ItemUtil.setString(itemStack, UID_PROPERTY, pathUid.toString());
		return itemStack;
	}

	public boolean isSetupComplete(){
		return !(name==null || name.isEmpty() || points.size()==0);
	}

	protected JsonObject save(){
		JsonObject result = new JsonObject();
		result.addProperty("path_uid", this.pathUid.toString());
		result.addProperty("name", this.name);
		JsonArray pointsArray = new JsonArray();
		for(Location l : points){
			JsonObject pointSection = new JsonObject();
			pointSection.addProperty("x", l.getX());
			pointSection.addProperty("y", l.getY());
			pointSection.addProperty("z", l.getZ());
			pointSection.addProperty("yaw", l.getYaw());
			pointSection.addProperty("pitch", l.getPitch());
			pointsArray.add(pointSection);
		}
		result.add("points", pointsArray);
		return result;
	}

	public static Optional<CameraPath> get(UUID pathUid){
		return CameraStudioWorlds.getPath(pathUid);
	}
	
	public static Optional<CameraPath> get(UUID pathUid, World world){
		CameraStudioWorld studioWorld = CameraStudioWorld.get(world);
		return studioWorld.getPath(pathUid);
	}

	public static Optional<CameraPath> get(ItemStack itemStack){
		String uidString = ItemUtil.getString(itemStack, CameraPath.UID_PROPERTY);
		UUID pathUid;
		try{
			pathUid = UUID.fromString(uidString);
		}
		catch(Exception e){
			e.printStackTrace();
			return Optional.empty();
		}

		return get(pathUid);
	}

	protected static CameraPath load(World world, JsonObject json){
		UUID pathUid;
		try{
			String pathUidString = JsonUtil.getString("path_uid", json);
			if(pathUidString==null){
				return null;
			}
			pathUid = UUID.fromString(pathUidString);
		}
		catch(Exception e){
			return null;
		}
		String name = JsonUtil.getString("name", json);
		List<Location> points = new ArrayList<>();
		if(json.has("points")){
			JsonArray pointsArray = json.getAsJsonArray("points");
			for(JsonElement element : pointsArray){
				if(!element.isJsonObject()) continue;
				Location point = JsonUtil.getLocation(world, element.getAsJsonObject());
				if(point==null) continue;
				points.add(point);
			}
		}

		return new CameraPath(pathUid, world, name, points);
	}
}
