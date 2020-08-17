package ch.swisssmp.world.transfer;

import java.io.File;
import java.util.UUID;

import ch.swisssmp.utils.FileUtil;
import ch.swisssmp.webcore.FTPConnection;
import ch.swisssmp.webcore.ZipUtil;
import ch.swisssmp.world.WorldManagerPlugin;

public class WorldUpload extends WorldTransfer{
	
	protected WorldUpload(String worldName, String overrideWorldName) {
		super(worldName, overrideWorldName);
	}

	@Override
	public void run() {
		File packedDirectory = new File(WorldManagerPlugin.getInstance().getDataFolder(), "temp/"+overrideWorldName);
		WorldTransferManager.packWorld(worldName, overrideWorldName, packedDirectory);
		File zipped = new File(WorldManagerPlugin.getInstance().getDataFolder(), "temp/"+UUID.randomUUID()+".zip");
		ZipUtil.zip(packedDirectory.getPath(), zipped.getPath());
		FileUtil.deleteRecursive(packedDirectory);
		this.statusObserver = FTPConnection.upload(zipped.getPath(), "worlds/"+worldName+".zip", true);
	}
}
