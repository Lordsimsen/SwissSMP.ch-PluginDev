package ch.swisssmp.adventuredungeons.mmoevent;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.adventuredungeons.mmocamp.MmoCampMob;
import ch.swisssmp.adventuredungeons.mmoworld.MmoDungeon;
import ch.swisssmp.adventuredungeons.mmoworld.MmoDungeonInstance;

public class MmoEventListenerEntity extends MmoEventListener{
	public MmoEventListenerEntity(JavaPlugin plugin) {
		super(plugin);
	}
	@EventHandler
	private void onCampEntityDeath(EntityDeathEvent event){
		LivingEntity entity = event.getEntity();
		Player player = entity.getKiller();
		if(!MmoCampMob.instances.containsKey(entity.getUniqueId())){
			return;
		}
		MmoCampMob campMob = MmoCampMob.instances.get(entity.getUniqueId());
		campMob.spawnpoint.manageEntityDeath(campMob, entity, player);
	}
	@EventHandler
	private void onEntitySpawn(CreatureSpawnEvent event){
		Location location = event.getLocation();
		MmoDungeonInstance dungeonInstance = MmoDungeon.getInstance(location);
		if(dungeonInstance==null) return;
		SpawnReason reason = event.getSpawnReason();
		if(reason == SpawnReason.CHUNK_GEN || reason == SpawnReason.NATURAL){
			event.setCancelled(true);
		}
	}
}
