package ch.swisssmp.craftmmo.mmoitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.util.MmoResourceManager;
import net.minecraft.server.v1_11_R1.NBTTagList;
import net.minecraft.server.v1_11_R1.NBTTagString;

public class MmoMining {
	public static HashMap<MmoMiningType, MmoMining> types = new HashMap<MmoMiningType, MmoMining>();
	
	public final HashMap<MmoMiningQuality,List<String>> materials = new HashMap<MmoMiningQuality,List<String>>();
	
	public MmoMining(ConfigurationSection dataSection){
		MmoMiningType type = MmoMiningType.valueOf(dataSection.getName());
		if(type==null){
			Main.info("Unkown mining type '"+dataSection.getName()+"'!");
			return;
		}
		for(String key : dataSection.getKeys(false)){
			MmoMiningQuality quality = MmoMiningQuality.valueOf(key);
			if(quality==null){
				Main.info("Unkown mining quality '"+key+"'!");
				continue;
			}
			List<String> materialNames = dataSection.getStringList(key);
			this.materials.put(quality, materialNames);
		}
		types.put(type, this);
	}
	
	public static NBTTagList getBreakable(HashMap<MmoMiningType,MmoMiningQuality> mining){
		NBTTagList result = new NBTTagList();
		ArrayList<String> materials = new ArrayList<String>();
		for(Entry<MmoMiningType,MmoMiningQuality> entry : mining.entrySet()){
			List<String> materialNames = getMaterials(entry.getKey(), entry.getValue());
			materials.addAll(materialNames);
		}
		if(materials.size()<1) return null;
		for(String material : materials){
			result.add(new NBTTagString("minecraft:"+material.toLowerCase()));
		}
		Main.debug("Assembled breakable data: "+result);
		return result;
	}
	private static List<String> getMaterials(MmoMiningType type, MmoMiningQuality quality){
		MmoMining mmoMining = types.get(type);
		if(mmoMining==null) return new ArrayList<String>();
		return mmoMining.materials.get(quality);
	}
	public static void loadMining(){
		types.clear();
		//initialize
		YamlConfiguration yamlConfiguration = MmoResourceManager.getYamlResponse("mining.php");
		for(String IDstring : yamlConfiguration.getKeys(false)){
			ConfigurationSection dataSection = yamlConfiguration.getConfigurationSection(IDstring);
			new MmoMining(dataSection);
		}
	}
}
