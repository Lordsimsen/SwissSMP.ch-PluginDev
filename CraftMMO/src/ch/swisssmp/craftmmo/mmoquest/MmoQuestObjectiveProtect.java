package ch.swisssmp.craftmmo.mmoquest;


import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

public class MmoQuestObjectiveProtect extends MmoQuestObjective{

	public MmoQuestObjectiveProtect(UUID player_uuid, ConfigurationSection dataSection) {
		super(player_uuid, dataSection);
		//ConfigurationSection configurationSection = dataSection.getConfigurationSection("configuration");
		// TODO Auto-generated constructor stub
		if(localSaveData.exists()){
			load();
		}
	}

	@Override
	public String getObjectiveText() {
		String result = getObjectiveHeader();
		result+= createSubtitle("Ziel:");
		return result;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load() {
		// TODO Auto-generated method stub
		
	}

}
