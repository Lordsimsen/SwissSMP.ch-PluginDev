package ch.swisssmp.zvieriplausch.game;

import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zvieriplausch.ZvieriArena;
import ch.swisssmp.zvieriplausch.ZvieriGame;
import ch.swisssmp.zvieriplausch.ZvieriGamePlugin;
import ch.swisssmp.zvieriplausch.ZvieriSound;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;

public class EndingPhase extends Phase{

    private final ZvieriGame game;
    private final ZvieriArena arena;
    private final World world;

    public EndingPhase(ZvieriGame game){
        super(game);
        this.game = game;
        this.arena = game.getArena();
        this.world = arena.getWorld();
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
            ZvieriGame.cleanseInventory(player.getInventory());
            updateLevelUnlocks();
        }
        if (game.getArena().updateHighscore(game.getLevel().getLevelNumber(), game.getScore(), game.getParticipants())){
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
            Bukkit.getServer().broadcastMessage(players + " einen neuen Highscore in " + ChatColor.DARK_PURPLE +
                    game.getGameName() + ChatColor.RESET + " aufgestellt: " + ChatColor.YELLOW + game.getScore() + " Smaragdmuenzen");
            playFinishSound(true);
        } else{
            playFinishSound(false);
        }
    }

    private void playFinishSound(boolean highscore){
        Location location = arena.getJukebox().getLocation();
        if(highscore) {
            world.playSound(location, ZvieriSound.HIGHSCORE, SoundCategory.RECORDS, 10f, 1f);
            return;
        }
        if(game.getScore() >= game.getLevel().getThreshhold()) {
            world.playSound(location, ZvieriSound.SUCCESS, SoundCategory.RECORDS, 10f, 1f);
        } else{
            world.playSound(location, ZvieriSound.FAILED, SoundCategory.RECORDS, 10f, 1f);
        }
    }

    private void updateLevelUnlocks(){
        int threshhold = game.getLevel().getThreshhold();
        if(game.getScore() >= threshhold){
            game.getArena().updateLevelUnlock(game.getParticipants(), game.getLevel());
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
