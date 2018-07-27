package ch.swisssmp.dungeongenerator;

import java.util.UUID;

import com.sk89q.worldedit.session.SessionKey;

public class WorldEditSessionKey implements SessionKey{

	private final UUID uuid;
	private final String name = "DungeonGeneratorSessionKey";
	
	public WorldEditSessionKey(){
		this.uuid = UUID.randomUUID();
	}
	
	@Override
	public UUID getUniqueId() {
		return this.uuid;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public boolean isPersistent() {
		return true;
	}

}
