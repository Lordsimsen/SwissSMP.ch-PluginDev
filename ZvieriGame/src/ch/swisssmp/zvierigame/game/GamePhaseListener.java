package ch.swisssmp.zvierigame.game;

import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.npc.event.PlayerInteractNPCEvent;
import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.zvierigame.ZvieriArena;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

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
    }

    @EventHandler
    private void onNPCInteract(PlayerInteractNPCEvent event){
        if(event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        NPCInstance npc = event.getNPC();
        JsonObject json = npc.getJsonData();
        if(json == null) return;
        if(json.has("zvieriarena")) {
            Player player = event.getPlayer();
            RestockView.open(this.arena, this.gamePhase, this.ingredients, player);
        }
        if(json.has("client")){
            ItemStack order = event.getNPC().getBase().getEquipment().getHelmet();
            ItemStack mainHand = event.getPlayer().getInventory().getItemInMainHand();
            ItemStack offHand = event.getPlayer().getInventory().getItemInOffHand();

            if(order == mainHand || order == offHand){
                //TODO transfer price, play villager- and katsching sound
//                gamePhase.addToScore(Integer.parseInt(order.getItemMeta().getLore().get(0))); //need to add them prices first
                gamePhase.addToScore(JsonUtil.getInt("tip", npc.getJsonData()));
                for(Counter counter : gamePhase.getCounters()){
                    if(counter.getClient().getNPCInstance() == npc) {
                        counter.reset();
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    private void onItemCraft(CraftItemEvent event){
        Player player = (Player) event.getWhoClicked();
        if(!participants.contains(player)) {
            event.setCancelled(true);
            return;
        }
        //TODO giv'em the correct output
    }

    @EventHandler
    private void onSmelting(FurnaceSmeltEvent event){
        //TODO giv'em the correct output
    }

    @EventHandler
    private void onBrewing(BrewEvent event){
        //TODO giv'em the correct output
    }
}
