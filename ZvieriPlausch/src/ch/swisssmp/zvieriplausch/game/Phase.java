package ch.swisssmp.zvieriplausch.game;

import ch.swisssmp.zvieriplausch.ZvieriPlauschGame;
import org.bukkit.event.player.PlayerRespawnEvent;

public abstract class Phase implements Runnable {

    private final ZvieriPlauschGame game;
    private boolean completed = false;

    protected Phase(ZvieriPlauschGame game) {
        this.game = game;
    }

    protected ZvieriPlauschGame getGame(){
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
