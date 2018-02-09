package ch.swisssmp.event.pluginlisteners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ch.swisssmp.transformations.TransformationTriggerEvent;

public class AreaTransformationsListener implements Listener{
	@EventHandler
	private void TransformationTriggerEvent(TransformationTriggerEvent event){
		EventListenerMaster.getInst().trigger(event);
	}
}
