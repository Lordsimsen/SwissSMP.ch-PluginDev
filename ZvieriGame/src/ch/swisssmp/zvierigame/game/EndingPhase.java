package ch.swisssmp.zvierigame.game;

import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zvierigame.ZvieriGame;
import ch.swisssmp.zvierigame.ZvieriGamePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;

public class EndingPhase extends Phase{

    private final ZvieriGame game;

    public EndingPhase(ZvieriGame game){
        super(game);
        this.game = game;
    }

    @Override
    public void run() {
        game.clearArena();
        for(Player player : game.getParticipants()){
            ZvieriGame.cleanseInventory(player.getInventory());
            player.teleport(game.getArena().getQueue().getLocation(game.getArena().getWorld()));
        }
        this.setCompleted();
    }

    @Override
    public void finish() {
        for(Player player : game.getParticipants()){
            Bukkit.getScheduler().runTaskLater(ZvieriGamePlugin.getInstance(), () -> {
                SwissSMPler.get(player).sendTitle(ChatColor.GREEN + "Fin de partie!", "Score: " + ChatColor.YELLOW + game.getScore());
            }, 1L);
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
        if (game.getArena().updateHighscore(game.getLevel().getLevelNumber(), game.getScore(), game.getParticipants())){
            Bukkit.getLogger().info(game.getParticipants().size() + " Teilnehmer im finish");
            String players = "";
            List<Player> participants = game.getParticipants();
            if(participants.size() == 1) {
                players += (participants.get(0).getDisplayName() + " hat");
            } else {
                for(int i = 0; i < participants.size(); i++){
                    players += (participants.get(i).getDisplayName() + " ");
                }
                players += " haben";
            }
            Bukkit.getServer().broadcastMessage(players + " einen neuen Highscore in " +
                    game.getGameName() + " aufgestellt: " + game.getScore() + " Smaragdmuenzen"); //TODO seperate-with-comma-method please
        }
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
