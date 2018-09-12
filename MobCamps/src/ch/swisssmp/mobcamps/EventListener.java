package ch.swisssmp.mobcamps;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.PlayerRenameItemEvent;
import ch.swisssmp.utils.SwissSMPler;

public class EventListener implements Listener{
	@EventHandler
	private void onChunkLoad(ChunkLoadEvent event){
		MobCampInstance.loadAll(event.getChunk());
	}
	
	@EventHandler
	private void onChunkUnload(ChunkUnloadEvent event){
		MobCampInstance.unloadAll(event.getChunk());
	}
	
	@EventHandler
	private void onEntityDeath(EntityDeathEvent event){
		MobCampInstance instance = MobCampInstance.get(event.getEntity());
		if(instance!=null) instance.remove();
	}
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEntityEvent event){
		if(event.getPlayer().getGameMode()!=GameMode.CREATIVE) return;
		if(!event.getPlayer().hasPermission("mobcamps.admin")) return;
		MobCampInstance instance = MobCampInstance.get(event.getRightClicked());
		if(instance==null) return;
		if(event.getPlayer().isSneaking()){
			PlayerInventory inventory = event.getPlayer().getInventory();
			inventory.setItemInMainHand(instance.getMobCamp().getInventoryToken(1));
		}
		else{
			MobCampEditor.open(event.getPlayer(), instance.getMobCamp());
		}
	}
	
	@EventHandler
	private void onPlayerDamageEntity(EntityDamageByEntityEvent event){
		if(!(event.getDamager() instanceof Player)) return;
		Player player = (Player)event.getDamager();
		if(player.getGameMode()!=GameMode.CREATIVE) return;
		if(!player.hasPermission("mobcamps.admin")) return;
		MobCampInstance instance = MobCampInstance.get(event.getEntity());
		if(instance==null) return;
		instance.remove();
		SwissSMPler.get(player).sendActionBar(ChatColor.RED+"'"+instance.getMobCamp().getName()+"' entfernt.");
	}
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getPlayer().getGameMode()!=GameMode.CREATIVE) return;
		if(event.getAction()!=Action.RIGHT_CLICK_AIR && event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		if(event.getItem()==null) return;
		ItemStack itemStack = event.getItem();
		int camp_id = ItemUtil.getInt(itemStack, "mob_camp");
		MobCamp mobCamp = MobCamp.get(camp_id);
		if(mobCamp!=null){
			if(event.getAction()==Action.RIGHT_CLICK_BLOCK){
				Block block = event.getClickedBlock().getRelative(event.getBlockFace());
				MobCampInstance.create(block.getLocation().add(0.5, 0, 0.5),mobCamp);
			}
			else{
				mobCamp.openEditor(event.getPlayer());
			}
		}
		else if(camp_id>0){
			itemStack.setAmount(0);
			event.getPlayer().sendMessage("[MobCamps] Camp nicht gefunden. Vielleicht wurde es gelöscht? Du kannst mit '/camps' herausfinden, welche Camps existieren.");
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onItemRename(PlayerRenameItemEvent event){
		MobCamp mobCamp = MobCamp.get(event.getItemStack());
		if(mobCamp==null) return;
		mobCamp.setName(event.getNewName());
		event.setName(ChatColor.LIGHT_PURPLE+event.getNewName());
	}
}
