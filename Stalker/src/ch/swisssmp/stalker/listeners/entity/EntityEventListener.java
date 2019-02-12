package ch.swisssmp.stalker.listeners.entity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ch.swisssmp.stalker.LogEntry;
import ch.swisssmp.stalker.Stalker;
import ch.swisssmp.utils.SwissSMPUtils;

public class EntityEventListener implements Listener {
	
	private static List<EntityType> importantTypes = new ArrayList<EntityType>();
	
	public EntityEventListener(){
		importantTypes.add(EntityType.PLAYER);
		importantTypes.add(EntityType.LLAMA);
		importantTypes.add(EntityType.PARROT);
		importantTypes.add(EntityType.WOLF);
		importantTypes.add(EntityType.HORSE);
		importantTypes.add(EntityType.DONKEY);
		importantTypes.add(EntityType.MULE);
		importantTypes.add(EntityType.ZOMBIE_HORSE);
		importantTypes.add(EntityType.SKELETON_HORSE);
		importantTypes.add(EntityType.ELDER_GUARDIAN);
		importantTypes.add(EntityType.WITHER);
		importantTypes.add(EntityType.ENDER_DRAGON);
		importantTypes.add(EntityType.IRON_GOLEM);
		importantTypes.add(EntityType.VILLAGER);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onCreatureSpawn(CreatureSpawnEvent event){
		if(event.isCancelled() || !importantTypes.contains(event.getEntityType())) return;
		LogEntry logEntry = new LogEntry(event.getEntity());
		logEntry.setWhat("CREATURE_SPAWN");
		logEntry.setWhere(event.getLocation().getBlock());
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onEntityBreakDoor(EntityBreakDoorEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getEntity());
		logEntry.setWhat("ENTITY_BREAK_DOOR");
		logEntry.setWhere(event.getBlock());
		logEntry.setCurrent(event.getBlock());
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onEntityBreed(EntityBreedEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getEntity());
		logEntry.setWhat("ENTITY_BREED");
		logEntry.setWhere(event.getEntity().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		extraData.addProperty("item", SwissSMPUtils.encodeItemStack(event.getBredWith()));
		extraData.addProperty("father", Stalker.getIdentifier(event.getFather()));
		extraData.addProperty("mother", Stalker.getIdentifier(event.getMother()));
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onEntityChangeBlock(EntityChangeBlockEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getEntity());
		logEntry.setWhat("ENTITY_CHANGE_BLOCK");
		logEntry.setWhere(event.getBlock());
		logEntry.setPrevious(event.getBlock());
		logEntry.setCurrent(event.getTo(), event.getBlockData());
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onEntityCreatePortal(EntityCreatePortalEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getEntity());
		logEntry.setWhat("ENTITY_CREATE_PORTAL");
		Block block = event.getBlocks().size() > 0 ? event.getBlocks().get(0).getBlock() : event.getEntity().getLocation().getBlock();
		logEntry.setWhere(block);
		logEntry.setCurrent(block);
		JsonObject extraData = new JsonObject();
		extraData.addProperty("portal_type", event.getPortalType().toString());
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onEntityDeath(EntityDeathEvent event){
		if(event instanceof PlayerDeathEvent) return;
		if(!importantTypes.contains(event.getEntityType())) return;
		LogEntry logEntry = new LogEntry(event.getEntity());
		logEntry.setWhat("ENTITY_DEATH");
		logEntry.setWhere(event.getEntity().getLocation().getBlock());
		if(event.getEntity().getKiller()!=null){
			JsonObject extraData = new JsonObject();
			extraData.addProperty("killer", Stalker.getIdentifier((Entity) event.getEntity().getKiller()));
			logEntry.setExtraData(extraData);
		}
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onEntityDropItem(EntityDropItemEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getEntity());
		logEntry.setWhat("ENTITY_DROP_ITEM");
		logEntry.setWhere(event.getEntity().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		extraData.addProperty("item", SwissSMPUtils.encodeItemStack(event.getItemDrop().getItemStack()));
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onEntityExplode(EntityExplodeEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getEntity());
		logEntry.setWhat("ENTITY_EXPLODE");
		logEntry.setWhere(event.getEntity().getLocation().getBlock());
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onEntityPickupItem(EntityPickupItemEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getEntity());
		logEntry.setWhat("ENTITY_PICKUP_ITEM");
		logEntry.setWhere(event.getEntity().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		extraData.addProperty("item", SwissSMPUtils.encodeItemStack(event.getItem().getItemStack()));
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
	/* too much spam
	@EventHandler(priority=EventPriority.MONITOR)
	private void onEntityPortalEnter(EntityPortalEnterEvent event){
		LogEntry logEntry = new LogEntry(event.getEntity());
		logEntry.setWhat("ENTITY_PORTAL_ENTER");
		logEntry.setWhere(event.getLocation().getBlock());
		logEntry.setCurrent(event.getLocation().getBlock());
		Stalker.log(logEntry);
	}
	*/
	@EventHandler(priority=EventPriority.MONITOR)
	private void onEntityPortalExit(EntityPortalExitEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getEntity());
		logEntry.setWhat("ENTITY_PORTAL_EXIT");
		logEntry.setWhere(event.getTo().getBlock());
		logEntry.setCurrent(event.getTo().getBlock());
		logEntry.setPrevious(event.getFrom().getBlock());
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onEntitySpawn(EntitySpawnEvent event){
		if(event instanceof CreatureSpawnEvent || event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getEntity());
		logEntry.setWhat("ENTITY_SPAWN");
		logEntry.setWhere(event.getLocation().getBlock());
		Stalker.log(logEntry);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerDeath(PlayerDeathEvent event){
		LogEntry logEntry = new LogEntry(event.getEntity());
		logEntry.setWhat("PLAYER_DEATH");
		logEntry.setWhere(event.getEntity().getLocation().getBlock());
		JsonObject extraData = new JsonObject();
		if(event.getEntity().getKiller()!=null){
			extraData.addProperty("killer", Stalker.getIdentifier((Entity) event.getEntity().getKiller()));
		}
		extraData.addProperty("exp", event.getDroppedExp());
		JsonArray dropsArray = new JsonArray();
		for(ItemStack itemStack : event.getDrops()){
			dropsArray.add(SwissSMPUtils.encodeItemStack(itemStack));
		}
		extraData.add("items", dropsArray);
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
}
