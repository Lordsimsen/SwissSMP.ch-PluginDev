package ch.swisssmp.zvieriplausch.game;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.npc.event.PlayerInteractNPCEvent;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zvieriplausch.ZvieriArena;
import ch.swisssmp.zvieriplausch.ZvieriGamePlugin;
import com.google.gson.JsonObject;
import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.*;

import java.util.List;

public class GamePhaseListener implements Listener {

    private final GamePhase gamePhase;
    private final ZvieriArena arena;

    private List<Player> participants;

    public GamePhaseListener(GamePhase gamePhase){
        this.gamePhase = gamePhase;
        arena = gamePhase.getArena();
        participants = gamePhase.getGame().getParticipants();
    }

//    @EventHandler
//    private void onZvieriItemCraft(PrepareItemCraftEvent event){
//        if(event.getView() == null) return;
//        if(event.getRecipe() == null) return;
//        if(!gamePhase.getGame().getParticipants().contains(event.getView().getPlayer())) return;
//        CraftingInventory inventory = event.getInventory();
//        ItemStack result = inventory.getResult();
//        if(result == null) return;
//        ItemUtil.setBoolean(result, "zvieriGameItem", true);
//        (event.getInventory()).setResult(result);
//    }

    @EventHandler
    private void onZvieriItemCook(FurnaceSmeltEvent event){
        if(arena == null) return;
        if(arena.getGame() == null) return;
        Block furnace = event.getBlock();
        World world = arena.getWorld();
        ProtectedRegion region = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getRegion(arena.getArenaRegion());
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        for(int i = min.getBlockX(); i <= max.getBlockX(); i++){
            for(int j = min.getBlockY(); j <= max.getBlockY(); j++){
                for(int k = min.getBlockZ(); k <= max.getBlockZ(); k++){
                    Block block = arena.getWorld().getBlockAt(i, j, k);
                    if (block.equals(furnace)) {
                        ItemUtil.setBoolean(event.getResult(), "zvieriGameItem", true);
                    }
                }
            }
        }
    }

//    @EventHandler
//    private void onZvieriItemBrew(BrewEvent event){
//        if(arena == null) return;
//        if(arena.getGame() == null) return;
//        Block brewingStand = event.getBlock();
//        World world = arena.getWorld();
//        ProtectedRegion region = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getRegion(arena.getArenaRegion());
//        BlockVector3 min = region.getMinimumPoint();
//        BlockVector3 max = region.getMaximumPoint();
//        for(int i = min.getBlockX(); i <= max.getBlockX(); i++){
//            for(int j = min.getBlockY(); j <= max.getBlockY(); j++){
//                for(int k = min.getBlockZ(); k <= max.getBlockZ(); k++){
//                    Block block = arena.getWorld().getBlockAt(i, j, k);
//                    if (block.equals(brewingStand)) {
//                        if(arena.getGame().getParticipants().contains((event.getContents().getViewers()))){
//                            for(ItemStack item : event.getContents().getContents()){
//                                ItemUtil.setBoolean(item, "zvieriGameItem", true);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event){
        if(arena == null) return;
        if(arena.getGame() == null) return;
        if(!arena.getGame().getParticipants().contains(event.getPlayer())) return;
        arena.getGame().getParticipants().remove(event.getPlayer());
        if(arena.getGame().getParticipants().size() == 0) arena.getGame().cancel();
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    private void onNPCInteract(PlayerInteractNPCEvent event){
        if(arena == null) return;
        if(arena.getGame() == null) return;
        if(event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if(!arena.getGame().getParticipants().contains(event.getPlayer())) return;
        NPCInstance npc = event.getNPC();
        JsonObject json = npc.getJsonData();
        if(json == null) {
            return;
        }
        if(npc.getIdentifier().equalsIgnoreCase("logistics")) {
            Player player = event.getPlayer();
            RestockView.open(this.arena, this.gamePhase, player);
        }
        ConfigurationSection dishesSection = ZvieriGamePlugin.getInstance().getConfig().getConfigurationSection("dishes");
        for(Counter counter : gamePhase.getCounters()) {
            if(!counter.isOccupied()) continue;
            if(!counter.getClient().getNPCInstance().getIdentifier().equalsIgnoreCase(event.getNPC().getIdentifier())) continue;
            Client client = counter.getClient();
            ArmorStand dishCarrier = (ArmorStand) event.getNPC().getEntity().getPassengers().get(0);
            if (dishCarrier == null) continue;
            ItemStack order = dishCarrier.getEquipment().getHelmet();
            if(order == null) continue;
            ItemStack mainHand = event.getPlayer().getInventory().getItemInMainHand();
            if(mainHand == null) continue;
            if (!order.isSimilar(mainHand)) {
                arena.getWorld().playSound(npc.getEntity().getLocation(), Sound.ENTITY_VILLAGER_NO, 5f, 5f);
                continue;
            }
            int tip = client.getTip();
            String customEnum = CustomItems.getCustomEnum(order); //TODO t'es niqué
            int price = 0;
            for(String key : dishesSection.getKeys(false)){
                if(!key.equalsIgnoreCase(customEnum)) continue;
                price = dishesSection.getInt(key + ".price");
                break;
            }
            arena.getWorld().playSound(npc.getEntity().getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 5f, 5f);
            gamePhase.addToScore(price + tip);
            SwissSMPler.get(event.getPlayer()).sendActionBar(price + "(" + ChatColor.YELLOW +"+" + tip + ChatColor.RESET + ") Smaragdmuenzen erhalten");
            counter.reset();
            mainHand.setAmount(mainHand.getAmount()-1);
            gamePhase.displayScore();
            break;
        }
    }

    @EventHandler
    private void onItemPlace(BlockPlaceEvent event){
        if(arena == null) return;
        if(arena.getGame() == null) return;
        if(participants.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onArenaEnter(RegionEnterEvent event){
        ZvieriArena arena = ZvieriArena.get(event.getRegion().getId());
        if(arena == null) return;
        if(arena.getGame() == null) return;
        SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.RED + "Du kannst während einem laufenden Spiel nicht in die Lokalität!");
        event.setCancelled(true);
    }

    @EventHandler
    private void onArenaExit(RegionLeaveEvent event){
        ZvieriArena arena = ZvieriArena.get(event.getRegion().getId());
        if(arena == null) return;
        if(arena.getGame() == null) return;
        if(arena.getGame().getParticipants().contains(event.getPlayer())){
            arena.getGame().leave(event.getPlayer());
        }
    }
}
