package ch.swisssmp.crafting.brewing;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

/**
 * Called when an ItemStack is successfully brewed in a brewing stand.
 */
public class BrewingStandBrewEvent extends Event implements Cancellable {
    private final static HandlerList handlers = new HandlerList();

    private final BrewerInventory inventory;
    private final ItemStack ingredient;
    private final ItemStack result;
    private final int slot;

    private boolean cancelled = false;

    public BrewingStandBrewEvent(BrewerInventory inventory, ItemStack ingredient, ItemStack result, int slot){
        this.inventory = inventory;
        this.ingredient = ingredient;
        this.result = result;
        this.slot = slot;
    }

    /**
     * @return Das betroffene Inventar.
     */
    public BrewerInventory getInventory(){
        return inventory;
    }

    /**
     * @return Die Zutat, welche für dieses Rezept verwendet worden ist.
     */
    public ItemStack getIngredient(){
        return ingredient;
    }

    /**
     * @return Der resultierende ItemStack. Modifiziere diesen, um dein Rezept anzuwenden
     */
    public ItemStack getResult(){
        return result;
    }

    /**
     * @return Der Slot, in welchem sich das Resultat befindet.
     */
    public int getSlot(){
        return slot;
    }

    @Override
    public boolean isCancelled(){
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled){
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }
}
