package ch.swisssmp.crafting.brewing;

import ch.swisssmp.crafting.CustomRecipe;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class BrewingRecipe extends CustomRecipe {

    private static final int DEFAULT_BREWING_TIME = 400; //20s

    private final ItemStack ingredient;
    private final int time;
    private final BrewingFilter filter;
    private final BrewingAction action;

    public BrewingRecipe(NamespacedKey key, ItemStack ingredient, int time, BrewingFilter resultFilter, BrewingAction action){
        super(key);
        this.ingredient = ingredient;
        this.time = time;
        this.filter = resultFilter;
        this.action = action;
    }

    public BrewingRecipe(NamespacedKey key, ItemStack ingredient, BrewingFilter resultFilter, BrewingAction action){
        this(key, ingredient, DEFAULT_BREWING_TIME, resultFilter, action);
    }

    public BrewingRecipe(NamespacedKey key, Material ingredient, int time, BrewingFilter resultFilter, BrewingAction action){
        this(key, new ItemStack(ingredient), time, resultFilter, action);
    }

    public BrewingRecipe(NamespacedKey key, Material ingredient, BrewingFilter resultFilter, BrewingAction action){
        this(key, ingredient, DEFAULT_BREWING_TIME, resultFilter, action);
    }

    public ItemStack getIngredient(){
        return ingredient;
    }

    public int getTime(){
        return time;
    }

    public BrewingFilter getResultFilter(){
        return filter;
    }

    public BrewingAction getAction(){
        return action;
    }

    protected BrewingProcess startBrewing(BrewerInventory inventory){
        return BrewingProcess.start(this, inventory);
    }

    protected boolean canStart(BrewerInventory inventory){
        for(int i = 0; i < 3; i++){
            ItemStack item = inventory.getItem(i);
            if(item==null || !filter.isMatch(item)) continue;
            return true;
        }
        return false;
    }

    @Override
    protected void register(){
        BrewingRecipes.add(this);
    }

    @Override
    protected void unregister(){
        BrewingRecipes.remove(this);
    }

    protected static Optional<BrewingRecipe> get(ItemStack itemStack){
        return BrewingRecipes.get(itemStack);
    }

    protected static Optional<BrewingRecipe> get(BrewerInventory inventory){
        return get(inventory.getIngredient());
    }
}

