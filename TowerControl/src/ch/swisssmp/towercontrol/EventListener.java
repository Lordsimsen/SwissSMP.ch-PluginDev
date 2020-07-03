package ch.swisssmp.towercontrol;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.meta.ItemMeta;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class EventListener implements Listener{

	@EventHandler(ignoreCancelled=true)
	private void onRegionEnter(RegionEnterEvent event){
		ProtectedRegion region = event.getRegion();
		String regionName = region.getId();
		Player player = event.getPlayer();
		if(player.getGameMode()!=GameMode.ADVENTURE) return;
		if(regionName.contains("team")&&regionName.contains("_")){
			//first leave the old team if there was any
			TowerControlTeam oldTeam = TowerControlTeam.get(player);
			if(oldTeam!=null)oldTeam.leave(player.getUniqueId());
			
			String key = regionName.split("_")[1];
			//special section for spectators
			if(key.equals("spectate")){
				player.setFallDistance(0);
				if(TowerControl.getCurrentArena()!=null){
					player.teleport(TowerControl.getCurrentArena().getWorld().getSpawnLocation());
				}
				else{
					player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
				}
				return;
			}
			//do nothing if the player just wants to leave the team
			else if(key.equals("neutral")){
				
				player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
				return;
			}
			else if(key.equals("info")){
				if(TowerControl.game!=null){
					player.sendMessage("[TowerControl] Aktuell läuft keine Partie.");
				}
				else{
					TowerControl.game.sendGameState(player);
				}
				player.sendMessage("- "+TowerControl.getTeamRed().getColor()+TowerControl.getTeamRed().getName()+ChatColor.RESET+": "+TowerControl.getTeamRed().getPlayerCount()+" Mitspieler");
				player.sendMessage("- "+TowerControl.getTeamBlue().getColor()+TowerControl.getTeamBlue().getName()+ChatColor.RESET+": "+TowerControl.getTeamBlue().getPlayerCount()+" Mitspieler");
				return;
			}
			//join the new team
			else if(TowerControl.game==null || TowerControl.game.getGameState()==GameState.PREGAME || TowerControl.game.getGameState()==GameState.FINISHED){
				TowerControlTeam team = (key.equals("red")) ? TowerControl.getTeamRed() : TowerControl.getTeamBlue();
				if(team==null){
					Bukkit.getLogger().info("[TowerControl] Team "+key+" nicht gefunden.");
					return;
				}
				team.join(player);
				return;
			}
		}
	}
	@EventHandler
	private void onPlayerInteractBlock(PlayerInteractEvent event){
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK){
			if(!event.getPlayer().hasPermission("towercontrol.admin")) return;
			Arena arena = Arena.get(event.getPlayer().getWorld());
			if(arena==null)return;
			if(event.getItem()==null) return;
			if(event.getItem().getType()!=Material.DIAMOND_PICKAXE) return;
			ItemMeta itemMeta = event.getItem().getItemMeta();
			if(itemMeta==null || !itemMeta.hasLore()) return;
			String action = itemMeta.getLore().get(0);
			if(action.length()<3) return;
			action = action.substring(2);
			Block block = event.getClickedBlock();
			Location loc = event.getPlayer().getLocation();
//			YamlConfiguration yamlConfiguration;
//			yamlConfiguration = DataSource.getYamlResponse("towercontrol/editor.php", new String[]{
//				"arena="+arena.getArenaId(),
//				"block_x="+block.getX(),
//				"block_y="+block.getY(),
//				"block_z="+block.getZ(),
//				"x="+loc.getX(),
//				"y="+loc.getY(),
//				"z="+loc.getZ(),
//				"yaw="+loc.getYaw(),
//				"pitch="+loc.getPitch(),
//				"action="+URLEncoder.encode(action.toLowerCase().replace(' ', '_'))
//			});
//			if(yamlConfiguration==null){
//				return;
//			}
//			if(yamlConfiguration.contains("message")){
//				event.getPlayer().sendMessage(yamlConfiguration.getString("message"));
//			}
//			if(yamlConfiguration.contains("actionbar")){
//				SwissSMPler.get(event.getPlayer()).sendActionBar(yamlConfiguration.getString("actionbar"));
//			}
//			if(yamlConfiguration.contains("signals")){
//				ConfigurationSection signalsSection = yamlConfiguration.getConfigurationSection("signals");
//				Location location;
//				for(String key : signalsSection.getKeys(false)){
//					location = signalsSection.getLocation(key,block.getWorld());
//					block.getWorld().spawnParticle(Particle.NOTE, location, 1);
//				}
//			}
		}
	}
	@EventHandler
	private void onEntityExplode(EntityExplodeEvent event){
		event.blockList().clear();
	}
	@EventHandler(ignoreCancelled=true)
	private void onPlayerLogin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		player.setGameMode(GameMode.ADVENTURE);
		TowerControlTeam team = TowerControlTeam.get(player);
		ChatColor playerNameColor;
		if(team==null){
			playerNameColor = ChatColor.WHITE;
			player.getInventory().clear();
		}
		else{
			playerNameColor = team.getColor();
		}
		event.setJoinMessage("["+ChatColor.GREEN+"+"+ChatColor.RESET+"] "+playerNameColor+player.getDisplayName()+ChatColor.RESET);
		Bukkit.getScheduler().runTaskLater(TowerControl.plugin, new Runnable(){
			public void run(){
				if(TowerControl.game!=null) TowerControl.game.updatePlayer(player);
				else player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
			}
		}, 2L);
	}
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		TowerControlTeam team = TowerControlTeam.get(player);
		ChatColor playerNameColor = ChatColor.WHITE;
		if(team!=null){
			if(TowerControl.game!=null && TowerControl.game.getGameState()==GameState.FIGHT){
				playerNameColor = team.getColor();
				player.setHealth(0);
			}
			else{
				team.leave(player.getUniqueId());
			}
		}
		event.setQuitMessage("["+ChatColor.RED+"-"+ChatColor.RESET+"] "+playerNameColor+player.getDisplayName()+ChatColor.RESET);
		
	}

	@EventHandler(ignoreCancelled=true)
	private void onItemDrop(PlayerDropItemEvent event){
		if(event.getPlayer().getGameMode()==GameMode.CREATIVE || TowerControl.game.getGameState()!=GameState.FIGHT) return;
		event.setCancelled(true);
	}
	@EventHandler
	private void onPlayerRespawn(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		TowerControlTeam team = TowerControlTeam.get(player);
		if(team!=null && TowerControl.game!=null && TowerControl.game.getGameState()==GameState.FIGHT){
			Location spawnLocation = TowerControl.game.getArena().getSpawn(team.getSide());
			if(spawnLocation!=null){
				event.setRespawnLocation(spawnLocation);
			}
			else{
				event.setRespawnLocation(player.getWorld().getSpawnLocation());
			}
			if(TowerControl.game.getArena().getPotionTowerOwner()==team){
				Bukkit.getScheduler().runTaskLater(TowerControl.plugin, new Runnable(){
					public void run(){
						player.addPotionEffects(TowerControl.game.getArena().getPotionEffects());
					}
				}, 5L);
			}
			if(TowerControl.game.getGameState()==GameState.FIGHT) TowerControl.game.updateInventory(player);
		}
		else{
			event.setRespawnLocation(Bukkit.getWorlds().get(0).getSpawnLocation());
		}
	}
	@EventHandler
	private void onPlayerInteractEntity(PlayerInteractEntityEvent event){
		if(event.getRightClicked().getType()!=EntityType.ITEM_FRAME) return;
		if(event.getPlayer().getGameMode()==GameMode.CREATIVE) return;
		event.setCancelled(true);
	}
	@EventHandler
	private void onPlayerInteractItemFrame(EntityDamageByEntityEvent event){
		if(event.getEntity().getType()!=EntityType.ITEM_FRAME) return;
		if(event.getDamager() instanceof Player && ((Player)event.getDamager()).getGameMode()==GameMode.CREATIVE) return;
		event.setCancelled(true);
	}
}
