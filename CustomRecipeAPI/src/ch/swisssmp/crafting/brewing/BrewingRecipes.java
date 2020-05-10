package ch.swisssmp.crafting.brewing;

import org.bukkit.Material;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BrewingRecipes {
    private static final List<BrewingRecipe> recipes = new ArrayList<BrewingRecipe>();

    protected static void add(BrewingRecipe recipe){
        recipes.add(recipe);
    }

    protected static void remove(BrewingRecipe recipe){
        recipes.remove(recipe);
    }

    protected static Optional<BrewingRecipe> get(ItemStack ingredient){
        if(ingredient==null || ingredient.getType()== Material.AIR){
            return Optional.empty();
        }
        for(BrewingRecipe recipe : recipes){
            if(!ingredient.isSimilar(recipe.getIngredient())) continue;
            return Optional.of(recipe);
        }
        return Optional.empty();
    }
}
