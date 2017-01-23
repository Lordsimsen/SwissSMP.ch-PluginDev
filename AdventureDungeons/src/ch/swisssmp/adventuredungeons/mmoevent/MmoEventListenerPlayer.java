package ch.swisssmp.adventuredungeons.mmoevent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.swisssmp.adventuredungeons.Main;
import ch.swisssmp.adventuredungeons.mmoblock.MmoBlock;
import ch.swisssmp.adventuredungeons.mmoblock.MmoBlockScheduler;
import ch.swisssmp.adventuredungeons.mmocamp.MmoCampEditor;
import ch.swisssmp.adventuredungeons.mmoitem.MmoLootInventory;
import ch.swisssmp.adventuredungeons.mmoplayer.MmoPlayer;
import ch.swisssmp.adventuredungeons.mmosound.MmoSound;
import ch.swisssmp.adventuredungeons.mmoworld.MmoDungeon;
import ch.swisssmp.adventuredungeons.mmoworld.MmoDungeonInstance;
import ch.swisssmp.adventuredungeons.mmoworld.MmoRegion;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorld;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorldInstance;
import ch.swisssmp.adventuredungeons.util.MmoResourceManager;

public class MmoEventListenerPlayer extends MmoEventListener{
	public MmoEventListenerPlayer(JavaPlugin plugin) {
		super(plugin);
	}
	@EventHandler
	private void onPlayerLogin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		MmoPlayer.login(player);
	}
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		MmoPlayer.unregisterResourcepack(player);
		MmoCampEditor editor = MmoCampEditor.get(player);
		if(editor!=null) editor.quit();
	}
	@EventHandler
	private void onPlayerResourcepackResponse(PlayerResourcePackStatusEvent event){
		Status status = event.getStatus();
		Player player = event.getPlayer();
		if(status== Status.DECLINED){
			player.sendMessage(ChatColor.RED+"Du hast das Server Resourcepack abgelehnt. Schade!");
			player.sendMessage(ChatColor.YELLOW+"Das Pack dient dazu, neue Inhalte wie Sounds und Items ins Spiel einzufügen und verändert nur sehr wenige bestehende Texturen");
			player.sendMessage(ChatColor.GREEN+"Mehr Infos zum Thema: https://swisssmp.ch/threads/server-resourcepack.8360/");
		}
		if(status==Status.FAILED_DOWNLOAD){
			player.sendMessage(ChatColor.RED+"Der Download des Server-Resourcepacks ist fehlgeschlagen, die Servertechniker wurden informiert.");
			Bukkit.getLogger().info("Failed downloading Resourcepack for "+player.getName()+"!");
			Bukkit.getLogger().info("Attempted Resourcepack: "+MmoPlayer.getResourcepack(player.getUniqueId()));
		}
	}
	@EventHandler(ignoreCancelled=true)
	private void onTeleport(PlayerTeleportEvent event){
		Location from = event.getFrom();
		Location to = event.getTo();
		if(from.getWorld()!=to.getWorld()){
			Player player = event.getPlayer();
			if(MmoSound.musicLoops.containsKey(player.getUniqueId())){
				MmoSound.musicLoops.get(player.getUniqueId()).cancel();
				MmoSound.musicLoops.remove(player.getUniqueId());
			}
			MmoPlayer.updateResourcepack(player);
			MmoDungeonInstance dungeonInstance = MmoDungeon.getInstance(player);
			if(dungeonInstance==null) {
				if(player.getGameMode()==GameMode.ADVENTURE) player.setGameMode(GameMode.SURVIVAL);
				return;
			}
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
		if(player.getGameMode()!=GameMode.ADVENTURE) return;
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
	private void onPlayerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		Action action = event.getAction();
		Block block = event.getClickedBlock();
		if(action==Action.RIGHT_CLICK_BLOCK){
			if(block.getType()==Material.REDSTONE_TORCH_ON){
				if(player.getGameMode()!=GameMode.ADVENTURE) return;
				ItemStack itemStack = event.getItem();
				if(itemStack!=null){
					if(itemStack.getType()==Material.FLINT_AND_STEEL){
						MmoBlock.set(block, new MaterialData(Material.TORCH), event.getPlayer().getUniqueId());
						MmoBlockScheduler.schedule(block, new MaterialData(Material.REDSTONE_TORCH_ON), 300, event.getPlayer().getUniqueId());
						return;
					}
				}
			}
			else if(block.getType()==Material.JUKEBOX){
				if(player.getGameMode()!=GameMode.ADVENTURE) return;
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
			else if(block.getType()==Material.WALL_SIGN || block.getType()==Material.SIGN_POST){
		        Sign sign = (Sign) block.getState();
		        String[] lines = sign.getLines();
		        if(!lines[0].equals("[Geisterhaus]")) {
		            return;
		    	}
		        else{
		        	String playerAction = lines[1];
		        	MmoDungeon mmoDungeon = MmoDungeon.get(2);
		        	if(mmoDungeon==null) return;
		        	if(playerAction.equals("join")){
		        		mmoDungeon.join(player.getUniqueId(), null);
		        	}
		        	else if(playerAction.equals("leave")){
		        		MmoDungeonInstance dungeonInstance = MmoDungeon.getInstance(event.getPlayer());
		        		if(dungeonInstance==null) return;
		        		mmoDungeon.leave(player.getUniqueId());
		        	}
		        }
			}
		}
		MmoEventManager.callEvent(new MmoActionEvent(MmoAction.valueOf(action.toString()), event.getPlayer().getUniqueId(), event.getItem(), block));
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
