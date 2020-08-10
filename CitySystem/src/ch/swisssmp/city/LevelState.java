package ch.swisssmp.city;

import org.bukkit.ChatColor;

public enum LevelState {
    UNAVAILABLE("Nicht verfügbar", ChatColor.GRAY),
    BLOCKED("Blockiert", ChatColor.DARK_RED),
    AVAILABLE("Verfügbar", ChatColor.GREEN),
    UNLOCKED("Freigeschaltet", ChatColor.AQUA)
    ;

    private final String displayName;
    private final ChatColor color;

    LevelState(String displayName, ChatColor color){
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName(){
        return this.displayName;
    }

    public ChatColor getColor(){
        return this.color;
    }

    public static LevelState get(String key){
        try{
            return LevelState.valueOf(key);
        }
        catch(Exception e){
            for(LevelState state : LevelState.values()){
                if(state.toString().toLowerCase().equals(key.toLowerCase())) return state;
                String displayString = state.getColor()+state.getDisplayName();
                if(displayString.equals(key)) return state;
            }
            return null;
        }
    }
}
