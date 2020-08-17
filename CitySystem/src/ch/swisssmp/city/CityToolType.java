package ch.swisssmp.city;

import org.bukkit.inventory.ItemStack;

public enum CityToolType {
    SIGIL_RING,
    CITIZEN_BILL;

    public static CityToolType of(String s){
        try{
            return s!=null ? CityToolType.valueOf(s.toUpperCase()) : null;
        }
        catch(Exception ignored){
            return null;
        }
    }
}
