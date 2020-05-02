package ch.swisssmp.zvierigame.game;

import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.utils.Random;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Level {

    private final int level;

    protected long duration;

    private final ItemStack[] allIngredients = new ItemStack[]{
            new ItemStack(Material.CARROT),
            new ItemStack(Material.SUGAR),
            new ItemStack(Material.COCOA_BEANS),
            new ItemStack(Material.PORKCHOP),
            new ItemStack(Material.MILK_BUCKET),
            new ItemStack(Material.HONEY_BOTTLE),
            new ItemStack(Material.CHICKEN),
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


//    private final ItemStack[] allDishes = new ItemStack[]{
//            new ItemStack(CustomItems.getCustomItemBuilder("ROESCHTI").build()),
//            new ItemStack(CustomItems.getCustomItemBuilder("FLEISCH_SCHMAUS").build()),
//            new ItemStack(CustomItems.getCustomItemBuilder("HONIGMILCH").build()),
//            new ItemStack(CustomItems.getCustomItemBuilder("MILCHREIS").build()),
//            new ItemStack(CustomItems.getCustomItemBuilder("SUSHI").build()),
//            new ItemStack(CustomItems.getCustomItemBuilder("SOUPE_ASENSEE").build()),
//            new ItemStack(CustomItems.getCustomItemBuilder("SCHNI_PO").build()),
//            new ItemStack(CustomItems.getCustomItemBuilder("VEGIS_DELIGHT").build()),
//
//            new ItemStack(CustomItems.getCustomItemBuilder("SPAGHETTI_BOLOGNESE").build()),
//            new ItemStack(CustomItems.getCustomItemBuilder("CREEPER_SUCRE").build()),
//            new ItemStack(CustomItems.getCustomItemBuilder("ZUERIGSCHNETZLETS").build()),
//            new ItemStack(CustomItems.getCustomItemBuilder("HEISSI_SCHOGGI").build()),
//            new ItemStack(CustomItems.getCustomItemBuilder("PEKING_ENTE").build()),
//            new ItemStack(CustomItems.getCustomItemBuilder("PIZZA_MARTHARITA").build()),
//            new ItemStack(CustomItems.getCustomItemBuilder("MARITIMER_MEERESPLATTE").build()),
//            new ItemStack(CustomItems.getCustomItemBuilder("THE_LORDS_BLESSING").build()),
//
//            new ItemStack(CustomItems.getCustomItemBuilder("COUPE_DAENEMARK").build()),
//    };
//
//    private final ItemStack[] easyDishes = new ItemStack[]{allDishes[0], allDishes[1], allDishes[2], allDishes[3],
//                                                            allDishes[4], allDishes[5], allDishes[6], allDishes[7]};
//
//    private final ItemStack[] mediumDishes = new ItemStack[]{allDishes[8], allDishes[9], allDishes[10], allDishes[11],
//                                                            allDishes[12], allDishes[13], allDishes[14], allDishes[15]};
//
//    private final ItemStack[] hardDishes = new ItemStack[]{allDishes[16]};


    public static ItemStack[] ingredients;
    public static HashMap<String, ItemStack> recipes = new HashMap<String, ItemStack>();

    public Level(int level) {
        this.level = level;
        setDuration();
        setClientTypes(level);
    }

    protected Client spawnClient(long time, Location location){
        switch(level){
            case 1:{
                if(time==(10*20)) return new Client(getNPC(location));
                if(time%(15*20)==0) return new Client(getNPC(location));
                if(time>(120*20) && time%(20*20) == 0) return new Client(getNPC(location));
//                if(time>200 && time%10 == 0)return true;
                if(time>duration) return null;
            }
            default: return null;
        }
    }

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
            case 1:{
                int random = new Random().nextInt(3);
                switch(random){
                    case 0:{
                        villager.setProfession(Villager.Profession.NITWIT);
                        npc.setName("Landstreicher");
                        npc.setNameVisible(true);
                        JsonUtil.set("baseTip", 5, npc.getJsonData());
                        JsonUtil.set("patience", 1.0, npc.getJsonData());
                        break;
                    }
                    case 1:{
                        villager.setProfession(Villager.Profession.WEAPONSMITH);
                        npc.setName("Schmied");
                        npc.setNameVisible(true);
                        JsonUtil.set("baseTip", 10, npc.getJsonData());
                        JsonUtil.set("patience", 0.75, npc.getJsonData());
                        break;
                    }
                    case 2:{
                        villager.setProfession(Villager.Profession.LIBRARIAN);
                        npc.setName("Gelehrter");
                        npc.setNameVisible(true);
                        JsonUtil.set("baseTip", 20, npc.getJsonData());
                        JsonUtil.set("patience", 0.4, npc.getJsonData());
                        break;
                    }
                }
            }
            default: return npc;
        }
    }

    protected ItemStack getRandomDish(){
        return new ItemStack(Material.PUMPKIN);
//        switch(level) {
//            case 1: {
//                int random = new Random().nextInt(easyDishes.length - 5);
//                return easyDishes[random];
//            }
//            case 2: {
//                int random = new Random().nextInt(easyDishes.length - 2);
//                return easyDishes[random];
//            }
//            case 3: {
//                int random = new Random().nextInt(mediumDishes.length - 4);
//                return mediumDishes[random];
//            }
//            case 4: {
//                int random = new Random().nextInt(mediumDishes.length - 2);
//                return mediumDishes[random+2];
//            }
//            case 5: {
//                int random = new Random().nextInt(allDishes.length);
//                return allDishes[random];
//            }
//        }
//        return null;
    }

    private void setDuration() {
        switch (level) {
            case 1: {
                duration = 300;
                break;
            }
            case 2: {
                duration = 360;
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

    private void setClientTypes(int level){
        JsonObject json = new JsonObject(); // NPCInstance.create(EntityType.VILLAGER, new Location(Bukkit.getWorld("Valinor"), 0, 0, 0)).getJsonData();
//        json.addProperty("patience", );
    }

    public NPCInstance getRandomType(){

        return null;
    }


    private ItemStack[] getDishes(int level){
        return null;
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

    private ItemStack getIngredient(int i){
        return allIngredients[i];
    }

    /*
     * Takes the first (level + 4) ingredients out of the possible ingredients to be used for this game.
     */
    protected ItemStack[] getIngredients() {
        return allIngredients;
    }
}
