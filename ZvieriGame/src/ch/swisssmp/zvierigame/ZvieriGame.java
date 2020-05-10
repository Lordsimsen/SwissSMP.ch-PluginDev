package ch.swisssmp.zvierigame;

import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zvierigame.game.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class ZvieriGame implements Runnable{

    private final static long PREPARATION_TIME = 600;
    private final String gameName;

    private static List<ZvieriGame> games = new ArrayList<ZvieriGame>();

    private List<Player> participants;

    private ZvieriArena arena;
    private Level level;

    private int score;

    private Phase currentPhase;

    private BukkitTask task;

    private ZvieriGame(ZvieriArena arena, Level level){
        this.arena = arena;
        this.level = level;
        participants = new ArrayList<Player>();

        gameName = level.getName() + " " + arena.getName();
    }

    @Override
    public void run() {
        if(!currentPhase.isCompleted()) currentPhase.run();
        if(currentPhase.isCompleted()){
            currentPhase.complete();
            currentPhase.finish();
            this.currentPhase = getNextPhase(this.currentPhase);
            if(this.currentPhase == null){
                this.complete();
            } else{
                this.currentPhase.initialize();
            }
        }
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event){
        if(this.participants.contains(event.getPlayer())) return;
        this.leave(event.getPlayer());
    }

    @EventHandler
    private void onPlayerDeath(PlayerRespawnEvent event){
        this.currentPhase.onPlayerRespawn(event);
    }

    private Phase getNextPhase(Phase currentPhase){
        if(currentPhase instanceof PreparationPhase) {
            GamePhase gamePhase = new GamePhase(this);
            return gamePhase;
        }
        if(currentPhase instanceof GamePhase) return new EndingPhase(this);
        return null;
    }

    public void join(Player player){
        if(participants.size() >= 4) {
            SwissSMPler.get(player).sendActionBar(ChatColor.YELLOW + "Spiel bereits voll");
            return;
        }
        this.participants.add(player);
        SwissSMPler.get(player).sendActionBar(ChatColor.GREEN + "Spiel beigetreten");
    }

    public void leave(Player player){
        this.participants.remove(player);
        if(!(this.currentPhase instanceof PreparationPhase)){
            player.teleport(this.arena.getQueue().getLocation(this.arena.getWorld()));
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
        SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Spiel verlassen");
        if(this.participants.isEmpty()){
            this.cancel();
        }
    }

    public static ZvieriGame prepare(ZvieriArena arena, Level level){
        ZvieriGame game = new ZvieriGame(arena, level);
        games.add(game);
        game.start();
        return game;
    }

    private void start(){
        this.currentPhase = new PreparationPhase(this, PREPARATION_TIME);
        task = Bukkit.getScheduler().runTaskTimer(ZvieriGamePlugin.getInstance(), this, 0, 1);
    }

    public void startNow(){
        if(!(currentPhase instanceof PreparationPhase)) return;
        currentPhase.setCompleted();
    }

    public List<Player> getParticipants(){
        return new ArrayList<Player>(participants);
    }

    private void finish(){
        if(task!=null) task.cancel();
        games.remove(this);
        participants.clear();
        arena.endGame();
        Bukkit.getPluginManager().registerEvents(ZvieriGamePlugin.getEventListener(), ZvieriGamePlugin.getInstance()); //TODO odr
    }

    public void cancel(){
        if(this.currentPhase!=null){
            this.currentPhase.cancel();
            this.currentPhase.finish();
        }
        for(Player player : participants){
            SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Spiel abgebrochen");
        }
        finish();
    }

    private void complete(){
        finish();
    }

    public Phase getCurrentPhase(){
        return currentPhase;
    }

    public ZvieriArena getArena(){
        return this.arena;
    }

    public Level getLevel(){
        return this.level;
    }

    public String getGameName(){
        return gameName;
    }

    public int getScore(){
        return score;
    }

    public void setScore(int score){
        this.score = score;
    }
}
