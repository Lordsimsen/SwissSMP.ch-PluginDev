package ch.swisssmp.crafting.brewing;

import ch.swisssmp.crafting.CustomRecipeAPI;
import ch.swisssmp.utils.Mathf;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Optional;

public class BrewingProcess extends BukkitRunnable {

    private static final int BASE_BREWING_TICKS = 400;

    private static final HashMap<Inventory,BrewingProcess> processes = new HashMap<Inventory, BrewingProcess>();

    private final BrewerInventory inventory;
    private final BrewingRecipe recipe;
    private final BrewingStand brewingStand;
    private final int time;
    private final float step;
    private float t = 0;

    protected BrewingProcess(BrewingRecipe recipe, BrewerInventory inventory) {
        this.recipe = recipe;
        this.inventory = inventory;
        this.brewingStand = inventory.getHolder();
        this.time = recipe.getTime();
        this.step = BASE_BREWING_TICKS / (float) recipe.getTime();
    }

    @Override
    public void run() {
        ItemStack ingredient = inventory.getIngredient();
        if(ingredient==null){
            cancel();
            return;
        }
        if (!ingredient.isSimilar(ingredient)) {
            brewingStand.setBrewingTime(BASE_BREWING_TICKS);
            cancel();
            return;
        }
        if (time == 0) {
            complete();
            return;
        }
        t+=step;
        brewingStand.setBrewingTime(Mathf.roundToInt((1-t)*BASE_BREWING_TICKS));
    }

    private void complete(){
        ItemStack ingredient = inventory.getIngredient();
        if(ingredient==null) return;

        for (int i = 0; i < 3; i++) {
            ItemStack result = inventory.getItem(i);
            if (result == null || result.getType() == Material.AIR) continue;
            BrewingStandBrewEvent event = new BrewingStandBrewEvent(inventory, ingredient, result, i);
            try{
                Bukkit.getPluginManager().callEvent(event);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            if(event.isCancelled()) continue;
            recipe.getAction().brew(new BrewResult(inventory, ingredient, result, i));
        }
        ingredient.setAmount(ingredient.getAmount()-1);
        finish();
    }

    @Override
    public void cancel(){
        processes.remove(inventory);
        finish();
    }

    private void finish(){
        super.cancel();
    }

    protected static Optional<BrewingProcess> get(Inventory inventory){
        BrewingProcess process = processes.get(inventory);
        return process!=null ? Optional.of(process) : Optional.empty();
    }

    protected static BrewingProcess start(BrewingRecipe recipe, BrewerInventory inventory){
        BrewingProcess result = new BrewingProcess(recipe, inventory);
        result.runTaskTimer(CustomRecipeAPI.getInstance(), 0L, 1L);
        processes.put(inventory, result);
        return result;
    }
}
