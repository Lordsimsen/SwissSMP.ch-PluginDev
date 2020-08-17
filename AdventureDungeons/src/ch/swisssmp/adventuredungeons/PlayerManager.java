package ch.swisssmp.adventuredungeons;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.SwissSMPler;
import net.md_5.bungee.api.ChatColor;

public class PlayerManager {
	private final DungeonInstance instance;
	private final List<String> player_uuids = new ArrayList<String>();
	private final List<String> ready_uuids = new ArrayList<String>();
	private final ArrayList<UUID> invited_players = new ArrayList<UUID>();

	CountdownTask countdownTask = null;
	
	public PlayerManager(DungeonInstance instance){
		this.instance = instance;
	}
	
	public void join(Player player){
		UUID player_uuid = player.getUniqueId();
		SwissSMPler swisssmpler = SwissSMPler.get(player);
		Dungeon dungeon = Dungeon.get(this.instance);
		if(this.player_uuids.size()>=dungeon.getMaxPlayers()){
			swisssmpler.sendActionBar(ChatColor.RED+"Dieser Dungeon ist auf "+dungeon.getMaxPlayers()+" Spieler beschränkt.");
			return;
		}
		if(!this.player_uuids.contains(player_uuid.toString())){
			swisssmpler.sendMessage("["+ChatColor.RED+dungeon.getName()+ChatColor.RESET+"]"+ChatColor.YELLOW+" beigetreten.");
			swisssmpler.sendMessage("["+ChatColor.RED+dungeon.getName()+ChatColor.RESET+"]"+ChatColor.YELLOW+" Verwende jederzeit §o§a/leave§r§E, um die Instanz wieder zu verlassen.");
			switch(this.instance.getDifficulty()){
			case EASY:{
				swisssmpler.sendMessage("["+ChatColor.RED+dungeon.getName()+ChatColor.RESET+"]"+ChatColor.YELLOW+" Schwierigkeit: "+ChatColor.GREEN+"Einfach "+ChatColor.YELLOW+"(Behalte XP und Items bei Tod)");
				break;
			}
			case NORMAL:{
				swisssmpler.sendMessage("["+ChatColor.RED+dungeon.getName()+ChatColor.RESET+"]"+ChatColor.YELLOW+" Schwierigkeit: "+ChatColor.RED+"Normal "+ChatColor.YELLOW+"(Verliere XP und behalte Items bei Tod)");
				break;
			}
			case HARD:{
				swisssmpler.sendMessage("["+ChatColor.RED+dungeon.getName()+ChatColor.RESET+"]"+ChatColor.YELLOW+" Schwierigkeit: "+ChatColor.DARK_PURPLE+"Hart "+ChatColor.YELLOW+"(Verliere XP und Items bei Tod)");
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
			if(player!=null){
				Location teleport_target;
				if(dungeon.getLobbyJoin()!=null){
					teleport_target = dungeon.getLobbyJoin().getLocation(this.instance.getWorld());
				}
				else{
					teleport_target = this.instance.getWorld().getSpawnLocation();
				}
				if(player!=null) player.teleport(teleport_target);
			}
		}
		invited_players.remove(player_uuid);
		checkReady();
	}
	
	public void leave(UUID player_uuid){
		String player_uuid_string = player_uuid.toString();
		Player player = Bukkit.getPlayer(player_uuid);
		Dungeon dungeon = Dungeon.get(this.instance);
		if(player!=null){
			player.setGameMode(GameMode.SURVIVAL);
			Location teleport_target = dungeon.getLobbyLeave().getLocation(Bukkit.getWorlds().get(0));
			player.teleport(teleport_target);
		}
		if(this.player_uuids.contains(player_uuid_string)){
			this.player_uuids.remove(player_uuid_string);
			SwissSMPler swisssmpler = SwissSMPler.get(player_uuid);
			if(swisssmpler!=null) swisssmpler.sendMessage("["+ChatColor.RED+dungeon.getName()+ChatColor.RESET+"]"+ChatColor.YELLOW+" verlassen.");
			if(this.player_uuids.size()>0){
				for(String otherPlayer : this.player_uuids){
					SwissSMPler othersmpler = SwissSMPler.get(UUID.fromString(otherPlayer));
					if(othersmpler!=null) othersmpler.sendMessage(ChatColor.GRAY+player.getDisplayName()+" hat den Dungeon verlassen.");
				}
			}
			else{
				Bukkit.getScheduler().runTaskLater(AdventureDungeonsPlugin.getInstance(), new Runnable(){
					public void run(){
						instance.delete(true);
					}
				}, 20L);
				return;
			}
		}
		this.ready_uuids.remove(player_uuid_string);
		this.invited_players.remove(player_uuid);
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
	
	public boolean arePlayersReady(){
		if(this.instance.isRunning()) return false;
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
		if(arePlayersReady()){
			if(this.countdownTask==null){
				this.countdownTask = CountdownTask.runCountdown(this.instance);
			}
			return true;
		}
		else if(this.countdownTask!=null){
			this.countdownTask.cancel();
			this.countdownTask = null;
		}
		return false;
	}
	
	public void addInvitedPlayer(UUID player_uuid){
		if(!this.invited_players.contains(player_uuid)){
			this.invited_players.add(player_uuid);
		}
	}
	
	public boolean isInvitedPlayer(UUID player_uuid){
		return this.invited_players.contains(player_uuid);
	}
	
	public void removeInvitedPlayer(UUID player_uuid){
		this.invited_players.remove(player_uuid);
	}

	public void announce(String message) {
		for(String player_uuid_string : this.getPlayers()){
			Player player = Bukkit.getPlayer(UUID.fromString(player_uuid_string));
			if(player!=null){
				player.sendMessage(message);
			}
		}
	}
	
	public void sendTitle(String title){
		this.sendTitle(title, "");
	}
	
	public void sendTitle(String title, String subtitle){
		for(String player_uuid : player_uuids){
			SwissSMPler swisssmpler = SwissSMPler.get(UUID.fromString(player_uuid));
			if(swisssmpler!=null)swisssmpler.sendTitle(title, subtitle);
		}
	}
	
	/**
	 * @return A List of all Player UUIDs as Strings
	 */
	public List<String> getPlayers(){
		return new ArrayList<String>(this.player_uuids);
	}
}
