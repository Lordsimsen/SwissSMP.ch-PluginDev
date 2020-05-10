package ch.swisssmp.zvierigame.game;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.utils.Random;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Level {

    private final int level;

    private int clientCount = 0;

    protected long duration;

    public final ItemStack[] allIngredients = new ItemStack[]{
            new ItemStack(Material.CARROT),
            new ItemStack(Material.SUGAR),
            new ItemStack(Material.COCOA_BEANS),
            new ItemStack(Material.PORKCHOP),
            new ItemStack(Material.BEEF),
            new ItemStack(Material.CHICKEN),
            new ItemStack(Material.MILK_BUCKET),
            new ItemStack(Material.HONEY_BOTTLE),
            new ItemStack(Material.PUFFERFISH),
            new ItemStack(Material.POTATO),
            new ItemStack(Material.RABBIT_FOOT),
            new ItemStack(Material.CREEPER_HEAD),
            new ItemStack(Material.WHEAT),
            new ItemStack(Material.SALMON),
            new ItemStack(Material.KELP),
            new ItemStack(Material.PUMPKIN),
            new ItemStack(Material.SWEET_BERRIES),
            new ItemStack(Material.BEETROOT)
    };


    private final ItemStack[] allDishes = new ItemStack[]{
            (CustomItems.getCustomItemBuilder("HASH_BROWNS").build()),
            CustomItems.getCustomItemBuilder("MEAT_FEAST").build(),
            (CustomItems.getCustomItemBuilder("HONEY_MILK").build()),
            (CustomItems.getCustomItemBuilder("RICE_PUDDING").build()),
            (CustomItems.getCustomItemBuilder("SUSHI").build()),
            new ItemStack(Material.RABBIT_STEW),
            (CustomItems.getCustomItemBuilder("SCHNITZEL_FRIES").build()),
            (CustomItems.getCustomItemBuilder("VEGGIES_DELIGHT").build()),

            (CustomItems.getCustomItemBuilder("SPAGHETTI_BOLOGNESE").build()),
            (CustomItems.getCustomItemBuilder("CREEPER_SUCRE").build()),

            (CustomItems.getCustomItemBuilder("ZURICH_GESCHNETZELTES").build()),
            (CustomItems.getCustomItemBuilder("HOT_CHOCOLATE").build()),
            (CustomItems.getCustomItemBuilder("PEKING_DUCK").build()),
            (CustomItems.getCustomItemBuilder("PIZZA_MARGHERITA").build()),
            (CustomItems.getCustomItemBuilder("MARITIME_PLATTER").build()),
            (CustomItems.getCustomItemBuilder("LORDS_BLESSING").build()),
            (CustomItems.getCustomItemBuilder("DAME_BLANCHE").build())
    };

    private final ItemStack[] easyDishes = new ItemStack[]{allDishes[0], allDishes[1], allDishes[2], allDishes[3],
                                                            allDishes[4], allDishes[5], allDishes[6], allDishes[7]};

    private final ItemStack[] mediumDishes = new ItemStack[]{allDishes[8], allDishes[9], allDishes[10], allDishes[11],
                                                            allDishes[12], allDishes[13], allDishes[14], allDishes[15]};

    private final ItemStack[] hardDishes = new ItemStack[]{allDishes[16]};

    public static HashMap<String, List<String>> recipes = new HashMap<String, List<String>>();


    /*
    Presets all the recipes. Could/Should probably be defined in a constant yml or json object in the plugin folder.
     */
    protected void setRecipes(){
        List<String> hashBrownsRecipe = new ArrayList<String>();
        hashBrownsRecipe.add("2x Gebratene Kartoffel");
        recipes.put("HASH_BROWNS", hashBrownsRecipe);

        List<String> meatFeastRecipe = new ArrayList<String>();
        meatFeastRecipe.add("1x Gebratenes Poulet");
        meatFeastRecipe.add("1x Steak");
        meatFeastRecipe.add("1x Gebratenes Schweinefleisch");
        recipes.put("MEAT_FEAST", meatFeastRecipe);

        List<String> honeyMilkRecipe = new ArrayList<String>();
        honeyMilkRecipe.add("1x Honig");
        honeyMilkRecipe.add("1x Milch");
        recipes.put("HONEY_MILK", honeyMilkRecipe);

        List<String> ricePuddingRecipe = new ArrayList<String>();
        ricePuddingRecipe.add("1x Milch");
        ricePuddingRecipe.add("1x Weizen");
        recipes.put("RICE_PUDDING", ricePuddingRecipe);

        List<String> sushiRecipe = new ArrayList<String>();
        sushiRecipe.add("1x Getrockneter Seetang");
        sushiRecipe.add("1x roher Lachs");
        sushiRecipe.add("1x Weizen");
        recipes.put("SUSHI", sushiRecipe);

        List<String> rabbitStewRecipe = new ArrayList<String>();
        rabbitStewRecipe.add("1x Hasenfuss");
        rabbitStewRecipe.add("1x Kochwasser");
        rabbitStewRecipe.add("1x Karotte");
        rabbitStewRecipe.add("1x Kartoffel");
        recipes.put("RABBIT_STEW", rabbitStewRecipe);

        List<String> schnitzelFriesRecipe = new ArrayList<String>();
        schnitzelFriesRecipe.add("1x gebratenes Schweinefleisch");
        schnitzelFriesRecipe.add("1x Bratkartoffeln");
        schnitzelFriesRecipe.add("1x Weizen");
        recipes.put("SCHNITZEL_FRIES", schnitzelFriesRecipe);

        List<String> veggiesDelightRecipe = new ArrayList<String>();
        veggiesDelightRecipe.add("1x Seegras");
        veggiesDelightRecipe.add("1x Weizen");
        veggiesDelightRecipe.add("1x Karotte");
        recipes.put("VEGGIES_DELIGHT", veggiesDelightRecipe);

        List<String> spaghettiBologneseRecipe = new ArrayList<String>();
        spaghettiBologneseRecipe.add("1x Pasta");
        spaghettiBologneseRecipe.add("1x Rote Sauce");
        spaghettiBologneseRecipe.add("1x Steak");
        recipes.put("SPAGHETTI_BOLOGNESE", spaghettiBologneseRecipe);

        List<String> creeperSucreRecipe = new ArrayList<String>();
        creeperSucreRecipe.add("1x Geduensteter Creeperkopf");
        creeperSucreRecipe.add("1x Zucker");
        creeperSucreRecipe.add("1x Sahne");
        recipes.put("CREEPER_SUCRE", creeperSucreRecipe);

        List<String> zurichGeschnetzeltesRecipe = new ArrayList<String>();
        zurichGeschnetzeltesRecipe.add("1x Steak");
        zurichGeschnetzeltesRecipe.add("1x Röschti");
        zurichGeschnetzeltesRecipe.add("1x Milch");
        recipes.put("ZURICH_GESCHNETZELTES", zurichGeschnetzeltesRecipe);

        List<String> hotChocolateRecipe = new ArrayList<String>();
        hotChocolateRecipe.add("1x Milch");
        hotChocolateRecipe.add("1x Schokoladenpulver");
        hotChocolateRecipe.add("1x Zucker");
        recipes.put("HOT_CHOCOLATE", hotChocolateRecipe);

        List<String> pekingDuckRecipe = new ArrayList<String>();
        pekingDuckRecipe.add("1x gebratenes Poulet");
        pekingDuckRecipe.add("1x Zucker");
        pekingDuckRecipe.add("1x Kochwasser");
        recipes.put("PEKING_DUCK", pekingDuckRecipe);

        List<String> pizzaMargheritaRecipe = new ArrayList<String>();
        pizzaMargheritaRecipe.add("1x Brot");
        pizzaMargheritaRecipe.add("1x Rote Sauce");
        pizzaMargheritaRecipe.add("1x Kaese");
        recipes.put("PIZZA_MARGHERITA", pizzaMargheritaRecipe);

        List<String> maritimePlatterRecipe = new ArrayList<String>();
        maritimePlatterRecipe.add("1x Gebratener Laches");
        maritimePlatterRecipe.add("1x Pufferfischextrakt");
        maritimePlatterRecipe.add("1x Gebratenes Seegras");
        recipes.put("MARITIME_PLATTER", maritimePlatterRecipe);

        List<String> lordsBlessingRecipe = new ArrayList<String>();
        lordsBlessingRecipe.add("1x geduensteter Kuerbis");
        lordsBlessingRecipe.add("1x Bratkartoffeln");
        lordsBlessingRecipe.add("1x Kochwasser");
        lordsBlessingRecipe.add("1x Sahne");
        recipes.put("LORDS_BLESSING", lordsBlessingRecipe);

        List<String> dameBlancheRecipe = new ArrayList<String>();
        dameBlancheRecipe.add("1x Heissi Schoggi");
        dameBlancheRecipe.add("1x Sahen");
        dameBlancheRecipe.add("1x Wildbeeren");
        recipes.put("DAME_BLANCHE", dameBlancheRecipe);
    }

    /*
    Gives the recipe for [dishEnum] in form of a piece of paper with the ingredients necessary as its lore
     */
    protected ItemStack getRecipe(String dishEnum){
        ItemStack recipe = new ItemStack(Material.PAPER);
        ItemMeta recipeMeta = recipe.getItemMeta();
        String name;
        if(!dishEnum.equalsIgnoreCase("RABBIT_STEW")){
            name = CustomItems.getCustomItemBuilder(dishEnum).build().getItemMeta().getDisplayName();
        } else{
            name = "Hasensee-Ragout";
        }
        recipeMeta.setDisplayName(ChatColor.AQUA + "Rezept fuer " + ChatColor.WHITE + name);
        recipeMeta.setLore(recipes.get(dishEnum));
        recipe.setItemMeta(recipeMeta);
        return recipe;
    }

    public Level(int level) {
        this.level = level;
        setDuration();
        setRecipes();
    }

    /*
    Define the intensity of each level by spawning clients in more or less big intervals.
    Note that in addition to more or less clients spawning, higher levels have more (and more complex) dishes
    as possible orders.
     */
    protected Client spawnClient(long time, Location location){
        switch(level){
            case 1:{
                if(time==(10*20)) return new Client(getNPC(location));
                if(time%(20*20)==0) return new Client(getNPC(location));
                if(time>(120*20) && time%(25*20) == 0) return new Client(getNPC(location));
                if(time>duration) return null;
            }
            case 2:{
                if(time==(10*20)) return new Client(getNPC(location));
                if(time%(15*20)==0) return new Client(getNPC(location));
                if(time>(120*20) && time%(25*20) == 0) return new Client(getNPC(location));
                if(time>duration) return null;
            }
            case 3:{
                if(time==(5*20)) return new Client(getNPC(location));
                if(time%(15*20)==0) return new Client(getNPC(location));
                if(time>(120*20) && time%(25*20) == 0) return new Client(getNPC(location));
                if(time>duration) return null;
            }
            case 4:{
                if(time==(5*20)) return new Client(getNPC(location));
                if(time%(15*20)==0) return new Client(getNPC(location));
                if(time>(120*20) && time%(25*20) == 0) return new Client(getNPC(location));
                if(time>(240*20) && time%(10*20) == 0) return new Client(getNPC(location));
                if(time>duration) return null;
            }
            case 5:{
                if(time==(5*20)) return new Client(getNPC(location));
                if(time%(15*20)==0) return new Client(getNPC(location));
                if(time>(120*20) && time%(25*20) == 0) return new Client(getNPC(location));
                if(time>(300*20) && time < (500*20) && time%(10*20) == 0) return new Client(getNPC(location));
                if(time>(500*20) && time%(20*20) == 0) return new Client(getNPC(location));
                if(time>duration) return null;
            }
            default: return null;
        }
    }

    /*
    Returns a random NPC with dish and type from a selection which is defined per level.
     */
    private NPCInstance getNPC(Location location){
        NPCInstance npc = NPCInstance.create(EntityType.VILLAGER, location.add(new Random().nextDouble()*3, 0, new Random().nextDouble()*3));
        if(npc.getJsonData()==null) npc.setJsonData(new JsonObject());
        ArmorStand dishCarrier = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        dishCarrier.setVisible(false);
        dishCarrier.setGravity(false);
        dishCarrier.setSmall(true);
        dishCarrier.getEquipment().setHelmet((getRandomDish()));
        npc.getEntity().addPassenger(dishCarrier);
        Villager villager = (Villager) npc.getEntity();
        switch(level){
            case 1:
            case 2:{
                int random = new Random().nextInt(3);
                switch(random){
                    case 0:{
                        getNPCType(npc, villager, 0);
                        break;
                    }
                    case 1:{
                        getNPCType(npc, villager, 1);
                        break;
                    }
                    case 2:{
                        getNPCType(npc, villager, 2);
                        break;
                    }
                }
            }
            case 3:
            case 4: {
                int random = new Random().nextInt(5);
                switch(random){
                    case 0:{
                        getNPCType(npc, villager, 0);
                        break;
                    }
                    case 1:{
                        getNPCType(npc, villager, 1);
                        break;
                    }
                    case 2:{
                        getNPCType(npc, villager, 2);
                        break;
                    }
                    case 3: {
                        getNPCType(npc, villager, 3);
                        break;
                    }
                    case 4: {
                        getNPCType(npc, villager, 4);
                        break;
                    }
                }
            }
            case 5: {
                int random = new Random().nextInt(6);
                switch(random){
                    case 0:{
                        getNPCType(npc, villager, 0);
                        break;
                    }
                    case 1:{
                        getNPCType(npc, villager, 1);
                        break;
                    }
                    case 2:{
                        getNPCType(npc, villager, 2);
                        break;
                    }
                    case 3: {
                        getNPCType(npc, villager, 3);
                        break;
                    }
                    case 4: {
                        getNPCType(npc, villager, 4);
                        break;
                    }
                    case 5: {
                        getNPCType(npc, villager, 5);
                        break;
                    }
                }
            }
            default: {
                npc.setIdentifier("client_" + clientCount);
                Bukkit.getLogger().info("returning npc with basetip" + JsonUtil.getInt("baseTip", npc.getJsonData()) + "and patience " + JsonUtil.getDouble("patience", npc.getJsonData()));
                clientCount++;
                return npc;
            }
        }
    }

    private void getNPCType(NPCInstance npc, Villager villager, int type){
        JsonObject json = npc.getJsonData();
        switch(type){
            case 0:{
                villager.setProfession(Villager.Profession.NITWIT);
//                npc.setName("Landstreicher");
//                npc.setNameVisible(true);
                JsonUtil.set("baseTip", 5, json);
                JsonUtil.set("patience", 1.0, json);
                break;
            }
            case 1:{
                villager.setProfession(Villager.Profession.WEAPONSMITH);
//                npc.setName("Schmied");
//                npc.setNameVisible(true);
                JsonUtil.set("baseTip", 10, json);
                JsonUtil.set("patience", 0.75, json);
                break;
            }
            case 2:{
                villager.setProfession(Villager.Profession.LIBRARIAN);
//                npc.setName("Gelehrter");
//                npc.setNameVisible(true);
                JsonUtil.set("baseTip", 20, json);
                JsonUtil.set("patience", 0.4, json);
                break;
            }
            case 3: {
                villager.setProfession(Villager.Profession.CLERIC);
//                npc.setName("Alchemist");
//                npc.setNameVisible(true);
                JsonUtil.set("baseTip", 15, json);
                JsonUtil.set("patience", 0.6, json);
                break;
            }
            case 4: {
                villager.setProfession((Villager.Profession.SHEPHERD));
//                npc.setName("Hirte");
//                npc.setNameVisible(true);
                JsonUtil.set("baseTip", 8, json);
                JsonUtil.set("patience", 0.9, json);
                break;
            }
            case 5: {
                villager.setProfession(Villager.Profession.CARTOGRAPHER);
//                npc.setName("Aristokrat");
//                npc.setNameVisible(true);
                JsonUtil.set("baseTip", 40, json);
                JsonUtil.set("patience", 0.2, json);
                break;
            }
        }
        npc.setJsonData(json);
    }

    /*
    Returns the recipes as papers with ingredients as lore for dishes that can be ordered in the selected level.
     */
    protected ItemStack[] getRecipes(){
        switch(level){
            case 1: {
                return new ItemStack[]{getRecipe("MEAT_FEAST"), getRecipe("HONEY_MILK"), getRecipe("HASH_BROWNS")};
            }
            case 2: {
                return new ItemStack[]{getRecipe("MEAT_FEAST"), getRecipe("HONEY_MILK"), getRecipe("HASH_BROWNS")
                                ,getRecipe("RICE_PUDDING"), getRecipe("SUSHI"), getRecipe("RABBIT_STEW")};
            }
            case 3: {
                return new ItemStack[]{getRecipe("ZURICH_GESCHNETZELTES"), getRecipe("HOT_CHOCOLATE"),
                                                    getRecipe("CREEPER_SUCRE"), getRecipe("SPAGHETTI_BOLOGNESE")};
            }
            case 4: {
                return new ItemStack[]{getRecipe("ZURICH_GESCHNETZELTES"), getRecipe("HOT_CHOCOLATE"),
                                getRecipe("PEKING_DUCK"), getRecipe("PIZZA_MARGHERITA"), getRecipe("MARITIME_PLATTER"),
                                getRecipe("LORDS_BLESSING")};
            }
            case 5: {
                ItemStack[] allRecipes = new ItemStack[recipes.size()];
                int i = 0;
                for(String key : recipes.keySet()){
                    ItemStack recipe = new ItemStack(Material.PAPER);
                    ItemMeta recipeMeta = recipe.getItemMeta();
                    recipeMeta.setDisplayName(ChatColor.AQUA + "Rezept fuer " + CustomItems.getCustomItemBuilder(key).build().getItemMeta().getDisplayName());
                    recipeMeta.setLore(recipes.get(key));
                    recipe.setItemMeta(recipeMeta);
                    allRecipes[i] = recipe;
                    i++;
                }
                return allRecipes;
            }
            default: return null;
        }
    }

    /*
    Returns one of some selected dishes, depending on the selected level.
     */
    protected ItemStack getRandomDish(){
        switch(level) {
            case 1: {
                int random = new Random().nextInt(easyDishes.length - 5);
                return easyDishes[random];
            }
            case 2: {
                int random = new Random().nextInt(easyDishes.length - 2);
                return easyDishes[random];
            }
            case 3: {
                int random = new Random().nextInt(mediumDishes.length - 4);
                return mediumDishes[random];
            }
            case 4: {
                int random = new Random().nextInt(mediumDishes.length - 2);
                return mediumDishes[random+2];
            }
            case 5: {
                int random = new Random().nextInt(allDishes.length);
                return allDishes[random];
            }
        }
        return null;
    }

    private void setDuration() {
        switch (level) {
            case 1: {
                duration = 30; //quicktesting. +'0' originally
                break;
            }
            case 2: {
                duration = 90; //360 originally
                break;
            }
            case 3: {
                duration = 420;
                break;
            }
            case 4: {
                duration = 480;
                break;
            }
            case 5: {
                duration = 540;
                break;
            }
            default:
                duration = 600;
                break;
        }
    }

    public String getName(){
        switch (level) {
            case 1: {
                return "Apprentis";
            }
            case 2: {
                return "Commis de Cuisine";
            }
            case 3: {
                return "Chef de Partie";
            }
            case 4: {
                return "Sous Chef";
            }
            case 5: {
                return "Chef de cuisine";
            }
            default:{
                return "Secret";
            }
        }
    }

    public int getLevelNumber(){
        return level;
    }

    protected ItemStack[] getIngredients() {
        return allIngredients;
    }
}
