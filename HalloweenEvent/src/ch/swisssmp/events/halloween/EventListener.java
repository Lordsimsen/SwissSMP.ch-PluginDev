package ch.swisssmp.events.halloween;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockVector;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.Random;

public class EventListener implements Listener {
	private Random random = new Random();
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK){
			if(event.getClickedBlock().getType()==Material.JUKEBOX){
				JukeboxHandler.handleJukeboxInteraction(event);
			}
			return;
		}
		if(event.getAction()==Action.RIGHT_CLICK_AIR){
			if(event.getItem()==null) return;
			String customEnum = CustomItems.getCustomEnum(event.getItem());
			if(customEnum==null) return;
			switch(customEnum){
			case "BOMBON_RED":
			case "BOMBON_ORANGE":
			case "BOMBON_PURPLE":
			case "BOMBON_AQUA":{
				Bombon.launch(event.getPlayer(), event.getItem());
				return;
			}
			default: return;
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onEntitySpawn(EntitySpawnEvent event){
		EntityCostumeDesigner.applyCostume(event.getEntity());
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onBlockBreak(BlockBreakEvent event){
		if(event.getBlock().getType()!=Material.JUKEBOX) return;
		if(event.getBlock().getWorld()!=Bukkit.getWorlds().get(0)) return;
		BlockVector blockVector = new BlockVector(event.getBlock().getX(),event.getBlock().getY(),event.getBlock().getZ());
		FlashMobBattle battle = FlashMobBattle.get(blockVector);
		if(battle==null) return;
		battle.stop();
	}
	
	@EventHandler
	private void onProjectileHit(ProjectileHitEvent event){
		Bombon bombon = Bombon.get(event.getEntity());
		if(bombon==null) return;
		bombon.hit();
		event.getEntity().remove();
	}
	
	@EventHandler
	private void onEntityDeath(EntityDeathEvent event){
		if(event.getEntityType()!=EntityType.CREEPER) return;
		if(event.getEntity().getWorld().getTime()<15000) return;
		if(random.nextDouble()>0.1) return;
		Bukkit.getLogger().info("Drop Disc");
		event.getDrops().add(HalloweenEventPlugin.getSpookyDisc());
	}
	
	@EventHandler
	private void onResourcepackUpdate(PlayerResourcePackUpdateEvent event){
		event.addComponent("halloween");
	}
}
