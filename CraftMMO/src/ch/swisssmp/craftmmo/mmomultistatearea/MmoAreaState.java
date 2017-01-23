package ch.swisssmp.craftmmo.mmomultistatearea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import ch.swisssmp.craftmmo.mmoblock.MmoBlock;
import ch.swisssmp.craftmmo.mmoevent.MmoEvent;
import ch.swisssmp.craftmmo.mmoevent.MmoEventType;

public class MmoAreaState {
	public final int mmo_multistatearea_id;
	public final String schematicName;
	public final Location location;
	public final String triggerMessage;
	public final double messageRange;
	public final ArrayList<MmoMultiStateLogic> logicGates = new ArrayList<MmoMultiStateLogic>();
	public final HashMap<MmoEventType, MmoEvent> events = new HashMap<MmoEventType, MmoEvent>();
	
	public MmoAreaState(MmoMultiStateArea multiStateArea, ConfigurationSection dataSection){
		this.mmo_multistatearea_id = multiStateArea.mmo_multistatearea_id;
		this.schematicName = dataSection.getString("name");
		this.location = MmoBlock.get(dataSection, multiStateArea.world).getLocation();
		this.triggerMessage = dataSection.getString("trigger_message");
		this.messageRange = dataSection.getDouble("message_range");

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
			UUID player_uuid = null;
			if(player!=null) player_uuid = player.getUniqueId();
			MmoEvent.fire(events, MmoEventType.TRANSFORMATION_TRIGGERED, player_uuid);
			if(triggerMessage!=null){
				if(player!=null && messageRange<=0)
					player.sendMessage(triggerMessage);
				if(messageRange>0){
					Collection<Entity> entities = location.getWorld().getNearbyEntities(location, messageRange, messageRange, messageRange);
					for(Entity entity : entities){
						if(!(entity instanceof Player)){
							continue;
						}
						Player nearbyPlayer = (Player) entity;
						nearbyPlayer.sendMessage(triggerMessage);
					}
				}
			}
			return true;
		}
		else return false;
	}
}
