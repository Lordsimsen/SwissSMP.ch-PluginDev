package ch.swisssmp.city.ceremony.promotion;

import ch.swisssmp.ceremonies.Ceremonies;
import ch.swisssmp.ceremonies.Ceremony;
import ch.swisssmp.ceremonies.Phase;
import ch.swisssmp.city.City;
import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.city.ceremony.effects.CityCeremonyCircleEffect;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class CityPromotionCeremony extends Ceremony implements Listener {

    public static final Material baseMaterial = Material.HAY_BLOCK;
    public static final float ceremonyRange = 20;

    private static final Collection<Player> ceremoniesParticipants = new ArrayList<Player>();
    private static final Collection<CityPromotionCeremony> ceremonies = new ArrayList<CityPromotionCeremony>();

    public final ch.swisssmp.utils.Random random = new Random();

    private final City city;
    private final Block chest;
    private final Player initiator;

    private final PromotionCeremonyData ceremonyParameters;

    private CityCeremonyCircleEffect ringEffect;

    private List<Player> participants;

    private BukkitTask timeoutTask;
    private BukkitTask musicTask;
    private BukkitTask ringEffectTask;

    private CitizenProximityCheck participantsCheckTask;

    private PromotionPhase phase = null;

    private String cityName;

    private long t;
    private final long orbitTime = 1200;
    private Location spectatorLocation;

    private CityPromotionCeremony(Block Chest, Player initiator, City city, PromotionCeremonyData data){
        super(CitySystemPlugin.getInstance());
        this.city = city;
        cityName = city.getName();
        this.chest = Chest;
        this.initiator = initiator;
//        participants.add(initiator);

        ceremonyParameters = data;
    }

    public Block getChest(){
        return chest;
    }

    public City getCity(){
        return city;
    }

    public String getCityName(){
        return cityName;
    }

    public Player getInitiator(){
        return initiator;
    }

    public List<Player> getParticipants(){
        return participants;
    }


    public PromotionCeremonyData getCeremonyParameters() {
        return ceremonyParameters;
    }

    public CityCeremonyCircleEffect getRingEffect(){
        return ringEffect;
    }

    public void setRingEffect(CityCeremonyCircleEffect ringEffect){
        this.ringEffect = ringEffect;
    }

    public void setRingEffectTask(BukkitTask task){
        this.ringEffectTask = task;
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
                phase = PromotionPhase.Burning;
                BurningPhase burningPhase = new BurningPhase(this);
                Bukkit.getPluginManager().registerEvents(burningPhase, CitySystemPlugin.getInstance());
                return burningPhase;
            }
            case Burning:{
                phase = PromotionPhase.Climax;
                return new ClimaxPhase(this);
            }
            case Climax:{
                phase = PromotionPhase.Ending;
                return new EndingPhase(this);
            }
            case Ending:{
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
        participantsCheckTask = new CitizenProximityCheck(this);
        participantsCheckTask.runTaskTimer(CitySystemPlugin.getInstance(), 1L, 40L);
        participants = participantsCheckTask.getCeremonyParticipants();
    }

    @Override
    public void run(){
        super.run();
        this.updateSpectatorLocation();
        participants = participantsCheckTask.getCeremonyParticipants();
    }

    @Override
    protected void finish(){
        super.finish();
        if(timeoutTask != null) timeoutTask.cancel();
        if(participantsCheckTask != null) participantsCheckTask.cancel();
        for(Player player : this.participants){
            ceremoniesParticipants.remove(player);
        }
        if(ringEffectTask != null) ringEffectTask.cancel();
        stopMusic();
        HandlerList.unregisterAll(this);
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

    public static CityPromotionCeremony start(Block Chest, Player initiator, City city, PromotionCeremonyData data){
        if(ceremoniesParticipants.contains(initiator) || Ceremonies.isParticipantAnywhere(initiator)) return null; //Todo permission ?
        List<Player> nearbyPlayers = getNearbyPlayers(Chest.getLocation());
        for(CityPromotionCeremony nearby : ceremonies){
            if(nearby.getChest().getLocation().distanceSquared(Chest.getLocation()) < 10000){
                SwissSMPler.get(initiator).sendActionBar(ChatColor.RED + "Es findet bereits eine Aufstiegszeremonie in der Nähe statt");
                return null;
            }
        }
        ceremoniesParticipants.add(initiator);
        CityPromotionCeremony result = new CityPromotionCeremony(Chest, initiator, city, data);
        ceremonies.add(result);
        result.begin(CitySystemPlugin.getInstance());
        return result;
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event){
        if(!participants.contains(event.getPlayer())) return;
        participants.remove(event.getPlayer());
        ceremoniesParticipants.remove(event.getPlayer());
        if(initiator != event.getPlayer() && this.participants.size() >= ceremonyParameters.getPromotionPlayercount()) return;
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
