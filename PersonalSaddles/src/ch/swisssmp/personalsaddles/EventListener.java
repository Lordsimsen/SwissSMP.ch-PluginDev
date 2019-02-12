package ch.swisssmp.personalsaddles;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.SwissSMPler;

public class EventListener implements Listener {

	private final DamageCause[] protectedFrom = new DamageCause[]{
	    	DamageCause.BLOCK_EXPLOSION,
	    	DamageCause.ENTITY_EXPLOSION,
	    	DamageCause.ENTITY_ATTACK,
	    	DamageCause.FALLING_BLOCK,
	    	DamageCause.SUFFOCATION,
	    	DamageCause.LIGHTNING,
	    	DamageCause.PROJECTILE,
	    	DamageCause.MAGIC,
	    	DamageCause.POISON,
	    	DamageCause.CONTACT,
	    	DamageCause.FIRE,
	    	DamageCause.FIRE_TICK
	};

	@EventHandler
    public void prepareCraftItem(PrepareItemCraftEvent event) {
    	ItemStack result = event.getInventory().getResult();
    	SaddleInfo saddleInfo = SaddleInfo.get(result);
    	if(saddleInfo==null) return;
        HumanEntity human = event.getView().getPlayer();
        if(!(human instanceof Player)) return;
    	Player crafter = (Player) human;
    	if(!crafter.hasPermission("personalsaddle.craft") && !crafter.hasPermission("personalsaddle.admin")){
    		event.getInventory().setResult(null);
    		return;
    	}
    	saddleInfo.setOwner(crafter);
    	saddleInfo.apply(result);
		event.getInventory().setResult(result);
    }
    
    @EventHandler
    public void mountHorse(VehicleEnterEvent event){
    	SaddleInfo saddleInfo = SaddleInfo.get(event.getVehicle());
    	if(saddleInfo==null)
    		return;
		Entity rider = event.getEntered();
		if(saddleInfo.isOwner(rider) || rider.hasPermission("personalsaddle.admin")) return;
		if(rider instanceof Player){
    		Player player = (Player) rider;
			SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Dieses Pferd gehört nicht dir!");
		}
		event.setCancelled(true);
    }

    @EventHandler
    public void damageHorse(EntityDamageEvent event){
    	SaddleInfo saddleInfo = SaddleInfo.get(event.getEntity());
    	if(saddleInfo==null)
    		return;
    	DamageCause cause = event.getCause();
    	Horse horse = (Horse) event.getEntity();
    	if(horse.getPassengers().size()>0) return;
    	for(int i = 0; i < protectedFrom.length; i++){
    		if(protectedFrom[i]==cause){
    			event.setCancelled(true);
    			return;
    		}
    	}
    }
    
    @EventHandler
    public void accessHorse(InventoryOpenEvent event){
    	if(!(event.getInventory().getHolder() instanceof Horse)) return;
    	SaddleInfo saddleInfo = SaddleInfo.get((Horse) event.getInventory().getHolder());
    	if(saddleInfo==null)
    		return;
    	if(saddleInfo.isOwner(event.getPlayer()) || event.getPlayer().hasPermission("personalsaddle.admin")) return;
		event.setCancelled(true);
		SwissSMPler.get((Player) event.getPlayer()).sendActionBar(ChatColor.RED+"Dieses Pferd gehört nicht dir!");
    }
    
    @EventHandler
    public void leashHorse(PlayerInteractEntityEvent event){
    	SaddleInfo saddleInfo = SaddleInfo.get(event.getRightClicked());
    	if(saddleInfo==null)
    		return;
    	if(saddleInfo.isOwner(event.getPlayer()) || event.getPlayer().hasPermission("personalsaddle.admin")) return;
		event.setCancelled(true);
		SwissSMPler.get((Player) event.getPlayer()).sendActionBar(ChatColor.RED+"Dieses Pferd gehört nicht dir!");
    }
}
