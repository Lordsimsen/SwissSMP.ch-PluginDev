package ch.swisssmp.craftmmo.mmoquest;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoentity.MmoMob;

import ch.swisssmp.craftmmo.mmoworld.MmoRegion;
import net.md_5.bungee.api.ChatColor;

public class MmoQuestObjectiveHunt extends MmoQuestObjective{

	public final HashMap<Integer, MmoQuestHuntMob> mobs = new HashMap<Integer, MmoQuestHuntMob>();
	public final int mmo_region_id;
	
	public MmoQuestObjectiveHunt(UUID player_uuid, ConfigurationSection dataSection) {
		super(player_uuid, dataSection);
		ConfigurationSection configurationSection = dataSection.getConfigurationSection("configuration");
		int mmo_mob_id = configurationSection.getInt("mob");
		this.mmo_region_id = configurationSection.getInt("region");
		int target_amount = configurationSection.getInt("count");
		this.mobs.put(mmo_mob_id, new MmoQuestHuntMob(mmo_mob_id, target_amount));
		if(localSaveData.exists()){
			load();
		}
	}
	
	@EventHandler
	private void onPlayerKillEntity(EntityDeathEvent event){
		Main.debug("EntityDeathEvent");
		LivingEntity entity = event.getEntity();
		Player player = (Player) entity.getKiller();
		if(player==null){
			Main.debug("Player is null in EntityDeathEvent");
			return;
		}
		//optional parameter checkParty defaults to objective setting
		if(!canContribute(player.getUniqueId())){
			Main.debug("!canContribute");
			return;
		}
		MmoMob mmoMob = MmoMob.get(entity);
		if(mmoMob==null){
			Main.debug("mmoMob==null");
			return;
		}
		if(!MmoRegion.regionMatch(entity.getLocation(), this.mmo_region_id)){
			Main.debug("!regionMatch");
			return;
		}
		if(mobs.containsKey(mmoMob.mmo_mob_id)){
			Main.debug("huntMob+=1");
			MmoQuestHuntMob huntMob = mobs.get(mmoMob.mmo_mob_id);
			huntMob.current_amount+=1;
			save();
			updateCleared();
			if(!this.isCompleted()){
				updateObjective();
			}
		}
	}

	public void updateCleared(){
		for(MmoQuestHuntMob huntMob : mobs.values()){
			if(huntMob.current_amount<huntMob.target_amount){
				return;
			}
		}
		this.setCompleted(true);
	}
	
	@Override
	public String getObjectiveText(){
		String result = getObjectiveHeader();
		result+= createSubtitle("Fortschritt:");
		for(MmoQuestHuntMob huntMob : mobs.values()){
			MmoMob mmoMob = MmoMob.get(huntMob.mmo_mob_id);
			if(mmoMob==null){
				continue;
			}
			result+= ChatColor.RESET+String.valueOf(huntMob.current_amount)+"/"+huntMob.target_amount+" "+mmoMob.name+"\n";
		}
		return result;
	}

	@Override
	public void save() {
		YamlConfiguration saveData = new YamlConfiguration();
		for(Entry<Integer, MmoQuestHuntMob> huntMob : mobs.entrySet()){
			saveData.set(String.valueOf(huntMob.getKey()), huntMob.getValue().current_amount);
		}
		try {
			saveData.save(localSaveData);
			Main.info("Quest-Fortschritt gespeichert.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void load() {
		YamlConfiguration saveData = new YamlConfiguration();
		try {
			saveData.load(localSaveData);
			for(String mmo_mob_id_string : saveData.getKeys(false)){
				int current_amount = saveData.getInt(mmo_mob_id_string);
				mobs.get(Integer.parseInt(mmo_mob_id_string)).current_amount = current_amount;
			}
			Main.info("Quest-Fortschritt geladen.");
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
}
