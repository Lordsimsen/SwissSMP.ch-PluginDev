package ch.swisssmp.zones.editor;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;

public class ZoneEditors {
    private static final HashMap<Player,ZoneEditor> editors = new HashMap<>();

    protected static Optional<ZoneEditor> get(Player player){
        return editors.containsKey(player) ? Optional.of(editors.get(player)) : Optional.empty();
    }

    protected static void add(Player player, ZoneEditor editor){
        editors.put(player, editor);
    }

    protected static void remove(Player player){
        editors.remove(player);
    }

    protected static void cancelAll(){
        for(ZoneEditor editor : editors.values()){
            editor.cancel();
        }
    }
}
