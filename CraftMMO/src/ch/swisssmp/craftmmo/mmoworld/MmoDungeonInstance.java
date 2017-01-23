package ch.swisssmp.craftmmo.mmoworld;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoevent.MmoEvent;
import ch.swisssmp.craftmmo.mmoevent.MmoEventType;
import ch.swisssmp.craftmmo.mmoevent.MmoPlayerDeathEvent;
import ch.swisssmp.craftmmo.mmoplayer.MmoPlayer;
import net.md_5.bungee.api.ChatColor;

public class MmoDungeonInstance implements Listener{
	public final int instance_id;
	public final int mmo_dungeon_id;
	public boolean running = false;
	public final List<String> player_uuids;
	public final List<String> ready_uuids = new ArrayList<String>();
	protected BukkitTask countdownTask = null;

	public static MmoDungeonInstance load(ConfigurationSection dataSection){
		if(dataSection==null) return null;
		return new MmoDungeonInstance(dataSection);
	}
	//constructors
	private MmoDungeonInstance(ConfigurationSection dataSection){
		this.instance_id = dataSection.getInt("instance_id");
		this.mmo_dungeon_id = dataSection.getInt("mmo_dungeon_id");
		if(dataSection.contains("running")){
			this.running = dataSection.getBoolean("running");
		}
		if(dataSection.contains("player_uuids")){
			this.player_uuids = dataSection.getStringList("player_uuids");
			for(String player_uuid : player_uuids){
				MmoDungeon.playerMap.put(player_uuid, this.instance_id);
			}
		}
		else{
			this.player_uuids = new ArrayList<String>();
		}
		MmoDungeon.instances.put(this.instance_id, this);
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
		Main.debug("Loaded dungeon instance "+this.instance_id);
	}
	public MmoDungeonInstance(int mmo_dungeon_id, int instance_id, ArrayList<String> player_uuids){
		this.mmo_dungeon_id = mmo_dungeon_id;
		this.instance_id = instance_id;
		this.player_uuids = player_uuids;
		for(String player_uuid : player_uuids){
			MmoDungeon.playerMap.put(player_uuid, this.instance_id);
		}
		MmoDungeon mmoDungeon = MmoDungeon.get(mmo_dungeon_id);
		//copy world data
		MmoWorld.copyDirectory(mmoDungeon.getTemplateDirectory(), this.getWorldDirectory());
		//copy worldguard regions
		MmoWorld.copyDirectory(mmoDungeon.getWorldguardDirectory(), this.getWorldguardDirectory());
		
		MmoDungeon.instances.put(this.instance_id, this);
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
		MmoDungeon.saveDungeons();
	}
	
	//Event
	@EventHandler
	private void onPlayerDeath(MmoPlayerDeathEvent event){
		MmoRegion mmoRegion = event.mmoRegion;
		MmoDungeonInstance dungeonInstance = MmoDungeon.getInstance(event.player.getUniqueId());
		MmoDungeon mmoDungeon = MmoDungeon.get(dungeonInstance);
		if(dungeonInstance==this && mmoDungeon.respawn_points.containsKey(mmoRegion.mmo_region_id)){
			String respawn_point = mmoDungeon.respawn_points.get(mmoRegion.mmo_region_id);
			Vector vector = mmoDungeon.points.get(respawn_point);
			if(vector!=null){
				World world = dungeonInstance.getWorld();
				if(world!=null){
					Location teleport_target = new Location(world, vector.getX()+0.5, vector.getY()+0.5, vector.getZ()+0.5);
					event.player.teleport(teleport_target);
				}
				return;
			}
		}
	}
	
	//actions
	public void start(){
		Main.info("Dungeon instance "+this.instance_id+" has started!");
		this.running = true;
		MmoDungeon.saveDungeons();
		MmoDungeon mmoDungeon = MmoDungeon.get(this);
		if(mmoDungeon==null) return;
		MmoEvent.fire(mmoDungeon.events, MmoEventType.DUNGEON_START, null);
		this.sendTitle(ChatColor.GREEN+"START!", mmoDungeon.name, 2, 3, 2);
	}
	protected void join(Player player){
		UUID player_uuid = player.getUniqueId();
		MmoDungeon mmoDungeon = MmoDungeon.get(this);
		if(this.player_uuids.size()>=mmoDungeon.maxPlayers){
			MmoPlayer.sendMessage(player_uuid, ChatColor.RED+"Dieser Dungeon ist auf "+mmoDungeon.maxPlayers+" Spieler beschränkt.");
			MmoPlayer.sendMessage(player_uuid, ChatColor.GRAY+"Du kannst mit '/party leave' die Gruppe verlassen, um eine separate Instanz zu starten.");
			return;
		}
		if(!this.player_uuids.contains(player_uuid.toString())){
			MmoPlayer.sendMessage(player_uuid, ChatColor.GRAY+mmoDungeon.name+" beigetreten.");
			for(String otherPlayer : this.player_uuids){
				MmoPlayer.sendMessage(UUID.fromString(otherPlayer), ChatColor.GRAY+player.getDisplayName()+" ist beigetreten.");
			}
			this.player_uuids.add(player_uuid.toString());
			MmoDungeon.playerMap.put(player_uuid.toString(), this.instance_id);
			if(mmoDungeon.lobby_join!=null){
				Location teleport_target = new Location(this.getWorld(), mmoDungeon.lobby_join.getX()+0.5, mmoDungeon.lobby_join.getY()+0.5, mmoDungeon.lobby_join.getZ()+0.5);
				if(player!=null) player.teleport(teleport_target);
			}
		}
		MmoDungeon.saveDungeons();
		checkReady();
	}
	protected void leave(UUID player_uuid){
		ready_uuids.remove(player_uuid.toString());
		Player player = Bukkit.getPlayer(player_uuid);
		MmoDungeon mmoDungeon = MmoDungeon.get(this);
		if(this.player_uuids.contains(player_uuid.toString())){
			this.player_uuids.remove(player_uuid.toString());
			MmoDungeon.playerMap.remove(player_uuid);
			MmoPlayer.sendMessage(player_uuid, ChatColor.GRAY+mmoDungeon.name+" verlassen.");
			if(this.player_uuids.size()>0){
				for(String otherPlayer : this.player_uuids){
					MmoPlayer.sendMessage(UUID.fromString(otherPlayer), ChatColor.GRAY+player.getDisplayName()+" hat den Dungeon verlassen.");
				}
			}
			else{
				this.delete();
			}
			if(mmoDungeon.lobby_leave!=null && player!=null){
				Location teleport_target = mmoDungeon.getLeavePoint();
				player.teleport(teleport_target);
			}
		}
		MmoDungeon.saveDungeons();
		checkReady();
	}
	public boolean toggleReady(String player_uuid){
		boolean result;
		if(ready_uuids.contains(player_uuid)){
			ready_uuids.remove(player_uuid);
			result = false;
		}
		else{
			ready_uuids.add(player_uuid);
			result = true;
		}
		checkReady();
		return result;
	}
	public boolean playersReady(){
		boolean ready = true;
		for(String player_uuid : this.player_uuids){
			if(!this.ready_uuids.contains(player_uuid)){
				ready = false;
			}
		}
		return ready;
	}
	public boolean checkReady(){
		if(playersReady()){
			if(this.countdownTask==null){
				Runnable countdownTask = new MmoDungeonCountdownTask(this, 3);
				this.countdownTask = Bukkit.getScheduler().runTaskLater(Main.plugin, countdownTask, 20);
			}
			return true;
		}
		else{
			if(this.countdownTask!=null){
				this.countdownTask.cancel();
				this.countdownTask = null;
			}
			return false;
		}
	}
	public void sendTitle(String title, int fadeIn, int stay, int fadeOut){
		sendTitle(title, "", fadeIn, stay, fadeOut);
	}
	public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut){
		for(String player_uuid : player_uuids){
			Player player = Bukkit.getPlayer(UUID.fromString(player_uuid));
			MmoPlayer.sendTitle(player, title, subtitle, fadeIn, stay, fadeOut);
		}
	}
	
	//getters
	public File getWorldguardDirectory(){
		WorldGuardPlugin worldGuard = Main.worldGuardPlugin;
		return new File(worldGuard.getDataFolder(), "worlds/"+getWorldName());
	}
	public File getWorldDirectory(){
		return new File(Bukkit.getWorldContainer(), this.getWorldName());
	}
	public String getWorldName(){
		return "dungeon_instance_"+this.instance_id;
	}
	public MmoWorldInstance getWorldInstance(){
		return MmoWorld.getInstance(this.getWorldName());
	}
	public World getWorld(){
		return Bukkit.getWorld(this.getWorldName());
	}
	
	//system
	public void save(ConfigurationSection dataSection){
		dataSection.set("instance_id", this.instance_id);
		dataSection.set("mmo_dungeon_id", this.mmo_dungeon_id);
		dataSection.set("player_uuids", this.player_uuids);
		dataSection.set("running", running);
	}
	public void delete(){
		MmoDungeon mmoDungeon = MmoDungeon.get(this.mmo_dungeon_id);
		MmoWorldInstance worldInstance = MmoWorld.getInstance(this);
		World world = worldInstance.world;
		for(Player player : world.getPlayers()){
			leave(player.getUniqueId());
		}
		worldInstance.delete(mmoDungeon.getLeavePoint(), true);
		
		MmoDungeon.instances.remove(this.instance_id);
		MmoDungeon.saveDungeons();
		HandlerList.unregisterAll(this);
	}
}
