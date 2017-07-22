package ch.swisssmp.adventuredungeons.world;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.block.MmoBlockScheduler;
import ch.swisssmp.adventuredungeons.block.MmoScheduledBlock;
import ch.swisssmp.adventuredungeons.entity.MmoEntitySaveData;
import ch.swisssmp.adventuredungeons.util.MmoFileUtil;
import ch.swisssmp.utils.ConfigurationSection;

public class AdventureWorldInstance {
	
	public final int world_id;
	public final String system_name;
	public final World world;
	public final AdventureWorldType type;
	public final MmoBlockScheduler blockScheduler;
	//public HashMap<Integer, MmoRegion> regions = new HashMap<Integer, MmoRegion>();
	//public HashMap<String, MmoRegion> regionTriggers = new HashMap<String, MmoRegion>();
	public final MmoEntitySaveData entitySaveData;
	
	public synchronized static AdventureWorldInstance load(ConfigurationSection dataSection){
		if(dataSection==null) return null;
		return new AdventureWorldInstance(dataSection);
	}
	
	private AdventureWorldInstance(ConfigurationSection dataSection){
		this.world_id = dataSection.getInt("mmo_world_id");
		this.system_name = dataSection.getString("system_name");
		this.type = AdventureWorldType.valueOf(dataSection.getString("world_type"));
		String world_name = dataSection.getString("world_name");
		World world = Bukkit.getWorld(world_name);
		if(world!=null){
			this.world = world;
		}
		else{
			this.world = Bukkit.createWorld(new WorldCreator(world_name));
		}
		this.applyDefaultSettings();
		File dataFolder = this.getDataFolder();
		if(!dataFolder.exists()){
			dataFolder.mkdirs();
		}
		this.blockScheduler = new MmoBlockScheduler(this);
		this.entitySaveData = new MmoEntitySaveData(this);
        AdventureWorld.instances.put(this.world.getName(), this);
	}
	
	public AdventureWorldInstance(int mmo_world_id, String system_name, World world, AdventureWorldType type){
		this.world_id = mmo_world_id;
		this.system_name = system_name;
		this.world = world;
		this.type = type;
		this.applyDefaultSettings();
		this.blockScheduler = new MmoBlockScheduler(this);
		this.entitySaveData = new MmoEntitySaveData(this);
        AdventureWorld.instances.put(this.world.getName(), this);
        AdventureWorld.saveWorlds();
        WorldGuardPlugin.inst().reloadConfig();
	}
	
	public void save(ConfigurationSection dataSection){
		dataSection.set("mmo_world_id", this.world_id);
		dataSection.set("system_name", this.system_name);
		dataSection.set("world_name", this.world.getName());
		dataSection.set("world_type", this.type.toString());
	}
	
	public boolean delete(Location leavePoint, boolean deleteConfiguration){
		//first make a copy of all the blocks because they will self-destruct (work against concurrent modification here)
		ArrayList<MmoScheduledBlock> scheduledBlocks = new ArrayList<MmoScheduledBlock>();
		for(MmoScheduledBlock scheduledBlock : this.blockScheduler.blocks.values()){
			scheduledBlocks.add(scheduledBlock);
		}
		for(MmoScheduledBlock scheduledBlock : scheduledBlocks){
			scheduledBlock.cancel();
		}
    	List<Player> players = world.getPlayers();
    	for(Player player : players){
    		player.teleport(leavePoint);
    	}
    	players.clear();
    	String worldName = world.getName();
        if(Bukkit.getServer().unloadWorld(world, true)){
	    	AdventureDungeons.info("Unloaded world "+world.getName());
	    	AdventureWorld.instances.remove(worldName);
			File path = new File(Bukkit.getWorldContainer(), worldName);
	    	deleteFiles(path);
			WorldGuardPlugin worldGuard = AdventureDungeons.worldGuardPlugin;
	    	if(deleteConfiguration){
				File regionConfiguration = new File(worldGuard.getDataFolder(), "worlds/"+worldName);
				MmoFileUtil.deleteRecursive(regionConfiguration);
				File dataFolder = this.getDataFolder();
				if(dataFolder.exists()) dataFolder.delete();
	    	}
	        AdventureWorld.saveWorlds();
			worldGuard.reloadConfig();
	    	return true;
        }
        else{
        	AdventureDungeons.info("There was an error unloading the world "+world.getName());
        	AdventureDungeons.info("Players still inside: "+world.getPlayers().size());
        	return false;
        }
	}
	private void applyDefaultSettings(){
		world.setDifficulty(Difficulty.HARD);
		//world.setGameRuleValue("doMobSpawning", "false");
		//world.setGameRuleValue("doDaylightCycle", "false");
	}
	public void setSpawnpoint(Location location){
		if(location!=null){
			world.setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		}
	}
	/*public MmoRegion getRegion(int mmo_region_id){
		return this.regions.get(mmo_region_id);
	}*/
	public File getDataFolder(){
		return new File(AdventureDungeons.dataFolder, "worlds/"+this.world.getName());
	}
	
	//static stuff
	public static void deleteFiles(File path){
		Bukkit.getScheduler().runTaskLater(AdventureDungeons.plugin, new Runnable(){
			public void run(){
				Thread thread = new Thread(()->{
					MmoFileUtil.deleteRecursive(path);
				});
				thread.start();
			}
		}, 20);
	}
}
