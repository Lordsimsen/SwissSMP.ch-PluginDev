package ch.swisssmp.zvierigame;

import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.npc.event.PlayerInteractNPCEvent;
import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.PlayerRenameItemEvent;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zvierigame.game.PreparationPhase;
import com.google.gson.JsonObject;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Lectern;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.*;

import java.util.List;
import java.util.UUID;

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
		ItemStack itemStack = e.getItem();
		if((e.getAction() != Action.RIGHT_CLICK_AIR) && (e.getAction() != Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		if(!e.getPlayer().hasPermission("zvieriarena.admin")) {
			return;
		}
		ZvieriArena arena = ZvieriArena.get(itemStack);
		if(arena != null){
			arena.openEditor(e.getPlayer());
			return;
		}
		//Storagechest procedure
		String arena_id = ItemUtil.getString(itemStack, "link_zvieriarena");
		if(arena_id == null) {
			return;
		}
		arena = ZvieriArena.get(UUID.fromString(arena_id));
		if(arena == null) {
			return;
		}
		if(ItemUtil.getString(itemStack, "zvieritool").equalsIgnoreCase("chest")) {
			if(!(e.getClickedBlock().getState() instanceof Chest)) return;
			arena.setStorage(e.getClickedBlock());
			SwissSMPler.get(e.getPlayer()).sendActionBar(ChatColor.GREEN + "Lagerkiste  zugewiesen");
			e.setCancelled(true);
			itemStack.setAmount(0);
		} else if(ItemUtil.getString(itemStack, "zvieritool").equalsIgnoreCase("lectern")){
			if(!(e.getClickedBlock().getState() instanceof Lectern)) return;
			arena.setLectern(e.getClickedBlock());
			SwissSMPler.get(e.getPlayer()).sendActionBar(ChatColor.GREEN + "Highscores-Lesepult  zugewiesen");
			e.setCancelled(true);
			itemStack.setAmount(0);
		} else if(ItemUtil.getString(itemStack, "zvieritool").equalsIgnoreCase("jukebox")){
			if(!e.getClickedBlock().getType().equals(Material.JUKEBOX)) return;
			arena.setJukebox(e.getClickedBlock());
			SwissSMPler.get(e.getPlayer()).sendActionBar(ChatColor.GREEN + "Jukebox  zugewiesen");
			e.setCancelled(true);
			itemStack.setAmount(0);
		}
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
		if(ItemUtil.getString(itemStack, "npc").equalsIgnoreCase("chef")) {
			arena.setChef(npc);
			SwissSMPler.get(p).sendActionBar(ChatColor.GREEN + "Küchenchef zugewiesen");
		} else if(ItemUtil.getString(itemStack, "npc").equalsIgnoreCase("logistics")){
			arena.setLogisticsNPC(npc);
			SwissSMPler.get(p).sendActionBar(ChatColor.GREEN + "Logistiker zugewiesen");
		}
		itemStack.setAmount(0);
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
			if(arena.getGame() == null) {
				LevelSelectionView.open(player, arena);
				return;
			} else{
				player.sendMessage(ZvieriGamePlugin.getPrefix() + " Es läuft bereits ein Spiel");
				return;
			}
		}
		if(arena.getGame().getCurrentPhase() instanceof PreparationPhase){
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
	private void onPlayerOpenLectern(InventoryOpenEvent event){
		if(event.getView().getType() != InventoryType.LECTERN) return;
		LecternInventory lecternInventory = (LecternInventory) event.getInventory();
		ItemStack itemStack = lecternInventory.getItem(0);
		ZvieriArena zvieriArena = null;
		for(ZvieriArena arena : ZvieriArenen.get(event.getPlayer().getWorld())) {
			Lectern lectern = arena.getLectern();
			if(lectern == null) continue;
			if (!lectern.getLocation().equals(event.getInventory().getLocation())) continue;
			zvieriArena = arena;
		}
		if(zvieriArena == null) return;
		if(itemStack == null || itemStack.getType() != Material.WRITTEN_BOOK){
			itemStack = new ItemStack(Material.WRITTEN_BOOK);
		}
		zvieriArena.getHighscoreBook(itemStack);
		lecternInventory.setItem(0, itemStack);
	}

	@EventHandler
	private void onBookStealAttempt(PlayerTakeLecternBookEvent event){ //could this be done with worldguard?
		Bukkit.getLogger().info("BookStealAttempt detected");
		for(ZvieriArena arena : ZvieriArenen.get(event.getPlayer().getWorld())){
			Bukkit.getLogger().info("Arena: " + arena.getName());
			Bukkit.getLogger().info("Lectern: " + arena.getLectern());
			Bukkit.getLogger().info("Event lectern: " + event.getLectern());
			if(!arena.getLectern().equals(event.getLectern())) continue;
			Bukkit.getLogger().info("Lectern found");
//			if(event.getPlayer().hasPermission("zvierigame.admin")) return;
			event.setCancelled(true);
		}
	}

	@EventHandler
	private void onSignPlace(SignChangeEvent event){
		Bukkit.getLogger().info("SignPlace Event");
		Player player = event.getPlayer();
		String[] lines = event.getLines();
		if(!lines[0].toLowerCase().equals("[zvierigame]")){
			return;
		}
		if(!player.hasPermission("zvierigame.admin")){
			return;
		}

		String arenaString = lines[1];
		if(ZvieriArenen.getAll().stream().noneMatch(a->a.getWorld()==player.getWorld() && arenaString.equalsIgnoreCase(a.getName()))){
			event.setCancelled(true);
			return;
		}
		int level = Integer.parseInt(lines[2]);
		ZvieriArena arena = ZvieriArena.get(arenaString, true);
		event.setLine(0, "§4[Highscore Lvl " + level + "]");

		Bukkit.getLogger().info("Getting highscore");
		event.setLine(1, arena.getPlayerData().getHighscoreScore(level) + "");

		Bukkit.getLogger().info("Getting players");
		List<String> players = arena.getPlayerData().getHighscorePlayers(level);
		Bukkit.getLogger().info(players.toString());
		switch(players.size()){
			case 1: {
				event.setLine(2, players.get(0));
				break;
			}
			case 2: {
				event.setLine(2, players.get(0));
				event.setLine(3, players.get(1));
				break;
			}
			case 3: {
				event.setLine(2, players.get(0));
				event.setLine(3, players.get(1) + ", " + players.get(2));
				break;
			}
			case 4: {
				event.setLine(2, players.get(0) + ", " + players.get(1));
				event.setLine(3, players.get(2) + ", " + players.get(3));
				break;
			}
			default: return;
		}
	}

    @EventHandler
    private void onArenaExit(RegionLeaveEvent event){
		ZvieriArena arena = ZvieriArena.get(event.getRegion().getId());
		if(arena == null) return;
		ZvieriGame.cleanseInventory(event.getPlayer().getInventory());
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
