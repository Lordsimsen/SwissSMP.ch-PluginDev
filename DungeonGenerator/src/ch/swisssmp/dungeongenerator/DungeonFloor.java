package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.Collection;

public class DungeonFloor {
	private final int floor;
	
	private int chamberCount = 0;
	private int forkCount = 0;
	
	private Collection<GenerationPart> doors = new ArrayList<GenerationPart>();
	
	protected DungeonFloor(int floorIndex){
		this.floor = floorIndex;
	}
	
	protected int getFloorIndex(){
		return this.floor;
	}
	
	protected int getChamberCount(){
		return this.chamberCount;
	}
	
	protected void addChamber(){
		this.chamberCount++;
	}
	
	protected void removeChamber(){
		this.chamberCount--;
	}
	
	protected int getForkCount(){
		return this.forkCount;
	}
	
	protected void addFork(){
		this.forkCount++;
	}
	
	protected void removeFork(){
		this.forkCount--;
	}
	
	protected Collection<GenerationPart> getDoors(){
		return this.doors;
	}
	
	protected void addDoor(GenerationPart door){
		this.doors.add(door);
	}
	
	protected void removeDoor(GenerationPart door){
		this.doors.remove(door);
	}
}
