package ch.swisssmp.craftmmo.mmoquest;

import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

public class MmoQuestObjectiveVarious extends MmoQuestObjective{
	public MmoQuestObjectiveVarious(UUID player_uuid, ConfigurationSection dataSection) {
		super(player_uuid, dataSection);
		//no configuration
		//ConfigurationSection configurationSection = dataSection.getConfigurationSection("configuration");
		if(localSaveData.exists()){
			load();
		}
	}

	@Override
	public String getObjectiveText() {
		String result = getObjectiveHeader();
		return result;
	}

	@Override
	public void save() {
		//no local data to save
	}

	@Override
	public void load() {
		//no local data to load
	}
}
