package ch.swisssmp.craftmmo.mmoplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import ch.swisssmp.craftmmo.util.MmoResourceManager;

public class MmoPlayerParty {
	public static final HashMap<Integer, MmoPlayerParty> parties = new HashMap<Integer, MmoPlayerParty>();
	public static HashMap<UUID, Integer> playerMap = new HashMap<UUID, Integer>();
	public static Scoreboard emptyScoreboard;
	
	public final int mmo_party_id;
	public String name;
	public String tag;
	public UUID leader;
	public final ArrayList<UUID> members = new ArrayList<UUID>();
	public final HashMap<String, UUID> membersMap = new HashMap<String, UUID>();
	public final Scoreboard scoreboard;
	public final Team team;
	public final Objective objective;
	
	public ArrayList<Player> invitedPlayers = new ArrayList<Player>();
	
	public MmoPlayerParty(ConfigurationSection dataSection){
		this.mmo_party_id = dataSection.getInt("mmo_party_id");
		this.name = dataSection.getString("name");
		this.tag = dataSection.getString("tag");
		this.leader = UUID.fromString(dataSection.getString("leader"));
		
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		this.objective = this.scoreboard.registerNewObjective("health", "health");
		this.objective.setDisplayName(this.name);
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.team = this.scoreboard.registerNewTeam(this.name);
		this.team.setPrefix("["+this.tag+"] ");
		this.team.setDisplayName(this.name);
		this.team.setAllowFriendlyFire(false);
		
		if(dataSection.contains("leader_name")){
			this.membersMap.put(dataSection.getString("leader_name"), leader);
			playerMap.put(leader, this.mmo_party_id);
		}
		if(dataSection.contains("members")){
			ConfigurationSection membersSection = dataSection.getConfigurationSection("members");
			for(String key : membersSection.getKeys(false)){
				UUID memberUUID = UUID.fromString(membersSection.getString(key));
				membersMap.put(key, memberUUID);
				if(!members.contains(memberUUID)){
					members.add(memberUUID);
				}
				playerMap.put(memberUUID, this.mmo_party_id);
				setupPlayer(memberUUID);
			}
		}
		parties.put(this.mmo_party_id, this);
	}
	
	public void setupPlayer(UUID uuid){
		setupPlayer(Bukkit.getPlayer(uuid));
	}
	
	public void setupPlayer(Player player){
		if(player==null){
			return;
		}
		if(!members.contains(player.getUniqueId())){
			return;
		}
		if(!this.team.hasEntry(player.getName())){
			this.team.addEntry(player.getName());
		}
		player.setScoreboard(scoreboard);
	}
	
	public static void clearPlayer(Player player){
		player.setScoreboard(MmoPlayerParty.emptyScoreboard);
	}
	
	private static void createEmptyScoreboard(){
		emptyScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	}
	public void invite(Player invited){
		if(members.contains(invited)){
			MmoPlayer.sendMessage(leader, ChatColor.GRAY+invited.getDisplayName()+" ist bereits in deiner Gruppe.");
			return;
		}
		if(!this.invitedPlayers.contains(invited)){
			this.invitedPlayers.add(invited);
		}
		MmoPlayer.sendMessage(leader, ChatColor.GRAY+"Du hast "+invited.getDisplayName()+ChatColor.GRAY+" in deine Gruppe eingeladen.");
		Player inviter = Bukkit.getPlayer(leader);
		if(inviter!=null){
			invited.sendMessage(ChatColor.YELLOW+inviter.getDisplayName()+ChatColor.YELLOW+" hat dich in seine Gruppe eingeladen.");
		}
		else{
			invited.sendMessage(ChatColor.YELLOW+"Du wurdest in die Gruppe '"+this.name+"' eingeladen.");
		}
	    String json_accept = ("{'text':'[Annehmen]','color':'green','clickEvent':{'action':'run_command','value':'/party join "+String.valueOf(this.mmo_party_id)+"'}}");
	    String json_refuse = ("{'text':'[Ablehnen]','color':'red','clickEvent':{'action':'run_command','value':'/party refuse "+String.valueOf(this.mmo_party_id)+"'}}");
	    String json_invite = ("{'text':'','extra':["+json_accept+","+json_refuse+"]}").replace("'", "\"");
	    MmoPlayer.sendRawMessage(invited, json_invite);
	}
	public void join(Player player){
		if(members.contains(player)){
			player.sendMessage(ChatColor.GRAY+"Du bist bereits in dieser Gruppe.");
		}
		else if(invitedPlayers.contains(player)){
			invitedPlayers.remove(player);
			MmoResourceManager.processYamlResponse(player.getUniqueId(), "party/join.php", new String[]{
				"party_id="+this.mmo_party_id,
				"player_uuid="+player.getUniqueId().toString(),
				"player_name="+player.getName()
			});
		}
	}
	public void leave(UUID player_uuid){
			MmoResourceManager.processYamlResponse(player_uuid, "party/leave.php", new String[]{
				"party_id="+this.mmo_party_id,
				"player_uuid="+player_uuid.toString()
			});
	}
	public void leave(String player_name){
		MmoResourceManager.processYamlResponse(null, "party/leave.php", new String[]{
			"party_id="+this.mmo_party_id,
			"player_name="+player_name
		});
	}
	public void kick(UUID kicked){
		if(members.contains(kicked)){
			leave(kicked);
		}
		else{
			MmoPlayer.sendMessage(leader, ChatColor.GRAY+"Dieser Spieler ist nicht in deiner Gruppe.");
		}
	}
	public void kick(String kicked){
		if(membersMap.containsKey(kicked)){
			leave(kicked);
		}
		else{
			MmoPlayer.sendMessage(leader, ChatColor.GRAY+kicked+" ist nicht in deiner Gruppe.");
		}
	}
	public void setLeader(Player newLeader){
		MmoResourceManager.processYamlResponse(leader, "party/edit.php", new String[]{
				"party_id="+this.mmo_party_id,
				"name="+this.name,
				"tag="+this.tag,
				"leader="+newLeader.getUniqueId().toString()
		});
	}
	public UUID getLeader(){
		return this.leader;
	}
	public void refuse(UUID player_uuid){
		if(invitedPlayers.contains(player_uuid)){
			invitedPlayers.remove(player_uuid);
			MmoPlayer.sendMessage(player_uuid, ChatColor.GRAY+"Einladung abgelehnt.");
		}
	}
	public void setName(String name){
		MmoResourceManager.processYamlResponse(leader, "party/edit.php", new String[]{
				"party_id="+this.mmo_party_id,
				"name="+name.substring(0,Math.min(name.length(), 16-1)),
				"tag="+this.tag,
				"leader="+leader.toString()
		});
	}
	public String getName(){
		return this.name;
	}
	public void setTag(String tag){
		MmoResourceManager.processYamlResponse(leader, "party/edit.php", new String[]{
				"party_id="+this.mmo_party_id,
				"name="+this.name,
				"tag="+tag.substring(0,Math.min(tag.length(), 3)),
				"leader="+leader.toString()	
		});
	}
	public String getTag(){
		return this.tag;
	}
	public UUID getPlayerUUID(String playerName){
		return membersMap.get(playerName);
	}
	public String getPlayerName(UUID player_uuid){
		for(Entry<String, UUID> entry : membersMap.entrySet()){
			if(entry.getValue().equals(player_uuid))return entry.getKey();
		}
		return null;
	}
	public void disband(){
		MmoResourceManager.processYamlResponse(leader, "party/disband.php", new String[]{
			"party_id="+this.mmo_party_id
		});
	}
	
	public static MmoPlayerParty get(int mmo_party_id){
		return parties.get(mmo_party_id);
	}
	
	public static MmoPlayerParty get(UUID player_uuid){
		Integer mmo_party_id = playerMap.get(player_uuid);
		if(mmo_party_id==null)return null;
		else return get(mmo_party_id);
	}
	
	public static void loadParties() throws Exception{
		if(emptyScoreboard==null){
			createEmptyScoreboard();
		}
		YamlConfiguration yamlConfiguration = MmoResourceManager.getYamlResponse("parties.php");
		for(String key : yamlConfiguration.getKeys(false)){
			ConfigurationSection dataSection = yamlConfiguration.getConfigurationSection(key);
			new MmoPlayerParty(dataSection);
		}
	}
}
