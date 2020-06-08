package ch.swisssmp.camerastudio;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CameraPathRunnable extends BukkitRunnable {

    private final CameraPath path;
    private final Player player;
    private final long time;

    protected CameraPathRunnable(CameraPath path, Player player, long time){
        this.path = path;
        this.player = player;
        this.time = time;
    }

    @Override
    public void run() {

    }

    protected void complete(){
        this.finish();
    }

    @Override
    public void cancel(){
        this.finish();
    }

    private void finish(){
        super.cancel();
    }
}
