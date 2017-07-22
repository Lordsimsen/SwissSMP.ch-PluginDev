package ch.swisssmp.adventuredungeons.entity;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.world.AdventureWorldInstance;

public class MmoEntitySaveData implements Listener{
	public final AdventureWorldInstance mmoWorldInstance;
	public final YamlConfiguration entityData = new YamlConfiguration();
	public MmoEntitySaveData(AdventureWorldInstance mmoWorldInstance){
		if(mmoWorldInstance==null) throw new NullPointerException("MmoWorld is null!");
		this.mmoWorldInstance = mmoWorldInstance;
		this.load();
		Bukkit.getPluginManager().registerEvents(this, AdventureDungeons.plugin);
	}
	public void load(){
		File file = new File(mmoWorldInstance.getDataFolder(), "entities.yml");
		if(file.exists()){
			try {
				entityData.load(file);
			} catch (IOException | InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@EventHandler(ignoreCancelled=true)
	private void onWorldSave(WorldSaveEvent event){
		World world = event.getWorld();
		if(this.mmoWorldInstance.world==world){
			YamlConfiguration saveData = new YamlConfiguration();
			for(LivingEntity entity : world.getLivingEntities()){
				ConfigurationSection configurationSection = entityData.getConfigurationSection(entity.getUniqueId().toString());
				if(configurationSection==null) continue;
				saveData.set(entity.getUniqueId().toString(), configurationSection);
			}
			try {
				saveData.save(new File(mmoWorldInstance.getDataFolder(), "entities.yml"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
