package ch.swisssmp.crafting.brewing;

import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

public class BrewingResult {
    private final BrewerInventory inventory;
    private final ItemStack result;
    private final ItemStack ingredient;
    private final int slot;

    protected BrewingResult(BrewerInventory inventory, ItemStack ingredient, ItemStack result, int slot){
        this.inventory = inventory;
        this.result = result;
        this.ingredient = ingredient;
        this.slot = slot;
    }

    /**
     * @return Das betroffene Inventar.
     */
    public BrewerInventory getInventory(){
        return inventory;
    }

    /**
     * @return Der resultierende ItemStack. Modifiziere diesen, um dein Rezept anzuwenden
     */
    public ItemStack getResult(){
        return result;
    }

    /**
     * @return Die Zutat, welche für dieses Rezept verwendet worden ist.
     */
    public ItemStack getIngredient(){
        return ingredient;
    }

    /**
     * @return Der Slot, in welchem sich das Resultat befindet.
     */
    public int getSlot(){
        return slot;
    }
}
