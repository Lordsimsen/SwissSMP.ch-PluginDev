package ch.swisssmp.adventuredungeons.world;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.camp.Camp;
import ch.swisssmp.adventuredungeons.event.DungeonStartEvent;
import ch.swisssmp.adventuredungeons.player.MmoPlayer;
import ch.swisssmp.transformations.AreaState;
import ch.swisssmp.transformations.TransformationArea;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.RandomizedLocation;
import ch.swisssmp.utils.SwissSMPler;
import net.md_5.bungee.api.ChatColor;

public class DungeonInstance implements Listener,Instancable{
	private final int instance_id;
	public final int dungeon_id;
	public boolean running = false;
	public final List<String> player_uuids;
	public final List<String> ready_uuids = new ArrayList<String>();
	private int respawnIndex = 0;
	protected BukkitTask countdownTask = null;
	private final ArrayList<UUID> invited_players = new ArrayList<UUID>();
	public HashMap<Integer, Camp> camps = new HashMap<Integer, Camp>();

	public static DungeonInstance load(ConfigurationSection dataSection){
		if(dataSection==null) return null;
		return new DungeonInstance(dataSection);
	}
	//constructors
	private DungeonInstance(ConfigurationSection dataSection){
		this.instance_id = dataSection.getInt("instance_id");
		this.dungeon_id = dataSection.getInt("mmo_dungeon_id");
		if(dataSection.contains("running")){
			this.running = dataSection.getBoolean("running");
		}
		if(dataSection.contains("player_uuids")){
			this.player_uuids = dataSection.getStringList("player_uuids");
			for(String player_uuid : player_uuids){
				Dungeon.playerMap.put(player_uuid, this.instance_id);
			}
		}
		else{
			this.player_uuids = new ArrayList<String>();
		}
		if(dataSection.contains("respawnIndex")){
			this.respawnIndex = dataSection.getInt("respawnIndex");
		}
		Dungeon.worldMap.put(this.getWorldName(), this);
		Dungeon.instances.put(this.instance_id, this);
		Bukkit.getPluginManager().registerEvents(this, AdventureDungeons.plugin);
		AdventureDungeons.debug("Loaded dungeon instance "+this.instance_id);
	}
	public DungeonInstance(int mmo_dungeon_id, int instance_id, ArrayList<String> player_uuids){
		this.dungeon_id = mmo_dungeon_id;
		this.instance_id = instance_id;
		this.player_uuids = player_uuids;
		for(String player_uuid : player_uuids){
			Dungeon.playerMap.put(player_uuid, this.instance_id);
		}
		Dungeon mmoDungeon = Dungeon.get(mmo_dungeon_id);
		//copy world data
		AdventureWorld.copyDirectory(mmoDungeon.getTemplateDirectory(), this.getWorldDirectory());
		//copy worldguard regions
		AdventureWorld.copyDirectory(mmoDungeon.getWorldguardDirectory(), this.getWorldguardDirectory());
		
		Dungeon.instances.put(this.instance_id, this);
		Bukkit.getPluginManager().registerEvents(this, AdventureDungeons.plugin);
		Dungeon.saveDungeons();
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
		AdventureDungeons.info("Dungeon instance "+this.instance_id+" has started!");
		this.running = true;
		Dungeon.saveDungeons();
		Dungeon dungeon = Dungeon.get(this);
		if(dungeon==null) return;
		if(dungeon.lobby_trigger>0){
			AdventureWorldInstance worldInstance = this.getWorldInstance();
			if(worldInstance!=null){
				TransformationArea transformation = TransformationArea.get(dungeon.lobby_trigger);
				if(transformation!=null){
					AreaState areaState = transformation.schematics.get("Offen");
					if(areaState!=null) areaState.trigger();
				}
			}
		}
		Bukkit.getPluginManager().callEvent(new DungeonStartEvent(this.dungeon_id));
		this.sendTitle(ChatColor.GREEN+"START!", dungeon.name);
		for(String player_uuid : this.player_uuids){
			Player player = Bukkit.getPlayer(UUID.fromString(player_uuid));
			if(player!=null)
				MmoPlayer.updateMusic(player);
		}
	}
	protected void join(Player player){
		UUID player_uuid = player.getUniqueId();
		SwissSMPler swisssmpler = SwissSMPler.get(player);
		Dungeon mmoDungeon = Dungeon.get(this);
		if(this.player_uuids.size()>=mmoDungeon.maxPlayers){
			swisssmpler.sendMessage(ChatColor.RED+"Dieser Dungeon ist auf "+mmoDungeon.maxPlayers+" Spieler beschr�nkt.");
			return;
		}
		if(!this.player_uuids.contains(player_uuid.toString())){
			swisssmpler.sendMessage(ChatColor.RED+mmoDungeon.name+ChatColor.YELLOW+" beigetreten.");
			for(String otherPlayer : this.player_uuids){
				SwissSMPler othersmpler = SwissSMPler.get(UUID.fromString(otherPlayer));
				if(othersmpler!=null) othersmpler.sendMessage(ChatColor.YELLOW+player.getDisplayName()+ChatColor.YELLOW+" ist beigetreten.");
			}
			this.player_uuids.add(player_uuid.toString());
			Dungeon.playerMap.put(player_uuid.toString(), this.instance_id);
			
			if(mmoDungeon.lobby_join!=null){
				Location teleport_target = mmoDungeon.lobby_join.getLocation(this.getWorld());
				if(player!=null) player.teleport(teleport_target);
			}
		}
		invited_players.remove(player_uuid);
		Dungeon.saveDungeons();
		checkReady();
	}
	public void leave(UUID player_uuid){
		ready_uuids.remove(player_uuid.toString());
		Player player = Bukkit.getPlayer(player_uuid);
		if(player!=null){
			player.setGameMode(GameMode.SURVIVAL);
		}
		Dungeon mmoDungeon = Dungeon.get(this);
		if(this.player_uuids.contains(player_uuid.toString())){
			this.player_uuids.remove(player_uuid.toString());
			Dungeon.playerMap.remove(player_uuid);
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
		Dungeon.saveDungeons();
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
				Runnable countdownTask = new DungeonCountdownTask(this, 3);
				this.countdownTask = Bukkit.getScheduler().runTaskLater(AdventureDungeons.plugin, countdownTask, 20);
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
	public void setRespawnIndex(int respawnIndex){
		this.respawnIndex = Math.min(this.getDungeon().respawnLocations.size()-1, Math.max(this.respawnIndex, respawnIndex));
	}
	
	//getters
	public File getWorldguardDirectory(){
		WorldGuardPlugin worldGuard = AdventureDungeons.worldGuardPlugin;
		return new File(worldGuard.getDataFolder(), "worlds/"+getWorldName());
	}
	public File getWorldDirectory(){
		return new File(Bukkit.getWorldContainer(), this.getWorldName());
	}
	public String getWorldName(){
		return "dungeon_instance_"+this.instance_id;
	}
	public AdventureWorldInstance getWorldInstance(){
		return AdventureWorld.getInstance(this.getWorldName());
	}
	public World getWorld(){
		return Bukkit.getWorld(this.getWorldName());
	}
	public Dungeon getDungeon(){
		return Dungeon.get(this.dungeon_id);
	}
	public Location getRespawnLocation(){
		RandomizedLocation randomizedRespawn = this.getDungeon().respawnLocations.get(this.respawnIndex);
		return randomizedRespawn.getLocation(this.getWorld());
	}
	
	public Camp getCamp(int camp_id){
		return this.camps.get(camp_id);
	}
	
	//system
	public void save(ConfigurationSection dataSection){
		dataSection.set("instance_id", this.instance_id);
		dataSection.set("mmo_dungeon_id", this.dungeon_id);
		dataSection.set("player_uuids", this.player_uuids);
		dataSection.set("running", running);
		dataSection.set("respawnIndex", this.respawnIndex);
	}
	public void delete(){
		Dungeon mmoDungeon = Dungeon.get(this.dungeon_id);
		AdventureWorldInstance worldInstance = AdventureWorld.getInstance(this);
		World world = worldInstance.world;
		for(Player player : world.getPlayers()){
			leave(player.getUniqueId());
		}
		worldInstance.delete(mmoDungeon.getLeavePoint(), true);
		
		Dungeon.instances.remove(this.instance_id);
		Dungeon.saveDungeons();
		HandlerList.unregisterAll(this);
	}
	@Override
	public int getInstanceId() {
		return this.instance_id;
	}
}
