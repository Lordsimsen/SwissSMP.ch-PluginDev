package ch.swisssmp.adventuredungeons.world;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.event.DungeonEndEvent;
import ch.swisssmp.adventuredungeons.event.DungeonStartEvent;
import ch.swisssmp.adventuredungeons.event.listener.EventListenerMaster;
import ch.swisssmp.adventuredungeons.sound.MusicLoop;
import ch.swisssmp.transformations.AreaState;
import ch.swisssmp.transformations.TransformationArea;
import ch.swisssmp.transformations.TransformationWorld;
import ch.swisssmp.utils.RandomizedLocation;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.WorldUtil;
import net.md_5.bungee.api.ChatColor;

public class DungeonInstance{
	private final int instance_id;
	private final int dungeon_id;
	private World world;
	private final Difficulty difficulty;
	private final EventListenerMaster eventListener;
	private boolean running = false;
	private final List<String> player_uuids;
	private final List<String> ready_uuids = new ArrayList<String>();
	private int respawnIndex = 0;
	BukkitTask countdownTask = null;
	private final ArrayList<UUID> invited_players = new ArrayList<UUID>();
	private final HashMap<Entity,Integer> entityMap = new HashMap<Entity,Integer>();
	private final TransformationWorld transformationworld;

	public DungeonInstance(int dungeon_id, World world, Difficulty difficulty, int instance_id, ArrayList<String> player_uuids){
		this.dungeon_id = dungeon_id;
		this.instance_id = instance_id;
		this.player_uuids = player_uuids;
		for(String player_uuid : player_uuids){
			Dungeon.playerMap.put(player_uuid, this.instance_id);
		}
		
		this.world = world;
		this.difficulty = difficulty;
		this.eventListener = new EventListenerMaster(this);
		this.transformationworld = TransformationWorld.get(world);
		this.transformationworld.loadTransformations("dungeon_template_"+this.dungeon_id);
		
		Dungeon.instances.put(this.instance_id, this);
		Dungeon.worldMap.put(this.world.getName(), this);
	}
	
	//actions
	public void start(){
		AdventureDungeons.info("Dungeon instance "+this.instance_id+" has started!");
		this.running = true;
		Dungeon dungeon = Dungeon.get(this);
		if(dungeon==null) return;
		if(dungeon.lobby_trigger>0){
			TransformationArea transformation = transformationworld.getTransformation(dungeon.lobby_trigger);
			if(transformation!=null){
				AreaState areaState = transformation.getSchematic("Offen");
				if(areaState!=null) areaState.trigger();
			}
		}
		Bukkit.getPluginManager().callEvent(new DungeonStartEvent(this));
		this.sendTitle(ChatColor.GREEN+"START!", dungeon.name);
		for(String player_uuid : this.player_uuids){
			Player player = Bukkit.getPlayer(UUID.fromString(player_uuid));
			if(player!=null)
				MusicLoop.update(player);
		}
	}
	protected void join(Player player){
		UUID player_uuid = player.getUniqueId();
		SwissSMPler swisssmpler = SwissSMPler.get(player);
		Dungeon dungeon = Dungeon.get(this);
		if(this.player_uuids.size()>=dungeon.maxPlayers){
			swisssmpler.sendActionBar(ChatColor.RED+"Dieser Dungeon ist auf "+dungeon.maxPlayers+" Spieler beschränkt.");
			return;
		}
		if(!this.player_uuids.contains(player_uuid.toString())){
			swisssmpler.sendMessage("["+ChatColor.RED+dungeon.name+ChatColor.RESET+"]"+ChatColor.YELLOW+" beigetreten.");
			swisssmpler.sendMessage("["+ChatColor.RED+dungeon.name+ChatColor.RESET+"]"+ChatColor.YELLOW+" Verwende jederzeit §o§a/leave§r§E, um die Instanz wieder zu verlassen.");
			switch(this.difficulty){
			case EASY:{
				swisssmpler.sendMessage("["+ChatColor.RED+dungeon.name+ChatColor.RESET+"]"+ChatColor.YELLOW+" Schwierigkeit: "+ChatColor.GREEN+"Einfach "+ChatColor.YELLOW+"(Behalte XP und Items bei Tod)");
				break;
			}
			case NORMAL:{
				swisssmpler.sendMessage("["+ChatColor.RED+dungeon.name+ChatColor.RESET+"]"+ChatColor.YELLOW+" Schwierigkeit: "+ChatColor.RED+"Normal "+ChatColor.YELLOW+"(Verliere XP und behalte Items bei Tod)");
				break;
			}
			case HARD:{
				swisssmpler.sendMessage("["+ChatColor.RED+dungeon.name+ChatColor.RESET+"]"+ChatColor.YELLOW+" Schwierigkeit: "+ChatColor.DARK_PURPLE+"Hart "+ChatColor.YELLOW+"(Verliere XP und Items bei Tod)");
				break;
			}
			default:{
				break;
			}
			}
			for(String otherPlayer : this.player_uuids){
				SwissSMPler othersmpler = SwissSMPler.get(UUID.fromString(otherPlayer));
				if(othersmpler!=null) othersmpler.sendMessage(ChatColor.YELLOW+player.getDisplayName()+ChatColor.YELLOW+" ist beigetreten.");
			}
			this.player_uuids.add(player_uuid.toString());
			Dungeon.playerMap.put(player_uuid.toString(), this.instance_id);
			
			if(dungeon.lobby_join!=null){
				Location teleport_target = dungeon.lobby_join.getLocation(this.getWorld());
				if(player!=null) player.teleport(teleport_target);
			}
		}
		invited_players.remove(player_uuid);
		checkReady();
	}
	public void leave(UUID player_uuid){
		Player player = Bukkit.getPlayer(player_uuid);
		Dungeon dungeon = Dungeon.get(this);
		Dungeon.playerMap.remove(player_uuid.toString());
		if(player!=null){
			player.setGameMode(GameMode.SURVIVAL);
			if(dungeon.lobby_leave!=null){
				Location teleport_target = dungeon.getLeavePoint();
				if(teleport_target==null){
					teleport_target = Bukkit.getWorlds().get(0).getSpawnLocation();
					Bukkit.getLogger().info("[AdventureDungeons] Dungeon "+dungeon.getDungeonId()+" returned an invalid leave point!");
				}
				player.teleport(teleport_target);
			}
		}
		if(this.player_uuids.contains(player_uuid.toString())){
			this.player_uuids.remove(player_uuid.toString());
			SwissSMPler swisssmpler = SwissSMPler.get(player_uuid);
			if(swisssmpler!=null) swisssmpler.sendMessage("["+ChatColor.RED+dungeon.name+ChatColor.RESET+"]"+ChatColor.YELLOW+" verlassen.");
			if(this.player_uuids.size()>0){
				for(String otherPlayer : this.player_uuids){
					SwissSMPler othersmpler = SwissSMPler.get(UUID.fromString(otherPlayer));
					if(othersmpler!=null) othersmpler.sendMessage(ChatColor.GRAY+player.getDisplayName()+" hat den Dungeon verlassen.");
				}
			}
			else{
				Bukkit.getScheduler().runTaskLater(AdventureDungeons.plugin, new Runnable(){
					public void run(){
						delete(true);
					}
				}, 20L);
				return;
			}
		}
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
	public boolean setRespawnIndex(int respawnIndex){
		int oldIndex = this.respawnIndex;
		int newIndex = Math.min(this.getDungeon().respawnLocations.size()-1, Math.max(this.respawnIndex, respawnIndex));
		RandomizedLocation randomizedLocation = this.getDungeon().respawnLocations.get(newIndex);
		if(randomizedLocation==null){
			Bukkit.getLogger().info("[AdventureDungeons] Konnte Respawn-Index in Dungeon-Instanz "+this.instance_id+" nicht auf "+respawnIndex+" setzen, da es nicht so viele Respawn-Punkte gibt.");
			return false;
		}
		else{
			this.respawnIndex = newIndex;
			if(newIndex<respawnIndex){
				Bukkit.getLogger().info("[AdventureDungeons] Konnte Respawn-Index in Dungeon-Instanz "+this.instance_id+" nicht auf "+respawnIndex+" setzen, neuer Index ist "+newIndex+".");
			}
		}
		return newIndex==respawnIndex && newIndex!=oldIndex;
	}
	public Entity spawnEntity(Location location, EntityType entityType, int camp_id){
		Entity result = this.getWorld().spawnEntity(location, entityType);
		this.entityMap.put(result, camp_id);
		return result;
	}
	public void removeEntity(Entity entity){
		this.entityMap.remove(entity);
	}
	
	public boolean isRunning(){
		return this.running;
	}
/*
	public LootInventory createLootInventory(Player player, String action, boolean global, Block block, Inventory inventory){
		LootInventory result = new LootInventory(this, player, action, global, block, inventory);
		this.inventoryMap.put(inventory, result);
		if(!inventories.containsKey(block)){
			inventories.put(block, new ArrayList<LootInventory>());
		}
		inventories.get(block).add(result);
		return result;
	}
	
	public LootInventory getLootInventory(Player player, String action, Block block){
		ArrayList<LootInventory> inventoriesAtLocation = this.inventories.get(block);
		if(inventoriesAtLocation==null)
			return null;
		for(LootInventory lootInventory : inventoriesAtLocation){
			if((lootInventory.getPlayer().getUniqueId()==player.getUniqueId() || lootInventory.isGlobal()) && lootInventory.getAction().equals(action))
				return lootInventory;
		}
		return null;
	}
	
	public LootInventory getLootInventory(Inventory inventory){
		if(inventory==null) return null;
		return this.inventoryMap.get(inventory);
	}
	
	void closeLootInventory(Block block, LootInventory inventory){
		this.inventories.get(block).remove(inventory);
		this.inventoryMap.remove(inventory);
		if(this.inventories.get(block).size()==0){
			this.inventories.remove(block);
		}
	}
	*/
	
	//getters
	public File getWorldguardDirectory(){
		return new File(WorldGuardPlugin.inst().getDataFolder(), "worlds/"+getWorldName());
	}
	public File getWorldDirectory(){
		return new File(Bukkit.getWorldContainer(), this.getWorldName());
	}
	public String getWorldName(){
		return "dungeon_instance_"+this.instance_id;
	}
	public World getWorld(){
		return this.world;
	}
	public Dungeon getDungeon(){
		return Dungeon.get(this.dungeon_id);
	}
	public int getDungeonId(){
		return this.dungeon_id;
	}
	public Difficulty getDifficulty(){
		return this.difficulty;
	}
	public Location getRespawnLocation(){
		RandomizedLocation randomizedRespawn = this.getDungeon().respawnLocations.get(this.respawnIndex);
		return randomizedRespawn.getLocation(this.getWorld());
	}
	
	public List<String> getPlayers(){
		return this.player_uuids;
	}
	
	public int getCampId(Entity entity){
		if(entity==null) return -2;
		else if(!this.entityMap.containsKey(entity)) return -1;
		return this.entityMap.get(entity);
	}
	
	public void delete(boolean graceful){
		Bukkit.getPluginManager().callEvent(new DungeonEndEvent(this));
		Dungeon dungeon = Dungeon.get(this.dungeon_id);
		World world = this.getWorld();
		Location location = dungeon.getLeavePoint();
		if(location==null){
			Bukkit.getLogger().info("LeavePoint is null! Falling back to global Spawnpoint");
			location = Bukkit.getWorlds().get(0).getSpawnLocation();
		}
		for(Player player : world.getPlayers()){
			player.teleport(location);
		}
		try{
			if(graceful) WorldUtil.deleteWorld(this.world, location, true);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		Dungeon.instances.remove(this.instance_id);
		this.eventListener.unregister();
	}
	public int getInstanceId() {
		return this.instance_id;
	}

	public void announce(String message) {
		for(String player_uuid_string : this.getPlayers()){
			Player player = Bukkit.getPlayer(UUID.fromString(player_uuid_string));
			if(player!=null){
				player.sendMessage(message);
			}
		}
	}
}
