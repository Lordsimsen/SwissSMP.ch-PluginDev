package ch.swisssmp.knightstournament;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import net.querz.nbt.tag.CompoundTag;
import org.bukkit.*;
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
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.*;

import ch.swisssmp.customitems.CreateCustomItemBuilderEvent;
import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.SwissSMPler;
import org.spigotmc.event.entity.EntityDismountEvent;

public class EventListener implements Listener {

    @EventHandler
    private void onPlayerResourepackUpdate(PlayerResourcePackUpdateEvent event) {
        event.addComponent("knightstournament");
    }

    /**
     * Lance prepare crafting
     */
    @EventHandler
    private void onPrepareItemCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        ItemStack lance = null;
        List<LanceColor> colors = new ArrayList<LanceColor>();
        for (ItemStack itemStack : inventory) {
            if (itemStack == null) continue;
            if (TournamentLance.isLance(itemStack)) {
                if (lance != null) return;
                lance = itemStack;
                continue;
            }
            LanceColor color = LanceColor.of(itemStack.getType());
            if (color == null) return;
            colors.add(color);
        }
        if (lance == null || colors.size() != 2 || colors.get(0) == colors.get(1)) return;
        final String primaryColorKey = TournamentLance.primaryColorProperty;
        final String secondaryColorKey = TournamentLance.secondaryColorProperty;
        Collections.sort(colors);
        LanceColor main = colors.get(0);
        LanceColor secondary = colors.get(1);
        ItemStack result = lance.clone();
        CompoundTag nbt = ItemUtil.getData(result);
        CompoundTag lanceNbt = nbt!=null ? nbt.getCompoundTag(TournamentLance.dataProperty) : null;
        if(lanceNbt==null) return;
        final String lanceColorNone = LanceColor.NONE.toString();
        if (lanceNbt.containsKey(primaryColorKey) && !lanceNbt.getString(primaryColorKey).equalsIgnoreCase(lanceColorNone))
            return;
        if (lanceNbt.containsKey(secondaryColorKey) && !lanceNbt.getString(secondaryColorKey).equalsIgnoreCase(lanceColorNone))
            return;
        lanceNbt.putString(primaryColorKey, main.toString());
        lanceNbt.putString(secondaryColorKey, secondary.toString());
        nbt.put(TournamentLance.dataProperty, lanceNbt);
        ItemUtil.setData(result, nbt);
        String customEnum = (main + "_" + secondary + "_" + TournamentLance.customBaseEnum).toUpperCase();
        CustomItems.setCustomEnum(result, customEnum);
        CustomItemBuilder customitemBuilder = CustomItems.getCustomItemBuilder(customEnum);
        if (customitemBuilder != null) {
            int customModelId = customitemBuilder.getCustomModelId();
            ItemUtil.setInt(result, "CustomModelData", customModelId);
        }
        inventory.setResult(result);
    }

    /**
     * Lance perform crafting
     */
    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        Inventory inventory = event.getClickedInventory();

        // ignore non crafting views
        if (!(inventory instanceof CraftingInventory)) {
            return;
        }

        CraftingInventory craftingInventory = (CraftingInventory) inventory;
        // ignore clicks outside of result slot
        if (event.getSlot() != 0) {
            return;
        }
        ItemStack result = craftingInventory.getResult();
        // ignore results that aren't lances
        if (!TournamentLance.isLance(result)) {
            return;
        }

        // clone the result to make sure the stack is not modified afterwards
        ItemStack lance = result.clone();
        // cancel the event to prevent vanilla interaction
        event.setCancelled(true);

        // handle shift click
        if (event.getClick().isShiftClick()) {
            // do nothing if there is no space in the bottom inventory
            if (view.getBottomInventory().firstEmpty() < 0) return;
        }
        // cannot pick anything with a cursor that already contains something
        else if ((view.getCursor() != null && view.getCursor().getType() != Material.AIR)) {
            //Bukkit.getLogger().info("inventoryclickFIVE");
            return;
        }

        // check what dyes are needed
        CompoundTag nbt = ItemUtil.getData(result);
        CompoundTag lanceNbt = nbt.getCompoundTag(TournamentLance.dataProperty); // no need for null check, data is validated further up
        LanceColor primary = LanceColor.of(lanceNbt.getString(TournamentLance.primaryColorProperty));
        LanceColor secondary = LanceColor.of(lanceNbt.getString(TournamentLance.secondaryColorProperty));

        // find all three stacks, cancel if anything else is found
        ItemStack primaryDyeStack = null;
        ItemStack secondaryDyeStack = null;
        ItemStack baseLanceStack = null;

        for (ItemStack itemStack : craftingInventory.getMatrix()) {
            if (itemStack == null) continue;
            LanceColor color = LanceColor.of(itemStack.getType());
            if (color == null){
                // must be a lance and the first one to be found, otherwise cancel
                if(baseLanceStack!=null || !TournamentLance.isLance(itemStack)) return;
                baseLanceStack = itemStack;
                continue;
            }
            if(color==primary){
                // cancel if primary dye has already been found
                if(primaryDyeStack!=null) return;
                primaryDyeStack = itemStack;
            }
            else if(color==secondary){
                // cancel if secondary dye has already been found
                if(secondaryDyeStack!=null) return;
                secondaryDyeStack = itemStack;
            }
            else{
                // invalid dye
                return;
            }
        }

        // cancel if any of the stacks has not been found
        if(baseLanceStack==null || primaryDyeStack==null || secondaryDyeStack==null){
            return;
        }

        // subtract the consumed items
        int consumed = result.getAmount();
        baseLanceStack.setAmount(baseLanceStack.getAmount()-consumed);
        primaryDyeStack.setAmount(primaryDyeStack.getAmount()-consumed);
        secondaryDyeStack.setAmount(secondaryDyeStack.getAmount()-consumed);

        // clear the result slot, the result is added to the cursor or the bottom inventory directly further down
        craftingInventory.setResult(null);

        // trigger another prepare item craft event to allow crafting more lances afterwards
        Bukkit.getScheduler().runTaskLater(KnightsTournamentPlugin.getInstance(), ()->{
            PrepareItemCraftEvent prepareCraftEvent = new PrepareItemCraftEvent(craftingInventory, event.getView(), false);
            Bukkit.getPluginManager().callEvent(prepareCraftEvent);
        }, 1L);

        // add the lance directly to the inventory on shift click
        if (event.getClick().isShiftClick()) {
            view.getBottomInventory().addItem(lance);
            return;
        }

        // add the lance to the cursor, must be later because Bukkit does not like cursor modification in the click event
        Bukkit.getScheduler().runTaskLater(KnightsTournamentPlugin.getInstance(), () -> {
            view.setCursor(lance);
        }, 1L);
    }

    /**
     * Arena signs
     */
    @EventHandler(ignoreCancelled = true)
    private void onSignPlace(SignChangeEvent event) {
        Player player = event.getPlayer();
        String[] lines = event.getLines();
        if (!lines[0].toLowerCase().equals("[ritterspiele]")) {
            return;
        }
        if (!player.hasPermission("knightstournament.admin")) {
            return;
        }

        String arena = lines[1];
        if (KnightsArena.getLoadedArenas().stream().noneMatch(a -> a.getWorld() == player.getWorld() && arena.equalsIgnoreCase(a.getName()))) {
            event.setCancelled(true);
            return;
        }
        event.setLine(0, "§4Ritterspiele");
    }

    /**
     * Stop charges on dismount
     */
    @EventHandler
    private void onPlayerDismount(EntityDismountEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) return;
        Entity vehicle = event.getDismounted();
        Player player = (Player) entity;
        if (vehicle instanceof AbstractHorse && LoanerEquipment.isLoaner((AbstractHorse)vehicle)) {
            LoanerData loaner = LoanerData.load(player).orElse(null);
            if (loaner != null) {
                loaner.apply(player);
                loaner.delete();
            }
            vehicle.remove();
        }
        LanceCharge charge = LanceCharge.get(entity.getUniqueId()).orElse(null);
        if (charge != null) {
            charge.cancel();
        }
    }

    /**
     * Finish charge on click release
     */
    @EventHandler
    private void onPlayerShoot(EntityShootBowEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        ItemStack itemStack = event.getBow();
        if (!TournamentLance.isLance(itemStack)) return;
        event.setCancelled(true);
        LanceCharge charge = LanceCharge.get(event.getEntity().getUniqueId()).orElse(null);
        if (charge == null) return;
        charge.complete();
    }

    /**
     * Cancel charge because different item was selected
     */
    @EventHandler
    private void onPlayerChangeItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack previous = player.getInventory().getItem(event.getPreviousSlot());
        if (previous == null) return;
        LanceCharge charge = LanceCharge.get(player.getUniqueId()).orElse(null);
        if (charge == null) return;
        charge.cancel();
    }

    @EventHandler
    private void onPlayerSwapHands(PlayerSwapHandItemsEvent event){
        Player player = event.getPlayer();
        LanceCharge charge = LanceCharge.get(player.getUniqueId()).orElse(null);
        if (charge == null) return;
        event.setCancelled(true);
    }

    /**
     * Cancel charge because lance was dropped, prevent loaner from dropping items
     */
    @EventHandler
    private void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (LoanerEquipment.has(player)) {
            event.setCancelled(true);
        }
        LanceCharge charge = LanceCharge.get(player.getUniqueId()).orElse(null);
        if (charge != null){
            charge.cancel();
        }
    }

    @EventHandler
    private void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();
        if (LoanerEquipment.has(player)) {
            event.setCancelled(true);
            event.getItem().setPickupDelay(5);
        }
    }

    /**
     * Handle different interactions
     * Right click block: Interaction with arena signs and cauldrons
     * Right click air: Initiate lance charge
     * Right click air or right click block: Arena token interaction
     * Left click air: äuä (???)
     */
    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            onSignInteract(event);
            onCauldronInteract(event);
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            onItemUse(event);
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            onTokenInteract(event);
        }


        if (event.getAction() == Action.LEFT_CLICK_AIR) {
//			äuä
        }

    }

    /**
     * Wash colors off a lance
     */
    private void onCauldronInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (event.getClickedBlock().getType() != Material.CAULDRON) return;
        ItemStack itemStack = event.getItem();
        if (!TournamentLance.isLance(itemStack)) return;
        Levelled cauldron = (Levelled) event.getClickedBlock().getBlockData();
        if (cauldron.getLevel() <= 0) return;

        final String primaryColorKey = TournamentLance.primaryColorProperty;
        final String secondaryColorKey = TournamentLance.secondaryColorProperty;
        CompoundTag nbt = ItemUtil.getData(itemStack);
        CompoundTag lanceNbt = nbt.getCompoundTag(TournamentLance.dataProperty);
        final String lanceColorNone = LanceColor.NONE.toString();
        if (!lanceNbt.containsKey(primaryColorKey) || lanceNbt.getString(primaryColorKey).equalsIgnoreCase(lanceColorNone))
            return;
        if (!lanceNbt.containsKey(secondaryColorKey) || lanceNbt.getString(secondaryColorKey).equalsIgnoreCase(lanceColorNone))
            return;

        int cauldronLevel = cauldron.getLevel() - (event.getPlayer().getGameMode() != GameMode.CREATIVE ? 1 : 0);
        CauldronLevelChangeEvent cauldronEvent = new CauldronLevelChangeEvent(event.getClickedBlock(), event.getPlayer(),
                CauldronLevelChangeEvent.ChangeReason.UNKNOWN, cauldron.getLevel(), cauldronLevel);
        Bukkit.getPluginManager().callEvent(cauldronEvent);
        if (cauldronEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        lanceNbt.putString(primaryColorKey, lanceColorNone);
        lanceNbt.putString(secondaryColorKey, lanceColorNone);
        nbt.put(TournamentLance.dataProperty, lanceNbt);
        ItemUtil.setData(itemStack, nbt);
        String customEnum = (TournamentLance.bareCustomEnum).toUpperCase();
        CustomItems.setCustomEnum(itemStack, customEnum);
        CustomItemBuilder customitemBuilder = CustomItems.getCustomItemBuilder(customEnum);
        if (customitemBuilder != null) {
            int customModelId = customitemBuilder.getCustomModelId();
            ItemUtil.setInt(itemStack, "CustomModelData", customModelId);
        }

        cauldron.setLevel(cauldronLevel);
        event.getClickedBlock().setBlockData(cauldron);
    }

    /**
     * Initiate lance charge
     */
    private void onItemUse(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) return;
        LanceCharge existing = LanceCharge.get(player.getUniqueId()).orElse(null);
        if (existing != null) {
            if (existing.getHand() != event.getHand()) return;
            if (existing.getLance() == itemStack) {
                return; // Shouldn't happen as PlayerInteractEvent is only triggered once upon bow-draw
            }
            existing.cancel();
        }
        if (!TournamentLance.isLance(itemStack)) {
            return;
        }
        if (!player.hasPermission("knightstournament.lance.charge.anywhere")) {
            if (Tournament.get(player) == null) {
                event.setCancelled(true);
                return;
            }
        }
        // Bukkit.getLogger().info(event.getPlayer().getName() + ": Initiate lance charge");
        LanceCharge.initiate(player, event.getHand(), itemStack);
    }

    /**
     * Arena sign interaction
     */
    private void onSignInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        BlockState state = block.getState();
        if (!(state instanceof Sign)) return;
        Sign sign = (Sign) block.getState();
        if (!sign.getLine(0).equals("§4Ritterspiele")) {
            return;
        }
        String arenaName = sign.getLine(1);
        Optional<KnightsArena> arenaQuery = KnightsArena.get(block.getWorld(), arenaName);
        if (!arenaQuery.isPresent()) {
            SwissSMPler.get(event.getPlayer()).sendActionBar("§cArena aktuell inaktiv.");
            return;
        }
        KnightsArena arena = arenaQuery.get();
        Tournament tournament = arena.getTournament();
        if (sign.getLine(2).equals("Ausrüstung")) {
            LoanerData existing = LoanerData.load(event.getPlayer()).orElse(null);
            if (existing != null) {
                existing.apply(event.getPlayer());
                existing.delete();
            } else {
                LoanerEquipment.give(event.getPlayer());
            }

            return;
        }
        if (tournament == null) {
            if (sign.getLine(2).equals("Turnier öffnen")) {
                if (event.getPlayer().hasPermission("knightstournament.host")) {
                    if (!arena.isReady()) {
                        SwissSMPler.get(event.getPlayer()).sendActionBar("§cArena ist noch nicht fertig aufgesetzt.");
                        return;
                    }
                    Tournament.initialize(arena, event.getPlayer());
                } else {
                    SwissSMPler.get(event.getPlayer()).sendActionBar("§cKeine Berechtigung.");
                }
            } else {
                SwissSMPler.get(event.getPlayer()).sendActionBar("§cKein laufendes Turnier.");
                return;
            }
        }
        if (sign.getLine(2).equals("Teilnehmen") && event.getPlayer().hasPermission("knightstournament.participate")) {
            tournament.join(event.getPlayer());
        } else if (sign.getLine(2).equals("Verlassen")) {
            tournament.leave(event.getPlayer());
        } else if (sign.getLine(2).equals("Turnier starten")) {
            if (tournament.getMaster().getUniqueId() != event.getPlayer().getUniqueId() && !event.getPlayer().hasPermission("knightstournament.admin")) {
                SwissSMPler.get(event.getPlayer()).sendActionBar("§cKeine Berechtigung.");
                return;
            }
            if(tournament.isRunning()){
                SwissSMPler.get(event.getPlayer()).sendActionBar("§cDas Turnier läuft bereits.");
                return;
            }
            tournament.start();
        } else if (sign.getLine(2).equals("Beenden")) {
            if (tournament.getMaster().getUniqueId() != event.getPlayer().getUniqueId()) {
                SwissSMPler.get(event.getPlayer()).sendActionBar("Keine Berechtigung");
            }
            tournament.cancel();
        }
    }

    /**
     * Arena token interaction
     */
    private void onTokenInteract(PlayerInteractEvent e) {
        if (e.getItem() == null) {
            return;
        }
        if ((e.getAction() != Action.RIGHT_CLICK_AIR) && (e.getAction() != Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (!e.getPlayer().hasPermission("knightstournament.admin")) {
            return;
        }
        KnightsArena arena = KnightsArena.get(e.getItem());
        if (arena == null) {
            return;
        }
        arena.openEditor(e.getPlayer());
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        LoanerData.load(player).ifPresent((data)->{
            data.apply(player);
            data.delete();
        });
    }

    @EventHandler
    private void onPlayerInteractEntity(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        if(LoanerEquipment.has(player)){
            event.setCancelled(true);
        }
    }

    /**
     * Ensure all lances are marked as such
     */
    @EventHandler
    private void onItemBuilderCreate(CreateCustomItemBuilderEvent event) {
        JsonObject json = event.getJson();
        if (!json.has("tournament_lance")) return;
        JsonObject lanceSection = json.getAsJsonObject("tournament_lance");

        LanceColor primary = LanceColor.of(JsonUtil.getString("primary_color", lanceSection));
        LanceColor secondary = LanceColor.of(JsonUtil.getString("secondary_color", lanceSection));

        event.getCustomItemBuilder().addComponent((ItemStack itemStack) -> {
            CompoundTag nbt = ItemUtil.getData(itemStack);
            if (nbt == null) nbt = new CompoundTag();
            CompoundTag lanceNBT = new CompoundTag();
            lanceNBT.putString(TournamentLance.primaryColorProperty, primary != null ? primary.toString() : null);
            lanceNBT.putString(TournamentLance.secondaryColorProperty, secondary != null ? secondary.toString() : null);
            nbt.put(TournamentLance.dataProperty, lanceNBT);
            ItemUtil.setData(itemStack, nbt);
        });
    }

    /**
     * Update legacy lance items on inventory open and cancel lance charge
     */
    @EventHandler
    private void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        LanceCharge.get(player.getUniqueId()).ifPresent(LanceCharge::cancel);
        TournamentLance.updateLegacyLances(event.getInventory());
        if (LoanerEquipment.has(player)) {
            event.setCancelled(true);
        }
    }

    /**
     * Update legacy lance items on player join
     */
    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(KnightsTournamentPlugin.getInstance(), () -> {
            resetLoanerAndLanceCharge(event.getPlayer());
        }, 20L);

        TournamentLance.updateLegacyLances(event.getPlayer().getInventory());
    }

    /**
     * Remove loaner equipment
     */
    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        resetLoanerAndLanceCharge(event.getPlayer());
    }

    /**
     * Load arenas on world load
     */
    @EventHandler
    private void onWorldLoad(WorldLoadEvent event) {
        KnightsArena.load(event.getWorld());
    }

    @EventHandler
    private void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (!(entity instanceof AbstractHorse)) continue;
            if (!LoanerEquipment.isLoaner((AbstractHorse) entity))
                continue;
            if (entity.getPassengers().size() > 0) continue;
            entity.remove();
        }
    }

    /**
     * Unload arenas on world load
     */
    @EventHandler
    private void onWorldUnload(WorldUnloadEvent event) {
        KnightsArena.unload(event.getWorld());
    }

    @EventHandler
    private void onRegionExit(RegionLeaveEvent event){
        String regionId = event.getRegion().getId();
        KnightsArena arena = KnightsArena.getByRegion(regionId).orElse(null);
        if(arena==null) return;

        Player player = event.getPlayer();
        LoanerData loaner = LoanerData.load(player).orElse(null);
        if(loaner!=null){
            loaner.apply(player);
            loaner.delete();
            player.playSound(player.getLocation().add(0,2,0), "aoe.taunt.2", SoundCategory.VOICE, 2, 1);
        }
    }

    private void resetLoanerAndLanceCharge(Player player) {
        LanceChargerData chargerData = LanceChargerData.load(player).orElse(null);
        if (chargerData != null) {
            chargerData.apply(player);
            chargerData.delete();
        }
        LoanerData loanerData = LoanerData.load(player).orElse(null);
        if (loanerData != null) {
            loanerData.apply(player);
            loanerData.delete();
        }
    }
}
