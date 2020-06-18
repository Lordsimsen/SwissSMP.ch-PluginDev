package ch.swisssmp.crafting.brewing;

import ch.swisssmp.crafting.CustomRecipeAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class BrewingListener implements Listener {

    private static final boolean DEBUG_OUTPUT = false;

    private void debug(String s){
        if(!DEBUG_OUTPUT) return;
        Bukkit.getLogger().info(CustomRecipeAPI.getPrefix()+" "+s);
    }

    @EventHandler
    private void onBrewingIngredientPlace(InventoryClickEvent event){
        if(event.isShiftClick()) return;
        Inventory inventory = event.getClickedInventory();
        if(inventory == null || inventory.getType() != InventoryType.BREWING) return;
        if(event.getSlot() != 3) return;
        final ItemStack itemInSlot = event.getCurrentItem()!=null ? event.getCurrentItem() : new ItemStack(Material.AIR);
        final ItemStack itemInCursor = event.getCursor() != null ? event.getCursor() : new ItemStack(Material.AIR);

        debug("===== New brewing transaction =====");

        debug("Slot: "+itemInSlot.getType());
        debug("Cursor: "+itemInCursor.getType());

        TransferAction action;
        switch(event.getClick()){
            case LEFT:{
                if(!itemInSlot.isSimilar(itemInCursor)){
                    action = TransferAction.SWAP;
                }
                else{
                    action = TransferAction.PLACE_ALL;
                }
                break;
            }
            case RIGHT:{
                if(!itemInSlot.isSimilar(itemInCursor)){
                    action = TransferAction.SWAP;
                }
                else{
                    debug("Action: "+event.getClick()+", do nothing");
                    debug("===== End brewing transaction");
                    return;
                }
                break;
            }
            default:
                debug("Action: "+event.getClick()+", do nothing");
                debug("===== End brewing transaction =====");
                return;
        }

        Bukkit.getLogger().info("Action: "+action);

        Optional<BrewingRecipe> recipeQuery = BrewingRecipe.get(itemInCursor);
        if(!recipeQuery.isPresent()) {
            debug("No recipe, do nothing");
            debug("===== End brewing transaction =====");
            return;
        }

        ItemStack newItemInCursor;
        ItemStack newItemInSlot;

        switch(action){
            case SWAP:{
                newItemInCursor = itemInSlot.clone();
                newItemInSlot = itemInCursor.clone();
                Optional<BrewingProcess> process = BrewingProcess.get(inventory);
                if(process.isPresent()){
                    debug("Cancel previous brewing process");
                    process.get().cancel();
                }
                break;
            }
            case PLACE_ALL:{
                int placeAmount = Math.min(itemInSlot.getType().getMaxStackSize()-itemInSlot.getAmount(), itemInCursor.getAmount());

                newItemInCursor = itemInCursor.clone();
                newItemInSlot = itemInSlot.clone();

                newItemInCursor.setAmount(newItemInCursor.getAmount()+placeAmount);
                newItemInSlot.setAmount(newItemInSlot.getAmount()-placeAmount);
                break;
            }
            default:
                debug("===== End brewing transaction =====");
                return;
        }

        BrewerInventory brewingInventory = (BrewerInventory)inventory;
        InventoryView view = event.getView();
        final ItemStack slot = newItemInSlot;
        final ItemStack cursor = newItemInCursor;

        debug("Slot: "+itemInSlot.getType()+" to "+newItemInSlot.getType());
        debug("Cursor: "+itemInCursor.getType()+" to "+newItemInCursor.getType());

        Bukkit.getScheduler().runTaskLater(CustomRecipeAPI.getInstance(), () ->{
            view.setCursor(cursor);
            view.setItem(3, slot);
            checkCanStart(brewingInventory);

            debug("Slot: "+view.getItem(3).getType());
            debug("Cursor: "+view.getCursor().getType());
            debug("===== End brewing transaction =====");
        }, 1L);
    }

    @EventHandler
    public void brewingListener(InventoryClickEvent event){
        if(event.getClickedInventory() == null) return;
        if(event.getClickedInventory().getType() != InventoryType.BREWING) return;
        checkCanStart((BrewerInventory) event.getClickedInventory());
    }

    private void checkCanStart(BrewerInventory inventory){
        ItemStack ingredient = inventory.getIngredient();
        debug("===== New brewing recipe check =====");
        if(ingredient==null || ingredient.getType()==Material.AIR) return;
        Optional<BrewingRecipe> recipeQuery = BrewingRecipe.get(ingredient);
        if(!recipeQuery.isPresent()) {
            debug("Recipe: none");
            debug("===== End brewing recipe check =====");
            return;
        }
        BrewingRecipe recipe = recipeQuery.get();
        if(!recipe.canStart(inventory)) {
            debug("Recipe can't start");
            debug("===== End brewing recipe check =====");
            return;
        }
        Optional<BrewingProcess> process = BrewingProcess.get(inventory);
        if(process.isPresent()){
            if(process.get().getRecipe()==recipe){
                debug("Process already running");
                debug("===== End brewing recipe check =====");
                return;
            }
            process.get().cancel();
        }
        debug("Start process");
        recipe.startBrewing(inventory);
        debug("===== End brewing recipe check =====");
    }

    private enum TransferAction{
        PLACE_ONE,
        PLACE_ALL,
        NONE,
        SWAP
    }
}
