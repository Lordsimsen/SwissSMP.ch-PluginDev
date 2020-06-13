package ch.swisssmp.zvieriplausch.game;

import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zvieriplausch.ZvieriGame;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PreparationPhase extends Phase{

    private long time = 0L;
    private final long duration;

    private int lastRemaining = -1;

    public PreparationPhase(ZvieriGame game, long duration){
        super(game);
        this.duration = duration;
    }

    @Override
    public void run(){
        time++;
        int remaining = Mathf.ceilToInt((duration-time)/20);
        if(remaining != lastRemaining){
            for(Player player : this.getGame().getParticipants()){
                this.sendCountdown(player, remaining);
            }
            lastRemaining = remaining;
        }
        if(time>=duration){
            setCompleted();
        }
    }

    @Override
    public void cancel(){

    }

    private void sendCountdown(Player player, int remaining){
        if(remaining > 0) SwissSMPler.get(player).sendTitle("", "Spiel startet in " + remaining);
        else SwissSMPler.get(player).sendTitle("", "A la cuisine!");
    }

    @Override
    public void initialize(){

    }

    @Override
    public void finish(){

    }

    @Override
    public void complete(){

    }

    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event){

    }
}
