package ch.swisssmp.crafting;

import ch.swisssmp.crafting.brewing.BrewingRecipe;
import ch.swisssmp.crafting.brewing.BrewingRecipes;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CustomRecipes {
    private static final List<CustomRecipe> recipes = new ArrayList<CustomRecipe>();

    protected static void add(CustomRecipe recipe){
        recipes.add(recipe);
        recipe.registerRecipe();
    }

    protected static void remove(Plugin plugin){
        String namespace = plugin.getName().toLowerCase(Locale.ROOT);
        List<CustomRecipe> matches = recipes.stream().filter(r->r.getKey().getNamespace().equals(namespace)).collect(Collectors.toList());
        recipes.removeAll(matches);
        for(CustomRecipe recipe : matches){
            recipe.unregisterRecipe();
        }
    }

    protected static void remove(NamespacedKey key){
        List<CustomRecipe> matches = recipes.stream().filter(r->r.getKey().equals(key)).collect(Collectors.toList());
        recipes.removeAll(matches);
    }
}
