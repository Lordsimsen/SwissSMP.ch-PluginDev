package ch.swisssmp.zvierigame.game;

import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zvierigame.ZvieriGame;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;

public class EndingPhase extends Phase{

    private final ZvieriGame game;

    public EndingPhase(ZvieriGame game){
        super(game);
        this.game = game;
    }

    @Override
    public void run() {
        for(Player player : game.getParticipants()){
            player.teleport(game.getArena().getQueue().getLocation(game.getArena().getWorld()));
        }
        this.finish();
    }

    @Override
    public void finish() {
        for(Player player : game.getParticipants()){
//            if (highScore){
//                Bukkit.getServer().getConsoleSender().sendMessage("broadcast " + game.getParticipants() + " haben einen neuen Highscore in" +
//                    game.getGameName() + " aufgestellt:" + highScore.get()); //seperate-with-comma-method please
//            }
            SwissSMPler.get(player).sendTitle(ChatColor.GREEN + "Fin de partie!", "Score: " + ChatColor.YELLOW + game.getScore());
        }
        this.setCompleted();
    }

    @Override
    public void initialize() {
    }

    @Override
    public void complete() {
    }

    @Override
    public void cancel() {
    }

    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    }
}
