package ch.swisssmp.entitysafety;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EventListener implements Listener {

    private final EntitySafetyPlugin plugin;

    protected EventListener(EntitySafetyPlugin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    private void onEntityDeath(EntityDeathEvent event){
        LivingEntity entity = event.getEntity();
        if((!(entity instanceof Tameable) || !((Tameable)entity).isTamed()) && entity.getCustomName()==null){
            return;
        }

        EntityDeathLog log = plugin.getLog();
        log.add(entity);
        log.save();
    }
}
