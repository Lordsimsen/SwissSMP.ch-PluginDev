package ch.swisssmp.craftmmo.mmoevent;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.craftmmo.mmoattribute.IDurable;
import ch.swisssmp.craftmmo.mmocamp.MmoCampMob;
import ch.swisssmp.craftmmo.mmoentity.MmoEntity;
import ch.swisssmp.craftmmo.mmoentity.MmoEntityUtils;
import ch.swisssmp.craftmmo.mmoentity.MmoMob;
import ch.swisssmp.craftmmo.mmoitem.MmoChanceItem;
import ch.swisssmp.craftmmo.mmoplayer.MmoPlayer;
import net.md_5.bungee.api.ChatColor;

public class MmoEventListenerEntity extends MmoEventListener{
	public MmoEventListenerEntity(JavaPlugin plugin) {
		super(plugin);
	}
	@EventHandler(ignoreCancelled = true)
	private void onEntityAttackEntity(EntityDamageByEntityEvent event){
		if(event.getEntity().isInvulnerable()){
			return;
		}
		Entity attackerEntity = event.getDamager();
		Entity attackedEntity = event.getEntity();
		if(attackedEntity.isInvulnerable() && attackerEntity instanceof Player){
			Player attackerPlayer = (Player) attackerEntity;
			MmoPlayer.sendActionBar(attackerPlayer, ChatColor.LIGHT_PURPLE+"Unverwundbar!");
			return;
		}
		if(!(attackerEntity instanceof LivingEntity) || !(attackedEntity instanceof LivingEntity)){
			return;
		}
		MmoEntity attacker = new MmoEntity((LivingEntity) attackerEntity);
		MmoEntity defender = new MmoEntity((LivingEntity) attackedEntity);
		
		IDamageCalculator calculator;
		switch(event.getCause()){
		case ENTITY_ATTACK:
			MmoEntityAttackEntityEvent damageEvent = new MmoEntityAttackEntityEvent(attacker, defender);
			MmoEventManager.callEvent(damageEvent);
			calculator = damageEvent;
			break;
		default:
			return;
		}
		double damage = event.getFinalDamage();
		if(attackerEntity instanceof Player && attackedEntity instanceof LivingEntity){
			Player attackerPlayer = (Player) attackerEntity;
			LivingEntity livingEntity = (LivingEntity)attackedEntity;
			MmoPlayer.sendActionBar(attackerPlayer, MmoEntityUtils.damageFeedback(livingEntity, damage));
		}
		event.setDamage(damage);
		if(damage>0 && calculator!=null){
			//defender.entity.damage(damage, attacker.entity);
			defender.applyElementalEffects(calculator.getElementalDebuffs());
			if(calculator.getUsedItemStack()!=null){
				IDurable.changeDurability(calculator.getUsedItemStack().itemStack, -1);
			}
		}
	}
	@EventHandler
	private void onEntityDeath(EntityDeathEvent event){
		Entity entity = event.getEntity();
		MmoMob mmoMob = MmoMob.get(entity);
		if(mmoMob==null)
			return;
		List<ItemStack> items = event.getDrops();
		items.clear();
		List<MmoChanceItem> randomDrops = MmoChanceItem.getRandomItems(mmoMob.drops);
		int max_drops = mmoMob.max_drops;
		for(MmoChanceItem droppedItem : randomDrops){
			ItemStack drop = droppedItem.unpack();
			if(drop==null)
				continue;
			else if(drop.getAmount()<=0)
				continue;
			items.add(drop);
			if(max_drops>0 && max_drops==items.size()){
				return;
			}
		}
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
	/*@EventHandler(ignoreCancelled = true)
	private void onEntityAttackEntity(EntityDamageByEntityEvent event){
		if(event.getEntity().isInvulnerable()){
			return;
		}
		Entity attackerEntity = event.getDamager();
		Entity attackedEntity = event.getEntity();
		Main.info("invulnerable: "+attackedEntity.isInvulnerable());
		if(!(attackerEntity instanceof LivingEntity) || !(attackedEntity instanceof LivingEntity)){
			return;
		}
		MmoEntity attacker = new MmoEntity((LivingEntity) attackerEntity);
		MmoEntity defender = new MmoEntity((LivingEntity) attackedEntity);
		
		IDamageCalculator calculator;
		switch(event.getCause()){
		case ENTITY_ATTACK:
			MmoEntityAttackEntityEvent damageEvent = new MmoEntityAttackEntityEvent(attacker, defender);
			callEvent(damageEvent);
			calculator = damageEvent;
			break;
		default:
			calculator = null;
			break;
		}
		if(calculator!=null){
			double damage = calculator.getCalculatedDamage();
			if(attackerEntity instanceof Player && attackedEntity instanceof LivingEntity){
				Player attackerPlayer = (Player) attackerEntity;
				LivingEntity livingEntity = (LivingEntity)attackedEntity;
				MmoPlayer.sendActionBar(attackerPlayer, attackedEntity.getCustomName()+": "+ChatColor.GREEN+String.format("%.2f", livingEntity.getHealth())+ChatColor.RED+" -"+String.format("%.2f", damage));
			}
			event.setDamage(damage);
			if(damage>0){
				//defender.entity.damage(damage, attacker.entity);
				defender.applyElementalEffects(calculator.getElementalDebuffs());
				if(calculator.getUsedItemStack()!=null){
					IDurable.changeDurability(calculator.getUsedItemStack().itemStack, -1);
				}
			}
		}
	}*/
}
