package ch.swisssmp.flyinglanterns;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.Random;

public class EventListener implements Listener{
	private Random random = new Random();
	@EventHandler
	private void onLanternIgnite(PlayerInteractEvent event){
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		if(event.getItem()==null || event.getItem().getType()!=Material.CARROT_ON_A_STICK) return;
		ItemStack itemStack = event.getItem();
		String customEnum = CustomItems.getCustomEnum(itemStack);
		if(customEnum==null || !customEnum.equals("FLYING_LANTERN")) return;
		Block block = event.getClickedBlock();
		if(block.getType().isSolid()) block = block.getRelative(event.getBlockFace());
		FlyingLantern.spawn(block.getLocation().add(0.5,0.1,0.5));
		if(event.getPlayer().getGameMode()!=GameMode.CREATIVE) event.getItem().setAmount(event.getItem().getAmount()-1);
		event.setCancelled(true);
	}
	
	@EventHandler
	private void onEntityDamageByEntity(EntityDamageByEntityEvent event){
		if(event.getEntity().getType()!=EntityType.ARMOR_STAND) return;
		FlyingLantern lantern = FlyingLantern.get(event.getEntity());
		if(lantern==null) return;
		if(event.getCause() == DamageCause.ENTITY_ATTACK){
			if(event.getDamager() instanceof Player){
				Player player = (Player) event.getDamager();
				if(player.getGameMode()==GameMode.CREATIVE){
					lantern.remove();
					return;
				}
			}
		}
		else if(event.getDamager() instanceof Firework){
			if(event.getDamager().getLocation().distanceSquared(event.getEntity().getLocation())>2) return;
			Bukkit.getScheduler().runTaskLater(FlyingLanterns.plugin, new Runnable(){
				public void run(){
					lantern.explode();
				}
			}, 5+Mathf.floorToInt(random.nextDouble()*35));
			lantern.explode();
			return;
		}
		lantern.drop();
	}
	
	@EventHandler
	private void onProjectileHit(ProjectileHitEvent event){
		Entity hit = event.getHitEntity();
		if(hit==null) return;
		FlyingLantern lantern = FlyingLantern.get(hit);
		if(lantern!=null){
			lantern.explode();
			event.getEntity().remove();
		}
	}
	
	@EventHandler
	private void onEntityDeath(EntityDeathEvent event){
		FlyingLantern lantern = FlyingLantern.get(event.getEntity());
		if(lantern!=null) lantern.unload();
	}
	
	@EventHandler
	private void onChunkLoad(ChunkLoadEvent event){
		for(Entity entity : event.getChunk().getEntities()){
			if(!FlyingLanterns.isFlyingLantern(entity)) continue;
			FlyingLantern.load(entity);
		}
	}
	
	@EventHandler
	private void onChunkUnload(ChunkUnloadEvent event){
		FlyingLantern lantern;
		for(Entity entity : event.getChunk().getEntities()){
			lantern = FlyingLantern.get(entity);
			if(lantern!=null) lantern.unload();
		}
	}
}
