package ch.swisssmp.world.transfer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import ch.swisssmp.world.WorldManagerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import ch.swisssmp.utils.FileUtil;
import ch.swisssmp.webcore.FTPConnection;
import ch.swisssmp.webcore.HTTPRequest;
import ch.swisssmp.world.WorldManager;

public class WorldTransferManager {

	/**
	 * Returns <code>true</code> if a World with the given worldName exists
	 * @param worldName - The worldName to look for
	 * @return <code>true</code> if a World on the local Server exists;
	 *         <code>false</code> otherwise
	 */
	public static boolean localWorldExists(String worldName){
		File worldDirectory = new File(Bukkit.getWorldContainer(), worldName);
		return worldDirectory.exists();
	}

	/**
	 * Returns <code>true</code> if a World with the given worldName exists
	 * @param worldName - The worldName to look for
	 * @return <code>true</code> if a World on the FTP Server exists;
	 *         <code>false</code> otherwise
	 */
	public static HTTPRequest remoteWorldExists(String worldName){
		return FTPConnection.fileExists("worlds/"+worldName+".zip");
	}
	
	/**
	 * Unpacks a World package containing the World Directory and associated Plugin Files (e.g. WorldGuard region data)
	 * @param worldName - The name of the World to be unpacked
	 * @param overrideWorldName - The name of the World after unpacking
	 * @param packedDirectory - The Directory to unpack from
	 */
	private static void unpackWorld(String worldName, String overrideWorldName, File packedDirectory){
		File packedWorldDirectory = new File(packedDirectory,"World/"+worldName);
		File worldDirectory = new File(Bukkit.getWorldContainer(),overrideWorldName);
		FileUtil.copyDirectory(packedWorldDirectory, worldDirectory, new ArrayList<String>(Arrays.asList("session.lock")));
		if(!overrideWorldName.equals(worldName)){
			WorldDataPatcher.changeLevelName(worldDirectory, overrideWorldName);
		}
		try{
			Bukkit.getPluginManager().callEvent(new WorldUnpackEvent(overrideWorldName,packedDirectory,false));
		}
		catch(Exception e){
			e.printStackTrace();
			return;
		}
		Bukkit.getLogger().info("[WorldManager] Unpacking of World "+worldName+" finished.");
		Bukkit.getScheduler().runTaskLater(WorldManagerPlugin.getInstance(), ()->{
			FileUtil.deleteRecursive(packedDirectory);
		}, 5L);
	}
	
	/**
	 * Packs a World Directory into a package containing the World Directory and associated Plugin Files (e.g. WorldGuard region data)
	 * @param worldName - The name of the World to be packed
	 * @param overrideWorldName - The name of the packed World
	 * @param packedDirectory - The Directory to pack into
	 */
	protected static void packWorld(String worldName, String overrideWorldName, File packedDirectory){
		File worldDirectory = new File(Bukkit.getWorldContainer(),worldName);
		File packedWorldDirectory = new File(packedDirectory,"World/"+overrideWorldName);
		packedWorldDirectory.mkdirs();
		FileUtil.copyDirectory(worldDirectory, packedWorldDirectory, new ArrayList<String>(Arrays.asList("session.lock")));
		WorldDataPatcher.changeLevelName(packedWorldDirectory, overrideWorldName);
		Bukkit.getPluginManager().callEvent(new WorldPackEvent(worldName,packedDirectory,true));
	}
	
	/**
	 * Uploads a World to the FTP Server
	 * @param sender - The responsible Entity for this transaction
	 * @param worldName - The local World Folder to be uploaded
	 * @return A <code>WorldTransferTask</code> reporting the status of the upload to the sender
	 */
	public static WorldTransferObserver uploadWorld(CommandSender sender, String worldName){
		return WorldTransferManager.uploadWorld(sender, worldName, worldName);
	}

	/**
	 * Uploads a World to the FTP Server
	 * @param sender - The responsible Entity for this transaction
	 * @param worldName - The local World Folder to be uploaded
	 * @param overrideWorldName - The name of the World Folder on the remote Server
	 * @return A <code>WorldTransferTask</code> reporting the status of the upload to the sender
	 */
	public static WorldTransferObserver uploadWorld(CommandSender sender, String worldName, String overrideWorldName){
		WorldUpload worldUpload = new WorldUpload(worldName,overrideWorldName);
		WorldTransferObserver result = WorldTransferObserver.run(sender, worldName, worldUpload);
		Thread uploadThread = new Thread(worldUpload);
		uploadThread.start();
		return result;
	}
	
	/**
	 * Downloads a World from the FTP Server
	 * @param sender - The responsible Entity for this transaction
	 * @param worldName - The remote World Folder to be downloaded
	 * @return A <code>WorldTransferTask</code> reporting the status of the upload to the sender
	 */
	public static WorldTransferObserver downloadWorld(CommandSender sender, String worldName){
		return WorldTransferManager.downloadWorld(sender, worldName, worldName);
	}

	/**
	 * Downloads a World from the FTP Server
	 * @param sender - The responsible Entity for this transaction
	 * @param worldName - The remote World Folder to be downloaded
	 * @param overrideWorldName - The name of the World Folder on the local Server
	 * @return A <code>WorldTransferTask</code> reporting the status of the upload to the sender
	 */
	public static WorldTransferObserver downloadWorld(CommandSender sender, String worldName, String overrideWorldName){
		WorldDownload worldDownload = new WorldDownload(worldName,overrideWorldName);
		File packedDirectory = new File(WorldTransferManager.getTempFolder(), worldName);
		WorldTransferObserver result = WorldTransferObserver.run(sender, overrideWorldName, worldDownload);
		result.addImmediateOnFinishListener(()->{
			WorldTransferManager.unpackWorld(worldName, overrideWorldName, packedDirectory);
		});
		Thread downloadThread = new Thread(worldDownload);
		downloadThread.start();
		return result;
	}
	
	static File getTempFolder(){
		return new File(WorldManagerPlugin.getInstance().getDataFolder(), "temp");
	}
}
