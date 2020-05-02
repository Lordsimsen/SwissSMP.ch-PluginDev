package ch.swisssmp.zvierigame.game;

import ch.swisssmp.utils.Position;
import org.bukkit.entity.ArmorStand;

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
		ArmorStand dishCarrier = (ArmorStand) client.getNPCInstance().getEntity().getPassengers().get(0);
		client.getNPCInstance().teleport(this.position.getLocation(client.getNPCInstance().getEntity().getWorld()));
		dishCarrier.teleport(this.position.getLocation(client.getNPCInstance().getEntity().getWorld()));
		client.getNPCInstance().getEntity().addPassenger(dishCarrier);
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
			client.getNPCInstance().getEntity().removePassenger(client.getNPCInstance().getEntity().getPassengers().get(0));
			client.getNPCInstance().remove();
			client = null;
			toggleOccupied(false);
		}
	}
}
