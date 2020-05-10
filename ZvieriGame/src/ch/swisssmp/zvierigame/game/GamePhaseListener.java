package ch.swisssmp.zvierigame.game;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.npc.event.PlayerInteractNPCEvent;
import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zvierigame.ZvieriArena;
import ch.swisssmp.zvierigame.ZvieriGamePlugin;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.*;

import java.util.List;

public class GamePhaseListener implements Listener {

    private final GamePhase gamePhase;
    private final ZvieriArena arena;
    private final ItemStack[] ingredients;

    private List<Player> participants;

    public GamePhaseListener(GamePhase gamePhase){
        this.gamePhase = gamePhase;
        arena = gamePhase.getArena();
        ingredients = gamePhase.getLevel().getIngredients();
        participants = gamePhase.getGame().getParticipants();
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event){
        if(!arena.getGame().getParticipants().contains(event.getPlayer())) return;
        arena.getGame().getParticipants().remove(event.getPlayer());
        if(arena.getGame().getParticipants().size() == 0) arena.getGame().cancel();
    }


    @EventHandler (priority = EventPriority.HIGHEST)
    private void onNPCInteract(PlayerInteractNPCEvent event){
        if(event.getHand() != EquipmentSlot.HAND) {
            Bukkit.getLogger().info("hand != eqslot hand");
            return;
        }
        NPCInstance npc = event.getNPC();
        JsonObject json = npc.getJsonData();
        if(json == null) {
            return;
        }
        if(npc.getIdentifier().equalsIgnoreCase("logistics")) {
            Player player = event.getPlayer();
            RestockView.open(this.arena, this.gamePhase, this.ingredients, player);
        }
        for(Counter counter : gamePhase.getCounters()) {
            // find counter/client first, then apply the rest.. odr?

            if(!counter.isOccupied()) continue;
            if(!counter.getClient().getNPCInstance().getIdentifier().equalsIgnoreCase(event.getNPC().getIdentifier())) continue;
            Client client = counter.getClient();
            ArmorStand dishCarrier = (ArmorStand) event.getNPC().getEntity().getPassengers().get(0);
            if (dishCarrier == null) continue;
            ItemStack order = dishCarrier.getEquipment().getHelmet();
            if(order == null) continue;
            ItemStack mainHand = event.getPlayer().getInventory().getItemInMainHand();
            if(mainHand == null) continue;
            if (!order.isSimilar(mainHand)) continue;
            int tip = client.getTip();
            Bukkit.getLogger().info("Trinkgeld: " + tip);
//            int price = order.getItemMeta().getLore().isEmpty() ? 0 : Integer.parseInt(order.getItemMeta().getLore().get(0));
            gamePhase.addToScore(10 + tip);
            SwissSMPler.get(event.getPlayer()).sendActionBar((10 + tip) + " Smaragdmuenzen erhalten");
            counter.reset();
            mainHand.setAmount(mainHand.getAmount()-1);
            gamePhase.displayScore();
            break;
        }
    }

//    @EventHandler
//    private void onArenaEnter(RegionEnterEvent event){
//        //join if game is preparing
//    }
//
//    @EventHandler
//    private void onArenaExit(RegionExitEvent event){
//        //delete all zvieri items in inventory
//    }

    @EventHandler
    private void onItemDrop(PlayerDropItemEvent event){
        if(!participants.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
//        ItemStack[] blackList = gamePhase.getLevel().getIngredients();
//        for(int i = 0; i < blackList.length; i++){
//            if(event.getItemDrop().getItemStack().isSimilar(blackList[i])){
//                event.setCancelled(true);
//            }
//        }
    }

    @EventHandler
    private void onItemPlace(BlockPlaceEvent event){
        if(participants.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onBrewingIngredientPlace(InventoryClickEvent event){
        Inventory inventory = event.getClickedInventory();
        if(inventory == null) return;
        if(inventory.getType() != InventoryType.BREWING) return;
        if(event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT) return;
        if(event.isShiftClick()) return;
        if(event.getSlot() != 3) return;
        ItemStack current = event.getCurrentItem();
        if(event.getCursor() == null || event.getCursor().getType() == Material.AIR) return;
        InventoryView view = event.getView();
        ItemStack itemStack = event.getCursor();
        if(event.getClick() == ClickType.LEFT){
            if(current != null && current.isSimilar(itemStack)){
                current.setAmount(current.getAmount() + itemStack.getAmount());
            }
        }
        Bukkit.getScheduler().runTaskLater(ZvieriGamePlugin.getInstance(), () ->{
                view.setCursor(current);
                inventory.setItem(3, itemStack);
        }, 1L);
        ((Player) event.getView().getPlayer()).updateInventory();
    }

    @EventHandler
    public void brewingListener(InventoryClickEvent event){
        if(event.getClickedInventory() == null) return;
        if(event.getClickedInventory().getType() != InventoryType.BREWING) return;
        if(((BrewerInventory)event.getInventory()).getIngredient() == null) return;
        BrewingRecipe recipe = BrewingRecipe.getRecipe((BrewerInventory) event.getClickedInventory());
        if(recipe == null) return;
        recipe.startBrewing((BrewerInventory) event.getClickedInventory());
    }
}
