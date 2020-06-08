package ch.swisssmp.camerastudio;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CameraPathSequence {

	public static final String UID_PROPERTY = "cameraStudioSequenceId";

	private final UUID sequenceUid;
	private final String name;
	private final World world;
	private final List<UUID> sequence;
	private final HashMap<UUID,Integer> timings;

	protected CameraPathSequence(UUID sequenceUid, World world, String name){
		this(sequenceUid,world,name,new ArrayList<>(),new HashMap<>());
	}

	protected CameraPathSequence(UUID sequenceUid, World world, String name, List<UUID> sequence, HashMap<UUID,Integer> timings){
		this.sequenceUid = sequenceUid;
		this.name = name;
		this.world = world;
		this.sequence = sequence;
		this.timings = timings;
	}
	
	public UUID getUniqueId(){
		return this.sequenceUid;
	}
	
	public String getName(){
		return this.name;
	}

	public List<UUID> getPathSequence(){return sequence;}
	public HashMap<UUID,Integer> getTimings(){return timings;}

	public ItemStack getItemStack(){
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder(CameraStudioMaterial.PATH_SEQUENCE);
		itemBuilder.setAmount(1);
		itemBuilder.setDisplayName(ChatColor.AQUA+name);
		ItemStack itemStack = itemBuilder.build();
		ItemUtil.setString(itemStack, UID_PROPERTY, sequenceUid.toString());
		return itemStack;
	}

	public boolean isSetupComplete(){
		return !(name==null || name.isEmpty() || sequence.size()==0 || timings.size()<sequence.size());
	}

	public void run(Player player){
		run(player, null);
	}

	public void run(Player player, Runnable callback){
		if(player==null) return;
		CameraPathSequenceRunnable runnable = new CameraPathSequenceRunnable(this, player, callback);
		runnable.start();
	}

	protected JsonObject save(){
		JsonObject result = new JsonObject();
		result.addProperty("sequence_uid", this.sequenceUid.toString());
		result.addProperty("name", this.name);
		JsonArray sequenceArray = new JsonArray();
		for(UUID entryUid : sequence){
			sequenceArray.add(entryUid.toString());
		}
		result.add("sequence", sequenceArray);
		JsonObject timingsMap = new JsonObject();
		for(Map.Entry<UUID,Integer> entry : timings.entrySet()){
			timingsMap.addProperty(entry.getKey().toString(), entry.getValue());
		}
		result.add("timings", timingsMap);
		return result;
	}
	
	public static CameraPathSequence load(World world, JsonObject json){
		UUID sequenceUid;
		try{
			String sequenceUidString = json.get("sequence_uid").getAsString();
			if(sequenceUidString==null) return null;
			sequenceUid = UUID.fromString(sequenceUidString);
		}
		catch(Exception e){
			return null;
		}
		String name = JsonUtil.getString("name", json);
		List<UUID> sequence = new ArrayList<>();
		if(json.has("sequence")){
			for(JsonElement element : json.getAsJsonArray("sequence")){
				if(!element.isJsonPrimitive()) continue;
				UUID pathUid;
				try{
					pathUid = UUID.fromString(element.getAsString());
				} catch(Exception e) { continue;}
				sequence.add(pathUid);
			}
		}

		HashMap<UUID,Integer> timings = new HashMap<>();
		if(json.has("timings")){
			for(Map.Entry<String,JsonElement> entry : json.getAsJsonObject("timings").entrySet()){
				if(!entry.getValue().isJsonPrimitive()) continue;
				timings.put(UUID.fromString(entry.getKey()), entry.getValue().getAsInt());
			}
		}

		return new CameraPathSequence(sequenceUid, world, name, sequence, timings);
	}
}
