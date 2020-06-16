package ch.swisssmp.deathmessages;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import ch.swisssmp.webcore.RequestMethod;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

import java.util.ArrayList;

public class EventListener implements Listener {
    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        ArrayList<String> arguments = new ArrayList<String>();
        arguments.add("player="+ URLEncoder.encode(player.getDisplayName()));
        if(player.getKiller()!=null){
            arguments.add("killer="+URLEncoder.encode(player.getKiller().getDisplayName()));
        }
        arguments.add("message="+URLEncoder.encode(event.getDeathMessage()));
        arguments.add("world="+URLEncoder.encode(player.getWorld().getName()));
        EntityDamageEvent lastDamage = player.getLastDamageCause();
        if(lastDamage!=null){
            arguments.add("cause="+lastDamage.getCause());
            if(lastDamage instanceof EntityDamageByEntityEvent){
                EntityDamageByEntityEvent damageByEntity = (EntityDamageByEntityEvent) lastDamage;
                Entity entity = damageByEntity.getDamager();
                if(entity!=null){
                    if(entity instanceof Projectile){
                        Projectile projectile = (Projectile) entity;
                        arguments.add("arguments[projectile]="+projectile.getShooter());
                        ProjectileSource projectileSource = projectile.getShooter();
                        if(projectileSource instanceof Entity){
                            Entity shooter = (Entity)projectileSource;
                            arguments.add("arguments[entity]="+(shooter.getType()));
                            if(shooter.getCustomName()!=null) arguments.add("killer="+URLEncoder.encode(shooter.getCustomName()));
                        }
                        else if(projectileSource instanceof BlockProjectileSource){
                            BlockProjectileSource blockSource = (BlockProjectileSource) projectileSource;
                            arguments.add("arguments[block]="+blockSource.getBlock().getType());
                        }

                    }
                    else{
                        if(entity instanceof Zombie && ((Zombie)entity).isBaby()){
                            arguments.add("arguments[entity]=BABY_"+entity.getType());
                        }
                        else{
                            arguments.add("arguments[entity]="+entity.getType());
                        }
                        if(entity.getCustomName()!=null) arguments.add("killer="+URLEncoder.encode(entity.getCustomName()));
                    }
                }
            }
            else if(lastDamage instanceof EntityDamageByBlockEvent){
                EntityDamageByBlockEvent damageByBlock = (EntityDamageByBlockEvent) lastDamage;
                Block block = damageByBlock.getDamager();
                if(block!=null)
                    arguments.add("arguments[block]="+block.getType());
            }
        }
        HTTPRequest request = DataSource.getResponse(DeathMessagesPlugin.getInstance(), "death.php", arguments.toArray(new String[arguments.size()]), RequestMethod.POST_SYNC);
        YamlConfiguration yamlConfiguration = request.getYamlResponse();
        if(yamlConfiguration==null) return;
        if(yamlConfiguration.contains("message")){
            event.setDeathMessage(yamlConfiguration.getString("message"));
        }
    }
}
