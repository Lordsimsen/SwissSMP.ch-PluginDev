package ch.swisssmp.fortressassault;

import java.util.Set;

import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.RequestMethod;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.webcore.DataSource;

public class EventListener implements Listener{
	private final Game game;
	protected EventListener(Game game){
		this.game = game;
	}
	@EventHandler
	private void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
			return;
		}
		Block block = event.getBlock();
		if (!this.game.isBlockInteractionAllowed(event.getPlayer().getUniqueId(), event.getBlock())) {
			event.setCancelled(true);
			SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.RED + "Du kannst nur in deiner Basis bauen.");
			return;
		}
		if (block.getType() != FortressAssault.crystalMaterial) return;
		event.setCancelled(true);
		Player player = event.getPlayer();
		FortressTeam fortressTeam = FortressAssault.teamMap.get(player.getUniqueId());
		if (fortressTeam != null) {
			if (fortressTeam.leader.equals(player.getUniqueId())) {
				RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));
				ProtectedRegion region = regionManager.getRegion("base_" + fortressTeam.team_id);
				if (region.contains(block.getX(), block.getY(), block.getZ())) {
					fortressTeam.crystal = block;
					event.setCancelled(false);
				} else {
					SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Platziere den Kristall in deiner Basis.");
				}
			}
		}
	}
	@EventHandler(ignoreCancelled=true)
	private void onPlayerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		FortressTeam team = FortressAssault.teamMap.get(player.getUniqueId());
		if(this.game.getGameState()==GameState.BUILD && team!=null){
			Vector vector = team.getSpawn();
			player.setBedSpawnLocation(new Location(this.game.getInstance(), vector.getX(), vector.getY(), vector.getZ()), true);
			return;
		}
		if(this.game.getGameState()!=GameState.FIGHT){
			event.getEntity().setBedSpawnLocation(FortressAssault.getPoint(FortressAssault.getLobby(), "lobby"), true);
			return;
		}
		EntityType killerType = player.getLastDamageCause().getEntityType();
		if(killerType == EntityType.PLAYER){
			Player killer = event.getEntity().getKiller();
			this.game.addScore(killer, FortressAssault.config.getInt("scores.player_kill"), player.getName()+" getötet");
		}
		event.setKeepInventory(true);
	}
	@EventHandler
	private void onPlayerRespawn(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		FortressTeam team = FortressAssault.teamMap.get(player.getUniqueId());
		if(this.game.getGameState()==GameState.FIGHT && team!=null){
			Vector vector = team.getSpawn();
			event.setRespawnLocation(new Location(this.game.getInstance(), vector.getX(),vector.getY(),vector.getZ()));
			player.setGameMode(GameMode.SPECTATOR);
			Bukkit.getScheduler().runTaskLater(FortressAssault.plugin, new Runnable(){
				public void run(){
					if(game.getGameState()!=GameState.FIGHT) return;
					player.setGameMode(GameMode.ADVENTURE);
					player.teleport(new Location(game.getInstance(), vector.getX(),vector.getY(),vector.getZ()));
				}
			}, FortressAssault.config.getInt("death_timeout")*20L);
		}
		else if(this.game.getGameState()==GameState.BUILD && team!=null){
			Vector vector = team.getSpawn();
			event.setRespawnLocation(new Location(this.game.getInstance(), vector.getX(),vector.getY(),vector.getZ()));
		}
		else{
			event.setRespawnLocation(FortressAssault.getLobby().getSpawnLocation());
		}
		if(this.game.getGameState()==GameState.FIGHT) this.game.updateInventory(player);
	}
	@EventHandler(ignoreCancelled=true)
	private void onBlockBreak(BlockBreakEvent event){
		if(event.getPlayer().getGameMode()==GameMode.CREATIVE){
			return;
		}
		if(event.getBlock().getType()!=Material.STONE_BRICKS) event.setCancelled(true);
	}
	@EventHandler(ignoreCancelled=true)
	private void onPlayerLogin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		FortressTeam team = FortressAssault.teamMap.get(player.getUniqueId());
		ChatColor playerNameColor;
		if(team==null){
			playerNameColor = ChatColor.WHITE;
			player.getInventory().clear();
			player.sendMessage(ChatColor.DARK_AQUA+"Willkommen in Fortress Assault!");
		}
		else{
			playerNameColor = team.color;
		}
		event.setJoinMessage("["+ChatColor.GREEN+"+"+ChatColor.RESET+"] "+playerNameColor+player.getDisplayName()+ChatColor.RESET);
		Bukkit.getScheduler().runTaskLater(FortressAssault.plugin, new Runnable(){
			public void run(){
				game.updatePlayer(player);
			}
		}, 5L);
	}
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		FortressTeam team = FortressAssault.teamMap.get(player.getUniqueId());
		ChatColor playerNameColor = ChatColor.WHITE;
		if(team!=null){
			if(this.game.getGameState()==GameState.FIGHT){
				player.setHealth(0);
			}
			if(team.leader!=null && team.leader.equals(player.getUniqueId())){
				team.chooseNewLeader();
			}
			if(this.game.getGameState()==GameState.BUILD||this.game.getGameState()==GameState.FIGHT){
				player.getInventory().remove(FortressAssault.crystalMaterial);
				playerNameColor = team.color;
			}
			else{
				team.leave(player.getUniqueId());
			}
		}
		event.setQuitMessage("["+ChatColor.RED+"-"+ChatColor.RESET+"] "+playerNameColor+player.getDisplayName()+ChatColor.RESET);
		
	}
	@EventHandler
	private void onFoodEvent(FoodLevelChangeEvent event){
		if(this.game.getGameState()==GameState.BUILD){
			event.setCancelled(true);
		}
	}
	@EventHandler
	private void onEntityExplode(EntityExplodeEvent event){
		event.blockList().clear();
	}
	@EventHandler
	private void onPlayerChat(AsyncPlayerChatEvent event){
		FortressTeam team = FortressAssault.teamMap.get(event.getPlayer().getUniqueId());
		if(team!=null && (FortressAssault.game.getGameState()==GameState.BUILD||FortressAssault.game.getGameState()==GameState.FIGHT)){
			event.setMessage("(Team)"+event.getMessage());
			Set<Player> recipientsSet = event.getRecipients();
			Player[] recipients = new Player[recipientsSet.size()];
			event.getRecipients().toArray(recipients);
			for(Player player : recipients)
			{
				if(FortressAssault.teamMap.get(player.getUniqueId())!=team){
					recipientsSet.remove(player);
				}
			}
		}
	}
	@EventHandler(ignoreCancelled=true)
	private void onItemDrop(PlayerDropItemEvent event){
		if(event.getPlayer().getGameMode()==GameMode.CREATIVE || FortressAssault.game.getGameState()!=GameState.FIGHT) return;
		event.setCancelled(true);
	}
	@EventHandler(ignoreCancelled=true)
	private void onRegionEnter(RegionEnterEvent event){
		ProtectedRegion region = event.getRegion();
		String regionName = region.getId();
		Player player = event.getPlayer();
		if(player.getGameMode()==GameMode.CREATIVE) return;
		if(regionName.contains("team")&&regionName.contains("_")){
			//first leave the old team if there was any
			if(this.game.getGameState()==GameState.PREGAME){
				FortressTeam oldTeam = FortressAssault.teamMap.get(player.getUniqueId());
				if(oldTeam!=null)oldTeam.leave(player.getUniqueId());
			}
			
			String team_id = regionName.split("_")[1];
			//special section for spectators
			if(team_id.equals("spectate")){
				player.setFallDistance(0);
				player.teleport(FortressAssault.getPoint(this.game.getInstance(), "spectate"));
				return;
			}
			//do nothing if the player just wants to leave the team
			else if(team_id.equals("neutral")){
				
				player.teleport(FortressAssault.getPoint(FortressAssault.getLobby(), "lobby"));
				return;
			}
			else if(team_id.equals("info")){
				this.game.sendGameState(player);
				for(FortressTeam team : FortressTeam.teams.values()){
					player.sendMessage("- "+team.color+team.name+ChatColor.RESET+": "+team.player_uuids.size()+" Mitspieler");
				}
				return;
			}
			//join the new team
			else if(this.game.getGameState()==GameState.PREGAME || this.game.getGameState()==GameState.FINISHED){
				FortressTeam fortressTeam = FortressTeam.get(Integer.parseInt(team_id));
				if(fortressTeam==null){
					YamlConfiguration yamlConfiguration = DataSource.getResponse(FortressAssault.getInstance(), "fortress_assault/team.php", new String[]{
							"team="+team_id
					}, RequestMethod.POST_SYNC).getYamlResponse();
					fortressTeam = new FortressTeam(yamlConfiguration.getConfigurationSection(team_id));
				}
				fortressTeam.registerTeam(this.game);
				fortressTeam.join(player);
				return;
			}
		}
		else if(regionName.contains("class")&&regionName.contains("_")){
			int class_id = Integer.valueOf(regionName.split("_")[1]);
			PlayerClass playerClass = PlayerClass.get(class_id);
			if(playerClass==null) {
				FortressAssault.players.remove(player.getUniqueId());
			}
			else {
				FortressAssault.players.put(player.getUniqueId(), playerClass.class_id);
				SwissSMPler.get(player).sendActionBar("Du bist nun "+ChatColor.AQUA+playerClass.name+ChatColor.RESET+"!");
			}
			PlayerClass.setItems(player, playerClass, this.game.getGameState());
		}
		else if(regionName.contains("checkpoint")&&regionName.contains("_")){
			if(this.game.getGameState()!=GameState.BUILD) return;
			int team_id = Integer.valueOf(regionName.split("_")[1]);
			FortressTeam team = FortressTeam.get(team_id);
			if(team.isCheckpointPassed()) return;
			if(!team.leader.equals(event.getPlayer().getUniqueId())){
				SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.RED+"Der Team-Leader muss den Checkpoint passieren.");
				return;
			}
			if(team.setCheckpointPassed(true)){
				SwissSMPler.get(event.getPlayer()).sendMessage(ChatColor.GREEN+"Checkpoint passiert, aktiviere nun den Kristall.");
			}
			else{
				SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.RED+"Platziere den Kristall");
			}
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	private void onPlayerInteractBlock(PlayerInteractEvent event){
		if(event.getAction()!=Action.LEFT_CLICK_BLOCK && event.getAction()!=Action.RIGHT_CLICK_BLOCK){
			return;
		}
		if(event.getPlayer().getGameMode()==GameMode.CREATIVE) return;
		Block block = event.getClickedBlock();
		switch(this.game.getGameState()){
		case BUILD:
		{
			if(event.getHand()!=EquipmentSlot.HAND){
				return;
			}
			if(!this.game.isBlockInteractionAllowed(event.getPlayer().getUniqueId(), event.getClickedBlock())){
				event.setCancelled(true);
				return;
			}
			if(block.getType()==FortressAssault.crystalMaterial){
				FortressTeam team = FortressTeam.get(block);
				if(team.leader.equals(event.getPlayer().getUniqueId())){
					if(event.getAction()==Action.LEFT_CLICK_BLOCK){
						block.setType(Material.AIR);
						block.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation(), 1, FortressAssault.crystalMaterial);
						event.getPlayer().getInventory().addItem(new ItemStack(FortressAssault.crystalMaterial, 1));
						team.setReady(false);
						team.crystal = null;
					}
					else if(event.getAction()==Action.RIGHT_CLICK_BLOCK){
						team.toggleReady();
					}
				}
				else{
					if(event.getAction()==Action.LEFT_CLICK_BLOCK){
						SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.RED+"Nur der Team-Leader kann den Kristall verschieben!");
					}
					else if(event.getAction()==Action.RIGHT_CLICK_BLOCK){
						SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.RED+"Nur der Team-Leader kann die Bauphase beenden.");
					}
				}
				event.setCancelled(true);
			}
			else if(event.getAction()==Action.LEFT_CLICK_BLOCK){
				if(block.getType()!=Material.STONE_BRICKS && block.getType().isSolid()){
					//mining is disabled by default
					//Main.sendActionBar(event.getPlayer(), ChatColor.RED+"Du kannst nur Steinziegel abbauen.");
					return;
				}
				else{
					block.breakNaturally();
					event.setCancelled(true);
				}
			}
			break;
		}
		case FIGHT:
		{
			FortressTeam owningTeam = FortressTeam.get(block);
			if(owningTeam!=null){
				if(owningTeam.player_uuids.contains(event.getPlayer().getUniqueId())){
					//defuse
					owningTeam.setFused(false, event.getPlayer());
				}
				else{
					//fuse
					owningTeam.setFused(true, event.getPlayer());
				}
			}
			break;
		}
		default:
			return;
		}
	}
}
