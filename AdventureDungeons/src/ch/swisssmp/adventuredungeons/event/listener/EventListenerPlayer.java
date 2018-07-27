package ch.swisssmp.adventuredungeons.event.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Event.Result;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.player.AdventurePlayer;

public class EventListenerPlayer extends EventListenerBasic{
	public EventListenerPlayer(EventListenerMaster master) {
		super(master);
	}
	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode()!=GameMode.ADVENTURE) return;
		if(!this.getInstance().getPlayers().contains(player.getUniqueId().toString())) return;
		Bukkit.getScheduler().runTaskLater(AdventureDungeons.plugin, new Runnable(){
			public void run(){
				AdventurePlayer.updateMusic(player);
			}
		}, 60L);
	}
	@EventHandler
	private void onPlayerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		if(player.getGameMode()!=GameMode.ADVENTURE) return;
		if(!this.getInstance().getPlayers().contains(player.getUniqueId().toString())) return;
		switch(this.getInstance().getDifficulty()){
		case EASY:
			event.setKeepInventory(true);
			event.setKeepLevel(true);
			break;
		case NORMAL:
			event.setKeepInventory(true);
			event.setKeepLevel(false);
			break;
		case HARD:
			event.setKeepInventory(false);
			event.setKeepLevel(false);
			break;
		default:
			break;
		}
	}
	@EventHandler(ignoreCancelled=true)
	private void onPlayerChangedWorld(PlayerChangedWorldEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode()!=GameMode.ADVENTURE) return;
		if(!this.getInstance().getPlayers().contains(player.getUniqueId().toString())) return;
		if(this.getInstance().getWorld()!=player.getWorld()){
			this.getInstance().leave(player.getUniqueId());
		}
	}
	@EventHandler(ignoreCancelled=true,priority=EventPriority.LOWEST)
	private void onPlayerRespawn(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode()!=GameMode.ADVENTURE) return;
		if(!this.getInstance().getPlayers().contains(player.getUniqueId().toString())) return;
		Location respawn = this.getInstance().getRespawnLocation();
		if(respawn==null) return;
		event.setRespawnLocation(respawn);
	}
	
	@EventHandler(ignoreCancelled=true)
	private void preventPlayerRocketBoost(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode()!=GameMode.ADVENTURE) return;
		if(!this.getInstance().getPlayers().contains(player.getUniqueId().toString())) return;
		ItemStack itemStack = event.getItem();
		if(itemStack==null) return;
		if(itemStack.getType()==Material.FIREWORK) event.setUseItemInHand(Result.DENY);
	}
	
	@EventHandler(ignoreCancelled=true)
	private void preventPlayerGlide(EntityToggleGlideEvent event){
		Entity entity = event.getEntity();
		if(!(entity instanceof Player)) return;
		Player player = (Player) entity;
		if(player.getGameMode()!=GameMode.ADVENTURE) return;
		if(!this.getInstance().getPlayers().contains(player.getUniqueId().toString())) return;
		event.setCancelled(true);
	}
}
