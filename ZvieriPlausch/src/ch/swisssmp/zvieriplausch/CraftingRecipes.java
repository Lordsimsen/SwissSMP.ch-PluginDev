package ch.swisssmp.zvieriplausch;

import ch.swisssmp.crafting.CustomRecipeAPI;
import ch.swisssmp.crafting.brewing.BrewingAction;
import ch.swisssmp.crafting.brewing.BrewingRecipe;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

public class CraftingRecipes {
    /*
    Registers all the craftingRecipes for the 15 dishes (not for rabbit stew) and for
    the intermediate ingredient pasta
     */
    public static void registerCraftingRecipes(){
        ItemStack zvieriHotChocolate = getTaggedCustomItemStack("HOT_CHOCOLATE");
        ShapedRecipe zvieriHotChoolateRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "zvieri_hot_chocolate"), zvieriHotChocolate);
        zvieriHotChoolateRecipe.shape(
                "s",
                "p",
                "m"
        );
        zvieriHotChoolateRecipe.setIngredient('s', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.SUGAR)));
        zvieriHotChoolateRecipe.setIngredient('p', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("CHOCOLATE_POWDER")));
        zvieriHotChoolateRecipe.setIngredient('m', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("ZVIERI_MILK_BUCKET")));
        Bukkit.getServer().addRecipe(zvieriHotChoolateRecipe);

        ItemStack pasta = getTaggedCustomItemStack("PASTA");
        ShapedRecipe pastaRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "pasta_horizontal"), pasta);
        pastaRecipe.shape(
                "ww"
        );
        pastaRecipe.setIngredient('w', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.WHEAT)));

        ShapedRecipe pastaVerticalRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "pasta_vertical"), pasta);
        pastaVerticalRecipe.shape(
                "w",
                "w"
        );
        pastaVerticalRecipe.setIngredient('w', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.WHEAT)));

        Bukkit.getServer().addRecipe(pastaVerticalRecipe);
        Bukkit.getServer().addRecipe(pastaRecipe);


        ItemStack hashBrowns = getTaggedCustomItemStack("HASH_BROWNS");
        ShapedRecipe hashBrownsRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "hash_browns_horizontal"), hashBrowns);
        hashBrownsRecipe.shape(
                "pp"
        );
        hashBrownsRecipe.setIngredient('p', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.BAKED_POTATO)));

        ShapedRecipe hashBrownsVerticalRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "hash_browns_vertical"), hashBrowns);
        hashBrownsVerticalRecipe.shape(
                "p",
                "p"
        );
        hashBrownsVerticalRecipe.setIngredient('p', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.BAKED_POTATO)));

        Bukkit.getServer().addRecipe(hashBrownsVerticalRecipe);
        Bukkit.getServer().addRecipe(hashBrownsRecipe);


        ItemStack meatFeast = getTaggedCustomItemStack("MEAT_FEAST");
        ShapedRecipe meatFeastRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "meat_feast_horizontal"), meatFeast);
        meatFeastRecipe.shape(
                "pbc"
        );
        meatFeastRecipe.setIngredient('p', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.COOKED_PORKCHOP)));
        meatFeastRecipe.setIngredient('b', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.COOKED_BEEF)));
        meatFeastRecipe.setIngredient('c', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.COOKED_CHICKEN)));

        ShapedRecipe meatFeastHorizontalRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "meat_feast_vertical"), meatFeast);
        meatFeastHorizontalRecipe.shape(
                "p",
                "b",
                "c"
        );
        meatFeastHorizontalRecipe.setIngredient('p', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.COOKED_PORKCHOP)));
        meatFeastHorizontalRecipe.setIngredient('b', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.COOKED_BEEF)));
        meatFeastHorizontalRecipe.setIngredient('c', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.COOKED_CHICKEN)));

        Bukkit.getServer().addRecipe(meatFeastHorizontalRecipe);
        Bukkit.getServer().addRecipe(meatFeastRecipe);


        ItemStack honeyMilk = getTaggedCustomItemStack("HONEY_MILK");
        ShapedRecipe honeyMilkRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "honey_milk"), honeyMilk);
        honeyMilkRecipe.shape(
                "h",
                "m"
        );
        honeyMilkRecipe.setIngredient('h', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("ZVIERI_HONEY_BOTTLE")));
        honeyMilkRecipe.setIngredient('m', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("ZVIERI_MILK_BUCKET")));

        Bukkit.getServer().addRecipe(honeyMilkRecipe);


        ItemStack ricePudding = getTaggedCustomItemStack("RICE_PUDDING");
        ShapedRecipe ricePuddingRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "rice_pudding"), ricePudding);
        ricePuddingRecipe.shape(
                "w",
                "m"
        );
        ricePuddingRecipe.setIngredient('w', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.WHEAT)));
        ricePuddingRecipe.setIngredient('m', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("ZVIERI_MILK_BUCKET")));

        Bukkit.getServer().addRecipe(ricePuddingRecipe);


        ItemStack sushi = getTaggedCustomItemStack("SUSHI");
        ShapedRecipe sushiRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "sushi"), sushi);
        sushiRecipe.shape(
                "s",
                "k",
                "w"
        );
        sushiRecipe.setIngredient('s', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.SALMON)));
        sushiRecipe.setIngredient('k', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.DRIED_KELP)));
        sushiRecipe.setIngredient('w', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.WHEAT)));

        Bukkit.getServer().addRecipe(sushiRecipe);


        ItemStack schniPo = getTaggedCustomItemStack("SCHNITZEL_FRIES");
        ShapedRecipe schniPoRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "schnitzel_fries"), schniPo);
        schniPoRecipe.shape(
                "w ",
                "pb"
        );
        schniPoRecipe.setIngredient('w', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.WHEAT)));
        schniPoRecipe.setIngredient('p', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.COOKED_PORKCHOP)));
        schniPoRecipe.setIngredient('b', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.BAKED_POTATO)));

        Bukkit.getServer().addRecipe(schniPoRecipe);


        ItemStack veggie = getTaggedCustomItemStack("VEGGIES_DELIGHT");
        ShapedRecipe veggieRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "veggies_delight_horizontal"), veggie);
        veggieRecipe.shape(
                "kwc"
        );
        veggieRecipe.setIngredient('k', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.KELP)));
        veggieRecipe.setIngredient('w', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.WHEAT)));
        veggieRecipe.setIngredient('c', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.CARROT)));

        ShapedRecipe veggieVerticalRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "veggies_delight_vertical"), veggie);
        veggieVerticalRecipe.shape(
                "k",
                "w",
                "c"
        );
        veggieVerticalRecipe.setIngredient('k', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.KELP)));
        veggieVerticalRecipe.setIngredient('w', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.WHEAT)));
        veggieVerticalRecipe.setIngredient('c', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.CARROT)));

        Bukkit.getServer().addRecipe(veggieVerticalRecipe);
        Bukkit.getServer().addRecipe(veggieRecipe);


        ItemStack spaghetti = getTaggedCustomItemStack("SPAGHETTI_BOLOGNESE");
        ShapedRecipe spaghettiRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "spaghetti_bolognese"), spaghetti);
        spaghettiRecipe.shape(
                "r",
                "b",
                "p"
        );
        spaghettiRecipe.setIngredient('r', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("RED_SAUCE")));
        spaghettiRecipe.setIngredient('b', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.COOKED_BEEF)));
        spaghettiRecipe.setIngredient('p', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("PASTA")));

        Bukkit.getServer().addRecipe(spaghettiRecipe);


        ItemStack creeperSucre = getTaggedCustomItemStack("CREEPER_SUCRE");
        ShapedRecipe creeperSucreRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "creeper_sucre"), creeperSucre);
        creeperSucreRecipe.shape(
                "wps",
                " c "
        );
        creeperSucreRecipe.setIngredient('w', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("WHIPPED_CREAM")));
        creeperSucreRecipe.setIngredient('p', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("PUFFERFISH_EXTRACT")));
        creeperSucreRecipe.setIngredient('s', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.SUGAR)));
        creeperSucreRecipe.setIngredient('c', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("STEAMED_CREEPER_HEAD")));

        Bukkit.getServer().addRecipe(creeperSucreRecipe);


        ItemStack zhGeschnetzeltes = getTaggedCustomItemStack("ZURICH_GESCHNETZELTES");
        ShapedRecipe zhGeschnetzeltesRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "zurich_geschnetzeltes"), zhGeschnetzeltes);
        zhGeschnetzeltesRecipe.shape(
                "brm"
        );
        zhGeschnetzeltesRecipe.setIngredient('b', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.COOKED_BEEF)));
        zhGeschnetzeltesRecipe.setIngredient('r', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("HASH_BROWNS")));
        zhGeschnetzeltesRecipe.setIngredient('m', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("ZVIERI_MILK_BUCKET")));

        Bukkit.getServer().addRecipe(zhGeschnetzeltesRecipe);


        ItemStack pekingDuck = getTaggedCustomItemStack("PEKING_DUCK");
        ShapedRecipe pekingDuckRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "peking_duck"), pekingDuck);
        pekingDuckRecipe.shape(
                "s",
                "c",
                "w"
        );
        pekingDuckRecipe.setIngredient('s', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.SUGAR)));
        pekingDuckRecipe.setIngredient('c', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.COOKED_CHICKEN)));
        pekingDuckRecipe.setIngredient('w', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("BOILED_WATER")));

        Bukkit.getServer().addRecipe(pekingDuckRecipe);


        ItemStack pizza = getTaggedCustomItemStack("PIZZA_MARGHERITA");
        ShapedRecipe pizzaRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "pizza_margherita"), pizza);
        pizzaRecipe.shape(
                "c",
                "r",
                "b"
        );
        pizzaRecipe.setIngredient('c', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("CHEESE")));
        pizzaRecipe.setIngredient('r', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("RED_SAUCE")));
        pizzaRecipe.setIngredient('b', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.BREAD)));

        Bukkit.getServer().addRecipe(pizzaRecipe);


        ItemStack maritimePlatter = getTaggedCustomItemStack("MARITIME_PLATTER");
        ShapedRecipe maritimePlatterRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "maritime_platter"), maritimePlatter);
        maritimePlatterRecipe.shape(
                "ksp"
        );
        maritimePlatterRecipe.setIngredient('k', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.DRIED_KELP)));
        maritimePlatterRecipe.setIngredient('s', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.COOKED_SALMON)));
        maritimePlatterRecipe.setIngredient('p', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("PUFFERFISH_EXTRACT")));

        ShapedRecipe maritimePlatterVerticalRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "maritime_platter_vertical"), maritimePlatter);
        maritimePlatterVerticalRecipe.shape(
                "k",
                "s",
                "p"
        );
        maritimePlatterVerticalRecipe.setIngredient('k', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.DRIED_KELP)));
        maritimePlatterVerticalRecipe.setIngredient('s', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.COOKED_SALMON)));
        maritimePlatterVerticalRecipe.setIngredient('p', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("PUFFERFISH_EXTRACT")));

        Bukkit.getServer().addRecipe(maritimePlatterVerticalRecipe);
        Bukkit.getServer().addRecipe(maritimePlatterRecipe);


        ItemStack lordsBlessing = getTaggedCustomItemStack("LORDS_BLESSING");
        ShapedRecipe lordsBlessingRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "lords_blessing"), lordsBlessing);
        lordsBlessingRecipe.shape(
                "cpn",
                " w "
        );
        lordsBlessingRecipe.setIngredient('c', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("WHIPPED_CREAM")));
        lordsBlessingRecipe.setIngredient('p', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("STEAMED_PUMPKIN")));
        lordsBlessingRecipe.setIngredient('n', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.BAKED_POTATO)));
        lordsBlessingRecipe.setIngredient('w', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("BOILED_WATER")));

        Bukkit.getServer().addRecipe(lordsBlessingRecipe);


        ItemStack dameBlanche = getTaggedCustomItemStack("DAME_BLANCHE");
        ShapedRecipe dameBlancheRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "dame_blanche"), dameBlanche);
        dameBlancheRecipe.shape(
                "b",
                "c",
                "w"
        );
        dameBlancheRecipe.setIngredient('b', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.SWEET_BERRIES)));
        dameBlancheRecipe.setIngredient('c', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("HOT_CHOCOLATE")));
        dameBlancheRecipe.setIngredient('w', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("WHIPPED_CREAM")));

        Bukkit.getServer().addRecipe(dameBlancheRecipe);
    }

    private static ItemStack getTaggedCustomItemStack(String customEnum){
        CustomItemBuilder builder = CustomItems.getCustomItemBuilder(customEnum);
        checkItemBuilder(builder);
        builder.setAmount(1);
        ItemStack result = builder.build();
        ItemUtil.setBoolean(result, "zvieriGameItem", true);
        return result;
    }

    private static ItemStack getTaggedItemStack(Material material){
        ItemStack item = new ItemStack(material);
        ItemUtil.setBoolean(item, "zvieriGameItem", true);
        return item;
    }

    private static void checkItemBuilder(CustomItemBuilder builder){
        if(builder == null){
            throw new NullPointerException(ZvieriGamePlugin.getPrefix() + " ItemBuilder " + builder + " konnte nicht geladen werden");
        };
    }

    public static void registerFurnaceRecipes(){
        registerFurnaceRecipe("red_sauce", CustomItems.getCustomItemBuilder("RED_SAUCE").build(), Material.BEETROOT, 0, 200);
        registerFurnaceRecipe("steamed_creeper_head", CustomItems.getCustomItemBuilder("STEAMED_CREEPER_HEAD").build(), Material.CREEPER_HEAD, 0, 600);
        registerFurnaceRecipe("steamed_pumpkin", CustomItems.getCustomItemBuilder("STEAMED_PUMPKIN").build(), Material.PUMPKIN, 0, 600);
        registerFurnaceRecipe("cheese", CustomItems.getCustomItemBuilder("CHEESE").build(), "ZVIERI_MILK_BUCKET", 0, 200);
        registerFurnaceRecipe("zvieri_chocolate_powder", CustomItems.getCustomItemBuilder("CHOCOLATE_POWDER").build(), Material.COCOA_BEANS, 0, 200);
    }

    private static void registerFurnaceRecipe(String key, ItemStack result, Material ingredient, float experience, int cookingTime){
        ItemUtil.setBoolean(result, "zvieriGameItem", true);
        ItemStack ingredientStack = new ItemStack(ingredient);
        ItemUtil.setBoolean(ingredientStack, "zvieriGameItem", true);
        Bukkit.addRecipe(new FurnaceRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), key), result
                , new RecipeChoice.ExactChoice(ingredientStack), experience, cookingTime));
        Bukkit.addRecipe(new SmokingRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), key), result
                , new RecipeChoice.ExactChoice(ingredientStack), experience, cookingTime/2));
    }

    private static void registerFurnaceRecipe(String key, ItemStack result, String customIngredientEnum, float experience, int cookingTime){
        ItemUtil.setBoolean(result, "zvieriGameItem", true);
        ItemStack ingredientStack = CustomItems.getCustomItemBuilder(customIngredientEnum).build();
        ItemUtil.setBoolean(ingredientStack, "zvieriGameItem", true);
        Bukkit.addRecipe(new FurnaceRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), key), result
                , new RecipeChoice.ExactChoice(ingredientStack), experience, cookingTime));
        Bukkit.addRecipe(new SmokingRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), key), result
                , new RecipeChoice.ExactChoice(ingredientStack), experience, cookingTime/2));
    }

    public static void registerBrewingRecipes(){
        registerBrewingRecipe("pufferfish_extract", Material.PUFFERFISH
        , (result) -> CustomItems.getCustomItemBuilder("PUFFERFISH_EXTRACT").update(result.getResult()));

        registerBrewingRecipe("whipped_cream", "ZVIERI_MILK_BUCKET"
        , (result) -> CustomItems.getCustomItemBuilder("WHIPPED_CREAM").update(result.getResult()));

        registerBrewingRecipe("boiled_water", "ZVIERI_WATER_BUCKET"
                , (result) -> CustomItems.getCustomItemBuilder("BOILED_WATER").update(result.getResult()));
    }

    private static void registerBrewingRecipe(String key, Material ingredient, BrewingAction action){
        ItemStack ingredientStack = new ItemStack(ingredient);
        ItemUtil.setBoolean(ingredientStack, "zvieriGameItem", true);

        ItemStack container = new ItemStack(Material.GLASS_BOTTLE);
        ItemUtil.setBoolean(container, "zvieriGameItem", true);

        CustomRecipeAPI.addRecipe(new BrewingRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), key), ingredientStack
        , (itemStack) -> itemStack.isSimilar(container), action));
    }

    private static void registerBrewingRecipe(String key, String customIngredientEnum, BrewingAction action){
        ItemStack ingredientStack = CustomItems.getCustomItemBuilder(customIngredientEnum).build();
        ItemUtil.setBoolean(ingredientStack, "zvieriGameItem", true);

        ItemStack container = new ItemStack(Material.GLASS_BOTTLE);
        ItemUtil.setBoolean(container, "zvieriGameItem", true);

        CustomRecipeAPI.addRecipe(new BrewingRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), key), ingredientStack
                , (itemStack) -> itemStack.isSimilar(container), action));


    }
}
