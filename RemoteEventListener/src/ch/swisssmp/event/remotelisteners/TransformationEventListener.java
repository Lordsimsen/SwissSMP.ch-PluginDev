package ch.swisssmp.event.remotelisteners;

import org.bukkit.event.Event;

import ch.swisssmp.adventuredungeons.world.Dungeon;
import ch.swisssmp.adventuredungeons.world.DungeonInstance;
import ch.swisssmp.event.remotelisteners.filter.PlayerFilter;
import ch.swisssmp.event.remotelisteners.filter.TransformationFilter;
import ch.swisssmp.event.remotelisteners.filter.WorldFilter;
import ch.swisssmp.transformations.TransformationEvent;
import ch.swisssmp.utils.ConfigurationSection;

public class TransformationEventListener extends BasicEventListener implements TransformationFilter, PlayerFilter,WorldFilter {

	public TransformationEventListener(ConfigurationSection dataSection) {
		super(dataSection);
	}

	@Override
	public void trigger(Event event) {
		if(!(event instanceof TransformationEvent)) return;
		TransformationEvent transformationEvent = (TransformationEvent) event;
		if(!checkTransformation(this.dataSection, transformationEvent)) return;
		if(!checkPlayer(this.dataSection, transformationEvent.getPlayer())) return;
		if(!checkWorld(this.dataSection, transformationEvent.getWorld())) return;
		super.trigger(event, transformationEvent.getPlayer());
	}
	
	@Override
	protected String insertArguments(String command, Event event){
		command = super.insertArguments(command, event);
		TransformationEvent transformationEvent = (TransformationEvent) event;
		command = command.replace("{Transformation-ID}", String.valueOf(transformationEvent.getArea().getTransformationId()));
		command = command.replace("{Transformation}", transformationEvent.getArea().getName());
		command = command.replace("{State}", transformationEvent.getNewState().getSchematicName());
		command = command.replace("{World}", transformationEvent.getWorld().getName());
		if(command.contains("{Instance-ID}")){
			DungeonInstance dungeonInstance = Dungeon.getInstance(transformationEvent.getWorld().getName());
			if(dungeonInstance!=null){
				command = command.replace("{Instance-ID}", String.valueOf(dungeonInstance.getInstanceId()));
			}
		}
		return command;
	}
}
