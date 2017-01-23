package ch.swisssmp.adventuredungeons.mmoevent;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import ch.swisssmp.adventuredungeons.Main;
import ch.swisssmp.adventuredungeons.util.MmoResourceManager;

public class MmoEvent {
	public final MmoEventType event_type;
	public final String lore_id;
	public final String chapter_id;
	public final int delay;
	public final int force;
	
	public MmoEvent(ConfigurationSection dataSection){
		this.event_type = MmoEventType.valueOf(dataSection.getString("event_type"));
		this.lore_id = dataSection.getString("lore");
		this.chapter_id = dataSection.getString("chapter");
		this.delay = dataSection.getInt("delay");
		if(dataSection.contains("force")){
			this.force = dataSection.getInt("force");
		}
		else this.force = 1;
	}
	private void fire(){
		Main.info("Firing event "+this.event_type.toString());
		Runnable task = new Runnable(){
			@Override
			public void run(){
				MmoResourceManager.processYamlResponse(null, "progress/trigger.php", new String[]{
		    			"lore="+lore_id, 
		    			"chapter="+chapter_id,
		    			"force="+force
		    					});
			}
		};
		if(delay<1) task.run();
		else Bukkit.getScheduler().runTaskLater(Main.plugin, task, delay*20);
	}
	public void fire(UUID player_uuid){
		if(player_uuid==null){
			fire();
			return;
		}
		Main.info("Firing event "+this.event_type.toString()+" for player "+player_uuid.toString());
		Runnable task = new Runnable(){
			@Override
			public void run(){
				MmoResourceManager.processYamlResponse(player_uuid, "progress/trigger.php", new String[]{
		    			"player="+player_uuid.toString(),
		    			"lore="+lore_id, 
		    			"chapter="+chapter_id,
		    			"force="+force
		    					});
			}
		};
		if(delay<1) task.run();
		else Bukkit.getScheduler().runTaskLater(Main.plugin, task, delay*20);
	}
	public static void fire(HashMap<MmoEventType, MmoEvent> events, MmoEventType type, UUID player_uuid){
		MmoEvent mmoEvent = events.get(type);
		if(mmoEvent!=null){
			mmoEvent.fire(player_uuid);
		}
	}
	public static void registerAll(ConfigurationSection dataSection, HashMap<MmoEventType, MmoEvent> events){
		ConfigurationSection eventsSection = dataSection.getConfigurationSection("events");
		if(eventsSection!=null){
			for(String key : eventsSection.getKeys(false)){
				ConfigurationSection eventSection = eventsSection.getConfigurationSection(key);
				MmoEvent mmoEvent = new MmoEvent(eventSection);
				events.put(mmoEvent.event_type, mmoEvent);
			}
		}
	}
}
