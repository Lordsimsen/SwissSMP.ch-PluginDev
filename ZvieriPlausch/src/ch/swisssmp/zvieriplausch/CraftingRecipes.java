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

//        List<Material> pastaIngredients = new ArrayList<>();
//        pastaIngredients.add(Material.WHEAT);
//        pastaIngredients.add(Material.WHEAT);
//        List<String> pastaCustomIngredients = new ArrayList<>();
//        pastaCustomIngredients.add("BOILED_WATER");
//        registerCraftingRecipe("PASTA", pastaIngredients, pastaCustomIngredients);

//        CustomItemBuilder pastaBuilder = CustomItems.getCustomItemBuilder("PASTA");
//        if(pastaBuilder==null){
//            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
//            return;
//        }
//        pastaBuilder.setAmount(1);
////        pastaBuilder.setMaxStackSize(16);
//        ItemStack pasta = pastaBuilder.build();
//        ShapelessRecipe pastaRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "pasta"), pasta);
//        pastaRecipe.addIngredient(Material.WHEAT);
//        pastaRecipe.addIngredient(Material.WHEAT);
//        pastaRecipe.addIngredient(new RecipeChoice.ExactChoice(CustomItems.getCustomItemBuilder("BOILED_WATER").build()));
//        Bukkit.getServer().addRecipe(pastaRecipe);

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

//        List<Material> hashBrownsIngredients = new ArrayList<>();
//        hashBrownsIngredients.add(Material.BAKED_POTATO);
//        hashBrownsIngredients.add(Material.BAKED_POTATO);
//        registerCraftingRecipe("HASH_BROWNS", hashBrownsIngredients, null);

//        CustomItemBuilder hashBrownsBuilder = CustomItems.getCustomItemBuilder("HASH_BROWNS");
//        if(hashBrownsBuilder==null){
//            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
//            return;
//        }
//        hashBrownsBuilder.setAmount(1);
////        hashBrownsBuilder.setMaxStackSize(16);
//        ItemStack hashBrowns = hashBrownsBuilder.build();
//        ShapelessRecipe hashBrownsRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "hash_browns"), hashBrowns);
//        hashBrownsRecipe.addIngredient(Material.BAKED_POTATO);
//        hashBrownsRecipe.addIngredient(Material.BAKED_POTATO);
//        Bukkit.getServer().addRecipe(hashBrownsRecipe);

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

//        List<Material> meatFeastIngredients = new ArrayList<>();
//        meatFeastIngredients.add(Material.COOKED_PORKCHOP);
//        meatFeastIngredients.add(Material.COOKED_BEEF);
//        meatFeastIngredients.add(Material.COOKED_CHICKEN);
//        registerCraftingRecipe("MEAT_FEAST", meatFeastIngredients, null);

//        CustomItemBuilder meatFeastBuilder = CustomItems.getCustomItemBuilder("MEAT_FEAST");
//        if(meatFeastBuilder==null){
//            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
//            return;
//        }
//        meatFeastBuilder.setAmount(1);
////        meatFeastBuilder.setMaxStackSize(16);
//        ItemStack meatFeast = meatFeastBuilder.build();
//        ShapelessRecipe meatFeastRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "meat_feast"), meatFeast);
//        meatFeastRecipe.addIngredient(Material.COOKED_BEEF);
//        meatFeastRecipe.addIngredient(Material.COOKED_PORKCHOP);
//        meatFeastRecipe.addIngredient(Material.COOKED_CHICKEN);
//        Bukkit.getServer().addRecipe(meatFeastRecipe);

        ItemStack honeyMilk = getTaggedCustomItemStack("HONEY_MILK");
        ShapedRecipe honeyMilkRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "honey_milk"), honeyMilk);
        honeyMilkRecipe.shape(
                "h",
                "m"
        );
        honeyMilkRecipe.setIngredient('h', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("ZVIERI_HONEY_BOTTLE")));
        honeyMilkRecipe.setIngredient('m', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("ZVIERI_MILK_BUCKET")));

        Bukkit.getServer().addRecipe(honeyMilkRecipe);

//        List<String> honeyMilkCustomIngredients = new ArrayList<>();
//        honeyMilkCustomIngredients.add("ZVIERI_HONEY_BOTTLE");
//        honeyMilkCustomIngredients.add("ZVIERI_MILK_BUCKET");
//        registerCraftingRecipe("HONEY_MILK", null, honeyMilkCustomIngredients);

//        CustomItemBuilder honeyMilkBuilder = CustomItems.getCustomItemBuilder("HONEY_MILK");
//        if(honeyMilkBuilder==null){
//            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
//            return;
//        }
//        honeyMilkBuilder.setAmount(1);
//        ItemStack honeyMilk = honeyMilkBuilder.build();
//        ShapelessRecipe honeyMilkRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "honey_milk"), honeyMilk);
//        honeyMilkRecipe.addIngredient(Material.HONEY_BOTTLE);
//        honeyMilkRecipe.addIngredient(Material.MILK_BUCKET);
//        Bukkit.getServer().addRecipe(honeyMilkRecipe);

        ItemStack ricePudding = getTaggedCustomItemStack("RICE_PUDDING");
        ShapedRecipe ricePuddingRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "rice_pudding"), ricePudding);
        ricePuddingRecipe.shape(
                "w",
                "m"
        );
        ricePuddingRecipe.setIngredient('w', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.WHEAT)));
        ricePuddingRecipe.setIngredient('m', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("ZVIERI_MILK_BUCKET")));

        Bukkit.getServer().addRecipe(ricePuddingRecipe);

//        List<Material> ricePuddingIngredients = new ArrayList<>();
//        ricePuddingIngredients.add(Material.WHEAT);
//        List<String> ricePuddingCustomIngredients = new ArrayList<>();
//        ricePuddingCustomIngredients.add("ZVIERI_MILK_BUCKET");
//        registerCraftingRecipe("RICE_PUDDING", ricePuddingIngredients, ricePuddingCustomIngredients);

//        CustomItemBuilder ricePuddingBuilder = CustomItems.getCustomItemBuilder("RICE_PUDDING");
//        if(ricePuddingBuilder==null){
//            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
//            return;
//        }
//        ricePuddingBuilder.setAmount(1);
//        ItemStack ricePudding = ricePuddingBuilder.build();
//        ShapelessRecipe ricePuddingRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "rice_pudding"), ricePudding);
//        ricePuddingRecipe.addIngredient(Material.MILK_BUCKET);
//        ricePuddingRecipe.addIngredient(Material.WHEAT);
//        Bukkit.getServer().addRecipe(ricePuddingRecipe);

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

//        List<Material> sushiIngredients = new ArrayList<>();
//        sushiIngredients.add(Material.SALMON);
//        sushiIngredients.add(Material.DRIED_KELP);
//        sushiIngredients.add(Material.WHEAT);
//        registerCraftingRecipe("SUSHI", sushiIngredients, null);

//        CustomItemBuilder sushiBuilder = CustomItems.getCustomItemBuilder("SUSHI");
//        if(sushiBuilder==null){
//            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
//            return;
//        }
//        sushiBuilder.setAmount(1);
////        sushiBuilder.setMaxStackSize(16);
//        ItemStack sushi = sushiBuilder.build();
//        ShapelessRecipe sushiRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "sushi"), sushi);
//        sushiRecipe.addIngredient(Material.SALMON);
//        sushiRecipe.addIngredient(Material.DRIED_KELP);
//        sushiRecipe.addIngredient(Material.WHEAT);
//        Bukkit.getServer().addRecipe(sushiRecipe);

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

//        List<Material> schnitzelFriesIngredients = new ArrayList<>();
//        schnitzelFriesIngredients.add(Material.COOKED_PORKCHOP);
//        schnitzelFriesIngredients.add(Material.BAKED_POTATO);
//        schnitzelFriesIngredients.add(Material.WHEAT);
//        registerCraftingRecipe("SCHNITZEL_FRIES", schnitzelFriesIngredients, null);

//        CustomItemBuilder schniPoBuilder = CustomItems.getCustomItemBuilder("SCHNITZEL_FRIES");
//        if(schniPoBuilder==null){
//            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
//            return;
//        }
//        schniPoBuilder.setAmount(1);
////        schniPoBuilder.setMaxStackSize(16);
//        ItemStack schniPo = schniPoBuilder.build();
//        ShapelessRecipe schniPoRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "schnitzel_fries"), schniPo);
//        schniPoRecipe.addIngredient(Material.COOKED_PORKCHOP);
//        schniPoRecipe.addIngredient(Material.BAKED_POTATO);
//        schniPoRecipe.addIngredient(Material.WHEAT);
//        Bukkit.getServer().addRecipe(schniPoRecipe);

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

//        List<Material> veggieIngredients = new ArrayList<>();
//        veggieIngredients.add(Material.KELP);
//        veggieIngredients.add(Material.WHEAT);
//        veggieIngredients.add(Material.CARROT);
//        registerCraftingRecipe("VEGGIES_DELIGHT", veggieIngredients, null);

//        CustomItemBuilder veggieBuilder = CustomItems.getCustomItemBuilder("VEGGIES_DELIGHT");
//        if(veggieBuilder==null){
//            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
//            return;
//        }
//        veggieBuilder.setAmount(1);
////        veggieBuilder.setMaxStackSize(16);
//        ItemStack veggiesDelight = veggieBuilder.build();
//        ShapelessRecipe veggiesDelightRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "veggies_delight"), veggiesDelight);
//        veggiesDelightRecipe.addIngredient(Material.KELP);
//        veggiesDelightRecipe.addIngredient(Material.WHEAT);
//        veggiesDelightRecipe.addIngredient(Material.CARROT);
//        Bukkit.getServer().addRecipe(veggiesDelightRecipe);

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

//        List<Material> spaghettiIngredients = new ArrayList<>();
//        spaghettiIngredients.add(Material.COOKED_BEEF);
//        List<String> spaghettiCustomIngredients = new ArrayList<>();
//        spaghettiCustomIngredients.add("PASTA");
//        spaghettiCustomIngredients.add("RED_SAUCE");
//        registerCraftingRecipe("SPAGHETTI_BOLOGNESE", spaghettiIngredients, spaghettiCustomIngredients);

//        CustomItemBuilder spaghettiBuilder = CustomItems.getCustomItemBuilder("SPAGHETTI_BOLOGNESE");
//        if(spaghettiBuilder==null){
//            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
//            return;
//        }
//        spaghettiBuilder.setAmount(1);
////        spaghettiBuilder.setMaxStackSize(16);
//        ItemStack spaghetti = spaghettiBuilder.build();
//        ShapelessRecipe spaghettiRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "spaghetti_bolognese"), spaghetti);
//        spaghettiRecipe.addIngredient(CustomItems.getCustomItemBuilder("PASTA").build().getType());
//        spaghettiRecipe.addIngredient(CustomItems.getCustomItemBuilder("RED_SAUCE").build().getType());
//        spaghettiRecipe.addIngredient(Material.COOKED_BEEF);
//        Bukkit.getServer().addRecipe(spaghettiRecipe);

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

//        List<Material> creeperSucreIngredients = new ArrayList<>();
//        creeperSucreIngredients.add(Material.SUGAR);
//        List<String> creeperSucreCustomIngredients = new ArrayList<>();
//        creeperSucreCustomIngredients.add("STEAMED_CREEPER_HEAD");
//        creeperSucreCustomIngredients.add("WHIPPED_CREAM");
//        creeperSucreCustomIngredients.add("PUFFERFISH_EXTRACT");
//        registerCraftingRecipe("CREEPER_SUCRE", creeperSucreIngredients, creeperSucreCustomIngredients);

//        CustomItemBuilder creeperSucreBuilder = CustomItems.getCustomItemBuilder("CREEPER_SUCRE");
//        if(creeperSucreBuilder==null){
//            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
//            return;
//        }
//        creeperSucreBuilder.setAmount(1);
////        creeperSucreBuilder.setMaxStackSize(16);
//        ItemStack creeperSucre = creeperSucreBuilder.build();
//        ShapelessRecipe creeperSucreRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "creeper_sucre"), creeperSucre);
//        creeperSucreRecipe.addIngredient(Material.CREEPER_HEAD);
//        creeperSucreRecipe.addIngredient(Material.SUGAR);
//        creeperSucreRecipe.addIngredient(CustomItems.getCustomItemBuilder("WHIPPED_CREAM").build().getType());
//        creeperSucreRecipe.addIngredient(CustomItems.getCustomItemBuilder("PUFFERFISH_EXTRACT").build().getType());
//        Bukkit.getServer().addRecipe(creeperSucreRecipe);

        ItemStack zhGeschnetzeltes = getTaggedCustomItemStack("ZURICH_GESCHNETZELTES");
        ShapedRecipe zhGeschnetzeltesRecipe = new ShapedRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "zurich_geschnetzeltes"), zhGeschnetzeltes);
        zhGeschnetzeltesRecipe.shape(
                "brm"
        );
        zhGeschnetzeltesRecipe.setIngredient('b', new RecipeChoice.ExactChoice(getTaggedItemStack(Material.COOKED_BEEF)));
        zhGeschnetzeltesRecipe.setIngredient('r', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("HASH_BROWNS")));
        zhGeschnetzeltesRecipe.setIngredient('m', new RecipeChoice.ExactChoice(getTaggedCustomItemStack("ZVIERI_MILK_BUCKET")));

        Bukkit.getServer().addRecipe(zhGeschnetzeltesRecipe);

//        List<Material> zurichGeschnetzeltesIngredients = new ArrayList<>();
//        zurichGeschnetzeltesIngredients.add(Material.COOKED_BEEF);
//        List<String> zurichGeschnetzeltesCustomIngredients = new ArrayList<>();
//        zurichGeschnetzeltesCustomIngredients.add("HASH_BROWNS");
//        zurichGeschnetzeltesCustomIngredients.add("ZVIERI_MILK_BUCKET");
//        registerCraftingRecipe("ZURICH_GESCHNETZELTES", zurichGeschnetzeltesIngredients, zurichGeschnetzeltesCustomIngredients);

//        CustomItemBuilder zurichGeschnetzeltesBuilder = CustomItems.getCustomItemBuilder("ZURICH_GESCHNETZELTES");
//        if(zurichGeschnetzeltesBuilder==null){
//            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
//            return;
//        }
//        zurichGeschnetzeltesBuilder.setAmount(1);
////        zurichGeschnetzeltesBuilder.setMaxStackSize(16);
//        ItemStack zhSliced = zurichGeschnetzeltesBuilder.build();
//        ShapelessRecipe zhSlicedRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "zurich_geschnetzeltes"), zhSliced);
//        zhSlicedRecipe.addIngredient(Material.MILK_BUCKET);
//        zhSlicedRecipe.addIngredient(Material.COOKED_BEEF);
//        zhSlicedRecipe.addIngredient(CustomItems.getCustomItemBuilder("HASH_BROWNS").build().getType());
//        Bukkit.getServer().addRecipe(zhSlicedRecipe);

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

//        List<Material> pekingDuckIngredients = new ArrayList<>();
//        pekingDuckIngredients.add(Material.COOKED_CHICKEN);
//        pekingDuckIngredients.add(Material.SUGAR);
//        List<String> pekingDuckCustomIngredients = new ArrayList<>();
//        pekingDuckCustomIngredients.add("BOILED_WATER");
//        registerCraftingRecipe("PEKING_DUCK", pekingDuckIngredients, pekingDuckCustomIngredients);

//        CustomItemBuilder pekingDuckBuilder = CustomItems.getCustomItemBuilder("PEKING_DUCK");
//        if(pekingDuckBuilder==null){
//            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
//            return;
//        }
//        pekingDuckBuilder.setAmount(1);
////        pekingDuckBuilder.setMaxStackSize(16);
//        ItemStack pekingDuck = pekingDuckBuilder.build();
//        ShapelessRecipe pekingDuckRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "peking_duck"), pekingDuck);
//        pekingDuckRecipe.addIngredient(Material.COOKED_CHICKEN);
//        pekingDuckRecipe.addIngredient(Material.SUGAR);
//        pekingDuckRecipe.addIngredient(CustomItems.getCustomItemBuilder("BOILED_WATER").build().getType());
//        Bukkit.getServer().addRecipe(pekingDuckRecipe);

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

//        List<Material> pizzaIngredients = new ArrayList<>();
//        pizzaIngredients.add(Material.BREAD);
//        List<String> pizzaCustomIngredients = new ArrayList<>();
//        pizzaCustomIngredients.add("RED_SAUCE");
//        pizzaCustomIngredients.add("CHEESE");
//        registerCraftingRecipe("PIZZA_MARGHERITA", pizzaIngredients, pizzaCustomIngredients);

//        CustomItemBuilder pizzaBuilder = CustomItems.getCustomItemBuilder("PIZZA_MARGHERITA");
//        if(pizzaBuilder==null){
//            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
//            return;
//        }
//        pizzaBuilder.setAmount(1);
////        pizzaBuilder.setMaxStackSize(16);
//        ItemStack pizza = pizzaBuilder.build();
//        ShapelessRecipe pizzaRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "pizza_margherita"), pizza);
//        pizzaRecipe.addIngredient(Material.BREAD);
//        pizzaRecipe.addIngredient(CustomItems.getCustomItemBuilder("RED_SAUCE").build().getType());
//        pizzaRecipe.addIngredient(CustomItems.getCustomItemBuilder("CHEESE").build().getType());
//        Bukkit.getServer().addRecipe(pizzaRecipe);

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

//        List<Material> maritimePlatterIngredients = new ArrayList<>();
//        maritimePlatterIngredients.add(Material.COOKED_SALMON);
//        maritimePlatterIngredients.add(Material.DRIED_KELP);
//        List<String> maritimePlatterCustomIngredients = new ArrayList<>();
//        maritimePlatterCustomIngredients.add("PUFFERFISH_EXTRACT");
//        registerCraftingRecipe("MARITIME_PLATTER", maritimePlatterIngredients, maritimePlatterCustomIngredients);

//        CustomItemBuilder maritimePlatterBuilder = CustomItems.getCustomItemBuilder("MARITIME_PLATTER");
//        if(maritimePlatterBuilder==null){
//            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
//            return;
//        }
//        maritimePlatterBuilder.setAmount(1);
////        maritimePlatterBuilder.setMaxStackSize(16);
//        ItemStack maritimePlatter = maritimePlatterBuilder.build();
//        ShapelessRecipe maritimePlatterRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "maritime_platter"), maritimePlatter);
//        maritimePlatterRecipe.addIngredient(Material.COOKED_SALMON);
//        maritimePlatterRecipe.addIngredient(Material.DRIED_KELP);
//        pizzaRecipe.addIngredient(CustomItems.getCustomItemBuilder("PUFFERFISH_EXTRACT").build().getType());
//        Bukkit.getServer().addRecipe(maritimePlatterRecipe);

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

//        List<Material> lordsBlessingIngredients = new ArrayList<>();
//        lordsBlessingIngredients.add(Material.BAKED_POTATO);
//        List<String> lordsBlessingCustomIngredients = new ArrayList<>();
//        lordsBlessingCustomIngredients.add("BOILED_WATER");
//        lordsBlessingCustomIngredients.add("WHIPPED_CREAM");
//        lordsBlessingCustomIngredients.add("STEAMED_PUMPKIN");
//        registerCraftingRecipe("LORDS_BLESSING", lordsBlessingIngredients, lordsBlessingCustomIngredients);

//        CustomItemBuilder lordsBlessingBuilder = CustomItems.getCustomItemBuilder("LORDS_BLESSING");
//        if(lordsBlessingBuilder==null){
//            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
//            return;
//        }
//        lordsBlessingBuilder.setAmount(1);
////        lordsBlessingBuilder.setMaxStackSize(16);
//        ItemStack lordsBlessing = lordsBlessingBuilder.build();
//        ShapelessRecipe lordsBlessingRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "lords_blessing"), lordsBlessing);
//        lordsBlessingRecipe.addIngredient(Material.BAKED_POTATO);
//        lordsBlessingRecipe.addIngredient(CustomItems.getCustomItemBuilder("BOILED_WATER").build().getType());
//        lordsBlessingRecipe.addIngredient(CustomItems.getCustomItemBuilder("WHIPPED_CREAM").build().getType());
//        lordsBlessingRecipe.addIngredient(CustomItems.getCustomItemBuilder("STEAMED_PUMPKIN").build().getType());
//        Bukkit.getServer().addRecipe(lordsBlessingRecipe);

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

//        List<Material> dameBlancheIngredients = new ArrayList<>();
//        dameBlancheIngredients.add(Material.SWEET_BERRIES);
//        List<String> dameBlancheCustomIngredients = new ArrayList<>();
//        dameBlancheCustomIngredients.add("HOT_CHOCOLATE");
//        dameBlancheCustomIngredients.add("WHIPPED_CREAM");
//        registerCraftingRecipe("DAME_BLANCHE", dameBlancheIngredients, dameBlancheCustomIngredients);

//        CustomItemBuilder dameBlancheBuilder = CustomItems.getCustomItemBuilder("DAME_BLANCHE");
//        if(builderIsNull(dameBlancheBuilder) return;
//        dameBlancheBuilder.setAmount(1);
//        dameBlancheBuilder.setMaxStackSize(16);
//        ItemStack dameBlanche = dameBlancheBuilder.build();
//        ShapelessRecipe dameBlancheRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "dame_blanche"), dameBlanche);
//        dameBlancheRecipe.addIngredient(Material.SWEET_BERRIES);
//        dameBlancheRecipe.addIngredient(new RecipeChoice.ExactChoice(CustomItems.getCustomItemBuilder("HOT_CHOCOLATE").build()));
//        dameBlancheRecipe.addIngredient(new RecipeChoice.ExactChoice(CustomItems.getCustomItemBuilder("WHIPPED_CREAM").build()));
//        Bukkit.getServer().addRecipe(dameBlancheRecipe);
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

//    private static void registerCraftingRecipe(String customEnum, List<Material> ingredients, List<String> customIngredientEnums){
//        CustomItemBuilder builder = CustomItems.getCustomItemBuilder(customEnum);
//        builder.setAmount(1);
//        ItemStack item = builder.build();
//        ItemUtil.setBoolean(item, "zvieriGameItem", true);
//        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), customEnum.toLowerCase()), item);
//        if(ingredients != null){
//            for(Material ingredient : ingredients){
//                ItemStack ingredientStack = new ItemStack(ingredient);
//                ItemUtil.setBoolean(ingredientStack, "zvieriGameItem", true);
//                recipe.addIngredient(new RecipeChoice.ExactChoice(ingredientStack));
//            }
//        }
//        if(customIngredientEnums != null){
//            for(String customItemEnum : customIngredientEnums) {
//                ItemStack customIngredient = CustomItems.getCustomItemBuilder(customItemEnum).build();
//                ItemUtil.setBoolean(customIngredient, "zvieriGameItem", true);
//                recipe.addIngredient(new RecipeChoice.ExactChoice(customIngredient));
//            }
//        }
//        Bukkit.getServer().addRecipe(recipe);
//    }

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
