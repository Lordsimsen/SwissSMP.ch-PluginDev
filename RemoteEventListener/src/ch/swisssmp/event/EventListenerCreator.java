package ch.swisssmp.event;

import ch.swisssmp.event.pluginlisteners.EventListenerMaster;
import ch.swisssmp.event.remotelisteners.BasicEventListener;
import ch.swisssmp.event.remotelisteners.CampClearEventListener;
import ch.swisssmp.event.remotelisteners.CampClearedEventListener;
import ch.swisssmp.event.remotelisteners.CampTriggerEventListener;
import ch.swisssmp.event.remotelisteners.DungeonEventListener;
import ch.swisssmp.event.remotelisteners.DungeonJoinEventListener;
import ch.swisssmp.event.remotelisteners.ItemDiscoveredEventListener;
import ch.swisssmp.event.remotelisteners.RegionEventListener;
import ch.swisssmp.event.remotelisteners.TransformationEventListener;
import ch.swisssmp.utils.ConfigurationSection;

public class EventListenerCreator {
	public static BasicEventListener create(ConfigurationSection dataSection){
		if(dataSection==null) return null;
		switch(dataSection.getString("type")){
		case "CampTriggerEvent": if(EventListenerMaster.getInst().getAdventureDungeonsLoaded()) return new CampTriggerEventListener(dataSection); break;
		case "CampClearEvent": if(EventListenerMaster.getInst().getAdventureDungeonsLoaded()) return new CampClearEventListener(dataSection); break;
		case "CampClearedEvent": if(EventListenerMaster.getInst().getAdventureDungeonsLoaded()) return new CampClearedEventListener(dataSection); break;
		case "DungeonEndEvent": if(EventListenerMaster.getInst().getAdventureDungeonsLoaded()) return new DungeonEventListener(dataSection); break;
		case "DungeonJoinEvent": if(EventListenerMaster.getInst().getAdventureDungeonsLoaded()) return new DungeonJoinEventListener(dataSection); break;
		case "DungeonStartEvent": if(EventListenerMaster.getInst().getAdventureDungeonsLoaded()) return new DungeonEventListener(dataSection); break;
		case "ItemDiscoveredEvent": if(EventListenerMaster.getInst().getAdventureDungeonsLoaded()) return new ItemDiscoveredEventListener(dataSection); break;
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
