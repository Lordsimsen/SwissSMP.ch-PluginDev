package ch.swisssmp.zvierigame.game;

import ch.swisssmp.zvierigame.ZvieriGame;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public abstract class Phase implements Runnable {

    private final ZvieriGame game;
    private boolean completed = false;

    protected Phase(ZvieriGame game) {
        this.game = game;
    }

    protected ZvieriGame getGame(){
        return game;
    }

    public void setCompleted(){
        completed = true;
    }

    public boolean isCompleted(){
        return completed;
    }

    public abstract void initialize();
    public abstract void finish();
    public abstract void complete();
    public abstract void cancel();

    public abstract void onPlayerRespawn(PlayerRespawnEvent event);

}
