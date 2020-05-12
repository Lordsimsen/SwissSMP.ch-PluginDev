package ch.swisssmp.zvierigame.game;

import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.npc.event.PlayerInteractNPCEvent;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zvierigame.ZvieriArena;
import ch.swisssmp.zvierigame.ZvieriGamePlugin;
import com.google.gson.JsonObject;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

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


    /*
    All items crafted by a participant are marked as zvieriGameItems and will be removed upon leaving/ending the game.
     */
    @EventHandler
    private void onZvieriItemCraft(PrepareItemCraftEvent event){
        if(event.getView() == null) return;
        if(event.getRecipe() == null) return;
        if(!gamePhase.getGame().getParticipants().contains(event.getView().getPlayer())) return;
        CraftingInventory inventory = (CraftingInventory) event.getView();
        ItemStack result = inventory.getResult();
        ItemUtil.setBoolean(result, "zvieriGameItem", true);
        ((CraftingInventory) event.getView()).setResult(result);
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
            if (!order.isSimilar(mainHand)) continue;
            int tip = client.getTip();
            ItemMeta orderMeta = order.getItemMeta();
            String dishName = orderMeta.getDisplayName().substring(2);
            int price = 0; // brauche custom enum durch "order"... oder muss durch alle keys in dishes und schauen ob name mit dishName übereinstimmt, dann price rausgeben.
            for(String key : dishesSection.getKeys(false)){
                if(!dishesSection.getString(key + ".name").equalsIgnoreCase(dishName)) continue;
                price = dishesSection.getInt(key + ".price");
                break;
            }
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
        if(participants.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
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
