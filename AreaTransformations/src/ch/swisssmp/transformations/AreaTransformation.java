package ch.swisssmp.transformations;

import java.util.*;

import ch.swisssmp.schematics.SchematicUtil;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.ConfigurationSection;

public class AreaTransformation {

	private final TransformationContainer container;
	private final UUID uid;
	private String name;
	private Block origin;

	private TransformationState current;
	private final Set<TransformationState> states = new HashSet<>();
	
	private AreaTransformation(TransformationContainer container, UUID uid){
		this.container = container;
		this.uid = uid;
	}

	public Optional<TransformationState> getState(UUID stateId){
		return states.stream().filter(s->s.getUniqueId().equals(stateId)).findAny();
	}
	
	public TransformationContainer getContainer(){
		return this.container;
	}
	
	public UUID getUniqueId(){
		return this.uid;
	}
	
	public String getName(){
		return this.name;
	}
	
	public TransformationState getCurrentState(){
		return this.current;
	}
	
	public Collection<TransformationState> getStates(){
		return this.states;
	}

	public boolean setCurrentState(TransformationState state){
		if(this.current==state) {
			TransformationEvent event = new TransformationTriggerEvent(this, state);
			Bukkit.getPluginManager().callEvent(event);
			if(event.isCancelled()) return false;
		}
		boolean success = SchematicUtil.paste(state.getSchematicFile(), origin);
		if(!success) return false;
		current = state;
		return true;
	}

	protected void unload(){

	}

	public World getWorld(){
		return this.container.getWorld();
	}

	protected static Optional<AreaTransformation> load(TransformationContainer container, JsonObject json){
		UUID uid = JsonUtil.getUUID("uid", json);
		if(uid==null) return Optional.empty();
		AreaTransformation transformation = new AreaTransformation(container, uid);

		return Optional.of(transformation);
	}
}
