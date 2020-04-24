package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;

public class PartConfigurationUtil {
	
	public static YamlConfiguration getPartConfiguration(Block block){
		Collection<Block> boundingBox = BlockUtil.getBox(block);
		return PartConfigurationUtil.getPartConfiguration(boundingBox);
	}
	public static YamlConfiguration getPartConfiguration(Collection<Block> boundingBox){
		//read special properties from signs
		YamlConfiguration result = new YamlConfiguration();
		Collection<Sign> attachedSigns = BlockUtil.getAttachedSigns(boundingBox);
		ArrayList<String> configurationStrings = new ArrayList<String>();
		//int totalConfigurations = 0;
		for(Sign sign : attachedSigns){
			for(String line : sign.getLines()){
				if(line.isEmpty()) continue;
				configurationStrings.add(line);
				//totalConfigurations++;
			}
		}
		PartConfigurationUtil.applyPartConfiguration(result, configurationStrings);
		PartConfigurationUtil.applyPartTypes(result, BlockUtil.getPartTypes(boundingBox));
		PartConfigurationUtil.applyPartImage(result,BlockUtil.getPartImage(boundingBox));
		return result;
	}
	
	private static void applyPartConfiguration(ConfigurationSection dataSection, Collection<String> configurationLines){
		String[] lineParts;
		for(String line : configurationLines){
			lineParts = line.split(":");
			if(lineParts.length<2){
				Bukkit.getLogger().info("[DungeonGenerator] Ungültige Konfigurationszeile '"+line+"'");
				continue;
			}
			PartConfigurationUtil.applyPartConfiguration(dataSection, lineParts[0].trim(), lineParts[1].trim());
		}
	}
	
	private static void applyPartTypes(ConfigurationSection dataSection, Collection<PartType> partTypes){
		ArrayList<String> partTypeStrings = new ArrayList<String>();
		for(PartType partType : partTypes){
			partTypeStrings.add(partType.toString());
		}
		dataSection.set("types", partTypeStrings);
	}
	
	private static void applyPartImage(ConfigurationSection dataSection, Collection<Block> partImage){
		if(partImage==null || partImage.size()==0) return;
		Block min = BlockUtil.getMinBlock(partImage);
		Block max = BlockUtil.getMaxBlock(partImage);
		int width = max.getX()-min.getX()+1;
		int height = max.getZ()-min.getZ()+1;
		int start_x = min.getX();
		int start_z = min.getZ();
		JsonObject jsonData = new JsonObject();
		JsonArray imageArray = new JsonArray();
		JsonObject imagePart;
		for(Block block : partImage){
			imagePart = new JsonObject();
			imagePart.addProperty("x", block.getZ()-start_z);
			imagePart.addProperty("y", height-(block.getX()-start_x+1));
			imagePart.addProperty("color", ch.swisssmp.utils.BlockUtil.getWebColor(block));
			imageArray.add(imagePart);
		}
		jsonData.addProperty("width", width);
		jsonData.addProperty("height", height);
		jsonData.add("data", imageArray);
		dataSection.set("image", new String(Base64.encodeBase64URLSafe(jsonData.toString().getBytes())));
	}
	
	private static void applyPartConfiguration(ConfigurationSection dataSection, String fieldName, String fieldValue){
		switch(fieldName.toLowerCase()){
		case "name": applyNameSetting(dataSection, fieldValue);break;
		case "priorität":
		case "priority":
		case "prio":
		case "gewichtung":
		case "gewicht":
		case "weight": applyWeightSetting(dataSection, fieldValue); break;
		case "limit": applyLimitSetting(dataSection, fieldValue); break;
		case "distanz":
		case "distance":
		case "dist": applyDistanceSetting(dataSection, fieldValue); break;
		case "ebene":
		case "layer": applyLayerSetting(dataSection, fieldValue); break;
		case "rotation": applyRotationSetting(dataSection, fieldValue); break;
		default:break;
		}
	}
	
	private static void applyNameSetting(ConfigurationSection dataSection, String name){
		dataSection.set("name", name.trim());
	}
	
	private static void applyWeightSetting(ConfigurationSection dataSection, String weightString){
		try{
			dataSection.set("weight", Double.parseDouble(weightString));
		}
		catch(Exception e){}
	}
	
	private static void applyLimitSetting(ConfigurationSection dataSection, String limitString){
		try{
			dataSection.set("limit", Integer.parseInt(limitString));
		}
		catch(Exception e){};
	}
	
	private static void applyDistanceSetting(ConfigurationSection dataSection, String distanceString){
		try{
			boolean min_distance_mode = distanceString.contains(">");
			int distance = Integer.parseInt(distanceString.replace("<","").replace(">", ""));
			if(min_distance_mode) dataSection.set("min_distance", distance);
			else dataSection.set("max_distance", distance);
		}
		catch(Exception e){}
	}
	
	private static void applyLayerSetting(ConfigurationSection dataSection, String layerString){
		ArrayList<Integer> validLayers = new ArrayList<Integer>();
		String[] layerParts = layerString.replace(" ", "").split(",");
		int layer;
		for(String layerPart : layerParts){
			try{
				String[] fromTo;
				if(layerPart.contains("to")){
					fromTo = layerPart.split("to");
				}
				else if(layerPart.contains("bis")){
					fromTo = layerPart.split("bis");
				}
				else{
					layer = Integer.parseInt(layerPart);
					validLayers.add(layer);
					continue;
				}
				int from = Integer.parseInt(fromTo[0]);
				int to = Integer.parseInt(fromTo[1]);
				for(int i = Math.min(from, to); i <= Math.max(from, to); i++){
					validLayers.add(i);
				}
			}
			catch(Exception e){}
		}
		if(dataSection.contains("layers")){
			for(int existing : dataSection.getIntegerList("layers")){
				validLayers.add(existing);
			}
		}
		dataSection.set("layers", validLayers);
	}

	private static void applyRotationSetting(ConfigurationSection dataSection, String rotationsString){
		Set<Integer> validRotations = new HashSet<Integer>();
		String[] rotationStrings = rotationsString.replace(" ", "").split(",");
		for(String rotationString : rotationStrings){
			try{
				int rotation = Integer.parseInt(rotationString);
				validRotations.add(rotation);
			}
			catch(Exception e){}
		}
		if(dataSection.contains("rotations")){
			for(int existing : dataSection.getIntegerList("rotations")){
				validRotations.add(existing);
			}
		}
		System.out.println(validRotations.size()+" Rotationen gefunden.");
		dataSection.set("rotations", new ArrayList<Integer>(validRotations));
	}
}
