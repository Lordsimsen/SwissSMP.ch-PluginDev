package ch.swisssmp.crafting.brewing;

import ch.swisssmp.crafting.CustomRecipeAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class BrewingListener implements Listener {

    @EventHandler
    private void onBrewingIngredientPlace(InventoryClickEvent event){
        if(event.isShiftClick()) return;
        Inventory inventory = event.getClickedInventory();
        if(inventory == null || inventory.getType() != InventoryType.BREWING) return;
        if(event.getSlot() != 3) return;
        ItemStack itemInSlot = event.getCurrentItem();
        ItemStack itemInCursor = event.getCursor();
        TransferAction action;
        if(itemInSlot==null) itemInSlot = new ItemStack(Material.AIR);
        if(itemInCursor==null) itemInCursor = new ItemStack(Material.AIR);
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
                    return;
                }
                break;
            }
            default:
                return;
        }

        Optional<BrewingRecipe> recipeQuery = BrewingRecipe.get(itemInCursor);
        if(!recipeQuery.isPresent()) return;

        switch(action){
            case SWAP:{
                ItemStack cache = itemInCursor;
                itemInCursor = itemInSlot;
                itemInSlot = cache;
                Optional<BrewingProcess> process = BrewingProcess.get(inventory);
                if(process.isPresent()) process.get().cancel();
                break;
            }
            case PLACE_ALL:{
                int placeAmount = Math.min(itemInSlot.getType().getMaxStackSize()-itemInSlot.getAmount(), itemInCursor.getAmount());
                itemInSlot.setAmount(itemInSlot.getAmount()+placeAmount);
                itemInCursor.setAmount(itemInCursor.getAmount()-placeAmount);
                break;
            }
            default:
                return;
        }
        BrewerInventory brewingInventory = (BrewerInventory)inventory;
        InventoryView view = event.getView();
        ItemStack slot = itemInSlot;
        ItemStack cursor = itemInCursor;
        Bukkit.getScheduler().runTaskLater(CustomRecipeAPI.getInstance(), () ->{
            view.setCursor(slot);
            inventory.setItem(3, cursor);
            checkCanStart(brewingInventory);
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
        if(ingredient==null || ingredient.getType()==Material.AIR) return;
        Optional<BrewingRecipe> recipeQuery = BrewingRecipe.get(ingredient);
        if(!recipeQuery.isPresent()) return;
        BrewingRecipe recipe = recipeQuery.get();
        if(!recipe.canStart(inventory)) return;
        Optional<BrewingProcess> process = BrewingProcess.get(inventory);
        if(process.isPresent()){
            if(process.get().getRecipe()==recipe){
                return;
            }
            process.get().cancel();
        }
        recipe.startBrewing(inventory);
    }

    private enum TransferAction{
        PLACE_ONE,
        PLACE_ALL,
        NONE,
        SWAP
    }
}
