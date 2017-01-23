package ch.swisssmp.craftmmo.mmoquest;

import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;

import ch.swisssmp.craftmmo.mmoworld.MmoRegion;
import net.md_5.bungee.api.ChatColor;

public class MmoQuestObjectiveDiscover extends MmoQuestObjective{
	
	public final int mmo_region_id;
	public final String target_name;
	
	public MmoQuestObjectiveDiscover(UUID player_uuid, ConfigurationSection dataSection) {
		super(player_uuid, dataSection);
		ConfigurationSection configurationSection = dataSection.getConfigurationSection("configuration");
		this.mmo_region_id = configurationSection.getInt("region");
		if(configurationSection.contains("target_name")){
			this.target_name = configurationSection.getString("target_name");
		}
		else{
			this.target_name = "Unbekanntes Ziel";
		}
		if(localSaveData.exists()){
			load();
		}
	}
	
	@EventHandler
	private void onRegionEnter(RegionEnterEvent event){
		if(event.isCancelled()){
			return;
		}
		Player player = event.getPlayer();
		//optional parameter checkParty defaults to objective setting
		if(!canContribute(player.getUniqueId())){
			return;
		}
		if(MmoRegion.regionMatch(event.getPlayer().getWorld(), event.getRegion(), mmo_region_id)){
			updateCleared();
			if(!this.isCompleted()){
				updateObjective();
			}
		}
	}

	public boolean updateCleared(){
		this.setCompleted(true);
		return true;
	}
	
	@Override
	public String getObjectiveText(){
		String result = getObjectiveHeader();
		result+= createSubtitle("Fortschritt:");
		result+= ChatColor.RESET+target_name+"\n";
		return result;
	}

	@Override
	public void save() {
	}

	@Override
	public void load() {
	}
}
