package ch.swisssmp.city;

import java.util.Arrays;

public enum AddonStateReason {
    CITY_LEVEL,
    REQUIRED_ADDONS,
    NONE
    ;

    public static AddonStateReason of(String s){
        return Arrays.stream(AddonStateReason.values()).filter(e->e.toString().equalsIgnoreCase(s)).findAny().orElse(null);
    }
}
