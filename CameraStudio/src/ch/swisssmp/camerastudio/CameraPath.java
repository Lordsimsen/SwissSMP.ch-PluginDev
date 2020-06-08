package ch.swisssmp.camerastudio;

import java.util.*;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.editor.Removable;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class CameraPath extends CameraPathElement {

	private final List<Location> points;
	private final Collection<String> commands;

	protected CameraPath(CameraStudioWorld world, UUID pathUid, String name){
		this(world, pathUid, name, new ArrayList<>(), new ArrayList<>());
	}

	protected CameraPath(CameraStudioWorld world, UUID pathUid, String name, List<Location> points, Collection<String> commands){
		super(world, pathUid, name);
		this.points = points;
		this.commands = commands;
	}
	
	public List<Location> getPoints(){
		return this.points;
	}

	public void setPoints(Collection<Location> points){
		this.points.clear();
		this.points.addAll(points);
	}

	public Collection<String> getCommands(){return this.commands;}

	public void setCommands(Collection<String> commands){
		this.commands.clear();
		this.commands.addAll(commands);
	}

	@Override
	public ItemStack getItemStack(){
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder(CameraStudioMaterial.PATH);
		itemBuilder.setAmount(1);
		itemBuilder.setDisplayName(ChatColor.AQUA+getName());
		ItemStack itemStack = itemBuilder.build();
		ItemUtil.setString(itemStack, UID_PROPERTY, getUniqueId().toString());
		return itemStack;
	}

	public boolean isSetupComplete(){
		return !(getName()==null || getName().isEmpty() || points.size()==0);
	}

	@Override
	protected JsonObject save(){
		JsonObject result = super.save();
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
		if(pointsArray.size()>0) result.add("points", pointsArray);
		JsonArray commandsArray = new JsonArray();
		for(String c : commands){
			commandsArray.add(c);
		}
		if(commandsArray.size()>0) result.add("commands", commandsArray);
		return result;
	}

	@Override
	public void remove() {
		this.getWorld().remove(this);
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

	protected static CameraPath load(CameraStudioWorld world, JsonObject json){
		UUID pathUid;
		try{
			String pathUidString = JsonUtil.getString("uid", json);
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
				Location point = JsonUtil.getLocation(world.getBukkitWorld(), element.getAsJsonObject());
				if(point==null) continue;
				points.add(point);
			}
		}

		Collection<String> commands = new ArrayList<>();
		if(json.has("commands")){
			JsonArray commandsArray = json.getAsJsonArray("commands");
			for(JsonElement element : commandsArray){
				if(!element.isJsonPrimitive()) continue;
				commands.add(element.getAsString());
			}
		}

		return new CameraPath(world, pathUid, name, points, commands);
	}
}
