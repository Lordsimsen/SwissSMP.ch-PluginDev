package ch.swisssmp.towercontrol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class TowerControlTeam {
	private static final HashMap<UUID, TowerControlTeam> teamMap = new HashMap<UUID, TowerControlTeam>();
	
	private final String side;
	private final ChatColor color;
	private final String name;
	private ArrayList<UUID> player_uuids = new ArrayList<UUID>();
	private Team team;
	
	private boolean lost = false;
	
	protected TowerControlTeam(ConfigurationSection dataSection){
		
		this.side = dataSection.getString("side");
		this.name = dataSection.getString("name");
		String colorName = dataSection.getString("color");
		if(colorName==null){
			for(String key : dataSection.getKeys(false)){
				Bukkit.getLogger().info(key+": "+dataSection.get(key));
			}
		}
		this.color = ChatColor.valueOf(dataSection.getString("color"));
	}
	public String getSide(){
		return this.side;
	}
	protected void register(Scoreboard scoreboard){
		if(scoreboard.getTeam(this.name)!=null){
			this.team = scoreboard.getTeam(this.name);
		}
		else{
			this.team = scoreboard.registerNewTeam(this.name);
		}
		this.team.canSeeFriendlyInvisibles();
		this.team.setPrefix(this.color+"");
		this.team.setCanSeeFriendlyInvisibles(true);
		this.team.setAllowFriendlyFire(false);
		this.team.setOption(Option.DEATH_MESSAGE_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
		this.team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
		this.team.setColor(this.color);
		Player player;
		for(UUID player_uuid : this.player_uuids){
			player = Bukkit.getPlayer(player_uuid);
			if(player!=null) this.team.addEntry(player.getName());
		}
	}
	protected void join(Player player){
		TowerControlTeam.teamMap.put(player.getUniqueId(), this);
		this.player_uuids.add(player.getUniqueId());
		if(this.team!=null)this.team.addEntry(player.getName());
		SwissSMPler.get(player).sendActionBar("Du bist dem Team "+color+name+ChatColor.RESET+" beigetreten.");
		TowerControl.updateTabList(player, this);
	}
	protected void leave(UUID player_uuid){
		if(TowerControl.debug){
			Bukkit.getLogger().info("[TowerControl] "+player_uuid+" left the team "+this.name);
		}
		TowerControlTeam.teamMap.remove(player_uuid);
		this.player_uuids.remove(player_uuid);
		Player player = Bukkit.getPlayer(player_uuid);
		if(player==null) return;
		if(this.team!=null) this.team.removeEntry(player.getName());
		player.setDisplayName(player.getName());;
		SwissSMPler.get(player).sendActionBar("Du hast das Team "+color+name+ChatColor.RESET+" verlassen.");
		TowerControl.updateTabList(player, null);
	}
	protected void purgeDisconnected(){
		UUID[] player_uuids = new UUID[this.player_uuids.size()];
		this.player_uuids.toArray(player_uuids);
		for(UUID player_uuid : player_uuids){
			if(Bukkit.getPlayer(player_uuid)==null)this.leave(player_uuid);
		}
	}

	protected void setLost(){
		if(this.lost) return;
		this.lost = true;
		this.playLossAnimation();
		for(UUID player_uuid : this.player_uuids){
			Player player = Bukkit.getPlayer(player_uuid);
			if(player==null) continue;
			SwissSMPler.get(player).sendTitle("VERLOREN!", TowerControl.game.getMvpInfo());
		}
	}

	protected void setWon(){
		this.playVictoryAnimation();
		for(UUID player_uuid : this.player_uuids){
			Player player = Bukkit.getPlayer(player_uuid);
			if(player==null) continue;
			SwissSMPler.get(player).sendTitle("GEWONNEN!", TowerControl.game.getMvpInfo());
		}
	}
	
	protected void sendMessage(String message){
		Player player;
		for(UUID player_uuid : this.player_uuids){
			player = Bukkit.getPlayer(player_uuid);
			if(player==null)continue;
			player.sendMessage(message);
		}
	}
	
	protected void sendActionBar(String message){
		Player player;
		for(UUID player_uuid : this.player_uuids){
			player = Bukkit.getPlayer(player_uuid);
			if(player==null)continue;
			SwissSMPler.get(player).sendActionBar(message);
		}
	}
	
	protected void sendTitle(String title, String subtitle){
		Player player;
		for(UUID player_uuid : this.player_uuids){
			player = Bukkit.getPlayer(player_uuid);
			if(player==null)continue;
			SwissSMPler.get(player).sendTitle(title, subtitle);
		}
	}
	protected void playVictoryAnimation(){
		//TODO add code
	}
	protected void playLossAnimation(){
		//TODO add code
	}
	protected boolean hasLost(){
		return this.lost;
	}
	protected void reset(){
		this.lost = false;
	}
	protected void setOption(Option option, OptionStatus optionStatus) {
		this.team.setOption(option, optionStatus);
	}
	
	protected String getName(){
		return this.name;
	}
	
	protected ChatColor getColor(){
		return this.color;
	}
	
	protected Collection<UUID> getPlayers(){
		return this.player_uuids;
	}
	
	protected int getPlayerCount(){
		return this.player_uuids.size();
	}
	
	protected static TowerControlTeam load(String side){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("towercontrol/get_team.php", new String[]{
			"side="+side	
		});
		if(yamlConfiguration==null || !yamlConfiguration.contains("team")){
			Bukkit.getLogger().info("[TowerControl] Konnte das Team "+side+" nicht laden.");
			return null;
		}
		return new TowerControlTeam(yamlConfiguration.getConfigurationSection("team"));
	}
	
	protected static TowerControlTeam get(Player player){
		if(player==null) return null;
		return get(player.getUniqueId());
	}
	
	protected static TowerControlTeam get(UUID player_uuid){
		return teamMap.get(player_uuid);
	}
	
	protected static Collection<UUID> getAllPlayers(){
		return teamMap.keySet();
	}
}
