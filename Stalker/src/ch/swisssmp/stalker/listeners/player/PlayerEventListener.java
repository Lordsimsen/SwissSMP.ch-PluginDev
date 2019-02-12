package ch.swisssmp.stalker.listeners.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.google.gson.JsonObject;

import ch.swisssmp.stalker.LogEntry;
import ch.swisssmp.stalker.Stalker;
import ch.swisssmp.utils.SwissSMPUtils;

public class PlayerEventListener implements Listener {
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerAdvancementAwarded(PlayerAdvancementDoneEvent event){
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("PLAYER_ADVANCEMENT_AWARDED");
		logEntry.setWhere(event.getPlayer().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		extraData.addProperty("advancement", event.getAdvancement().getKey().toString());
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("PLAYER_ARMOR_STAND_MANIPULATE");
		logEntry.setWhere(event.getRightClicked().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		extraData.addProperty("armorstand_item", SwissSMPUtils.encodeItemStack(event.getArmorStandItem()));
		extraData.addProperty("item", SwissSMPUtils.encodeItemStack(event.getPlayerItem()));
		extraData.addProperty("slot", event.getSlot().toString());
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerChangedWorld(PlayerChangedWorldEvent event){
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("PLAYER_CHANGED_WORLD");
		logEntry.setWhere(event.getPlayer().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		extraData.addProperty("from", event.getFrom().getName().toString());
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerCommand(PlayerCommandPreprocessEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("PLAYER_COMMAND");
		logEntry.setWhere(event.getPlayer().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		extraData.addProperty("command", event.getMessage());
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerDropItem(PlayerDropItemEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("PLAYER_DROP_ITEM");
		logEntry.setWhere(event.getPlayer().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		extraData.addProperty("item", SwissSMPUtils.encodeItemStack(event.getItemDrop().getItemStack()));
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerEditBook(PlayerEditBookEvent event){
		if(event.isCancelled()) return;
		//TODO add Listener
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerGameModeChange(PlayerGameModeChangeEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("PLAYER_GAME_MODE_CHANGE");
		logEntry.setWhere(event.getPlayer().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		extraData.addProperty("from", event.getPlayer().getGameMode().toString());
		extraData.addProperty("to", event.getNewGameMode().toString());
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerItemBreakEvent(PlayerItemBreakEvent event){
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("PLAYER_ITEM_BREAK");
		logEntry.setWhere(event.getPlayer().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		extraData.addProperty("item", SwissSMPUtils.encodeItemStack(event.getBrokenItem()));
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerItemConsume(PlayerItemConsumeEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("PLAYER_ITEM_CONSUME");
		logEntry.setWhere(event.getPlayer().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		extraData.addProperty("item", SwissSMPUtils.encodeItemStack(event.getItem()));
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerJoin(PlayerJoinEvent event){
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("PLAYER_JOIN");
		logEntry.setWhere(event.getPlayer().getLocation().getBlock());
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerKick(PlayerKickEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("PLAYER_KICK");
		logEntry.setWhere(event.getPlayer().getLocation().getBlock());
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerLevelChange(PlayerLevelChangeEvent event){
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("PLAYER_LEVEL_CHANGE");
		logEntry.setWhere(event.getPlayer().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		extraData.addProperty("old", event.getOldLevel());
		extraData.addProperty("new", event.getNewLevel());
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerPortal(PlayerPortalEvent event){
		if(event.isCancelled()) return;
		//TODO add Listener
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerQuit(PlayerQuitEvent event){
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("PLAYER_QUIT");
		logEntry.setWhere(event.getPlayer().getLocation().getBlock());
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerRespawn(PlayerRespawnEvent event){
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("PLAYER_RESPAWN");
		logEntry.setWhere(event.getRespawnLocation().getBlock());
		JsonObject extraData = new JsonObject();
		extraData.addProperty("is_bed", event.isBedSpawn());
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerRiptide(PlayerRiptideEvent event){
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("PLAYER_RIPTIDE");
		logEntry.setWhere(event.getPlayer().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		extraData.addProperty("item", SwissSMPUtils.encodeItemStack(event.getItem()));
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerShearEntity(PlayerShearEntityEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("PLAYER_SHEAR_ENTITY");
		logEntry.setWhere(event.getPlayer().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		extraData.addProperty("entity", Stalker.getIdentifier(event.getEntity()));
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerToggleFlight(PlayerToggleFlightEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getPlayer());
		logEntry.setWhat("PLAYER_TOGGLE_FLIGHT");
		logEntry.setWhere(event.getPlayer().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		extraData.addProperty("flying", event.isFlying());
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
}
