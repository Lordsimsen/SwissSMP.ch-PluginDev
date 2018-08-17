package ch.swisssmp.world;

import java.io.File;
import java.util.UUID;

import ch.swisssmp.webcore.FTPConnection;
import ch.swisssmp.webcore.ZipUtil;

public class WorldUpload extends WorldTransfer{
	
	protected WorldUpload(String worldName, String overrideWorldName) {
		super(worldName, overrideWorldName);
	}

	@Override
	public void run() {
		File packedDirectory = new File(WorldManager.plugin.getDataFolder(), "temp/"+overrideWorldName);
		WorldManager.packWorld(worldName, overrideWorldName, packedDirectory);
		File zipped = new File(WorldManager.plugin.getDataFolder(), "temp/"+UUID.randomUUID()+".zip");
		ZipUtil.zip(packedDirectory.getPath(), zipped.getPath());
		this.statusObserver = FTPConnection.upload(zipped.getPath(), "worlds/"+worldName+".zip", true);
	}
}
