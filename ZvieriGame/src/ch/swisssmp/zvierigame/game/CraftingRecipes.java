package ch.swisssmp.zvierigame.game;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.zvierigame.ZvieriGamePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

public class CraftingRecipes {


    /*
    Registers all the craftingRecipes for the 15 dishes (not for rabbit stew) and for
    the intermediate ingredient pasta
     */
    public static void registerCraftingRecipes(){
        CustomItemBuilder pastaBuilder = CustomItems.getCustomItemBuilder("PASTA");
        if(pastaBuilder==null){
            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
            return;
        }
        pastaBuilder.setAmount(1);
        ItemStack pasta = pastaBuilder.build();
        ShapelessRecipe pastaRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "pasta"), pasta);
        pastaRecipe.addIngredient(Material.WHEAT);
        pastaRecipe.addIngredient(Material.WHEAT);
        pastaRecipe.addIngredient(new RecipeChoice.ExactChoice(CustomItems.getCustomItemBuilder("BOILED_WATER").build()));
        Bukkit.getServer().addRecipe(pastaRecipe);


        CustomItemBuilder hashBrownsBuilder = CustomItems.getCustomItemBuilder("HASH_BROWNS");
        if(hashBrownsBuilder==null){
            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
            return;
        }
        hashBrownsBuilder.setAmount(1);
        ItemStack hashBrowns = hashBrownsBuilder.build();
        ShapelessRecipe hashBrownsRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "hash_browns"), hashBrowns);
        hashBrownsRecipe.addIngredient(Material.POTATO);
        hashBrownsRecipe.addIngredient(Material.POTATO);
        Bukkit.getServer().addRecipe(hashBrownsRecipe);


        CustomItemBuilder meatFeastBuilder = CustomItems.getCustomItemBuilder("MEAT_FEAST");
        if(meatFeastBuilder==null){
            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
            return;
        }
        meatFeastBuilder.setAmount(1);
        ItemStack meatFeast = meatFeastBuilder.build();
        ShapelessRecipe meatFeastRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "meat_feast"), meatFeast);
        meatFeastRecipe.addIngredient(Material.COOKED_BEEF);
        meatFeastRecipe.addIngredient(Material.COOKED_PORKCHOP);
        meatFeastRecipe.addIngredient(Material.COOKED_CHICKEN);
        Bukkit.getServer().addRecipe(meatFeastRecipe);


        CustomItemBuilder honeyMilkBuilder = CustomItems.getCustomItemBuilder("HONEY_MILK");
        if(honeyMilkBuilder==null){
            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
            return;
        }
        honeyMilkBuilder.setAmount(1);
        ItemStack honeyMilk = honeyMilkBuilder.build();
        ShapelessRecipe honeyMilkRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "honey_milk"), honeyMilk);
        honeyMilkRecipe.addIngredient(Material.HONEY_BOTTLE);
        honeyMilkRecipe.addIngredient(Material.MILK_BUCKET);
        Bukkit.getServer().addRecipe(honeyMilkRecipe);


        CustomItemBuilder ricePuddingBuilder = CustomItems.getCustomItemBuilder("RICE_PUDDING");
        if(ricePuddingBuilder==null){
            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
            return;
        }
        ricePuddingBuilder.setAmount(1);
        ItemStack ricePudding = ricePuddingBuilder.build();
        ShapelessRecipe ricePuddingRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "rice_pudding"), ricePudding);
        ricePuddingRecipe.addIngredient(Material.MILK_BUCKET);
        ricePuddingRecipe.addIngredient(Material.WHEAT);
        Bukkit.getServer().addRecipe(ricePuddingRecipe);


        CustomItemBuilder sushiBuilder = CustomItems.getCustomItemBuilder("SUSHI");
        if(sushiBuilder==null){
            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
            return;
        }
        sushiBuilder.setAmount(1);
        ItemStack sushi = sushiBuilder.build();
        ShapelessRecipe sushiRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "sushi"), sushi);
        sushiRecipe.addIngredient(Material.SALMON);
        sushiRecipe.addIngredient(Material.DRIED_KELP);
        sushiRecipe.addIngredient(Material.WHEAT);
        Bukkit.getServer().addRecipe(sushiRecipe);


        CustomItemBuilder schniPoBuilder = CustomItems.getCustomItemBuilder("SCHNITZEL_FRIES");
        if(schniPoBuilder==null){
            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
            return;
        }
        schniPoBuilder.setAmount(1);
        ItemStack schniPo = schniPoBuilder.build();
        ShapelessRecipe schniPoRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "schnitzel_fries"), schniPo);
        schniPoRecipe.addIngredient(Material.COOKED_PORKCHOP);
        schniPoRecipe.addIngredient(Material.BAKED_POTATO);
        schniPoRecipe.addIngredient(Material.BREAD);
        Bukkit.getServer().addRecipe(schniPoRecipe);


        CustomItemBuilder veggieBuilder = CustomItems.getCustomItemBuilder("VEGGIES_DELIGHT");
        if(veggieBuilder==null){
            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
            return;
        }
        veggieBuilder.setAmount(1);
        ItemStack veggiesDelight = veggieBuilder.build();
        ShapelessRecipe veggiesDelightRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "veggies_delight"), veggiesDelight);
        veggiesDelightRecipe.addIngredient(Material.KELP);
        veggiesDelightRecipe.addIngredient(Material.WHEAT);
        veggiesDelightRecipe.addIngredient(Material.CARROT);
        Bukkit.getServer().addRecipe(veggiesDelightRecipe);


        CustomItemBuilder spaghettiBuilder = CustomItems.getCustomItemBuilder("SPAGHETTI_BOLOGNESE");
        if(spaghettiBuilder==null){
            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
            return;
        }
        spaghettiBuilder.setAmount(1);
        ItemStack spaghetti = spaghettiBuilder.build();
        ShapelessRecipe spaghettiRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "spaghetti_bolognese"), spaghetti);
        spaghettiRecipe.addIngredient(CustomItems.getCustomItemBuilder("PASTA").build().getType());
        spaghettiRecipe.addIngredient(CustomItems.getCustomItemBuilder("RED_SAUCE").build().getType());
        spaghettiRecipe.addIngredient(Material.COOKED_BEEF);
        Bukkit.getServer().addRecipe(spaghettiRecipe);


        CustomItemBuilder creeperSucreBuilder = CustomItems.getCustomItemBuilder("CREEPER_SUCRE");
        if(creeperSucreBuilder==null){
            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
            return;
        }
        creeperSucreBuilder.setAmount(1);
        ItemStack creeperSucre = creeperSucreBuilder.build();
        ShapelessRecipe creeperSucreRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "creeper_sucre"), creeperSucre);
        creeperSucreRecipe.addIngredient(Material.CREEPER_HEAD);
        creeperSucreRecipe.addIngredient(Material.SUGAR);
        creeperSucreRecipe.addIngredient(CustomItems.getCustomItemBuilder("WHIPPED_CREAM").build().getType());
        creeperSucreRecipe.addIngredient(CustomItems.getCustomItemBuilder("PUFFERFISH_EXTRACT").build().getType());
        Bukkit.getServer().addRecipe(creeperSucreRecipe);


        CustomItemBuilder zurichGeschnetzeltesBuilder = CustomItems.getCustomItemBuilder("ZURICH_GESCHNETZELTES");
        if(zurichGeschnetzeltesBuilder==null){
            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
            return;
        }
        zurichGeschnetzeltesBuilder.setAmount(1);
        ItemStack zhSliced = zurichGeschnetzeltesBuilder.build();
        ShapelessRecipe zhSlicedRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "zurich_geschnetzeltes"), zhSliced);
        zhSlicedRecipe.addIngredient(Material.MILK_BUCKET);
        zhSlicedRecipe.addIngredient(Material.COOKED_BEEF);
        zhSlicedRecipe.addIngredient(CustomItems.getCustomItemBuilder("HASH_BROWNS").build().getType());
        Bukkit.getServer().addRecipe(zhSlicedRecipe);


        CustomItemBuilder pekingDuckBuilder = CustomItems.getCustomItemBuilder("PEKING_DUCK");
        if(pekingDuckBuilder==null){
            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
            return;
        }
        pekingDuckBuilder.setAmount(1);
        ItemStack pekingDuck = pekingDuckBuilder.build();
        ShapelessRecipe pekingDuckRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "peking_duck"), pekingDuck);
        pekingDuckRecipe.addIngredient(Material.COOKED_CHICKEN);
        pekingDuckRecipe.addIngredient(Material.SUGAR);
        pekingDuckRecipe.addIngredient(CustomItems.getCustomItemBuilder("BOILED_WATER").build().getType());
        Bukkit.getServer().addRecipe(pekingDuckRecipe);


        CustomItemBuilder pizzaBuilder = CustomItems.getCustomItemBuilder("PIZZA_MARGHERITA");
        if(pizzaBuilder==null){
            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
            return;
        }
        pizzaBuilder.setAmount(1);
        ItemStack pizza = pizzaBuilder.build();
        ShapelessRecipe pizzaRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "pizza_margherita"), pizza);
        pizzaRecipe.addIngredient(Material.BREAD);
        pizzaRecipe.addIngredient(CustomItems.getCustomItemBuilder("RED_SAUCE").build().getType());
        pizzaRecipe.addIngredient(CustomItems.getCustomItemBuilder("CHEESE").build().getType());
        Bukkit.getServer().addRecipe(pizzaRecipe);


        CustomItemBuilder maritimePlatterBuilder = CustomItems.getCustomItemBuilder("MARITIME_PLATTER");
        if(maritimePlatterBuilder==null){
            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
            return;
        }
        maritimePlatterBuilder.setAmount(1);
        ItemStack maritimePlatter = maritimePlatterBuilder.build();
        ShapelessRecipe maritimePlatterRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "maritime_platter"), maritimePlatter);
        maritimePlatterRecipe.addIngredient(Material.COOKED_SALMON);
        maritimePlatterRecipe.addIngredient(Material.DRIED_KELP);
        pizzaRecipe.addIngredient(CustomItems.getCustomItemBuilder("PUFFERFISH_EXTRACT").build().getType());
        Bukkit.getServer().addRecipe(maritimePlatterRecipe);


        CustomItemBuilder lordsBlessingBuilder = CustomItems.getCustomItemBuilder("LORDS_BLESSING");
        if(lordsBlessingBuilder==null){
            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
            return;
        }
        lordsBlessingBuilder.setAmount(1);
        ItemStack lordsBlessing = lordsBlessingBuilder.build();
        ShapelessRecipe lordsBlessingRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "lords_blessing"), lordsBlessing);
        lordsBlessingRecipe.addIngredient(Material.BAKED_POTATO);
        lordsBlessingRecipe.addIngredient(CustomItems.getCustomItemBuilder("BOILED_WATER").build().getType());
        lordsBlessingRecipe.addIngredient(CustomItems.getCustomItemBuilder("WHIPPED_CREAM").build().getType());
        lordsBlessingRecipe.addIngredient(CustomItems.getCustomItemBuilder("STEAMED_PUMPKIN").build().getType());
        Bukkit.getServer().addRecipe(lordsBlessingRecipe);


        CustomItemBuilder dameBlancheBuilder = CustomItems.getCustomItemBuilder("DAME_BLANCHE");
        if(dameBlancheBuilder==null){
            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " Rezept konnte nicht geladen werden");
            return;
        }
        dameBlancheBuilder.setAmount(1);
        ItemStack dameBlanche = dameBlancheBuilder.build();
        ShapelessRecipe dameBlancheRecipe = new ShapelessRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "dame_blanche"), dameBlanche);
        dameBlancheRecipe.addIngredient(Material.SWEET_BERRIES);
        dameBlancheRecipe.addIngredient(new RecipeChoice.ExactChoice(CustomItems.getCustomItemBuilder("HOT_CHOCOLATE").build()));
        dameBlancheRecipe.addIngredient(new RecipeChoice.ExactChoice(CustomItems.getCustomItemBuilder("WHIPPED_CREAM").build()));
        Bukkit.getServer().addRecipe(dameBlancheRecipe);
    }

    public static void registerFurnaceRecipes(){
        //Todo furnace recipe or smelting event? And how does one make them dependent on permission (given to participants)?
        Bukkit.addRecipe(new FurnaceRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "red_sauce")
                , CustomItems.getCustomItemBuilder("RED_SAUCE").build(), Material.BEETROOT, 0, 160));
        Bukkit.addRecipe(new FurnaceRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "steamed_creeper_head")
                , CustomItems.getCustomItemBuilder("STEAMED_CREEPER_HEAD").build(), Material.CREEPER_HEAD, 0, 200));
        Bukkit.addRecipe(new FurnaceRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "steamed_pumpkin")
                , CustomItems.getCustomItemBuilder("STEAMED_PUMPKIN").build(), Material.PUMPKIN, 0, 200));
        Bukkit.addRecipe(new FurnaceRecipe(new NamespacedKey(ZvieriGamePlugin.getInstance(), "cheese")
                , CustomItems.getCustomItemBuilder("CHEESE").build(), Material.MILK_BUCKET, 0, 200));
    }

    public static void registerBrewingRecipes(){
        BrewingRecipe.addRecipe(new BrewingRecipe(Material.PUFFERFISH, (inventory, result, ingredient) -> {
            ItemStack pufferfishExtract = CustomItems.getCustomItemBuilder("PUFFERFISH_EXTRACT").build();
            result.setItemMeta(pufferfishExtract.getItemMeta());
            result.setAmount(1);
        }));
        BrewingRecipe.addRecipe(new BrewingRecipe(Material.MILK_BUCKET, (inventory, result, ingredient) -> {
            ItemStack whippedCream = CustomItems.getCustomItemBuilder("WHIPPED_CREAM").build();
            result.setItemMeta(whippedCream.getItemMeta());
            result.setAmount(1);
        }));
        BrewingRecipe.addRecipe(new BrewingRecipe(Material.WATER_BUCKET, (inventory, result, ingredient) -> {
            ItemStack boiledWater = CustomItems.getCustomItemBuilder("BOILED_WATER").build();
            result.setItemMeta(boiledWater.getItemMeta());
            result.setAmount(1);
        }));
    }
}
