package ch.swisssmp.zvieriplausch;

import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zvieriplausch.game.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Furnace;
import org.bukkit.block.Smoker;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;

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
        if(!(currentPhase instanceof PreparationPhase)) {
            SwissSMPler.get(player).sendActionBar(ChatColor.YELLOW + "Kann laufendem Spiel nicht beitreten");
            return;
        }
        this.participants.add(player);
        SwissSMPler.get(player).sendActionBar(ChatColor.GREEN + "Spiel beigetreten");
    }

    public void leave(Player player){
        this.participants.remove(player);
        if(!(this.currentPhase instanceof PreparationPhase)){
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
        arena.getRecipeDisplay().setLevel(level);
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

    /*
    Removes all items lying around inside the arena (Could be abused if someone throws something outside the boundingBox)
    which are tagged with "zvieriGameItem" or are empty buckets/bottles as they will be obtained during crafting
    but can't properly be tagged (I think).
     */
    public void clearArena(){
        World world = arena.getWorld();
        ProtectedRegion region = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getRegion(arena.getArenaRegion());
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        BoundingBox arenaBox = new BoundingBox(min.getX(),min.getY(),min.getZ(),max.getX(),max.getY(),max.getZ());
        for(Entity entity : arena.getWorld().getNearbyEntities(arenaBox)){
            if(!(entity instanceof Item)) continue;
            ItemStack item = ((Item) entity).getItemStack();
            if(ItemUtil.getBoolean(item, "zvieriGameItem") || item.getType() == Material.BUCKET || item.getType() == Material.GLASS_BOTTLE) entity.remove();
        }
        for(int i = min.getBlockX(); i <= max.getBlockX(); i++){
            for(int j = min.getBlockY(); j <= max.getBlockY(); j++){
                for(int k = min.getBlockZ(); k <= max.getBlockZ(); k++){
                    Block block = arena.getWorld().getBlockAt(i, j, k);
                    if(block.getType() == Material.FURNACE){
                        ((Furnace) block.getState()).getInventory().clear();
                    }
                    if(block.getType() == Material.BREWING_STAND){
                        ((BrewingStand) block.getState()).getInventory().clear();
                    }
                    if(block.getType() == Material.SMOKER){
                        ((Smoker) block.getState()).getInventory().clear();
                    }
                }
            }
        }
    }

    /*
    Cleanses a players inventory from all items tagged with "zvieriGameItem" as well as all empty buckets and bottles,
    because they will be obtained during crafting dishes, but can't easily be tagged (I think).
     */
    public static void cleanseInventory(PlayerInventory inventory){
        for(int i = 0; i < inventory.getContents().length; i++){
            ItemStack item = inventory.getContents()[i];
            if(item == null || item.getType() == Material.AIR) continue;
            if(ItemUtil.getBoolean(item, "zvieriGameItem") || item.getType() == Material.BUCKET || item.getType() == Material.GLASS_BOTTLE){
                inventory.remove(item);
            }
        }
        ItemStack helmet = inventory.getHelmet();
        if(helmet == null || helmet.getType() == Material.AIR) return;
        if(ItemUtil.getBoolean(helmet, "zvieriGameItem")) inventory.setHelmet(new ItemStack(Material.AIR));
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
