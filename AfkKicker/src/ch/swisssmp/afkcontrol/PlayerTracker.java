package ch.swisssmp.afkcontrol;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerTracker {

    private final Player player;
    private Location afkLocation;
    private long timeout;
    private boolean afk;
    private boolean warned;

    protected PlayerTracker(Player player){
        this.player = player;
    }

    public Player getPlayer(){
        return player;
    }

    public long getTimeout(){
        return timeout;
    }

    public void setTimeout(long timeout){
        this.timeout = timeout;
    }

    public boolean isAfk(){
        return afk;
    }

    protected void setAfk(boolean afk){
        this.afk = afk;
        if(!afk) warned = false;
    }

    public Location getAfkLocation(){
        return afkLocation;
    }

    public void setAfkLocation(Location location){
        this.afkLocation = location;
    }

    public void setWarned(boolean warned){
        this.warned = warned;
    }

    public boolean isWarned(){
        return warned;
    }

    protected void tick(long time){
        timeout+=time;
    }

    public synchronized void reset(){
        timeout = 0;
        warned = false;
        afk = false;
    }
}
