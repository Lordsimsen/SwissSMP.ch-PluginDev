package ch.swisssmp.fortressassault;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.bukkit.util.Vector;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.SwissSMPler;

public class FortressTeam {
	protected static HashMap<Integer, FortressTeam> teams = new HashMap<Integer, FortressTeam>();

	protected final int team_id;
	protected final ChatColor color;
	protected final String name;
	protected UUID leader;
	protected ArrayList<UUID> player_uuids = new ArrayList<UUID>();
	protected Block crystal = null;
	private Team team;
	protected final Vector spawn;
	
	private boolean checkpointPassed = false;
	private boolean ready = false;
	private boolean fused = false;
	private boolean lost = false;
	
	private BukkitTask fusedCountdownTask = null;
	
	protected FortressTeam(ConfigurationSection dataSection){
		
		this.team_id = dataSection.getInt("id");
		this.name = dataSection.getString("name");
		String colorName = dataSection.getString("color");
		if(colorName==null){
			for(String key : dataSection.getKeys(false)){
				Bukkit.getLogger().info(key+": "+dataSection.get(key));
			}
		}
		this.color = ChatColor.valueOf(dataSection.getString("color"));
		this.spawn = new Vector(dataSection.getInt("x"), dataSection.getInt("y"), dataSection.getInt("z"));
		teams.put(this.team_id, this);
	}
	protected void registerTeam(Game game){
		if(game.scoreboard.getTeam(this.name)!=null){
			this.team = game.scoreboard.getTeam(this.name);
		}
		else{
			this.team = game.scoreboard.registerNewTeam(this.name);
		}
		this.team.canSeeFriendlyInvisibles();
		this.team.setPrefix(this.color+"");
		this.team.setCanSeeFriendlyInvisibles(true);
		this.team.setAllowFriendlyFire(false);
		this.team.setOption(Option.DEATH_MESSAGE_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
		this.team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
		this.team.setColor(this.color);
	}
	protected void join(Player player){
		this.team.addEntry(player.getName());
		FortressAssault.teamMap.put(player.getUniqueId(), this);
		this.player_uuids.add(player.getUniqueId());
		SwissSMPler.get(player).sendActionBar("Du bist dem Team "+color+name+ChatColor.RESET+" beigetreten.");
		FortressAssault.updateTabList(player, this);
	}
	protected void leave(UUID player_uuid){
		if(FortressAssault.debug){
			Bukkit.getLogger().info("[FortressAssault] "+player_uuid+" left the team "+this.name);
		}
		FortressAssault.teamMap.remove(player_uuid);
		this.player_uuids.remove(player_uuid);
		if(this.leader==player_uuid){
			this.chooseNewLeader();
		}
		Player player = Bukkit.getPlayer(player_uuid);
		if(player==null) return;
		this.team.removeEntry(player.getName());
		player.setDisplayName(player.getName());;
		SwissSMPler.get(player).sendActionBar("Du hast das Team "+color+name+ChatColor.RESET+" verlassen.");
		FortressAssault.updateTabList(player, null);
	}
	protected void chooseNewLeader(){
		this.setCheckpointPassed(false);
		for(UUID player_uuid : this.player_uuids){
			if(player_uuid==this.leader) continue;
			if(Bukkit.getPlayer(player_uuid)==null)continue;
			this.leader = player_uuid;
			SwissSMPler.get(player_uuid).sendMessage(ChatColor.GREEN+"Du bist nun Team-Leader vom Team "+this.color+this.name);
			if(FortressAssault.game.getGameState()==GameState.BUILD && this.crystal==null){
				Bukkit.getPlayer(player_uuid).getInventory().addItem(new ItemStack(FortressAssault.crystalMaterial,1));
			}
			return;
		}
		this.leader = null;
		if(FortressAssault.game.getGameState()==GameState.BUILD){
			FortressAssault.game.setFinished(null);
		}
	}
	protected void purgeDisconnected(){
		UUID[] player_uuids = new UUID[this.player_uuids.size()];
		this.player_uuids.toArray(player_uuids);
		for(UUID player_uuid : player_uuids){
			if(Bukkit.getPlayer(player_uuid)==null)this.leave(player_uuid);
		}
	}
	protected void toggleReady(){
		this.setReady(!this.ready);
	}
	protected boolean setCheckpointPassed(boolean passed){
		if(this.crystal==null) return false;
		if(this.checkpointPassed==passed) return true;
		this.checkpointPassed = passed;

		for(UUID player_uuid : player_uuids){
			Player player = Bukkit.getPlayer(player_uuid);
			if(player==null) continue;
			if(this.checkpointPassed){
				player.setGameMode(GameMode.ADVENTURE);
				if(player.getUniqueId().equals(this.leader)){
					SwissSMPler.get(player).sendTitle("Bauphase beendet.", ChatColor.GOLD+"Aktiviere den Kristall");
				}
				else{
					SwissSMPler.get(player).sendTitle("Bauphase beendet.", ChatColor.GOLD+"Warte auf andere Teams");
				}
			}
			else{
				player.setGameMode(GameMode.SURVIVAL);
			}
		}
		return true;
	}
	protected boolean isCheckpointPassed(){
		return this.checkpointPassed;
	}
	protected void setReady(boolean ready){
		if(this.ready==ready) return;
		if(ready && !this.checkpointPassed){
			SwissSMPler.get(this.leader).sendActionBar(ChatColor.RED+"Du musst zuerst den Checkpoint passieren.");
			return;
		}
		this.ready = ready;
		
		for(UUID player_uuid : player_uuids){
			Player player = Bukkit.getPlayer(player_uuid);
			if(player==null) continue;
			FortressAssault.game.sendGameState(player);
		}
		
		boolean allReady = this.ready;
		
		if(this.ready){
			for(FortressTeam team : teams.values()){
				if(!team.ready) allReady = false;
			}
		}
		if(allReady) FortressAssault.game.setFightphase();
	}
	protected boolean isReady(){
		return this.ready;
	}
	protected void setLost(Player responsible){
		if(this.lost) return;
		this.lost = true;
		if(responsible!=null){
			FortressAssault.game.addScore(responsible, FortressAssault.config.getInt("scores.crystal_explode"), "Kristall gesprengt");
		}
		this.playLossAnimation();
		for(UUID player_uuid : this.player_uuids){
			Player player = Bukkit.getPlayer(player_uuid);
			if(player==null) continue;
			player.setGameMode(GameMode.SPECTATOR);
			SwissSMPler.get(player).sendTitle("VERLOREN!", FortressAssault.game.getMvpInfo());
		}
		ArrayList<FortressTeam> aliveTeams = new ArrayList<FortressTeam>();
		for(FortressTeam team : teams.values()){
			if(team==this) continue;
			if(team.hasLost()) continue;
			aliveTeams.add(team);
		}
		if(aliveTeams.size()>1){
			return;
		}
		else if(aliveTeams.size()==1){
			FortressAssault.game.setFinished(aliveTeams.get(0));
		}
		else FortressAssault.game.setFinished(null);
	}
	protected void playLossAnimation(){
		if(this.crystal!=null){
			World world = this.crystal.getWorld();
			world.createExplosion(this.crystal.getLocation(), 2f, true);
		}
	}
	protected boolean hasLost(){
		return this.lost;
	}
	protected void setFused(boolean fused, Player responsible){
		if(this.fused==fused) return;
		this.fused = fused;
		if(this.fused){
			FortressAssault.game.addScore(responsible, FortressAssault.config.getInt("scores.crystal_fuse"), "Feindlichen Kristall angegriffen");
			if(this.fusedCountdownTask!=null){
				return;
			}
			this.countdown(responsible, FortressAssault.config.getInt("countdown"));
			for(UUID player_uuid : this.player_uuids){
				Player player = Bukkit.getPlayer(player_uuid);
				if(player==null) continue;
				SwissSMPler.get(player).sendTitle(ChatColor.RED+"Rettet euren Kristall!", FortressAssault.config.getInt("countdown")+" Sekunden Zeit!");
			}
		}
		else{
			this.crystal.setType(Material.DIAMOND_BLOCK);
			FortressAssault.game.addScore(responsible, FortressAssault.config.getInt("scores.crystal_defuse"), "Kristall gerettet");
			if(this.fusedCountdownTask!=null){
				this.fusedCountdownTask.cancel();
				this.fusedCountdownTask = null;
			}
			for(UUID player_uuid : this.player_uuids){
				Player player = Bukkit.getPlayer(player_uuid);
				if(player==null) continue;
				SwissSMPler.get(player).sendActionBar(ChatColor.GREEN+"Der Kristall wurde gesichert!");
			}
		}
	}
	private void countdown(Player responsible, int remaining){
		if(!this.fused) return;
		switch(remaining){
		case 60:
			crystal.setType(Material.REDSTONE_BLOCK);
			break;
		case 48:
			crystal.setType(Material.IRON_BLOCK);
			break;
		case 36:
			crystal.setType(Material.GOLD_BLOCK);
			break;
		case 24:
			crystal.setType(Material.EMERALD_BLOCK);
			break;
		case 12:
			crystal.setType(Material.REDSTONE_LAMP_ON);
			break;
		case 0:
			crystal.setType(Material.TNT);
			break;
		default:
			crystal.setType(Material.REDSTONE_BLOCK);
			break;
		}
		if(remaining==0){
			setLost(responsible);
		}
		else{
			FortressTeam team = this;
			this.fusedCountdownTask = Bukkit.getScheduler().runTaskLater(FortressAssault.plugin, new Runnable(){
				public void run(){
					team.countdown(responsible, remaining-1);
					team.fusedCountdownTask = null;
				}
			}, 20L);
		}
	}
	protected void reset(){
		this.checkpointPassed = false;
		this.ready = false;
		this.fused = false;
		this.lost = false;
		this.crystal = null;
	}
	protected Vector getSpawn(){
		return this.spawn;
	}
	protected static FortressTeam get(int team_id){
		return teams.get(team_id);
	}
	protected static FortressTeam get(Block block){
		if(block==null) return null;
		for(FortressTeam team : teams.values()){
			if(team.crystal==null) continue;
			if(team.crystal.getLocation().equals(block.getLocation())){
				return team;
			}
		}
		return null;
	}
	protected void setOption(Option option, OptionStatus optionStatus) {
		this.team.setOption(option, optionStatus);
	}
}
