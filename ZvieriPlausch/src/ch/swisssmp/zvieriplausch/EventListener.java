package ch.swisssmp.zvieriplausch;

import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.npc.event.PlayerInteractNPCEvent;
import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.PlayerRenameItemEvent;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zvieriplausch.game.PreparationPhase;
import com.google.gson.JsonObject;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Lectern;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
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


	/*
	Checks whether given Entity is a player playing a ZvieriGame and if so allows the event (if the item in question
	is a zvieriGameItem of course).
	 */
	@EventHandler
	private void onZvieriItemPickup(EntityPickupItemEvent event){
		ItemStack item = event.getItem().getItemStack();
		if(item.getType() == Material.AIR) return;
		if(!ItemUtil.getBoolean(item, "zvieriGameItem")) return;

		if(!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		for(ZvieriArena arena : ZvieriArenen.get(player.getWorld())){
			if(arena.getGame() == null || !arena.isGameRunning()) continue;
			if(arena.getGame().getParticipants().contains(player)) return;
		}
		event.setCancelled(true);
		event.getItem().setItemStack(null);
	}

	/*
	Checks whether opened Inventory contains zvieriGameItem and removes them if player isn't in a game.
	 */
	@EventHandler
	private void onInventoryOpen(InventoryOpenEvent event){
		Player player = (Player) event.getPlayer();
		for(ZvieriArena arena : ZvieriArenen.get(player.getWorld())){
			if(arena.getGame() == null || !arena.isGameRunning()) continue;
			if(arena.getGame().getParticipants().contains(player)) return;
		}
		Inventory inventory = event.getInventory();
		ItemStack[] contents = inventory.getContents();
		for(int i = 0; i < contents.length; i++){
			ItemStack item = contents[i];
			if(ItemUtil.getBoolean(item, "zvieriGameItem")) inventory.remove(item);
		}
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
			event.getPlayer().sendMessage(ZvieriGamePlugin.getPrefix() + ChatColor.GRAY + " Momentan nicht benützbar.");
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
	private void onBookStealAttempt(PlayerTakeLecternBookEvent event){
		for(ZvieriArena arena : ZvieriArenen.get(event.getPlayer().getWorld())){
			if(!arena.getLectern().equals(event.getLectern())) continue;
			if(event.getPlayer().hasPermission("zvierigame.admin")) return;
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	private void onSignPlace(SignChangeEvent event){
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

		event.setLine(1, arena.getPlayerDataContainer().getHighscoreScore(level) + "");

		List<String> players = arena.getPlayerDataContainer().getHighscorePlayers(level);
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
