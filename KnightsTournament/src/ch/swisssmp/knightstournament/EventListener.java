package ch.swisssmp.knightstournament;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.*;

import ch.swisssmp.customitems.CreateCustomItemBuilderEvent;
import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.utils.nbt.NBTTagCompound;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import ch.swisssmp.webcore.RequestMethod;
import org.spigotmc.event.entity.EntityDismountEvent;

public class EventListener implements Listener {
	
	@EventHandler
	private void onPlayerResourepackUpdate(PlayerResourcePackUpdateEvent event) {
		event.addComponent("knightstournament");
	}

	@EventHandler
	private void onPrepareItemCraft(PrepareItemCraftEvent event){
		CraftingInventory inventory = event.getInventory();
		ItemStack lance = null;
		List<LanceColor> colors = new ArrayList<LanceColor>();
		for(ItemStack itemStack : inventory){
			if(itemStack==null) continue;
			if(TournamentLance.isLance(itemStack)) {
				if(lance!=null) return;
				lance = itemStack;
				continue;
			}
			LanceColor color = LanceColor.of(itemStack.getType());
			if(color==null) return;
			colors.add(color);
		}
		if(lance==null || colors.size()!=2 || colors.get(0) == colors.get(1)) return;
		final String primaryColorKey = TournamentLance.primaryColorProperty;
		final String secondaryColorKey = TournamentLance.secondaryColorProperty;
		Collections.sort(colors);
		LanceColor main = colors.get(0);
		LanceColor secondary = colors.get(1);
		ItemStack result = lance.clone();
		NBTTagCompound nbt = ItemUtil.getData(result);
		NBTTagCompound lanceNbt = nbt.getCompound(TournamentLance.dataProperty);
		final String lanceColorNone = LanceColor.NONE.toString();
		if(lanceNbt.hasKey(primaryColorKey) && !lanceNbt.getString(primaryColorKey).equalsIgnoreCase(lanceColorNone)) return;
		if(lanceNbt.hasKey(secondaryColorKey) && !lanceNbt.getString(secondaryColorKey).equalsIgnoreCase(lanceColorNone)) return;
		lanceNbt.setString(primaryColorKey, main.toString());
		lanceNbt.setString(secondaryColorKey, secondary.toString());
		nbt.set(TournamentLance.dataProperty, lanceNbt);
		ItemUtil.setData(result, nbt);
		String customEnum = (main + "_" + secondary + "_" + TournamentLance.customBaseEnum).toUpperCase();
		CustomItems.setCustomEnum(result, customEnum);
		CustomItemBuilder customitemBuilder = CustomItems.getCustomItemBuilder(customEnum);
		if(customitemBuilder!=null) {
			int customModelId = customitemBuilder.getCustomModelId();
			ItemUtil.setInt(result, "CustomModelData", customModelId);
		}
		inventory.setResult(result);
	}

	@EventHandler
	private void onInventoryClick(InventoryClickEvent event){
		InventoryView view = event.getView();
		Inventory inventory = event.getClickedInventory();
		if(!(inventory instanceof CraftingInventory)) {
			//Bukkit.getLogger().info("inventoryclickTWO");
			return;
		}
		CraftingInventory craftingInventory = (CraftingInventory) inventory;
		if(event.getSlot()!=0) {
			//Bukkit.getLogger().info("inventoryclickTHREE");
			return;
		}
		ItemStack result = craftingInventory.getResult();
		if(!TournamentLance.isLance(result)) {
			//Bukkit.getLogger().info("inventoryclickFOUR");
			return;
		}
		ItemStack lance = result.clone();
		event.setCancelled(true);
		if(event.getClick().isShiftClick()){
			//Bukkit.getLogger().info("IsShiftClick");
			if(view.getBottomInventory().firstEmpty()<0) return;
		} else if((view.getCursor()!=null && view.getCursor().getType()!=Material.AIR)) {
			//Bukkit.getLogger().info("inventoryclickFIVE");
			return;
		}
		//Bukkit.getLogger().info("inventoryclickPOSITIVE");
		for(ItemStack itemStack : craftingInventory.getMatrix()){
			if(itemStack==null) continue;
			LanceColor color = LanceColor.of(itemStack.getType());
			if(color!=null){
				itemStack.setAmount(itemStack.getAmount()-1);
				continue;
			}
			itemStack.setAmount(0);
		}
		craftingInventory.setResult(null);
		if(event.getClick().isShiftClick()){
			view.getBottomInventory().addItem(lance);
			return;
		}
		Bukkit.getScheduler().runTaskLater(KnightsTournamentPlugin.getInstance(), ()->{
			view.setCursor(lance);
		}, 1L);
	}

	@EventHandler(ignoreCancelled=true)
	private void onSignPlace(SignChangeEvent event){
		Player player = event.getPlayer();
		String[] lines = event.getLines();
		if(!lines[0].toLowerCase().equals("[ritterspiele]")){
			return;
		}
		if(!player.hasPermission("knightstournament.admin")){
			return;
		}

		String arena = lines[1];
		if(KnightsArena.getLoadedArenas().stream().noneMatch(a->a.getWorld()==player.getWorld() && arena.equalsIgnoreCase(a.getName()))){
			event.setCancelled(true);
			return;
		}
		event.setLine(0, "§4Ritterspiele");
	}

	@EventHandler
	private void onPlayerDismount(EntityDismountEvent event){
		Entity entity = event.getEntity();
		if(!(entity instanceof Player)) return;
		Entity vehicle = event.getDismounted();
		if(!(vehicle instanceof AbstractHorse)) return;
		LanceCharge charge = LanceCharge.get(entity.getUniqueId()).orElse(null);
		if(charge==null) return;
		charge.cancel();
	}

	@EventHandler
	private void onPlayerShoot(EntityShootBowEvent event){
		if(event.getEntityType()!= EntityType.PLAYER) return;
		ItemStack itemStack = event.getBow();
		if(!TournamentLance.isLance(itemStack)) return;
		event.setCancelled(true);
		LanceCharge charge = LanceCharge.get(event.getEntity().getUniqueId()).orElse(null);
		if(charge==null)return;
		charge.complete();
	}

	@EventHandler
	private void onPlayerChangeItem(PlayerItemHeldEvent event){
		Player player = event.getPlayer();
		ItemStack previous = player.getInventory().getItem(event.getPreviousSlot());
		if(previous==null) return;
		LanceCharge charge = LanceCharge.get(player.getUniqueId()).orElse(null);
		if(charge==null)return;
		charge.cancel();
	}

	@EventHandler
	private void onItemDrop(PlayerDropItemEvent event){
		Player player = event.getPlayer();
		LanceCharge charge = LanceCharge.get(player.getUniqueId()).orElse(null);
		if(charge==null)return;
		charge.cancel();
	}

	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK) {
			onSignInteract(event);
			onCauldronInteract(event);
		}
		if(event.getAction()==Action.RIGHT_CLICK_AIR) {
			onItemUse(event);
		}
		if(event.getAction()==Action.RIGHT_CLICK_AIR || event.getAction()==Action.RIGHT_CLICK_BLOCK) {
			onTokenInteract(event);
		}


		if(event.getAction()==Action.LEFT_CLICK_AIR){
//			äuä
		}

	}

	private void onCauldronInteract(PlayerInteractEvent event){
		if(event.getItem()==null) return;
		if(event.getClickedBlock().getType()!= Material.CAULDRON) return;
		ItemStack itemStack = event.getItem();
		if(!TournamentLance.isLance(itemStack)) return;
		Levelled cauldron = (Levelled) event.getClickedBlock().getBlockData();
		if(cauldron.getLevel()<=0) return;

		final String primaryColorKey = TournamentLance.primaryColorProperty;
		final String secondaryColorKey = TournamentLance.secondaryColorProperty;
		NBTTagCompound nbt = ItemUtil.getData(itemStack);
		NBTTagCompound lanceNbt = nbt.getCompound(TournamentLance.dataProperty);
		final String lanceColorNone = LanceColor.NONE.toString();
		if(!lanceNbt.hasKey(primaryColorKey) || lanceNbt.getString(primaryColorKey).equalsIgnoreCase(lanceColorNone)) return;
		if(!lanceNbt.hasKey(secondaryColorKey) || lanceNbt.getString(secondaryColorKey).equalsIgnoreCase(lanceColorNone)) return;

		int cauldronLevel = cauldron.getLevel()-(event.getPlayer().getGameMode()!=GameMode.CREATIVE ? 1 : 0);
		CauldronLevelChangeEvent cauldronEvent = new CauldronLevelChangeEvent(event.getClickedBlock(), event.getPlayer(),
				CauldronLevelChangeEvent.ChangeReason.UNKNOWN, cauldron.getLevel(), cauldronLevel);
		Bukkit.getPluginManager().callEvent(cauldronEvent);
		if(cauldronEvent.isCancelled()) {
			event.setCancelled(true);
			return;
		}
		lanceNbt.setString(primaryColorKey, lanceColorNone);
		lanceNbt.setString(secondaryColorKey, lanceColorNone);
		nbt.set(TournamentLance.dataProperty, lanceNbt);
		ItemUtil.setData(itemStack, nbt);
		String customEnum = (TournamentLance.bareCustomEnum).toUpperCase();
		CustomItems.setCustomEnum(itemStack, customEnum);
		CustomItemBuilder customitemBuilder = CustomItems.getCustomItemBuilder(customEnum);
		if(customitemBuilder!=null) {
			int customModelId = customitemBuilder.getCustomModelId();
			ItemUtil.setInt(itemStack, "CustomModelData", customModelId);
		}

		cauldron.setLevel(cauldronLevel);
		event.getClickedBlock().setBlockData(cauldron);
	}

	private void onItemUse(PlayerInteractEvent event){
		ItemStack itemStack = event.getItem();
		if(itemStack==null) return;
		Player player = event.getPlayer();
		if(player.getGameMode()== GameMode.SPECTATOR) return;
		LanceCharge existing = LanceCharge.get(player.getUniqueId()).orElse(null);
		if(existing!=null){
			if(existing.getHand()!=event.getHand()) return;
			if(existing.getLance()==itemStack) {
				return; // Shouldn't happen as PlayerInteractEvent is only triggered once upon bow-draw
			}
			existing.cancel();
		}
		if(!TournamentLance.isLance(itemStack)) return;

		if(!player.hasPermission("knightstournament.lance.charge.anywhere")) {
			if(Tournament.get(player)==null) {
				event.setCancelled(true);
				return;
			}
		}
		LanceCharge.initiate(player, event.getHand(), itemStack);
	}

	private void onSignInteract(PlayerInteractEvent event){
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		Block block = event.getClickedBlock();
		BlockState state = block.getState();
		if(!(state instanceof Sign)) return;
		Sign sign = (Sign)block.getState();
		if(!sign.getLine(0).equals("§4Ritterspiele")){
			return;
		}
		String arenaName = sign.getLine(1);
		Optional<KnightsArena> arenaQuery = KnightsArena.get(block.getWorld(), arenaName);
		if(!arenaQuery.isPresent()){
			SwissSMPler.get(event.getPlayer()).sendActionBar("§cArena aktuell inaktiv.");
			return;
		}
		KnightsArena arena = arenaQuery.get();
		Tournament tournament = arena.getTournament();
		if(tournament==null){
			if(sign.getLine(2).equals("Turnier öffnen")){
				if(event.getPlayer().hasPermission("knightstournament.host")){
					if(!arena.isReady()){
						SwissSMPler.get(event.getPlayer()).sendActionBar("§cArena ist noch nicht fertig aufgesetzt.");
						return;
					}
					Tournament.initialize(arena, event.getPlayer());
				}
				else{
					SwissSMPler.get(event.getPlayer()).sendActionBar("§cKeine Berechtigung.");
				}
			}
			else{
				SwissSMPler.get(event.getPlayer()).sendActionBar("§cKein laufendes Turnier.");
				return;
			}
		}
		if(sign.getLine(2).equals("Teilnehmen") && event.getPlayer().hasPermission("knightstournament.participate")){
			tournament.join(event.getPlayer());
		}
		else if(sign.getLine(2).equals("Verlassen")){
			tournament.leave(event.getPlayer());
		}
		else if(sign.getLine(2).equals("Ausrüstung")){
			LoanerEquipment.give(event.getPlayer());
		}
		else if(sign.getLine(2).equals("Turnier starten")){
			if(tournament.getMaster().getUniqueId()!=event.getPlayer().getUniqueId() && !event.getPlayer().hasPermission("knightstournament.admin")){
				SwissSMPler.get(event.getPlayer()).sendActionBar("§cKeine Berechtigung.");
				return;
			}
			tournament.start();
		}
	}

	private void onTokenInteract(PlayerInteractEvent e) {
		if(e.getItem() == null) {
			return;
		}
		if((e.getAction() != Action.RIGHT_CLICK_AIR) && (e.getAction() != Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		if(!e.getPlayer().hasPermission("knightstournament.admin")) {
			return;
		}
		KnightsArena arena = KnightsArena.get(e.getItem());
		if(arena == null) {
			return;
		}
		arena.openEditor(e.getPlayer());
	}
	
	@EventHandler
	private void onItemBuilderCreate(CreateCustomItemBuilderEvent event) {
		ConfigurationSection dataSection = event.getConfigurationSection();
		if(!dataSection.contains("tournament_lance")) return;
		ConfigurationSection lanceSection = dataSection.getConfigurationSection("tournament_lance");
		
		LanceColor primary = LanceColor.of(lanceSection.getString("primary_color"));
		LanceColor secondary = LanceColor.of(lanceSection.getString("secondary_color"));		
				
		event.getCustomItemBuilder().addComponent((ItemStack itemStack) -> {
			NBTTagCompound nbt = ItemUtil.getData(itemStack);
			if(nbt==null) nbt = new NBTTagCompound();
			NBTTagCompound lanceNBT = new NBTTagCompound();
			lanceNBT.setString(TournamentLance.primaryColorProperty, primary != null ? primary.toString() : null);
			lanceNBT.setString(TournamentLance.secondaryColorProperty, secondary != null ? secondary.toString() : null);
			nbt.set(TournamentLance.dataProperty, lanceNBT);
			ItemUtil.setData(itemStack, nbt);
		});
	}

	@EventHandler
	private void onInventoryOpen(InventoryOpenEvent event){
		Player player = (Player) event.getPlayer();
		LanceCharge charge = LanceCharge.get(player.getUniqueId()).orElse(null);
		if(charge!=null) charge.cancel();
		TournamentLance.updateLegacyLances(event.getInventory());
	}

	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event){
		LoanerEquipment loaner = LoanerEquipment.get(event.getPlayer());
		if(loaner!=null){
			loaner.remove();
		}
		TournamentLance.updateLegacyLances(event.getPlayer().getInventory());
	}

	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		LoanerEquipment loaner = LoanerEquipment.get(event.getPlayer());
		if(loaner!=null){
			loaner.remove();
		}
	}
	
	@EventHandler
	private void onWorldLoad(WorldLoadEvent event) {
		KnightsArena.load(event.getWorld());
	}
	
	@EventHandler
	private void onWorldUnload(WorldUnloadEvent event) {
		KnightsArena.unload(event.getWorld());
	}

}
