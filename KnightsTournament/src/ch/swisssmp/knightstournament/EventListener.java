package ch.swisssmp.knightstournament;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import net.minecraft.server.v1_15_R1.ItemBow;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.HorseJumpEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Cauldron;
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
		CustomItemBuilder customitemBuilder = CustomItems.getCustomItemBuilder(customEnum);
		if(customitemBuilder!=null) {
			int customModelId = customitemBuilder.getCustomModelId();
			ItemUtil.setInt(result, "CustomModelData", customModelId);
		}
		inventory.setResult(result);
	}


	@EventHandler
	private void onCraft(PrepareItemCraftEvent event){
		ItemStack result = event.getInventory().getResult();
		if(result==null) return;
		if(!result.hasItemMeta()) return;
		if(!result.getItemMeta().hasDisplayName()) return;
		if(!result.getItemMeta().getDisplayName().equals("§bTurnierlanze")) return;
		if(!(event.getView().getPlayer() instanceof Player)) return;
		Player player = (Player) event.getView().getPlayer();
		if(!player.hasPermission("knightstournament.craft")){
			event.getInventory().setResult(null);
		}
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
		HTTPRequest request = DataSource.getResponse(KnightsTournamentPlugin.getInstance(), "sign.php", new String[]{
				"arena="+URLEncoder.encode(lines[1]),
				"action="+URLEncoder.encode(lines[2])
		}, RequestMethod.POST_SYNC);
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		try{
			yamlConfiguration.loadFromString(request.getResponse());
			if(yamlConfiguration.contains("message")){
				player.sendMessage(yamlConfiguration.getString("message"));
			}
			if(yamlConfiguration.contains("lines")){
				List<String> linesSection = yamlConfiguration.getStringList("lines");
				for(int i = 0; i < linesSection.size() && i < 4; i++){
					event.setLine(i,linesSection.get(i));
				}
			}
		}
		catch(Exception e){
			System.out.print(e);
		}
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

		CauldronLevelChangeEvent cauldronEvent = new CauldronLevelChangeEvent(event.getClickedBlock(), event.getPlayer(),
				CauldronLevelChangeEvent.ChangeReason.UNKNOWN, cauldron.getLevel(), cauldron.getLevel()-1);
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
		CustomItemBuilder customitemBuilder = CustomItems.getCustomItemBuilder(customEnum);
		if(customitemBuilder!=null) {
			int customModelId = customitemBuilder.getCustomModelId();
			ItemUtil.setInt(itemStack, "CustomModelData", customModelId);
		}

		cauldron.setLevel(cauldron.getLevel()-1);
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
		else if(sign.getLine(2).equals("Turnier starten")){
			if(tournament.getMaster().getUniqueId()!=event.getPlayer().getUniqueId()){
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
	private void onWorldLoad(WorldLoadEvent event) {
		KnightsArena.load(event.getWorld());
	}
	
	@EventHandler
	private void onWorldUnload(WorldUnloadEvent event) {
		KnightsArena.unload(event.getWorld());
	}
}
