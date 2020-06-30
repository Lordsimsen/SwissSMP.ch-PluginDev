package ch.swisssmp.zvieriplausch;

import ch.swisssmp.customitems.CustomItems;
import org.bukkit.inventory.ItemStack;

public enum Dish {

    HASH_BROWNS("HASH_BROWNS"),
    MEAT_FEAST("MEAT_FEAST"),
    HONEY_MILK("HONEY_MILK"),
    RICE_PUDDING("RICE_PUDDING"),
    SUSHI("SUSHI"),
    SCHNITZEL_FRIES("SCHNITZEL_FRIES"),
    VEGGIES_DELIGHT("VEGGIES_DELIGHT"),
    SPAGHETTI_BOLOGNESE("SPAGHETTI_BOLOGNESE"),
    DAME_BLANCHE("DAME_BLANCHE"),
    ZURICH_GESCHNETZELTES("ZURICH_GESCHNETZELTES"),
    HOT_CHOCOLATE("HOT_CHOCOLATE"),
    PEKING_DUCK("PEKING_DUCK"),
    PIZZA_MARGHERITA("PIZZA_MARGHERITA"),
    MARITIME_PLATTER("MARITIME_PLATTER"),
    CREEPER_SUCRE("CREEPER_SUCRE"),
    LORDS_BLESSING("LORDS_BLESSING");

    private final String customEnum;

    Dish(String customEnum) {
        this.customEnum = customEnum;
    }

    public String getCustomEnum() {
        return customEnum;
    }

    public ItemStack getItemStack() {
        return CustomItems.getCustomItemBuilder(customEnum).build();
    }

    public static Dish of(String string) {
        switch (string.toLowerCase()) {
            case "hash_browns":
            case "röschti":
                return HASH_BROWNS;
            case "meat_feast":
            case "fleischschmaus":
                return MEAT_FEAST;
            case "honey_milk":
            case "honigmilch":
                return HONEY_MILK;
            case "rice_pudding":
            case "milchreis":
                return RICE_PUDDING;
            case "sushi":
                return SUSHI;
            case "schnitzel_fries":
            case "schnipo":
                return SCHNITZEL_FRIES;
            case "veggies_delight":
            case "veggie's delight":
            case "vegi's delight":
                return VEGGIES_DELIGHT;
            case "spaghetti_bolognese":
            case "spaghetti":
            case "spaghetti bolognese":
                return SPAGHETTI_BOLOGNESE;
            case "dame_blanche":
            case "coupe dänemark":
                return DAME_BLANCHE;
            case "zurich_geschnetzeltes":
            case "zürigschnetzlets":
            case "züri gschnetzlets":
                return ZURICH_GESCHNETZELTES;
            case "hot_chocolate":
            case "heissi schoggi":
                return HOT_CHOCOLATE;
            case "peking_duck":
            case "peking ente":
                return PEKING_DUCK;
            case "pizza_margherita":
            case "pizza":
            case "pizza margherita":
                return PIZZA_MARGHERITA;
            case "maritime_platter":
            case "maritimer meeresplatte":
                return MARITIME_PLATTER;
            case "creeper_sucre":
            case "creeper sucré":
            case "creeper sucre":
                return CREEPER_SUCRE;
            case "lords_blessing":
            case "the lord's blessing":
            case "lord's blessing":
            case "the lords blessing":
            case "lords blessing":
                return LORDS_BLESSING;
            default:
                return null;
        }
    }
}
