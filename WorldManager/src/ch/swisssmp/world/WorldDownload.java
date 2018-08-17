package ch.swisssmp.world;

import ch.swisssmp.webcore.FTPConnection;

public class WorldDownload extends WorldTransfer {

	protected WorldDownload(String worldName, String overrideWorldName) {
		super(worldName, overrideWorldName);
	}

	@Override
	public void run() {
		this.statusObserver = FTPConnection.download("worlds/"+worldName+".zip", WorldManager.getTempFolder().getPath(), true);
	}
}
