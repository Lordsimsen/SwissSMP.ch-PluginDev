package ch.swisssmp.city.npcs;

import ch.swisssmp.city.CitySystemPlugin;
import org.bukkit.NamespacedKey;

public enum NpcType {
    ADDON_GUIDE("ag");

    private static NamespacedKey key;

    private final String identifier;

    NpcType(String identifier){
        this.identifier = identifier;
    }

    public String getIdentifier(){return identifier;}

    public static NpcType getByIdentifier(String identifier){
        for(NpcType type : NpcType.values()){
            if(type.identifier.equals(identifier)) return type;
        }
        return null;
    }

    public static NamespacedKey getKey(){
        if(key==null){
            key = new NamespacedKey(CitySystemPlugin.getInstance(), "npc_type");
        }

        return key;
    }
}
