package ch.swisssmp.flyinglanterns;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItems;

public class EventListener implements Listener{
	
	@EventHandler
	private void onLanternIgnite(PlayerInteractEvent event){
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		if(event.getItem()==null || event.getItem().getType()!=Material.CARROT_STICK) return;
		ItemStack itemStack = event.getItem();
		String customEnum = CustomItems.getCustomEnum(itemStack);
		if(customEnum==null || !customEnum.equals("FLYING_LANTERN")) return;
		FlyingLantern.spawn(event.getClickedBlock().getRelative(event.getBlockFace()).getLocation().add(0.5,0.1,0.5));
		if(event.getPlayer().getGameMode()!=GameMode.CREATIVE) event.getItem().setAmount(event.getItem().getAmount()-1);
	}
	
	@EventHandler
	private void onPlayerInteractEntity(PlayerInteractEntityEvent event){
		FlyingLantern lantern = FlyingLantern.get(event.getRightClicked());
		if(lantern!=null) event.setCancelled(true);
		//TODO make breaking possible
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
