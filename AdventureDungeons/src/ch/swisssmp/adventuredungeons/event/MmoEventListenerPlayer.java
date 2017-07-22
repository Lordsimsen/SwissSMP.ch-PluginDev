package ch.swisssmp.adventuredungeons.event;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.block.MmoBlock;
import ch.swisssmp.adventuredungeons.block.MmoBlockScheduler;
import ch.swisssmp.adventuredungeons.camp.CampEditor;
import ch.swisssmp.adventuredungeons.item.LootInventory;
import ch.swisssmp.adventuredungeons.player.MmoPlayer;
import ch.swisssmp.adventuredungeons.sound.MmoSound;
import ch.swisssmp.adventuredungeons.util.MmoResourceManager;
import ch.swisssmp.adventuredungeons.world.Dungeon;
import ch.swisssmp.adventuredungeons.world.DungeonInstance;
import ch.swisssmp.adventuredungeons.world.AdventureWorld;
import ch.swisssmp.adventuredungeons.world.AdventureWorldInstance;

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
		CampEditor editor = CampEditor.get(player);
		if(editor!=null) editor.quit();
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
			DungeonInstance dungeonInstance = Dungeon.getInstance(player);
			if(dungeonInstance==null) {
				if(player.getGameMode()==GameMode.ADVENTURE) player.setGameMode(GameMode.SURVIVAL);
				return;
			}
			if(dungeonInstance.getWorld().getName().equals(from.getWorld().getName())){
				Dungeon mmoDungeon = Dungeon.get(dungeonInstance);
				if(mmoDungeon!=null){
					mmoDungeon.leave(player.getUniqueId());
				}
			}
		}
	}
	@EventHandler(ignoreCancelled=true,priority=EventPriority.LOWEST)
	private void onPlayerRespawn(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode()!=GameMode.ADVENTURE) return;
		DungeonInstance dungeonInstance = Dungeon.getInstance(player);
		if(dungeonInstance==null) return;
		Location respawn = dungeonInstance.getRespawnLocation();
		if(respawn==null) return;
		event.setRespawnLocation(respawn);
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
				LootInventory lootInventory = LootInventory.get((Player)event.getPlayer(), "oninteract", block);
				if(lootInventory!=null){
					AdventureDungeons.info("opening LootInventory from Cache");
					event.getPlayer().openInventory(lootInventory.inventory);
				}
				else{
					AdventureWorldInstance worldInstance = AdventureWorld.getInstance(block);
					MmoResourceManager.processYamlResponse(event.getPlayer().getUniqueId(), "adventure/treasure.php", new String[]{
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
		        	Dungeon mmoDungeon = Dungeon.get(2);
		        	if(mmoDungeon==null) return;
		        	if(playerAction.equals("join")){
		        		mmoDungeon.join(player.getUniqueId(), null);
		        	}
		        	else if(playerAction.equals("leave")){
		        		DungeonInstance dungeonInstance = Dungeon.getInstance(event.getPlayer());
		        		if(dungeonInstance==null) return;
		        		mmoDungeon.leave(player.getUniqueId());
		        	}
		        }
			}
		}
		MmoEventManager.callEvent(new MmoActionEvent(MmoAction.valueOf(action.toString()), event.getPlayer().getUniqueId(), event.getItem(), block));
		return;
	}
}
