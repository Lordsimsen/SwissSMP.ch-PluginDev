package ch.swisssmp.deathmessages;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class DeathMessages extends JavaPlugin implements Listener{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static DeathMessages plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	private void onPlayerDeath(PlayerDeathEvent event){
		try {
			Player player = event.getEntity();
			ArrayList<String> arguments = new ArrayList<String>();
			arguments.add("player="+URLEncoder.encode(player.getDisplayName(), "utf-8"));
			if(player.getKiller()!=null){
				arguments.add("killer="+URLEncoder.encode(player.getKiller().getDisplayName(), "utf-8"));
			}
			arguments.add("message="+URLEncoder.encode(event.getDeathMessage(), "utf-8"));
			arguments.add("world="+URLEncoder.encode(player.getWorld().getName(), "utf-8"));
			EntityDamageEvent lastDamage = player.getLastDamageCause();
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
							if(shooter.getCustomName()!=null) arguments.add("killer="+URLEncoder.encode(shooter.getCustomName(), "utf-8"));
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
						if(entity.getCustomName()!=null) arguments.add("killer="+URLEncoder.encode(entity.getCustomName(), "utf-8"));
					}
				}
			}
			else if(lastDamage instanceof EntityDamageByBlockEvent){
				EntityDamageByBlockEvent damageByBlock = (EntityDamageByBlockEvent) lastDamage;
				Block block = damageByBlock.getDamager();
				if(block!=null)
					arguments.add("arguments[block]="+block.getType());
			}
			YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("players/death.php", arguments.toArray(new String[arguments.size()]));
			if(yamlConfiguration==null) return;
			if(yamlConfiguration.contains("message")){
				event.setDeathMessage(yamlConfiguration.getString("message"));
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll((JavaPlugin)this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}
