package ch.swisssmp.ceremonies;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Ceremonies {

    private static List<Ceremony> ceremonies = new ArrayList<Ceremony>();

    public static void add(Ceremony ceremony){
        ceremonies.add(ceremony);
    }

    public static void remove(Ceremony ceremony){
        ceremonies.remove(ceremony);
    }

    public static Ceremony get(String key){
        for(Ceremony ceremony : ceremonies){
            if(!ceremony.isMatch(key)) continue;
            return ceremony;
        }
        return null;
    }

    public static Ceremony getLast(){
        if(ceremonies.size()==0) return null;
        return ceremonies.get(ceremonies.size()-1);
    }

    public static void cancelAll(){
        for(Ceremony ceremony : new ArrayList<>(ceremonies)){
            ceremony.cancel();
        }
    }

    public static boolean isParticipantAnywhere(Player player){
        for(Ceremony ceremony : ceremonies){
            if(ceremony.isParticipant(player)) return true;
        }
        return false;
    }
}
