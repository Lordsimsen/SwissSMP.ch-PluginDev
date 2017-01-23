package ch.swisssmp.craftmmo.mmoblock;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.craftmmo.Main;

public class MmoScheduledBlock implements Runnable{

	private final MmoBlockScheduler scheduler;
	public final Location location;
	public final MaterialData sourceData;
	public final MaterialData targetData;
	public final UUID player_uuid;
	protected BukkitTask task;
	
	private final int endTime;
	
	public MmoScheduledBlock(MmoBlockScheduler scheduler, Location location, MaterialData targetData, int time, UUID player_uuid){
		this.scheduler = scheduler;
		this.location = location;
		this.sourceData = location.getBlock().getState().getData();
		this.targetData = targetData;
		this.player_uuid = player_uuid;
		int startTime = Math.round(System.currentTimeMillis()/1000);
		this.endTime = startTime+time;
		MmoScheduledBlock conflicting = scheduler.blocks.get(this.location);
		if(conflicting!=null){
			conflicting.cancel();
		}
		scheduler.blocks.put(this.location, this);
		scheduler.saveAll();
	}
	
	private MmoScheduledBlock(MmoBlockScheduler scheduler, ConfigurationSection dataSection){
		this.scheduler = scheduler;
		this.location = MmoBlock.get(dataSection, scheduler.world).getLocation();
		this.sourceData = MmoBlock.getMaterialData(dataSection.getString("source"));
		this.targetData = MmoBlock.getMaterialData(dataSection.getString("target"));
		this.player_uuid = UUID.fromString(dataSection.getString("player_uuid"));
		int startTime = Math.round(System.currentTimeMillis()/1000);
		this.endTime = startTime+dataSection.getInt("time");
		MmoScheduledBlock conflicting = scheduler.blocks.get(this.location);
		if(conflicting!=null){
			conflicting.cancel();
		}
		scheduler.blocks.put(this.location, this);
	}
	protected static MmoScheduledBlock create(MmoBlockScheduler scheduler, Location location, MaterialData targetData, int time, UUID player_uuid){
		MmoScheduledBlock scheduledBlock = new MmoScheduledBlock(scheduler, location, targetData, time, player_uuid);
		BukkitTask task = Bukkit.getScheduler().runTaskLater(Main.plugin, scheduledBlock, time*20);
		scheduledBlock.task = task;
		return scheduledBlock;
	}
	public static MmoScheduledBlock load(MmoBlockScheduler scheduler, ConfigurationSection dataSection){
		MmoScheduledBlock scheduledBlock = new MmoScheduledBlock(scheduler, dataSection);
		BukkitTask task = Bukkit.getScheduler().runTaskLater(Main.plugin, scheduledBlock, dataSection.getInt("time")*20);
		scheduledBlock.task = task;
		return scheduledBlock;
	}
	
	@Override
	public void run() {
		if(this.location.getBlock().getState().getData().equals(this.sourceData)){
			MmoBlock.set(location.getBlock(), targetData, player_uuid);
		}
		else{
			Main.info("Block doesn't match anymore, cancelling.");
		}
		this.delete();
		this.scheduler.saveAll();
	}
	public void save(ConfigurationSection dataSection){
		dataSection.set("x", location.getX());
		dataSection.set("y", location.getY());
		dataSection.set("z", location.getZ());
		dataSection.set("source", MmoBlock.getMaterialString(sourceData, true));
		dataSection.set("target", MmoBlock.getMaterialString(targetData, true));
		int currentTime = Math.round(System.currentTimeMillis()/1000);
		dataSection.set("time", (this.endTime-currentTime));
		dataSection.set("player_uuid", this.player_uuid.toString());
	}
	public void delete(){
		scheduler.blocks.remove(this.location);
	}
	public void cancel(){
		this.delete();
		this.task.cancel();
	}
}
