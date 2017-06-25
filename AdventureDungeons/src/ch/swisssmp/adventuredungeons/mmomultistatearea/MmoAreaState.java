package ch.swisssmp.adventuredungeons.mmomultistatearea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import ch.swisssmp.adventuredungeons.Main;
import ch.swisssmp.adventuredungeons.mmoblock.MmoBlock;
import ch.swisssmp.adventuredungeons.mmoevent.MmoEvent;
import ch.swisssmp.adventuredungeons.mmoevent.MmoEventType;
import ch.swisssmp.adventuredungeons.mmosound.MmoSound;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorld;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorldInstance;
import ch.swisssmp.utils.ConfigurationSection;

public class MmoAreaState {
	public final int mmo_multistatearea_id;
	public final String schematicName;
	public final Location location;
	public final String triggerMessage;
	public final double messageRange;
	public final String triggerSound;
	public final ArrayList<MmoMultiStateLogic> logicGates = new ArrayList<MmoMultiStateLogic>();
	public final HashMap<MmoEventType, MmoEvent> events = new HashMap<MmoEventType, MmoEvent>();
	
	public MmoAreaState(MmoMultiStateArea multiStateArea, ConfigurationSection dataSection){
		this.mmo_multistatearea_id = multiStateArea.mmo_multistatearea_id;
		this.schematicName = dataSection.getString("name");
		this.location = MmoBlock.get(dataSection, multiStateArea.world).getLocation();
		this.triggerMessage = dataSection.getString("trigger_message");
		this.messageRange = dataSection.getDouble("message_range");
		this.triggerSound = dataSection.getString("sound");

		ConfigurationSection logicsSection = dataSection.getConfigurationSection("sensors");
		if(logicsSection!=null){
			for(String key : logicsSection.getKeys(false)){
				ConfigurationSection logicSection = logicsSection.getConfigurationSection(key);
				logicGates.add(new MmoMultiStateLogic(this, logicSection, multiStateArea.world));
			}
		}
		MmoEvent.registerAll(dataSection, events);
	}
	
	public boolean trigger(){
		return trigger(null);
	}
	
	public boolean trigger(Player player){
		boolean success = MmoSchematicUtil.paste(mmo_multistatearea_id+"/"+schematicName+".schematic", location);
		if(success){
			MmoWorldInstance worldInstance = MmoWorld.getInstance(this.location);
			if(worldInstance==null){
				Main.debug("Transformation "+this.schematicName+" was triggered but the world instance could not be found.");
				return false;
			}
			MmoMultiStateArea area = worldInstance.getTransformation(this.mmo_multistatearea_id);
			if(area.lastSchematic.equals(this.schematicName)) 
				return true;
			area.lastSchematic = this.schematicName;
			UUID player_uuid = null;
			if(player!=null) player_uuid = player.getUniqueId();
			MmoEvent.fire(events, MmoEventType.TRANSFORMATION_TRIGGERED, player_uuid);
			if(messageRange>0){
				Collection<Entity> entities = location.getWorld().getNearbyEntities(location, messageRange, messageRange, messageRange);
				for(Entity entity : entities){
					if(!(entity instanceof Player)){
						continue;
					}
					Player nearbyPlayer = (Player) entity;
					if(triggerMessage!=null){
						nearbyPlayer.sendMessage(triggerMessage);
					}
					if(triggerSound!=null){
						MmoSound.play(nearbyPlayer, this.triggerSound);
					}
				}
			}
			else if(player!=null){
				if(triggerMessage!=null){
					player.sendMessage(triggerMessage);
				}
				if(triggerSound!=null){
					MmoSound.play(player, this.triggerSound);
				}
			}
			return true;
		}
		else return false;
	}
}
