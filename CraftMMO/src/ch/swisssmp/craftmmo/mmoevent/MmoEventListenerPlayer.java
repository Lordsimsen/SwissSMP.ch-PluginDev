package ch.swisssmp.craftmmo.mmoevent;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoattribute.MmoElement;
import ch.swisssmp.craftmmo.mmoblock.MmoBlock;
import ch.swisssmp.craftmmo.mmoblock.MmoBlockScheduler;
import ch.swisssmp.craftmmo.mmocamp.MmoCampEditor;
import ch.swisssmp.craftmmo.mmoentity.MmoMob;
import ch.swisssmp.craftmmo.mmoitem.MmoItem;
import ch.swisssmp.craftmmo.mmoitem.MmoItemStack;
import ch.swisssmp.craftmmo.mmoitem.MmoLootInventory;
import ch.swisssmp.craftmmo.mmoworld.MmoDungeon;
import ch.swisssmp.craftmmo.mmoworld.MmoDungeonInstance;
import ch.swisssmp.craftmmo.mmoworld.MmoRegion;
import ch.swisssmp.craftmmo.mmoworld.MmoWorld;
import ch.swisssmp.craftmmo.mmoworld.MmoWorldInstance;
import ch.swisssmp.craftmmo.util.MmoResourceManager;

public class MmoEventListenerPlayer extends MmoEventListener{
	public MmoEventListenerPlayer(JavaPlugin plugin) {
		super(plugin);
	}
	@EventHandler
	private void onPlayerLogin(PlayerLoginEvent event){
		MmoPlayerLoginEvent loginEvent = new MmoPlayerLoginEvent(event.getPlayer());
		MmoEventManager.callEvent(loginEvent);
	}
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		MmoCampEditor editor = MmoCampEditor.get(player);
		if(editor!=null) editor.quit();
	}
	@EventHandler(ignoreCancelled=true)
	private void onTeleport(PlayerTeleportEvent event){
		Location from = event.getFrom();
		Location to = event.getTo();
		if(from.getWorld()!=to.getWorld()){
			Player player = event.getPlayer();
			MmoDungeonInstance dungeonInstance = MmoDungeon.getInstance(player);
			if(dungeonInstance==null) return;
			if(dungeonInstance.getWorld().getName().equals(from.getWorld().getName())){
				MmoDungeon mmoDungeon = MmoDungeon.get(dungeonInstance);
				if(mmoDungeon!=null){
					mmoDungeon.leave(player.getUniqueId());
				}
			}
		}
	}
	@EventHandler(ignoreCancelled=true)
	private void onGameModeChange(PlayerGameModeChangeEvent event){
		Player player = event.getPlayer();
		Location location = player.getLocation();
		RegionManager manager = Main.worldGuardPlugin.getRegionContainer().get(location.getWorld());
		ApplicableRegionSet regions = manager.getApplicableRegions(location);
		MmoWorldInstance worldInstance = MmoWorld.getInstance(event.getPlayer().getWorld());
		for(ProtectedRegion protectedRegion : regions){
			MmoRegion region = worldInstance.regionTriggers.get(protectedRegion.getId());
			if(region==null){
				continue;
			}
			region.playerLeave(player, player.getGameMode());
			region.playerEnter(player, event.getNewGameMode());
		}
	}
	@EventHandler
	private void onPlayerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		Location location = player.getLocation();
		RegionManager manager = Main.worldGuardPlugin.getRegionContainer().get(location.getWorld());
		ApplicableRegionSet regions = manager.getApplicableRegions(location);
		MmoWorldInstance worldInstance = MmoWorld.getInstance(event.getEntity().getWorld());
		for(ProtectedRegion protectedRegion : regions){
			MmoRegion region = worldInstance.regionTriggers.get(protectedRegion.getId());
			if(region==null){
				continue;
			}
			region.playerDeath(player);
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onPlayerInteractEntity(PlayerInteractEntityEvent event) throws Exception{
		if(event.getHand()!=EquipmentSlot.HAND)
			return;
		Entity entity = event.getRightClicked();
		MmoMob mmoMob = MmoMob.get(entity);
		if(mmoMob==null)
			return;
		Player player = event.getPlayer();
		MmoTalkEvent talkEvent = new MmoTalkEvent(player.getUniqueId(), mmoMob);
		MmoEventManager.callEvent(talkEvent);
		if(!talkEvent.isCancelled()){
			MmoResourceManager.processYamlResponse(player.getUniqueId(), "progress/talk.php", new String[]{
				"player="+player.getUniqueId(),
				"mob="+mmoMob.mmo_mob_id
			});
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onPlayerInteract(PlayerInteractEvent event){
		Action action = event.getAction();
		Block block = event.getClickedBlock();
		if(action==Action.RIGHT_CLICK_BLOCK){
			if(block.getType()==Material.REDSTONE_TORCH_ON){
				ItemStack itemStack = event.getItem();
				if(itemStack!=null){
					MmoItem mmoItem = MmoItem.get(itemStack);
					if(mmoItem!=null){
						if(mmoItem.elements!=null){
							if(mmoItem.elements.containsKey(MmoElement.FIRE)){
								MmoBlock.set(block, new MaterialData(Material.TORCH), event.getPlayer().getUniqueId());
								MmoBlockScheduler.schedule(block, new MaterialData(Material.REDSTONE_TORCH_ON), mmoItem.elements.get(MmoElement.FIRE), event.getPlayer().getUniqueId());
								return;
							}
						}
					}
				}
			}
			else if(block.getType()==Material.JUKEBOX){
				MmoLootInventory lootInventory = MmoLootInventory.get((Player)event.getPlayer(), "oninteract", block);
				if(lootInventory!=null){
					Main.info("opening LootInventory from Cache");
					event.getPlayer().openInventory(lootInventory.inventory);
				}
				else{
					MmoWorldInstance worldInstance = MmoWorld.getInstance(block);
					MmoResourceManager.processYamlResponse(event.getPlayer().getUniqueId(), "treasure.php", new String[]{
							"player="+event.getPlayer().getUniqueId().toString(),
							"mc_enum="+MmoBlock.getMaterialString(block, true),
							"action=oninteract",
							"x="+block.getX(),
							"y="+block.getY(),
							"z="+block.getZ(),
							"world="+worldInstance.system_name,
							"world_instance="+worldInstance.world.getName()
							});
				}
			}
		}
		MmoItemStack mmoItemStack = MmoItemStack.get(event.getItem());
		MmoEventManager.callEvent(new MmoActionEvent(MmoAction.valueOf(action.toString()), event.getPlayer().getUniqueId(), mmoItemStack, block));
		return;
	}
	
	/*@EventHandler
	private void onPlayerHunger(FoodLevelChangeEvent event){
		if(event.getEntityType()!=EntityType.PLAYER){
			return;
		}
		event.setCancelled(true);
	}*/
}
