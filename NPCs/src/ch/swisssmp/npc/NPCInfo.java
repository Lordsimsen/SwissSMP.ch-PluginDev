package ch.swisssmp.npc;

import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public class NPCInfo {


    public static Vector getBaseOffset(EntityType entityType){
        switch(entityType){
            case VILLAGER: return new Vector(0,-0.75,0);
            default: return new Vector(0, -0.9f, 0);
        }

    }
}
