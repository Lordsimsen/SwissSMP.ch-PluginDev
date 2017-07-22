package ch.swisssmp.event;

import ch.swisssmp.event.listeners.CampTriggerEventListener;
import ch.swisssmp.event.listeners.DungeonEventListener;
import ch.swisssmp.event.listeners.DungeonJoinEventListener;
import ch.swisssmp.event.listeners.RegionEventListener;
import ch.swisssmp.event.listeners.TransformationEventListener;
import ch.swisssmp.event.listeners.DefaultEventListener;
import ch.swisssmp.utils.ConfigurationSection;

public class EventListenerCreator {
	public static DefaultEventListener create(ConfigurationSection dataSection){
		if(dataSection==null) return null;
		switch(dataSection.getString("type")){
		case "CampTriggerEvent": return new CampTriggerEventListener(dataSection);
		case "DungeonEndEvent": return new DungeonEventListener(dataSection);
		case "DungeonJoinEvent": return new DungeonJoinEventListener(dataSection);
		case "DungeonStartEvent": return new DungeonEventListener(dataSection);
		case "RegionEnterEvent": return new RegionEventListener(dataSection);
		case "RegionEnteredEvent": return new RegionEventListener(dataSection);
		case "RegionLeaveEvent": return new RegionEventListener(dataSection);
		case "RegionLeftEvent": return new RegionEventListener(dataSection);
		case "TransformationEvent": return new TransformationEventListener(dataSection);
		default: return new DefaultEventListener(dataSection);
		}
	}
}
