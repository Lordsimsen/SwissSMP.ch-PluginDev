package ch.swisssmp.craftmmo.mmoworld;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoblock.MmoBlockScheduler;
import ch.swisssmp.craftmmo.mmoblock.MmoScheduledBlock;
import ch.swisssmp.craftmmo.mmocamp.MmoCamp;
import ch.swisssmp.craftmmo.mmomultistatearea.MmoMultiStateArea;
import ch.swisssmp.craftmmo.util.MmoDelayedThreadTask;
import ch.swisssmp.craftmmo.util.MmoFileUtil;

public class MmoWorldInstance {
	
	public final int mmo_world_id;
	public final String system_name;
	public final World world;
	public final MmoWorldType type;
	public final MmoBlockScheduler blockScheduler;
	public HashMap<Integer, MmoCamp> camps = new HashMap<Integer, MmoCamp>();
	public HashMap<Integer, MmoRegion> regions = new HashMap<Integer, MmoRegion>();
	public HashMap<String, MmoRegion> regionTriggers = new HashMap<String, MmoRegion>();
	public HashMap<Integer, MmoMultiStateArea> transformations = new HashMap<Integer, MmoMultiStateArea>();
	
	public synchronized static MmoWorldInstance load(ConfigurationSection dataSection){
		if(dataSection==null) return null;
		return new MmoWorldInstance(dataSection);
	}
	
	private MmoWorldInstance(ConfigurationSection dataSection){
		this.mmo_world_id = dataSection.getInt("mmo_world_id");
		this.system_name = dataSection.getString("system_name");
		this.type = MmoWorldType.valueOf(dataSection.getString("world_type"));
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
        MmoWorld.instances.put(this.world.getName(), this);
        loadResources();
	}
	
	public MmoWorldInstance(int mmo_world_id, String system_name, World world, MmoWorldType type){
		this.mmo_world_id = mmo_world_id;
		this.system_name = system_name;
		this.world = world;
		this.type = type;
		this.applyDefaultSettings();
		this.blockScheduler = new MmoBlockScheduler(this);
        MmoWorld.instances.put(this.world.getName(), this);
        loadResources();
        MmoWorld.saveWorlds();
	}
	
	private void loadResources(){
		try {
			MmoRegion.loadRegions(this, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			MmoMultiStateArea.loadTransformations(this, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save(ConfigurationSection dataSection){
		dataSection.set("mmo_world_id", this.mmo_world_id);
		dataSection.set("system_name", this.system_name);
		dataSection.set("world_name", this.world.getName());
		dataSection.set("world_type", this.type.toString());
	}
	
	public boolean delete(Location leavePoint, boolean deleteConfiguration){
		for(MmoScheduledBlock scheduledBlock : this.blockScheduler.blocks.values()){
			scheduledBlock.cancel();
		}
    	List<Player> players = world.getPlayers();
    	for(Player player : players){
    		player.teleport(leavePoint);
    	}
    	players.clear();
    	String worldName = world.getName();
        if(Bukkit.getServer().unloadWorld(world, true)){
	    	Main.info("Unloaded world "+world.getName());
	    	MmoWorld.instances.remove(worldName);
			File path = new File(Bukkit.getWorldContainer(), worldName);
	    	deleteFiles(path);
			WorldGuardPlugin worldGuard = Main.worldGuardPlugin;
	    	if(deleteConfiguration){
				File regionConfiguration = new File(worldGuard.getDataFolder(), "worlds/"+worldName);
				MmoFileUtil.deleteRecursive(regionConfiguration);
				File dataFolder = this.getDataFolder();
				if(dataFolder.exists()) dataFolder.delete();
	    	}
	        MmoWorld.saveWorlds();
			worldGuard.reloadConfig();
	    	return true;
        }
        else{
        	Main.info("There was an error unloading the world "+world.getName());
        	Main.info("Players still inside: "+world.getPlayers().size());
        	return false;
        }
	}
	private void applyDefaultSettings(){
		world.setDifficulty(Difficulty.HARD);
		world.setGameRuleValue("doMobSpawning", "false");
		world.setGameRuleValue("doDaylightCycle", "false");
	}
	public void setSpawnpoint(Location location){
		if(location!=null){
			world.setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		}
	}
	
	public MmoCamp getCamp(int mmo_camp_id){
		return this.camps.get(mmo_camp_id);
	}
	public MmoRegion getRegion(int mmo_region_id){
		return this.regions.get(mmo_region_id);
	}
	public MmoMultiStateArea getTransformation(int mmo_multistatearea_id){
		return this.transformations.get(mmo_multistatearea_id);
	}
	public File getDataFolder(){
		return new File(Main.dataFolder, "worlds/"+this.world.getName());
	}
	
	//static stuff
	public static void deleteFiles(File path){
		Runnable task = new MmoDelayedThreadTask(new Thread(()->{
			MmoFileUtil.deleteRecursive(path);
		}));
		Bukkit.getScheduler().runTaskLater(Main.plugin, task, 20);
	}
}
