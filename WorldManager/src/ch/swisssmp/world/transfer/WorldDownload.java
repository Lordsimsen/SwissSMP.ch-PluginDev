package ch.swisssmp.world.transfer;

import ch.swisssmp.webcore.FTPConnection;

public class WorldDownload extends WorldTransfer {

	protected WorldDownload(String worldName, String overrideWorldName) {
		super(worldName, overrideWorldName);
	}

	@Override
	public void run() {
		this.statusObserver = FTPConnection.download("worlds/"+worldName+".zip", WorldTransferManager.getTempFolder().getPath(), true);
	}
}
