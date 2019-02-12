package ch.swisssmp.travel;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.webcore.HTTPRequest;
import ch.swisssmp.world.WorldManager;
import ch.swisssmp.world.transfer.WorldTransferManager;
import ch.swisssmp.world.transfer.WorldTransferObserver;

public class TravelWorld {
	private static List<World> loadedWorlds = new ArrayList<World>();
	
	public static void edit(String worldName, Player player){

		if(worldName==null || worldName.isEmpty()) return;
		if(WorldTransferManager.localWorldExists(worldName)){
			loadWorld(worldName, player);
		}
		HTTPRequest checkRemoteWorld = WorldTransferManager.remoteWorldExists(worldName);
		checkRemoteWorld.onFinish(()->{
			if(checkRemoteWorld.getResponse().equals("true")){
				WorldTransferObserver observer = WorldTransferManager.downloadWorld(player, worldName);
				observer.addOnFinishListener(()->{
					loadWorld(worldName, player);
				});
			}
			else{
				Bukkit.dispatchCommand(player, "world create "+worldName);
			}
		});
	}
	
	private static void loadWorld(String worldName, Player player){
		World world = WorldManager.loadWorld(worldName);
		if(world==null) return;
		loadedWorlds.add(world);
		editTemplate(worldName, player);
	}
	
	private static void editTemplate(String worldName, Player player){
		Bukkit.getScheduler().runTaskLater(TravelSystem.getInstance(), ()->{
			World world = Bukkit.getWorld(worldName);
			player.teleport(world.getSpawnLocation());
		}, 5L);
	}
	
	public static void endedit(World world, CommandSender sender){
		if(!loadedWorlds.contains(world)) return;
		loadedWorlds.remove(world);
		for(Player player : world.getPlayers()){
			player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
		}
		world.save();
		Bukkit.getScheduler().runTaskLater(TravelSystem.getInstance(), ()->{
			if(!WorldManager.unloadWorld(world.getName())) return;
		}, 10L);
		Bukkit.getScheduler().runTaskLater(TravelSystem.getInstance(), ()->{
			WorldTransferObserver observer = WorldTransferManager.uploadWorld(sender, world.getName());
			observer.addOnFinishListener(()->{
				WorldManager.deleteWorld(world.getName());
			});
		}, 40L);
	}
}
