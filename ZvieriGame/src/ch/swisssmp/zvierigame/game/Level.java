package ch.swisssmp.zvierigame.game;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.utils.Random;
import ch.swisssmp.zvierigame.ZvieriGame;
import ch.swisssmp.zvierigame.ZvieriGamePlugin;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
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

    private final HashMap<String, ItemStack> allIngredients = new HashMap<>();

    private final ItemStack[] allDishes = new ItemStack[]{
            (CustomItems.getCustomItemBuilder("HASH_BROWNS").build()), //0
            CustomItems.getCustomItemBuilder("MEAT_FEAST").build(),
            (CustomItems.getCustomItemBuilder("HONEY_MILK").build()),//2
            (CustomItems.getCustomItemBuilder("RICE_PUDDING").build()),
            (CustomItems.getCustomItemBuilder("SUSHI").build()), //4
            (CustomItems.getCustomItemBuilder("SCHNITZEL_FRIES").build()), //5
            (CustomItems.getCustomItemBuilder("VEGGIES_DELIGHT").build()),

            (CustomItems.getCustomItemBuilder("SPAGHETTI_BOLOGNESE").build()), //7
            (CustomItems.getCustomItemBuilder("DAME_BLANCHE").build()),

            (CustomItems.getCustomItemBuilder("ZURICH_GESCHNETZELTES").build()), //9
            (CustomItems.getCustomItemBuilder("HOT_CHOCOLATE").build()),
            (CustomItems.getCustomItemBuilder("PEKING_DUCK").build()), //11
            (CustomItems.getCustomItemBuilder("PIZZA_MARGHERITA").build()),
            (CustomItems.getCustomItemBuilder("MARITIME_PLATTER").build()), //13
            (CustomItems.getCustomItemBuilder("CREEPER_SUCRE").build()),
            (CustomItems.getCustomItemBuilder("LORDS_BLESSING").build()) //15
    };

//    private final ItemStack[] easyDishes = new ItemStack[]{allDishes[0], allDishes[1], allDishes[2], allDishes[3],
//                                                            allDishes[4], allDishes[6], allDishes[7], allDishes[5]};
//
//    private final ItemStack[] mediumDishes = new ItemStack[]{allDishes[8], allDishes[9], allDishes[10], allDishes[11],
//                                                            allDishes[12], allDishes[13], allDishes[14], allDishes[15]};
//
//    private final ItemStack[] hardDishes = new ItemStack[]{allDishes[16]};

    public static HashMap<String, List<String>> recipes = new HashMap<String, List<String>>();

    /*
    Gives the recipe for [dishEnum] in form of a piece of paper with the ingredients necessary as its lore
     */
    protected ItemStack getRecipe(String dishEnum){
        Configuration config = ZvieriGamePlugin.getInstance().getConfig();
        ConfigurationSection dishes = config.getConfigurationSection("dishes");
        ItemStack recipe = new ItemStack(Material.PAPER);
        ItemMeta recipeMeta = recipe.getItemMeta();
        recipeMeta.setDisplayName(ChatColor.AQUA + "Rezept fuer " + ChatColor.WHITE + dishes.getString(dishEnum + ".name"));
        recipeMeta.setLore(dishes.getStringList(dishEnum + ".recipe"));
        recipe.setItemMeta(recipeMeta);
        ItemUtil.setBoolean(recipe, "zvieriGameItem", true);
        return recipe;
    }

    public Level(int level) {
        this.level = level;
        initializeIngredients();
        setDuration();
    }

    /*
    Define the intensity of each level by spawning clients in more or less big intervals.
    Note that in addition to more or less clients spawning, higher levels have more (and more complex) dishes
    as possible orders.
     */
    protected Client spawnClient(long time, Location location){
        Configuration config = ZvieriGamePlugin.getInstance().getConfig();
        ConfigurationSection levels = config.getConfigurationSection("levels");
        switch(level){
            case 1:{
                ConfigurationSection level = levels.getConfigurationSection("level_" + getLevelNumber());
                if(time >= duration*20) return null;
                if(time==(level.getInt("firstclient")*20)) return new Client(getNPC(location));
                if(time%(level.getInt("baseinterval")*20)==0) return new Client(getNPC(location));
                if(time>(120*20) && time%(25*20) == 0) return new Client(getNPC(location));
            }
            case 2:{
                ConfigurationSection level = levels.getConfigurationSection("level_" + getLevelNumber());
                if(time >= duration*20) return null;
                if(time==(level.getInt("firstclient")*20)) return new Client(getNPC(location));
                if(time%(level.getInt("baseinterval")*20)==0) return new Client(getNPC(location));
                if(time>(120*20) && time%(25*20) == 0) return new Client(getNPC(location));
            }
            case 3:{
                ConfigurationSection level = levels.getConfigurationSection("level_" + getLevelNumber());
                if(time >= duration*20) return null;
                if(time==(level.getInt("firstclient")*20)) return new Client(getNPC(location));
                if(time%(level.getInt("baseinterval")*20)==0) return new Client(getNPC(location));
                if(time>(120*20) && time%(25*20) == 0) return new Client(getNPC(location));
            }
            case 4:{
                ConfigurationSection level = levels.getConfigurationSection("level_" + getLevelNumber());
                if(time >= duration*20) return null;
                if(time==(level.getInt("firstclient")*20)) return new Client(getNPC(location));
                if(time%(level.getInt("baseinterval")*20)==0) return new Client(getNPC(location));
                if(time>(120*20) && time%(25*20) == 0) return new Client(getNPC(location));
                if(time>(240*20) && time%(10*20) == 0) return new Client(getNPC(location));
            }
            case 5:{
                ConfigurationSection level = levels.getConfigurationSection("level_" + getLevelNumber());
                if(time >= duration*20) return null;
                if(time==(level.getInt("firstclient")*20)) return new Client(getNPC(location));
                if(time%(level.getInt("baseinterval")*20)==0) return new Client(getNPC(location));
                if(time>(120*20) && time%(25*20) == 0) return new Client(getNPC(location));
                if(time>(300*20) && time < (500*20) && time%(10*20) == 0) return new Client(getNPC(location));
                if(time>(400*20) && time%(20*20) == 0) return new Client(getNPC(location));
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
                JsonUtil.set("baseTip", 2, json);
                JsonUtil.set("patience", 1.0, json);
                break;
            }
            case 1:{
                villager.setProfession(Villager.Profession.WEAPONSMITH);
//                npc.setName("Schmied");
//                npc.setNameVisible(true);
                JsonUtil.set("baseTip", 5, json);
                JsonUtil.set("patience", 0.75, json);
                break;
            }
            case 2:{
                villager.setProfession(Villager.Profession.LIBRARIAN);
//                npc.setName("Gelehrter");
//                npc.setNameVisible(true);
                JsonUtil.set("baseTip", 10, json);
                JsonUtil.set("patience", 0.4, json);
                break;
            }
            case 3: {
                villager.setProfession(Villager.Profession.CLERIC);
//                npc.setName("Alchemist");
//                npc.setNameVisible(true);
                JsonUtil.set("baseTip", 7, json);
                JsonUtil.set("patience", 0.6, json);
                break;
            }
            case 4: {
                villager.setProfession((Villager.Profession.SHEPHERD));
//                npc.setName("Hirte");
//                npc.setNameVisible(true);
                JsonUtil.set("baseTip", 4, json);
                JsonUtil.set("patience", 0.9, json);
                break;
            }
            case 5: {
                villager.setProfession(Villager.Profession.CARTOGRAPHER);
//                npc.setName("Aristokrat");
//                npc.setNameVisible(true);
                JsonUtil.set("baseTip", 20, json);
                JsonUtil.set("patience", 0.2, json);
                break;
            }
        }
        npc.setJsonData(json);
    }

    /*
    Returns the recipes as papers with ingredients as lore for dishes that can be ordered in the selected level.
    Done manually, so watch out when modifying the dishes order and stuff.
     */
    protected ItemStack[] getRecipes(){
        switch(level){
            case 1: {
                return new ItemStack[]{getRecipe("MEAT_FEAST"), getRecipe("HONEY_MILK"), getRecipe("HASH_BROWNS")};
            }
            case 2: {
                return new ItemStack[]{getRecipe("HONEY_MILK"), getRecipe("HASH_BROWNS")
                                ,getRecipe("RICE_PUDDING"), getRecipe("SUSHI")};
            }
            case 3: {
                return new ItemStack[]{getRecipe("SCHNITZEL_FRIES"), getRecipe("VEGGIES_DELIGHT"),
                                                    getRecipe("DAME_BLANCHE"), getRecipe("SPAGHETTI_BOLOGNESE")};
            }
            case 4: {
                return new ItemStack[]{getRecipe("VEGGIES_DELIGHT"), getRecipe("DAME_BLANCHE"),
                        getRecipe("SPAGHETTI_BOLOGNESE"), getRecipe("ZURICH_GESCHNETZELTES"), getRecipe("HOT_CHOCOLATE")};

            }
            case 5: {
                return new ItemStack[]{getRecipe("CREEPER_SUCRE"), getRecipe("LORDS_BLESSING"),
                        getRecipe("PIZZA_MARGHERITA"), getRecipe("DAME_BLANCHE"), getRecipe("SCHNITZEL_FRIES")};
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
                List<ItemStack> dishes = new ArrayList<>();
                dishes.add(allDishes[0]);
                dishes.add(allDishes[1]);
                dishes.add(allDishes[2]);
                int random = new Random().nextInt(dishes.size());
                ItemStack dish = dishes.get(random);
                ItemUtil.setBoolean(dish, "zvieriGameItem", true);
                return dish;
            }
            case 2: {
                List<ItemStack> dishes = new ArrayList<>();
                dishes.add(allDishes[0]);
                dishes.add(allDishes[2]);
                dishes.add(allDishes[3]);
                dishes.add(allDishes[4]);
                int random = new Random().nextInt(dishes.size());
                ItemStack dish = dishes.get(random);
                ItemUtil.setBoolean(dish, "zvieriGameItem", true);
                return dish;
            }
            case 3: {
                List<ItemStack> dishes = new ArrayList<>();
                int random = new Random().nextInt(dishes.size());
                dishes.add(allDishes[5]);
                dishes.add(allDishes[6]);
                dishes.add(allDishes[7]);
                dishes.add(allDishes[8]);
                ItemStack dish = dishes.get(random);
                ItemUtil.setBoolean(dish, "zvieriGameItem", true);
                return dish;
            }
            case 4: {
                List<ItemStack> dishes = new ArrayList<>();
                int random = new Random().nextInt(dishes.size());
                dishes.add(allDishes[6]);
                dishes.add(allDishes[7]);
                dishes.add(allDishes[8]);
                dishes.add(allDishes[9]);
                dishes.add(allDishes[10]);
                ItemStack dish = dishes.get(random);
                ItemUtil.setBoolean(dish, "zvieriGameItem", true);
                return dish;
            }
            case 5: {
                List<ItemStack> dishes = new ArrayList<>();
                int random = new Random().nextInt(dishes.size());
                dishes.add(allDishes[5]);
                dishes.add(allDishes[8]);
                dishes.add(allDishes[12]);
                dishes.add(allDishes[14]);
                dishes.add(allDishes[15]);
                ItemStack dish = dishes.get(random);
                ItemUtil.setBoolean(dish, "zvieriGameItem", true);
                return dish;
//                int random = new Random().nextInt(allDishes.length);
//                return allDishes[random];
            }
        }
        return null;
    }

    private void setDuration() {
        Configuration config = ZvieriGamePlugin.getInstance().getConfig();
        ConfigurationSection levels = config.getConfigurationSection("levels");
        duration = levels.getInt("level_" + getLevelNumber() + ".duration");
    }

    private void initializeIngredients(){
        allIngredients.put(Material.COCOA_BEANS.toString(), new ItemStack(Material.COCOA_BEANS));
        allIngredients.put(Material.CARROT.toString(), new ItemStack(Material.CARROT));
        allIngredients.put(Material.SUGAR.toString(), new ItemStack(Material.SUGAR));
        allIngredients.put(Material.COCOA_BEANS.toString(), new ItemStack(Material.COCOA_BEANS));
        allIngredients.put(Material.PORKCHOP.toString(), new ItemStack(Material.PORKCHOP));
        allIngredients.put(Material.BEEF.toString(), new ItemStack(Material.BEEF));
        allIngredients.put(Material.CHICKEN.toString(), new ItemStack(Material.CHICKEN));
        allIngredients.put("ZVIERI_MILK_BUCKET", CustomItems.getCustomItemBuilder("ZVIERI_MILK_BUCKET").build());
        allIngredients.put("ZVIERI_HONEY_BOTTLE", CustomItems.getCustomItemBuilder("ZVIERI_HONEY_BOTTLE").build());
        allIngredients.put(Material.PUFFERFISH.toString(), new ItemStack(Material.PUFFERFISH));
        allIngredients.put(Material.POTATO.toString(), new ItemStack(Material.POTATO));
        allIngredients.put(Material.RABBIT_FOOT.toString(), new ItemStack(Material.RABBIT_FOOT));
        allIngredients.put(Material.CREEPER_HEAD.toString(), new ItemStack(Material.CREEPER_HEAD));
        allIngredients.put(Material.WHEAT.toString(), new ItemStack(Material.WHEAT));
        allIngredients.put(Material.SALMON.toString(), new ItemStack(Material.SALMON));
        allIngredients.put(Material.KELP.toString(), new ItemStack(Material.KELP));
        allIngredients.put(Material.PUMPKIN.toString(), new ItemStack(Material.PUMPKIN));
        allIngredients.put(Material.SWEET_BERRIES.toString(), new ItemStack(Material.SWEET_BERRIES));
        allIngredients.put(Material.BEETROOT.toString(), new ItemStack(Material.BEETROOT));
        allIngredients.put(Material.COAL.toString(), new ItemStack(Material.COAL));
        allIngredients.put(Material.GLASS_BOTTLE.toString(), new ItemStack(Material.GLASS_BOTTLE));
        allIngredients.put("ZVIERI_WATER_BUCKET", CustomItems.getCustomItemBuilder("ZVIERI_WATER_BUCKET").build());
    }

    public String getName(){
        Configuration config = ZvieriGamePlugin.getInstance().getConfig();
        ConfigurationSection levels = config.getConfigurationSection("levels");
        return levels.getString("level_" + getLevelNumber() + ".name");
    }

    public int getThreshhold(){
        Configuration config = ZvieriGamePlugin.getInstance().getConfig();
        ConfigurationSection levels = config.getConfigurationSection("levels");
        return levels.getInt("level_" + getLevelNumber() + ".threshhold");
    }

    public int getLevelNumber(){
        return level;
    }

    protected HashMap<String,ItemStack> getIngredients() {
        return allIngredients;
    }
}
