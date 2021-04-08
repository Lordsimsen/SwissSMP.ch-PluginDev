package ch.swisssmp.ripzoneevent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GamePartipicant {

    private final UUID player_uuid;

    public GamePartipicant(Player player){
        if(player!=null)
            this.player_uuid = player.getUniqueId();
        else
            this.player_uuid = null;
    }

    public UUID getPlayerUUID(){
        return this.player_uuid;
    }
    public Player getPlayer(){
        return Bukkit.getPlayer(this.player_uuid);
    }

    public void sendMessage(String message){
        Player player = Bukkit.getPlayer(this.player_uuid);
        if(player!=null) player.sendMessage(message);
    }


}
