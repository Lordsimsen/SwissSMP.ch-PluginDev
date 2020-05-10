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
    private final BrewFilter filter;
    private final BrewAction action;

    private BrewingRecipe(NamespacedKey key, ItemStack ingredient, int time, BrewFilter resultFilter, BrewAction action){
        super(key);
        this.ingredient = ingredient;
        this.time = time;
        this.filter = resultFilter;
        this.action = action;
    }

    private BrewingRecipe(NamespacedKey key, ItemStack ingredient, BrewFilter resultFilter, BrewAction action){
        this(key, ingredient, DEFAULT_BREWING_TIME, resultFilter, action);
    }

    public BrewingRecipe(NamespacedKey key, Material ingredient, int time, BrewFilter resultFilter, BrewAction action){
        this(key, new ItemStack(ingredient), time, resultFilter, action);
    }

    public BrewingRecipe(NamespacedKey key, Material ingredient, BrewFilter resultFilter, BrewAction action){
        this(key, ingredient, DEFAULT_BREWING_TIME, resultFilter, action);
    }

    public ItemStack getIngredient(){
        return ingredient;
    }

    public int getTime(){
        return time;
    }

    public BrewFilter getResultFilter(){
        return filter;
    }

    public BrewAction getAction(){
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

