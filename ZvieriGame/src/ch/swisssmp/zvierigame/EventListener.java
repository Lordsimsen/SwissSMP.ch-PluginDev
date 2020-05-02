package ch.swisssmp.zvierigame;

import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.npc.event.PlayerInteractNPCEvent;
import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.PlayerRenameItemEvent;
import ch.swisssmp.utils.SwissSMPler;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

//import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;

public class EventListener implements Listener{

	@EventHandler
	private void onPlayerResourepackUpdate(PlayerResourcePackUpdateEvent event) {
		event.addComponent("zvieri");
	}
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent e) {
		if(e.getItem() == null) {
			return;
		}
		if((e.getAction() != Action.RIGHT_CLICK_AIR) && (e.getAction() != Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		if(!e.getPlayer().hasPermission("zvieriarena.admin")) {
			return;
		}
		ZvieriArena arena = ZvieriArena.get(e.getItem());
		if(arena == null) {
			return;
		}
		arena.openEditor(e.getPlayer());
	}
	
	@EventHandler
	private void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		NPCInstance npc = NPCInstance.get(e.getRightClicked());
		if(npc == null) {
			return;
		}
		Player p = e.getPlayer();
		PlayerInventory inventory = p.getInventory();
		
		ItemStack itemStack = (e.getHand() == EquipmentSlot.HAND ? inventory.getItemInMainHand() : inventory.getItemInOffHand());
		if(itemStack == null) {
			return;
		}
		String arena_id = ItemUtil.getString(itemStack, "link_zvieriarena");
		if(arena_id == null) {
			return;
		}
		ZvieriArena arena = ZvieriArena.get(UUID.fromString(arena_id));
		if(arena == null) {
			return;
		}
		arena.setChef(npc);
		itemStack.setAmount(0);
		SwissSMPler.get(p).sendActionBar(ChatColor.GREEN + "Küchenchef zugewiesen");
	}

	@EventHandler
	private void onPlayerInteractNPC(PlayerInteractNPCEvent event) {
		if(event.getHand() != EquipmentSlot.HAND) {
			return;
		}
		JsonObject json = event.getNPC().getJsonData();
		if(json == null || !json.has("zvieriarena")) {
			return;
		}
		Player player = event.getPlayer();
		String arena_id = json.get("zvieriarena").getAsString();
		ZvieriArena arena = ZvieriArena.get(UUID.fromString(arena_id));
		if(arena == null || !arena.isSetupComplete()) {
			event.getPlayer().sendMessage(ZvieriGamePlugin.getPrefix() + ChatColor.GRAY + " Arena existiert nicht oder ist nicht fertig aufgesetzt");
			return;
		}
		if(!arena.isGamePreparing()){
			if(!arena.isGameRunning()) {
				LevelSelectionView.open(player, arena);
			} else{
				player.sendMessage(ZvieriGamePlugin.getPrefix() + " Es läuft bereits ein Spiel");
			}
		}
		if(arena.isGamePreparing()){
			if(!arena.isParticipant(player)){
				arena.getGame().join(player);
			} else {
				LevelSelectionView.open(player,arena);
			}
		}
	}

	@EventHandler
	private void onItemRename(PlayerRenameItemEvent e) {
		if(!e.getPlayer().hasPermission("zvierigame.admin")) {
			return;
		}
		ZvieriArena arena = ZvieriArena.get(e.getItemStack());
		if(arena == null) {
			return;
		}
		arena.setName(e.getNewName());
		e.setName(ChatColor.AQUA + arena.getName());
	}
	
	@EventHandler
	private void onWorldLoad(WorldLoadEvent e) {
		ZvieriArenen.load(e.getWorld());
	}
	
	@EventHandler
	private void onWorldUnload(WorldUnloadEvent e) {
		ZvieriArenen.unload(e.getWorld());
	}

}
