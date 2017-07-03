package ch.swisssmp.adventuredungeons.mmoworld;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import ch.swisssmp.adventuredungeons.Main;
import ch.swisssmp.adventuredungeons.mmoevent.MmoEvent;
import ch.swisssmp.adventuredungeons.mmoevent.MmoEventType;
import ch.swisssmp.adventuredungeons.mmomultistatearea.MmoAreaState;
import ch.swisssmp.adventuredungeons.mmomultistatearea.MmoMultiStateArea;
import ch.swisssmp.adventuredungeons.mmoplayer.MmoPlayer;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.SwissSMPler;
import net.md_5.bungee.api.ChatColor;

public class MmoDungeonInstance implements Listener{
	public final int instance_id;
	public final int mmo_dungeon_id;
	public boolean running = false;
	public final List<String> player_uuids;
	public final List<String> ready_uuids = new ArrayList<String>();
	public final HashMap<String, Location> player_spawnpoints = new HashMap<String, Location>();
	protected BukkitTask countdownTask = null;
	private final ArrayList<UUID> invited_players = new ArrayList<UUID>();

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
		if(dataSection.contains("bedspawns")){
			ConfigurationSection bedspawnsSection = dataSection.getConfigurationSection("bedspawns");
			for(String key : bedspawnsSection.getKeys(false)){
				ConfigurationSection bedspawnSection = bedspawnsSection.getConfigurationSection(key);
				String worldName = bedspawnSection.getString("world");
				double x = bedspawnSection.getDouble("x");
				double y = bedspawnSection.getDouble("y");
				double z = bedspawnSection.getDouble("z");
				Location bedspawn = new Location(Bukkit.getWorld(worldName), x, y, z);
				player_spawnpoints.put(key, bedspawn);
			}
		}
		MmoDungeon.worldMap.put(this.getWorldName(), this);
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
	private void onPlayerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		MmoDungeonInstance dungeonInstance = MmoDungeon.getInstance(player.getUniqueId());
		MmoDungeon mmoDungeon = MmoDungeon.get(dungeonInstance);
		if(dungeonInstance==this && mmoDungeon.respawn!=null){
			Vector vector = mmoDungeon.respawn;
			if(vector!=null){
				World world = dungeonInstance.getWorld();
				if(world!=null){
					Location teleport_target = new Location(world, vector.getX()+0.5, vector.getY()+0.5, vector.getZ()+0.5);
					player.setBedSpawnLocation(teleport_target, true);
				}
				return;
			}
		}
	}
	@EventHandler(ignoreCancelled=true)
    public void onFrameBrake(HangingBreakEvent e) {
		if(e.getEntity().getLocation().getWorld()==this.getWorld()){
		    if (e.getCause()==RemoveCause.ENTITY || e.getCause()==RemoveCause.EXPLOSION) {
		        e.setCancelled(true);
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
		if(mmoDungeon.lobby_trigger>0){
			MmoWorldInstance worldInstance = this.getWorldInstance();
			if(worldInstance!=null){
				MmoMultiStateArea transformation = worldInstance.getTransformation(mmoDungeon.lobby_trigger);
				if(transformation!=null){
					MmoAreaState areaState = transformation.schematics.get("Offen");
					if(areaState!=null) areaState.trigger();
				}
			}
		}
		MmoEvent.fire(mmoDungeon.events, MmoEventType.DUNGEON_START, null);
		this.sendTitle(ChatColor.GREEN+"START!", mmoDungeon.name);
		for(String player_uuid : this.player_uuids){
			Player player = Bukkit.getPlayer(UUID.fromString(player_uuid));
			if(player!=null)
				MmoPlayer.updateMusic(player);
		}
	}
	protected void join(Player player){
		UUID player_uuid = player.getUniqueId();
		SwissSMPler swisssmpler = SwissSMPler.get(player);
		MmoDungeon mmoDungeon = MmoDungeon.get(this);
		if(this.player_uuids.size()>=mmoDungeon.maxPlayers){
			swisssmpler.sendMessage(ChatColor.RED+"Dieser Dungeon ist auf "+mmoDungeon.maxPlayers+" Spieler beschränkt.");
			return;
		}
		if(!this.player_uuids.contains(player_uuid.toString())){
			swisssmpler.sendMessage(ChatColor.RED+mmoDungeon.name+ChatColor.YELLOW+" beigetreten.");
			for(String otherPlayer : this.player_uuids){
				SwissSMPler othersmpler = SwissSMPler.get(UUID.fromString(otherPlayer));
				if(othersmpler!=null) othersmpler.sendMessage(ChatColor.YELLOW+player.getDisplayName()+ChatColor.YELLOW+" ist beigetreten.");
			}
			this.player_uuids.add(player_uuid.toString());
			Location bedspawn = player.getBedSpawnLocation();
			if(bedspawn==null) bedspawn = player.getWorld().getSpawnLocation();
			this.player_spawnpoints.put(player_uuid.toString(), new Location(bedspawn.getWorld(), bedspawn.getX(), bedspawn.getY(), bedspawn.getZ()));
			MmoDungeon.playerMap.put(player_uuid.toString(), this.instance_id);
			
			if(mmoDungeon.lobby_join!=null){
				Location teleport_target = new Location(this.getWorld(), mmoDungeon.lobby_join.getX()+0.5, mmoDungeon.lobby_join.getY()+0.5, mmoDungeon.lobby_join.getZ()+0.5);
				if(player!=null) player.teleport(teleport_target);
			}
			if(mmoDungeon.respawn!=null){
				player.setBedSpawnLocation(new Location(this.getWorld(), mmoDungeon.respawn.getX(),mmoDungeon.respawn.getY(),mmoDungeon.respawn.getZ()));
			}
		}
		invited_players.remove(player_uuid);
		MmoDungeon.saveDungeons();
		checkReady();
	}
	public void leave(UUID player_uuid){
		ready_uuids.remove(player_uuid.toString());
		Player player = Bukkit.getPlayer(player_uuid);
		if(player!=null){
			player.setGameMode(GameMode.SURVIVAL);
			Location bedspawn = this.player_spawnpoints.get(player_uuid.toString());
			if(bedspawn!=null){
				player.setBedSpawnLocation(bedspawn, true);
			}
		}
		MmoDungeon mmoDungeon = MmoDungeon.get(this);
		if(this.player_uuids.contains(player_uuid.toString())){
			this.player_uuids.remove(player_uuid.toString());
			MmoDungeon.playerMap.remove(player_uuid);
			SwissSMPler swisssmpler = SwissSMPler.get(player_uuid);
			if(swisssmpler!=null) swisssmpler.sendMessage(ChatColor.GRAY+mmoDungeon.name+" verlassen.");
			if(this.player_uuids.size()>0){
				for(String otherPlayer : this.player_uuids){
					SwissSMPler othersmpler = SwissSMPler.get(UUID.fromString(otherPlayer));
					if(othersmpler!=null) othersmpler.sendMessage(ChatColor.GRAY+player.getDisplayName()+" hat den Dungeon verlassen.");
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
		if(this.running) return false;
		if(this.player_uuids.size()<1) return false;
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
	public void sendTitle(String title){
		sendTitle(title, "");
	}
	public void sendTitle(String title, String subtitle){
		for(String player_uuid : player_uuids){
			SwissSMPler swisssmpler = SwissSMPler.get(UUID.fromString(player_uuid));
			if(swisssmpler!=null)swisssmpler.sendTitle(title, subtitle);
		}
	}
	public void addInvitedPlayer(UUID player_uuid){
		if(!invited_players.contains(player_uuid)){
			invited_players.add(player_uuid);
		}
	}
	public boolean isInvitedPlayer(UUID player_uuid){
		return invited_players.contains(player_uuid);
	}
	public void removeInvitedPlayer(UUID player_uuid){
		invited_players.remove(player_uuid);
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
		ConfigurationSection bedspawnsSection = dataSection.createSection("bedspawns");
		for(Entry<String, Location> bedspawnEntry : this.player_spawnpoints.entrySet()){
			ConfigurationSection bedspawnSection = bedspawnsSection.createSection(bedspawnEntry.getKey());
			Location bedspawn = bedspawnEntry.getValue();
			bedspawnSection.set("world", bedspawn.getWorld().getName());
			bedspawnSection.set("x", bedspawn.getX());
			bedspawnSection.set("y", bedspawn.getY());
			bedspawnSection.set("z", bedspawn.getZ());
		}
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
