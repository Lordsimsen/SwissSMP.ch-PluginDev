package ch.swisssmp.city.ceremony.promotion;

import ch.swisssmp.ceremonies.Ceremonies;
import ch.swisssmp.ceremonies.Ceremony;
import ch.swisssmp.ceremonies.Phase;
import ch.swisssmp.city.City;
import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.city.ceremony.promotion.phases.*;
import ch.swisssmp.utils.Random;
import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockVector;

import java.util.*;

public class CityPromotionCeremony extends Ceremony implements Listener {

    public static final Material baseMaterial = Material.HAY_BLOCK;
    public static final float ceremonyRange = 20;

    private static Collection<Player> ceremoniesParticipants = new ArrayList<Player>();
    private static Collection<CityPromotionCeremony> ceremonies = new ArrayList<CityPromotionCeremony>();

    public final ch.swisssmp.utils.Random random = new Random();

    private final City city;
    private final int requiredPlayers;
    private final int requiredHay;
    private final Block chest;
    private final Player initiator;
    private final int ironTribute = 3;
    private final int goldTribute = 2;
    private final int diamondTribute = 1;

    private List<Player> participants = new ArrayList<Player>();

    private BukkitTask timeoutTask;
    private BukkitTask musicTask;

    private PromotionPhase phase = null;

    private String cityName;

    private long t;
    private final long orbitTime = 1200;
    private Location spectatorLocation;

    private CityPromotionCeremony(Block Chest, Player initiator, City city){
        super(CitySystemPlugin.getInstance());
        this.city = city;
//        this.cityName = city.getName();
        this.cityName = "Guggi-no-kuni";
        this.chest = Chest;
        this.initiator = initiator;
        participants.add(initiator);
        requiredHay = 9; //Todo
        requiredPlayers = 1; //Todo

//        switch(city.getRank()){ //Todo
//            case Gemeinschaft: requiredPlayers = 3; requiredHay = 9; ceremonyRange = 20; ironTribute = 30; goldTribute = 15; diamondTribute = 10;
//            case Stadt: requiredPlayers = 4; ceremonyRange = 25; ....
//        }
    }

    public int getRequiredPlayers(){
        return requiredPlayers;
    }

    public Block getChest(){
        return chest;
    }

    public String getCityName(){
        return cityName;
    }

    public Player getInitiator(){
        return initiator;
    }

    public List<Player> getPlayers(){
        return participants;
    }

    public void addParticipant(Player player){
        if(isParticipant(player)) return;
        participants.add(player);
        ceremoniesParticipants.add(player);
        this.broadcast(player.getDisplayName() + ChatColor.RESET + ChatColor.LIGHT_PURPLE + " steht bereit!");
    }

    public void setMusic(Location location, String music, long length){
        if(musicTask != null) musicTask.cancel();
        musicTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CitySystemPlugin.getInstance(), () ->{
            for(Player player : this.participants){
                player.stopSound(music, SoundCategory.RECORDS);
            }
            location.getWorld().playSound(location, music, SoundCategory.RECORDS, 15, 1);
        }, 0, length);
    }

    private void updateSpectatorLocation(){
        t++;
        final double radius = 9;
        float progress = t / (float) orbitTime;
        double radians = 2*Math.PI*progress;
        double x = Math.cos(radians) * radius;
        double y = 6;
        double z = Math.sin(radians) * radius;
        float yaw = progress*360 + 90;
        float pitch = 35;
        spectatorLocation = new Location(chest.getWorld(), chest.getLocation().getX()+x, chest.getLocation().getY()+y, chest.getLocation().getZ()+z,yaw,pitch);
    }

    @Override
    protected Phase getNextPhase(){
        if(phase == null){
            phase = PromotionPhase.Beginning;
            return new BeginningPhase(this);
        }
        switch(phase){
            case Beginning:{
                Bukkit.getLogger().info("Beginning initiated");
                phase = PromotionPhase.Burning;
                BurningPhase burningPhase = new BurningPhase(this);
                Bukkit.getPluginManager().registerEvents(burningPhase, CitySystemPlugin.getInstance());
                return burningPhase;
            }
            case Burning:{
                Bukkit.getLogger().info("Burning initiated");
                phase = PromotionPhase.Climax;
                return new ClimaxPhase(this);
            }
            case Climax:{
                Bukkit.getLogger().info("Climax initiated");
                phase = PromotionPhase.Ending;
                return new EndingPhase(this);
            }
            case Ending:{
                Bukkit.getLogger().info("Ending initiated");
                this.complete();
                return null;
            }
            default: return null;
        }
    }

    @Override
    public void begin(JavaPlugin plugin){
        super.begin(CitySystemPlugin.getInstance());
        timeoutTask = Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), this::cancel, 12000L);
    }

    @Override
    public void run(){
        super.run();
        this.updateSpectatorLocation();
//        if(!(Chest.getState() instanceof org.bukkit.block.Chest)){
//            cancel();
//        }
    }

    @Override
    protected void finish(){
        super.finish();
        if(timeoutTask != null) timeoutTask.cancel();
        HandlerList.unregisterAll(this);
        for(Player player : this.participants){
            ceremoniesParticipants.remove(player);
        }
        stopMusic();
        ceremonies.remove(this);
    }

    private void stopMusic(){
        if(musicTask!=null) musicTask.cancel();
        for(Player player : this.participants){
            player.stopSound(CityPromotionCeremonyMusic.shaker, SoundCategory.RECORDS);
            player.stopSound(CityPromotionCeremonyMusic.drums, SoundCategory.RECORDS);
        }
    }

    @Override
    public boolean isParticipant(Player player){
        return this.participants.contains(player);
    }

    @Override
    public void broadcast(String message){
        for(Player player : this.participants){
            player.sendMessage(this.getPrefix()+message);
        }
    }

    @Override
    public void broadcastTitle(String title, String subtitle) {
        for(Player player : this.participants){
            SwissSMPler.get(player).sendTitle(title, subtitle);
        }
    }

    @Override
    public void broadcastActionBar(String message) {
        for(Player player : this.participants){
            SwissSMPler.get(player).sendActionBar(message);
        }
    }

    @Override
    protected boolean isMatch(String key){
        return (this.initiator.getName().toLowerCase().contains(key) || this.initiator.getDisplayName().toLowerCase().contains(key));
    }

    @Override
    public Location getSpectatorLocation(){
        return spectatorLocation.clone();
    }

    @Override
    public Location getInitialSpectatorLocation(){
        return initiator.getLocation().add(1, 0, 0);
    }

    private String getPrefix(){
        return ChatColor.WHITE + "[" + ChatColor.DARK_PURPLE + "Stadtaufstieg" + ChatColor.WHITE + "]" + ChatColor.RESET;
    }

    public static List<Player> getNearbyPlayers(Location location){
        return getNearbyPlayers(location, CityPromotionCeremony.ceremonyRange);
    }

    public static List<Player> getNearbyPlayers(Location location, float radius){
        List<Player> result = new ArrayList<Player>();
        for(Entity entity : location.getWorld().getNearbyEntities(location, radius, radius, radius)){
            if(!(entity instanceof Player)) continue;
            Player player = (Player) entity;
            if(player.getGameMode()!=GameMode.SURVIVAL) continue;
            if(ceremoniesParticipants.contains(player) || !player.hasPermission("citysystem.found")) continue;
            result.add(player);
        }
        return result;

    }

    public static CityPromotionCeremony start(Block Chest, Player initiator, City city){
        if(ceremoniesParticipants.contains(initiator) || Ceremonies.isParticipantAnywhere(initiator)) return null; //Todo permission ?
        List<Player> nearbyPlayers = getNearbyPlayers(Chest.getLocation());
//        int requiredPlayers = HTTPrequest oder whatever //Todo find out the amount of players needed to advance
//        if(nearbyPlayers.size() < requiredPlayers) return null;
        for(CityPromotionCeremony nearby : ceremonies){
            if(nearby.getChest().getLocation().distanceSquared(Chest.getLocation()) < 10000){
                SwissSMPler.get(initiator).sendActionBar(ChatColor.RED + "Es findet bereits eine Aufstiegszeremonie in der Nähe statt");
                return null;
            }
        }
        ceremoniesParticipants.add(initiator);
        CityPromotionCeremony result = new CityPromotionCeremony(Chest, initiator, city);
        ceremonies.add(result);
        result.begin(CitySystemPlugin.getInstance());
        return result;
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event){
        if(!participants.contains(event.getPlayer())) return;
        participants.remove(event.getPlayer());
        ceremoniesParticipants.remove(event.getPlayer());
//        if(initiator!=event.getPlayer() && this.participants.size()>=requiredPlayers) return; //Todo reinsert
        this.cancel();
    }

    @EventHandler
    private void onTributeChestDestruction(BlockBreakEvent event){
        if(phase == PromotionPhase.Climax  || phase == PromotionPhase.Ending) return;
        Block block = event.getBlock();
        if(!block.getType().equals(Material.CHEST)) return;
        if(!block.equals(chest)) return;
        this.cancel();
    }

    private enum PromotionPhase {
        Beginning,
        Burning,
        Climax,
        Ending
    }

}
