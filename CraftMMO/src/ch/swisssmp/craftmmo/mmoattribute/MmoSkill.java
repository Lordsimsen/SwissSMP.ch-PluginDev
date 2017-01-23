package ch.swisssmp.craftmmo.mmoattribute;
import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import ch.swisssmp.craftmmo.util.MmoResourceManager;

public class MmoSkill implements Runnable{
	public static HashMap<Integer, MmoSkill> templates;
	public final Integer mmo_skill_id;
	public final String name;
	public MmoSkill(ConfigurationSection dataSection){
		this.mmo_skill_id = Integer.parseInt(dataSection.getName());
		this.name = dataSection.getString("name");
		templates.put(mmo_skill_id, this);
	}
	
	@Override
	public void run() {
		
	}
	
	public static void loadSkills() throws Exception{
		templates = new HashMap<Integer, MmoSkill>();
		YamlConfiguration mmoSkillsConfiguration = MmoResourceManager.getYamlResponse("skills.php");
		for(String skillIDstring : mmoSkillsConfiguration.getKeys(false)){
			ConfigurationSection dataSection = mmoSkillsConfiguration.getConfigurationSection(skillIDstring);
			new MmoSkill(dataSection);
		}
	}
}
