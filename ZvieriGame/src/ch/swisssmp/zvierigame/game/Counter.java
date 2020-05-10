package ch.swisssmp.zvierigame.game;

import ch.swisssmp.npc.NPCInfo;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.utils.Position;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class Counter {
	
	private Position position;
	private boolean occupied;
	private Client client;
	
	public Counter(Position position) {
		this.position = position;
		occupied = false;
	}
	
	public Client getClient() {
		return this.client;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public void setPosition(Position position) {
		this.position = position;
	}

	public void setClient(Client client) {
		this.client = client;
		NPCInstance npc = client.getNPCInstance();
		Entity dishCarrier = client.getNPCInstance().getEntity().getPassengers().get(0);
		npc.getEntity().removePassenger(dishCarrier);
		Location location = this.position.getLocation(npc.getEntity().getWorld());
		dishCarrier.teleport(location);
		npc.teleport(location.add(NPCInfo.getBaseOffset(npc.getEntity().getType())),
				() -> {
					npc.getEntity().addPassenger(dishCarrier);
				});
		toggleOccupied(true);
	}
	
	public boolean isOccupied() {
		return occupied;
	}
	
	public void toggleOccupied(boolean occupied) {
		this.occupied = occupied;
	}
	
	public void reset() {
		if(client!=null) {
			client.getNPCInstance().getEntity().getPassengers().get(0).remove();
			client.getNPCInstance().remove();
			client = null;
			toggleOccupied(false);
		}
	}
}
