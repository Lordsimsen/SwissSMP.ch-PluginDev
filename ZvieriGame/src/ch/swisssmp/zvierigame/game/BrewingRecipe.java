package ch.swisssmp.zvierigame.game;

import ch.swisssmp.zvierigame.ZvieriGamePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class BrewingRecipe {
    private final static List<BrewingRecipe> recipes = new ArrayList<>();
    private final ItemStack ingredient;
    private final BrewAction action;

    public BrewingRecipe(ItemStack ingredient, BrewAction action){
        this.ingredient = ingredient;
        this.action = action;
    }

    public BrewingRecipe(Material ingredient, BrewAction action){
        this(new ItemStack(ingredient), action);
    }

    protected static void addRecipe(BrewingRecipe recipe){
        recipes.add(recipe);
    }

    public ItemStack getIngredient(){
        return ingredient;
    }

    public BrewAction getAction(){
        return action;
    }

    public static BrewingRecipe getRecipe(BrewerInventory inventory){
        boolean notallAir = true;
        for(int i = 0; i < 3 && !notallAir; i++){
            if(inventory.getItem(i) == null) continue;
            if(inventory.getItem(i).getType() == Material.AIR) continue;
            notallAir = true;
        }
        if(!notallAir) return null;
        for(BrewingRecipe recipe : recipes){
            if(inventory.getIngredient().isSimilar(recipe.getIngredient())) return recipe;
        }
        return null;
    }

    public void startBrewing(BrewerInventory inventory){
        new BrewClock(this, inventory);
    }

    private class BrewClock extends BukkitRunnable{
        private BrewerInventory inventory;
        private BrewingRecipe recipe;
        private ItemStack ingredient;
        private BrewingStand brewingStand;
        private int time = 400;

        public BrewClock(BrewingRecipe recipe, BrewerInventory inventory){
            this.recipe = recipe;
            this.inventory = inventory;
            this.ingredient = inventory.getIngredient();
            this.brewingStand = inventory.getHolder();
            this.runTaskTimer(ZvieriGamePlugin.getInstance(), 0L, 1L);
        }

//        @Override
//        public void cancel(){
//            super.cancel();
//            brewingStand.setBrewingTime(400);
//        }

        @Override
        public void run(){
            if(time == 0){
                inventory.setIngredient(new ItemStack(Material.AIR));
                for(int i = 0; i < 3; i++){
                    if((inventory.getItem(i) == null) || inventory.getItem(i).getType() == Material.AIR) continue;
                    recipe.getAction().brew(inventory, inventory.getItem(i), ingredient);
                }
                cancel();
                return;
            }
            if(inventory.getIngredient().isSimilar(ingredient)){
                brewingStand.setBrewingTime(400);
                cancel();
                return;
            }
            time--;
            brewingStand.setBrewingTime(time);
        }
    }
}
