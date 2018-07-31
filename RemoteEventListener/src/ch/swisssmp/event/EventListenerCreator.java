package ch.swisssmp.event;

import ch.swisssmp.event.pluginlisteners.EventListenerMaster;
import ch.swisssmp.event.remotelisteners.BasicEventListener;
import ch.swisssmp.event.remotelisteners.DungeonEventListener;
import ch.swisssmp.event.remotelisteners.DungeonJoinEventListener;
import ch.swisssmp.event.remotelisteners.RegionEventListener;
import ch.swisssmp.event.remotelisteners.TransformationEventListener;
import ch.swisssmp.utils.ConfigurationSection;

public class EventListenerCreator {
	public static BasicEventListener create(ConfigurationSection dataSection){
		if(dataSection==null) return null;
		switch(dataSection.getString("type")){
		case "DungeonEndEvent": if(EventListenerMaster.getInst().getAdventureDungeonsLoaded()) return new DungeonEventListener(dataSection); break;
		case "DungeonJoinEvent": if(EventListenerMaster.getInst().getAdventureDungeonsLoaded()) return new DungeonJoinEventListener(dataSection); break;
		case "DungeonStartEvent": if(EventListenerMaster.getInst().getAdventureDungeonsLoaded()) return new DungeonEventListener(dataSection); break;
		case "RegionEnterEvent": if(EventListenerMaster.getInst().getWGRegionEventsLoaded()) return new RegionEventListener(dataSection); break;
		case "RegionEnteredEvent": if(EventListenerMaster.getInst().getWGRegionEventsLoaded()) return new RegionEventListener(dataSection); break;
		case "RegionLeaveEvent": if(EventListenerMaster.getInst().getWGRegionEventsLoaded()) return new RegionEventListener(dataSection); break;
		case "RegionLeftEvent": if(EventListenerMaster.getInst().getWGRegionEventsLoaded()) return new RegionEventListener(dataSection); break;
		case "TransformationTriggerEvent": if(EventListenerMaster.getInst().getAreaTransformationsLoaded()) return new TransformationEventListener(dataSection); break;
		default: return new BasicEventListener(dataSection);
		}
		return null;
	}
}
