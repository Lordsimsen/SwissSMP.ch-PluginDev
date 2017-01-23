package ch.swisssmp.craftmmo.mmoevent;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoattribute.MmoAttributes;
import ch.swisssmp.craftmmo.mmoattribute.MmoElement;
import ch.swisssmp.craftmmo.mmoentity.MmoEntity;
import ch.swisssmp.craftmmo.mmoentity.MmoEntityUtils;
import ch.swisssmp.craftmmo.mmoitem.MmoItem;
import ch.swisssmp.craftmmo.mmoitem.MmoItemThrowable;
import ch.swisssmp.craftmmo.mmoplayer.MmoPlayer;

public class MmoEventListenerThrowable extends MmoEventListener{
	
	public MmoEventListenerThrowable(JavaPlugin plugin) {
		super(plugin);
		Main.debug("Registered projectile events.");
	}
	@EventHandler//(ignoreCancelled=true)
	private void onPlayerInteract(PlayerInteractEvent event){
		if(!(event.getAction()==Action.RIGHT_CLICK_AIR || (event.getAction()==Action.RIGHT_CLICK_BLOCK && !event.isCancelled()))) return;
		ItemStack itemStack = event.getItem();
		MmoItem mmoItem = MmoItem.get(itemStack);
		if(mmoItem==null){
			return;
		}
		if(!(mmoItem instanceof MmoItemThrowable)) {
			return;
		}
		event.setCancelled(true);
		MmoItemThrowable throwable = (MmoItemThrowable) mmoItem;
		Player player = event.getPlayer();
		Snowball snowball = player.launchProjectile(Snowball.class);
		snowball.setBounce(throwable.bounce);
		snowball.setShooter(player);
		snowball.setMetadata("damage", new FixedMetadataValue(plugin, throwable.damage));
		snowball.setMetadata("mmo_item_id", new FixedMetadataValue(plugin, throwable.mmo_item_id));
		snowball.setMetadata("shooter", new FixedMetadataValue(plugin, event.getPlayer().getUniqueId().toString()));
		if(itemStack.getAmount()>1){
			itemStack.setAmount(itemStack.getAmount()-1);
		}
		else{
			player.getInventory().removeItem(itemStack);
		}
		Item item = player.getWorld().dropItem(snowball.getLocation(), new ItemStack(itemStack.getType()));
		item.setPickupDelay(Integer.MAX_VALUE);
		snowball.setPassenger(item);
		CraftEntity craftEntity = (CraftEntity)snowball;
		net.minecraft.server.v1_11_R1.Entity nmsEntity = craftEntity.getHandle();
		nmsEntity.setInvisible(true);
	}
	@EventHandler(ignoreCancelled=true)
	private void onProjectileHit(EntityDamageByEntityEvent event){
		Entity attacker = event.getDamager();
		if(!(attacker instanceof Snowball) || !attacker.hasMetadata("mmo_item_id")){
			return;
		}
		Snowball snowball = (Snowball) attacker;
		int damage = snowball.getMetadata("damage").get(0).asInt();
		event.setDamage(damage);
		MmoItem mmoItem = MmoItem.get(snowball.getMetadata("mmo_item_id").get(0).asInt());
		Entity attacked = event.getEntity();
		UUID player_uuid = ((Player)snowball.getShooter()).getUniqueId();
		if(mmoItem!=null && attacked instanceof LivingEntity){
			LivingEntity livingEntity = (LivingEntity) attacked;
			MmoEntity defender = new MmoEntity(livingEntity);
		    HashMap<MmoElement, Integer> elementalDebuffs = MmoAttributes.calculateElementalEffects(mmoItem.elements, defender.attributes.elements);
			defender.applyElementalEffects(elementalDebuffs);
			MmoPlayer.sendActionBar(player_uuid, MmoEntityUtils.damageFeedback(livingEntity, damage));
		}
	}
	@EventHandler(ignoreCancelled=true)
	private void onProjectileHitAny(ProjectileHitEvent event){
		Projectile projectile = event.getEntity();
		Entity passenger = projectile.getPassenger();
		if(passenger!=null){
			passenger.remove();
		}
	}
}
