package ch.swisssmp.event.quarantine.tasks;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.event.quarantine.MaterialSpawner;
import ch.swisssmp.event.quarantine.QuarantineEventInstance;
import ch.swisssmp.event.quarantine.QuarantineEventPlugin;
import ch.swisssmp.event.quarantine.QuarantineMaterial;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.Random;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.event.quarantine.QuarantineEventInstance.Phase;

/**
 * Führe Spielpartie aus
 * @author Oliver
 *
 */
public class PlayPhase extends QuarantineEventInstanceTask implements Listener {
	
	private static Random random = new Random();
	
	private long t;
	private Collection<MaterialSpawner> spawners;
	
	public PlayPhase(QuarantineEventInstance instance) {
		super(instance);

	}
	
	@Override
	protected void onInitialize() {
		random.setSeed(LocalDateTime.now().getNano()+LocalDateTime.now().getSecond());
		Bukkit.getPluginManager().registerEvents(this, QuarantineEventPlugin.getInstance());
		this.spawners = this.getInstance().getSpawners();
		for(MaterialSpawner spawner : spawners) {
			spawner.setTimeout(Mathf.roundToInt(spawner.getSpawnrate() * random.nextDouble()));
		}
	}

	@Override
	public void run() {
		for(MaterialSpawner spawner : spawners) {
			spawner.run();
		}
		t++;
		if(t>20 * 300) {
			complete();
		}
	}
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		QuarantineEventInstance instance = getInstance();
		if(!instance.isSurvivor(player)) return;
		if(event.getItem()!=null) {
			String customEnum = CustomItems.getCustomEnum(event.getItem());
			QuarantineMaterial material = QuarantineMaterial.of(event.getItem().getType());
			if(material!=QuarantineMaterial.FOOD && (customEnum==null || !customEnum.equals("QUARANTINE_HEAL_POTION"))) {
				event.setUseItemInHand(Result.DENY);
				//Bukkit.getLogger().info("Deny Item Usage");
			}
		}
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		Block block = event.getClickedBlock();
		Optional<MaterialSpawner> spawnerQuery = instance.getSpawner(block);
		if(!spawnerQuery.isPresent()) return;
		MaterialSpawner spawner = spawnerQuery.get();
		if(!spawner.isFilled()) return;
		ItemStack itemStack = spawner.collect();
		player.getInventory().addItem(itemStack);
		Location location = player.getEyeLocation();
		//location.getWorld().dropItem(location, itemStack);
		location.getWorld().playSound(block.getLocation(), Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1, 1);
	}
	
	@EventHandler
	private void onPlayerDamaged(EntityDamageByEntityEvent event) {
		Entity damaged = event.getEntity();
		Entity damager = event.getDamager();
		
		if(!(damaged instanceof Player)) return;
		Player player = (Player) damaged;
		QuarantineEventInstance instance = getInstance();
		if(!instance.isPlaying(player)) return;
		
		if(instance.isSurvivor(player) && instance.isInfected(damager.getUniqueId())) {
			event.setDamage(4);
		}
		
		if(instance.isInfected(player) && instance.isInfected(damager.getUniqueId())) {
			SwissSMPler.get(damager.getUniqueId()).sendActionBar(ChatColor.RED+damaged.getName()+" ist bereits infiziert!");
		}
		
		if(instance.isSurvivor(player) && instance.isSurvivor(damager.getUniqueId())) {
			SwissSMPler.get(damager.getUniqueId()).sendActionBar(ChatColor.RED+damaged.getName()+" ist nicht infiziert!");
			event.setCancelled(true);
		}
		
		if(event.getDamage()<player.getHealth()) return; // not lethal
		
		if(instance.isSurvivor(player) && damager instanceof Player) {
			Player killer = (Player) damager;
			if(!instance.isInfected(killer)) return; // dont care
			instance.infect(player, killer);
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	private void onInfectedRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		QuarantineEventInstance instance = getInstance();
		if(!instance.isInfected(player)) return;
		Location respawnLocation = getArena().getRespawn().getLocation(getArena().getWorld());
		event.setRespawnLocation(respawnLocation);
		instance.initializeInfected(player);
	}
	
	@EventHandler
	private void onSurvivorKilled(PlayerDeathEvent event) {
		Player player = event.getEntity();
		QuarantineEventInstance instance = getInstance();
		if(!instance.isSurvivor(player)) {
			return;
		}
		
		instance.infect(player, null);
	}
	
	@EventHandler
	private void onPlayerConsume(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		QuarantineEventInstance instance = getInstance();
		if(!instance.isSurvivor(player)) return;
		QuarantineMaterial material = QuarantineMaterial.of(event.getItem().getType());
		if(material!=null) Bukkit.getLogger().info(material.toString());
		if(material!=QuarantineMaterial.FOOD) {
			// Bukkit.getLogger().info("Deny item consumption");
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	private void onPlayerHunger(FoodLevelChangeEvent event) {
		Player player = (Player) event.getEntity();
		QuarantineEventInstance instance = getInstance();
		if(!instance.isInfected(player) || event.getFoodLevel()>=20) return;
		event.setCancelled(true);
		player.setFoodLevel(20);
	}
	
	@EventHandler
	private void onPlayerPickupItem(EntityPickupItemEvent event) {
		Entity entity = event.getEntity();
		if(!(entity instanceof Player)) return;
		Player player = (Player) entity;
		QuarantineEventInstance instance = getInstance();
		if(!instance.isPlaying(player)) return;
		if(instance.isInfected(player)) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	private void onPlayerPotionEffect(PotionSplashEvent event) {
		ThrownPotion potion = event.getEntity();
		ProjectileSource source = potion.getShooter();
		if(!(source instanceof Player)) return;
		Player thrower = (Player) source;

		QuarantineEventInstance instance = getInstance();
		if(!instance.isPlaying(thrower)) return;

		event.setCancelled(true);
		
		Collection<LivingEntity> entities = event.getAffectedEntities();
		for(LivingEntity target : entities) {
			if(!(target instanceof Player)) continue;
			Player player = (Player) target;
			if(!instance.isPlaying(player)) return;
			if(instance.isInfected(player)) {
				
				instance.cure(player, thrower);
			}
		}
	}
	
	@Override
	protected void onComplete() {
		getInstance().setPhase(Phase.Finish);
	}

	@Override
	protected void onFinish() {
		HandlerList.unregisterAll(this);
		for(MaterialSpawner spawner : spawners) {
			spawner.reset();
		}
	}
}
