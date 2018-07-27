package ch.swisssmp.archery;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.SwissSMPler;

public class EventListener implements Listener{

	private List<Material> validDummyTypes = new ArrayList<Material>();
	private HashMap<Arrow,ItemStack> bowMap = new HashMap<Arrow,ItemStack>();
	
	public EventListener(){
		validDummyTypes.add(Material.HAY_BLOCK);
		validDummyTypes.add(Material.WOOD);
		validDummyTypes.add(Material.STONE);
		validDummyTypes.add(Material.IRON_BLOCK);
		validDummyTypes.add(Material.GOLD_BLOCK);
		validDummyTypes.add(Material.DIAMOND_BLOCK);
	}
	
	@EventHandler
	private void onPrepareItemCraft(PrepareItemCraftEvent event){
		//Bukkit.getLogger().info("[Archery] PrepareItemCraftEvent");
		ItemStack result = event.getInventory().getResult();
		if(result==null)return;
		//Bukkit.getLogger().info("[Archery] There is a result");
		if(result.getType()!=Material.ARROW && result.getType()!=Material.WOOD_SWORD) return;
		//Bukkit.getLogger().info("[Archery] Result is an arrow or a wood_sword");
		String customEnum = CustomItems.getCustomEnum(result);
		if(customEnum==null) return;
		//Bukkit.getLogger().info("[Archery] Result has a CustomEnum");
		ItemStack template = ItemManager.getItemStack(customEnum);
		//Bukkit.getLogger().info("[Archery] Check if recipe is from Archery");
		if(template==null) return;
		//at this point we estimated that the result is an item of this plugin, so we need to check whether the player has the necessary permission
		//Bukkit.getLogger().info("[Archery] Recipe is from Archery");
		if(!event.getView().getPlayer().hasPermission("archery.craft")){
			event.getInventory().setResult(null);
			//Bukkit.getLogger().info("[Archery] Player does not have permission");
		}
	}
	
	@EventHandler
	private void onArrowShoot(EntityShootBowEvent event){
		if(event.getEntityType()!=EntityType.PLAYER) return;
		if(event.getProjectile().getType()!=EntityType.ARROW) return;
		this.bowMap.put((Arrow)event.getProjectile(),event.getBow());
		//Bukkit.getLogger().info("[Archery] Shooting Arrow.");
		Player player = (Player)event.getEntity();
		PlayerInventory playerInventory = player.getInventory();
		ItemStack arrowStack;
		ItemStack mainhand = playerInventory.getItemInMainHand();
		ItemStack offhand = playerInventory.getItemInOffHand();
		if(
				mainhand==null || offhand==null || (
				mainhand.getType()!=Material.ARROW && offhand.getType()!=Material.ARROW &&
				mainhand.getType()!=Material.TIPPED_ARROW && offhand.getType()!=Material.TIPPED_ARROW
				)){
			arrowStack = ItemManager.getFirstArrowStack(playerInventory);
			//if(arrowStack==null) Bukkit.getLogger().info("[Archery] Could not find an Arrow Stack in Inventory.");
		}
		else{
			if(mainhand.getType()==Material.ARROW || mainhand.getType() == Material.TIPPED_ARROW){
				arrowStack = playerInventory.getItemInMainHand();
			}
			else if(offhand.getType()==Material.ARROW || mainhand.getType() == Material.TIPPED_ARROW){
				arrowStack = playerInventory.getItemInOffHand();
			}
			else{
				//should not happen, it would mean that no arrow stack was found (maybe in creative?)
				//Bukkit.getLogger().info("[Archery] Could not find an Arrow Stack in Hands.");
				return;
			}
		}
		if(arrowStack==null){
			//Bukkit.getLogger().info("[Archery] Could not find an Arrow Stack.");
			return;
		}
		//Bukkit.getLogger().info("[Archery] Arrow Stack is called "+arrowStack.getItemMeta().getDisplayName());
		String customEnum = CustomItems.getCustomEnum(arrowStack);
		if(customEnum!=null){
			if(ItemManager.getItemStack(customEnum)==null)return;
			Arrow arrow = (Arrow)event.getProjectile();
			if(!customEnum.equals("MULTI_ARROW")) arrow.setMetadata("arrow_type", new FixedMetadataValue(Archery.plugin, customEnum));
			ArrowManager.onArrowShoot(event, arrow, customEnum);
			if(player.getGameMode()!=GameMode.CREATIVE) ArrowManager.ensureArrowConsumption(event.getBow(),arrow,arrowStack, customEnum);
			//Bukkit.getLogger().info("[Archery] Shot arrow is "+customEnum);
		}
		ItemStack quiverStack = ItemManager.getQuiver(playerInventory);
		if(quiverStack==null)return;
		Bukkit.getScheduler().runTaskLater(Archery.plugin, new Runnable(){
			public void run(){
				QuiverManager.refillFromQuiver(quiverStack, arrowStack);
			}
		}, 1l);
	}
	
	@EventHandler
	private void onProjectileHit(ProjectileHitEvent event){
		if(!(event.getEntity() instanceof Arrow))return;
		Arrow arrow = (Arrow) event.getEntity();
		if(event.getHitBlock()!=null && arrow.getShooter() instanceof Player){
			Block hit = event.getHitBlock();
			Material dummyType = Material.AIR;
			boolean isDummy = false;
			if(hit.getType()==Material.PUMPKIN || hit.getType() == Material.JACK_O_LANTERN){
				if(validDummyTypes.contains(hit.getRelative(BlockFace.DOWN).getType())){
					isDummy = true;
					dummyType = hit.getRelative(BlockFace.DOWN).getType();
				}
			}
			else if(validDummyTypes.contains(hit.getType())){
				if(hit.getRelative(BlockFace.UP).getType()==Material.PUMPKIN || hit.getRelative(BlockFace.UP).getType()==Material.JACK_O_LANTERN){
					isDummy = true;
					dummyType = hit.getType();
				}
			}
			if(isDummy){
				double damage = ArrowManager.getArrowDamage(bowMap.get(arrow), arrow, dummyType);
				SwissSMPler.get((Player)arrow.getShooter()).sendActionBar(ChatColor.YELLOW+String.format("%.1f", damage)+" Schaden");
			}
		}
		String arrowType = ArrowManager.getArrowType(arrow);
		if(arrowType==null){
			//Bukkit.getLogger().info("[Archery] Hitting arrow is not special.");
			return;
		}
		//Bukkit.getLogger().info("[Archery] Hitting arrow is "+arrowType);
		ArrowManager.onArrowHit(event, arrow, arrowType);
		bowMap.remove(arrow);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.LOW)
	private void onArrowPickup(PlayerPickupArrowEvent event){
		Arrow arrow = (Arrow) event.getArrow();
		String arrowType = ArrowManager.getArrowType(arrow);
		if(arrowType!=null){
			//Bukkit.getLogger().info("[Archery] Picked up arrow is "+arrowType);
			ItemStack arrowStack = ItemManager.getItemStack(arrowType);
			if(arrowStack==null)return;
			arrowStack = arrowStack.clone();
			arrowStack.setAmount(1);
			event.getItem().setItemStack(arrowStack);
		}
		Player player = event.getPlayer();
		ItemStack quiverStack = ItemManager.getQuiver(player.getInventory());
		if(quiverStack==null)return;
		ItemStack remaining = QuiverManager.refillQuiver(quiverStack, event.getItem().getItemStack());
		if(remaining!=null){
			event.getItem().setItemStack(remaining);
		}
		else{
			event.getArrow().remove();
			event.setCancelled(true);
			player.getWorld().playSound(event.getArrow().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
		}
		QuiverView view = QuiverView.get(player);
		if(view!=null)view.update();
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	private void onItemPickup(EntityPickupItemEvent event){
		ItemStack itemStack = event.getItem().getItemStack();
		if(itemStack==null || (itemStack.getType()!=Material.ARROW && itemStack.getType()!=Material.TIPPED_ARROW))return;
		if(!(event.getEntity() instanceof Player)) return;
		itemStack = itemStack.clone();
		itemStack.setAmount(itemStack.getAmount()+event.getRemaining());
		//int initialAmount = itemStack.getAmount();
		Player player = (Player)event.getEntity();
		ItemStack quiverStack = ItemManager.getQuiver(player.getInventory());
		if(quiverStack==null)return;
		ItemStack remaining = QuiverManager.refillQuiver(quiverStack, itemStack);
		if(remaining!=null){
			HashMap<Integer,ItemStack> overflow = player.getInventory().addItem(remaining);
			if(overflow.size()>0) remaining = overflow.get(0);
			else remaining = null;
		}
		event.setCancelled(true);
		int remainingAmount = (remaining!=null?remaining.getAmount():0);
		player.getWorld().playSound(event.getItem().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
		//Bukkit.getLogger().info("[Archery] "+remainingAmount+"/"+initialAmount+" did not fit.");
		if(remainingAmount<=0) event.getItem().remove();
		else{
			event.getItem().getItemStack().setAmount(remainingAmount);
		}
		QuiverView view = QuiverView.get(player);
		if(view!=null)view.update();
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	private void onEntityDamageByEntity(EntityDamageByEntityEvent event){
		if(!(event.getDamager() instanceof Arrow)) return;
		Arrow arrow = (Arrow)event.getDamager();
		String arrowType = ArrowManager.getArrowType(arrow);
		if(arrowType==null){
			//Bukkit.getLogger().info("[Archery] Damaging arrow is not special.");
			return;
		}
		ArrowManager.onArrowDamage(event, arrow, arrowType);
	}
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event){
		if((event.getAction()!=Action.RIGHT_CLICK_AIR && event.getAction()!=Action.RIGHT_CLICK_BLOCK)) return;
		ItemStack itemStack = event.getItem();
		if(itemStack==null || itemStack.getType()!=Material.WOOD_SWORD) return;
		String customEnum = CustomItems.getCustomEnum(itemStack);
		if(customEnum==null || !customEnum.equals("QUIVER"))return;
		ItemStack offhand = event.getPlayer().getInventory().getItemInOffHand();
		if(event.getHand()==EquipmentSlot.HAND && offhand!=null && offhand.getType()==Material.BOW) return;
		event.setCancelled(false);
		QuiverManager.openQuiver(event.getPlayer(), itemStack);
	}
}
