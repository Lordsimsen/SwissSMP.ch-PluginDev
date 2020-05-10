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
                    Bukkit.getLogger().info("case right, add one to slot");
                    return;
                }
                break;
            }
            default:
                Bukkit.getLogger().info("undefined clicktype " + event.getClick());
                return;
        }

        Optional<BrewingRecipe> recipeQuery = BrewingRecipe.get(itemInCursor);
        if(!recipeQuery.isPresent()) {
            Bukkit.getLogger().info("Recipe not present");
            return;
        }

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
                Bukkit.getLogger().info("No behavior defined for action " + action);
                return;
        }
        BrewerInventory brewingInventory = (BrewerInventory)inventory;
        InventoryView view = event.getView();
        ItemStack slot = itemInSlot;
        ItemStack cursor = itemInCursor;
        Bukkit.getScheduler().runTaskLater(CustomRecipeAPI.getInstance(), () ->{
            view.setCursor(cursor);
            view.setItem(3, slot);
            checkCanStart(brewingInventory);
        }, 1L);
        Bukkit.getLogger().info("Allowed placement");
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
        if(!recipeQuery.isPresent()) {
            Bukkit.getLogger().info("No recipe found");
            return;
        }
        BrewingRecipe recipe = recipeQuery.get();
        if(!recipe.canStart(inventory)) {
            Bukkit.getLogger().info("Recipe cannot start");
            return;
        }
        Optional<BrewingProcess> process = BrewingProcess.get(inventory);
        if(process.isPresent()){
            if(process.get().getRecipe()==recipe){
                Bukkit.getLogger().info("Process already running");
                return;
            }
            process.get().cancel();
        }
        Bukkit.getLogger().info("Starting brewing");
        recipe.startBrewing(inventory);
    }

    private enum TransferAction{
        PLACE_ONE,
        PLACE_ALL,
        NONE,
        SWAP
    }
}
