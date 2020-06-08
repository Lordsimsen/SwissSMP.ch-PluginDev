package ch.swisssmp.camerastudio;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CameraPathSequenceRunnable {

    private final CameraPathSequence sequence;
    private final Player player;
    private final Runnable callback;

    private final List<UUID> pathSequence;
    private final HashMap<UUID,Integer> timings;

    private boolean finished;

    protected CameraPathSequenceRunnable(CameraPathSequence sequence, Player player, Runnable callback){
        this.sequence = sequence;
        this.player = player;
        this.callback = callback;

        this.pathSequence = sequence.getPathSequence();
        this.timings = sequence.getTimings();
    }

    protected void start(){
        this.runSequence(0);
    }

    private void runSequence(int index){
        if(index>=this.pathSequence.size()){
            complete();
            return;
        }
        UUID elementUid = pathSequence.get(index);
        CameraPath cameraPath = CameraStudio.inst().getPath(elementUid).orElse(null);
        if(cameraPath!=null){
            int duration = timings.get(cameraPath.getUniqueId());
            CameraStudio.inst().travelSimple(player, cameraPath.getPoints(), duration*20, ()->runSequence(index+1));
            return;
        }
        CameraPathSequence sequence = CameraStudio.inst().getSequence(elementUid).orElse(null);
        if(sequence!=null){
            sequence.run(player, ()->runSequence(index+1));
            return;
        }
        runSequence(index+1);
    }

    private void complete(){
        if(finished) return;
        finished = true;
        if(callback!=null) callback.run();
        finish();
    }

    public void cancel(){
        if(finished) return;
        finished = true;
        finish();
    }

    private void finish(){
        if(finished) return;
        finished = true;

    }
}
