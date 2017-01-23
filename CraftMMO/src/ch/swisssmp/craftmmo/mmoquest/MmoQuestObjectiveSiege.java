package ch.swisssmp.craftmmo.mmoquest;

import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

public class MmoQuestObjectiveSiege extends MmoQuestObjective{

	public final int mmo_dungeon_id;
	
	public MmoQuestObjectiveSiege(UUID player_uuid, ConfigurationSection dataSection) {
		super(player_uuid, dataSection);
		ConfigurationSection configurationSection = dataSection.getConfigurationSection("configuration");
		this.mmo_dungeon_id = configurationSection.getInt("dungeon");
		if(localSaveData.exists()){
			load();
		}
	}

	@Override
	public String getObjectiveText() {
		String result = getObjectiveHeader();
		result+= createSubtitle("Fortschritt:");
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
