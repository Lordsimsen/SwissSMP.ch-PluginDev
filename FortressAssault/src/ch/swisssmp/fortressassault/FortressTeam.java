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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.bukkit.util.Vector;

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
		this.team.setAllowFriendlyFire(true);
		this.team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM);
	}
	protected void join(Player player){
		this.team.addEntry(player.getName());
		Game.teamMap.put(player.getUniqueId(), this);
		this.player_uuids.add(player.getUniqueId());
		player.setDisplayName(this.color+player.getName()+ChatColor.RESET);
		Main.sendActionBar(player, "Du bist dem Team "+color+name+ChatColor.RESET+" beigetreten.");
	}
	protected void leave(UUID player_uuid){
		Game.teamMap.remove(player_uuid);
		this.player_uuids.remove(player_uuid);
		Player player = Bukkit.getPlayer(player_uuid);
		if(player==null) return;
		this.team.removeEntry(player.getName());
		player.setDisplayName(player.getName());;
		Main.sendActionBar(player, "Du hast das Team "+color+name+ChatColor.RESET+" verlassen.");
	}
	protected void toggleReady(){
		this.setReady(!this.ready);
	}
	protected void setReady(boolean ready){
		if(this.ready==ready) return;
		this.ready = ready;
		
		boolean allReady = this.ready;
		
		if(this.ready){
			for(FortressTeam team : teams.values()){
				if(!team.ready) allReady = false;
			}
		}
		if(allReady) Main.game.setFightphase();
		else{
			for(UUID player_uuid : player_uuids){
				Player player = Bukkit.getPlayer(player_uuid);
				if(player==null) continue;
				if(this.ready){
					player.setGameMode(GameMode.ADVENTURE);
				}
				else{
					player.setGameMode(GameMode.SURVIVAL);
				}
				Main.game.sendGameState(player);
			}
		}
	}
	protected boolean isReady(){
		return this.ready;
	}
	protected void setLost(Player responsible){
		if(this.lost) return;
		this.lost = true;
		if(responsible!=null){
			Main.game.addScore(responsible, Main.config.getInt("scores.crystal_explode"), "Kristall gesprengt");
		}
		this.playLossAnimation();
		for(UUID player_uuid : this.player_uuids){
			Player player = Bukkit.getPlayer(player_uuid);
			if(player==null) continue;
			player.setGameMode(GameMode.SPECTATOR);
			Main.sendTitle(player, "VERLOREN!", Main.game.getMvpInfo(), 1, 5, 1);
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
			Main.game.setFinished(aliveTeams.get(0));
		}
		else Main.game.setFinished(null);
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
			Main.game.addScore(responsible, Main.config.getInt("scores.crystal_fuse"), "Feindlichen Kristall angegriffen");
			if(this.fusedCountdownTask!=null){
				return;
			}
			this.countdown(responsible, Main.config.getInt("countdown"));
			for(UUID player_uuid : this.player_uuids){
				Player player = Bukkit.getPlayer(player_uuid);
				if(player==null) continue;
				Main.sendActionBar(player, ChatColor.RED+"Rettet euren Kristall!");
			}
		}
		else{
			this.crystal.setType(Material.DIAMOND_BLOCK);
			Main.game.addScore(responsible, Main.config.getInt("scores.crystal_defuse"), "Kristall gerettet");
			if(this.fusedCountdownTask!=null){
				this.fusedCountdownTask.cancel();
				this.fusedCountdownTask = null;
			}
			for(UUID player_uuid : this.player_uuids){
				Player player = Bukkit.getPlayer(player_uuid);
				if(player==null) continue;
				Main.sendActionBar(player, ChatColor.GREEN+"Der Kristall wurde gesichert!");
			}
		}
	}
	private void countdown(Player responsible, int remaining){
		if(!this.fused) return;
		switch(remaining){
		case 5:
			crystal.setType(Material.REDSTONE_BLOCK);
			break;
		case 4:
			crystal.setType(Material.IRON_BLOCK);
			break;
		case 3:
			crystal.setType(Material.GOLD_BLOCK);
			break;
		case 2:
			crystal.setType(Material.EMERALD_BLOCK);
			break;
		case 1:
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
			this.fusedCountdownTask = Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable(){
				public void run(){
					team.countdown(responsible, remaining-1);
					team.fusedCountdownTask = null;
				}
			}, 20L);
		}
	}
	protected void reset(){
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
}
