package ch.swisssmp.camerastudio;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CameraPathSequenceRunnable {

    private final CameraPathSequence sequence;
    private final Player player;
    private final Runnable callback;

    private final List<UUID> pathSequence;
    private final List<Integer> timings;

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
        int pathSequenceSize = this.pathSequence.size();
        if(index>=pathSequenceSize){
            complete();
            return;
        }
        UUID elementUid = pathSequence.get(index);
        CameraPathElement element = CameraStudioWorlds.getElement(elementUid).orElse(null);
        if(element!=null){
            if(element instanceof CameraPath){
                CameraPath path = (CameraPath) element;
                boolean isLastPath = index+1>=pathSequenceSize;
                CameraStudio.inst().travel(player, path, timings.get(index), isLastPath, ()->runSequence(index+1));
                runCommands(path.getCommands());
                return;
            }
            else if(element instanceof CameraPathSequence){
                ((CameraPathSequence) element).run(player, ()->runSequence(index+1));
            }
            return;
        }
        runSequence(index+1);
    }

    private void runCommands(Collection<String> commands){
        CommandSender sender = Bukkit.getConsoleSender();
        for(String c : commands){
            String commandLine = c
                    .replace("{player}", player.getName())
                    .replace("{world}", sequence.getWorld().getBukkitWorld().getName());
            try{
                Bukkit.dispatchCommand(sender, commandLine);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
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
