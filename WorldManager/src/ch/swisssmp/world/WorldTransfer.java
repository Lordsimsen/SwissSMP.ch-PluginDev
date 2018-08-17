package ch.swisssmp.world;

import ch.swisssmp.webcore.FTPStatus;

public abstract class WorldTransfer implements Runnable {

	protected final String worldName;
	protected final String overrideWorldName;
	
	protected FTPStatus statusObserver = null;
	
	protected WorldTransfer(String worldName, String overrideWorldName){
		this.worldName = worldName;
		this.overrideWorldName = overrideWorldName;
	}
	
	public FTPStatus getStatusObserver(){
		return this.statusObserver;
	}
}
