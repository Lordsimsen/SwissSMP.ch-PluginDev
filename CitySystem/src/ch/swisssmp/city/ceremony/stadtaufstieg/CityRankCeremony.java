package ch.swisssmp.city.ceremony.stadtaufstieg;

import ch.swisssmp.ceremonies.Ceremonies;
import ch.swisssmp.ceremonies.Ceremony;
import ch.swisssmp.ceremonies.Phase;
import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.city.ceremony.stadtaufstieg.phases.BeginPhase;
import ch.swisssmp.city.ceremony.stadtaufstieg.phases.BurningPhase;
import ch.swisssmp.city.ceremony.stadtaufstieg.phases.IgnitionPhase;
import ch.swisssmp.city.ceremony.stadtaufstieg.phases.ClimaxPhase;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CityRankCeremony extends Ceremony implements Listener {

    public static final float ceremonyRange = 20;
    public static final int requiredPlayers = 3;
    public static final Material baseMaterial = Material.HAY_BLOCK;

    private static Collection<Player> ceremoniesParticipants = new ArrayList<Player>();
    private static Collection<CityRankCeremony> ceremonies = new ArrayList<CityRankCeremony>();

    private final Block banner;
    private final Player initiator;

    private List<Player> participants = new ArrayList<Player>();

    private BukkitTask timeoutTask;
    private BukkitTask musicTask;

    private CityRankCeremonyPhase phase = null;

    private String cityName;

    private long t;
    private final long orbitTime = 1200;
    private Location spectatorLocation;


    private CityRankCeremony(Block banner, Player initiator){
        super(CitySystemPlugin.getInstance());
        this.banner = banner;
        this.initiator = initiator;
        participants.add(initiator);
    }

    public Block getBanner(){
        return banner;
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
        spectatorLocation = new Location(banner.getWorld(),banner.getLocation().getX()+x,banner.getLocation().getY()+y,banner.getLocation().getZ()+z,yaw,pitch);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event){
        if(!participants.contains(event.getPlayer())) return;
        participants.remove(event.getPlayer());
        ceremoniesParticipants.remove(event.getPlayer());
        if(initiator!=event.getPlayer() && this.participants.size()>=requiredPlayers) return;
        this.cancel();
    }

    @Override
    protected Phase getNextPhase(){
        if(phase == null){
            phase = CityRankCeremonyPhase.Begin;
            return new BeginPhase(this);
        }
        switch(phase){
            case Begin:{
                phase = CityRankCeremonyPhase.Ignition;
                IgnitionPhase ignitionPhase = new IgnitionPhase(this);
                Bukkit.getPluginManager().registerEvents(ignitionPhase, CitySystemPlugin.getInstance());
                return ignitionPhase;
            }
            case Ignition:{
                phase = CityRankCeremonyPhase.Burning;
                BurningPhase burningPhase = new BurningPhase(this);
                Bukkit.getPluginManager().registerEvents(burningPhase, CitySystemPlugin.getInstance());
                return burningPhase;
            }
            case Burning:{
                phase = CityRankCeremonyPhase.Climax;
                return new ClimaxPhase(this);
            }
            case Climax:{
                this.complete();
                return null;
            }
            default: return null;
        }
    }

    @Override
    public void begin(JavaPlugin plugin){
        super.begin(CitySystemPlugin.getInstance());
        this.updateSpectatorLocation();
        Location center = banner.getLocation().clone().add(0.5, 0.5, 0.5);

        timeoutTask = Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), () ->{
            this.cancel();
        }, 6000L);
    }

    @Override
    public void run(){
        super.run();
        this.updateSpectatorLocation();
        if(!(banner.getState() instanceof Banner)){
            cancel();
        }
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
        Ceremonies.remove(this);
    }

    private void stopMusic(){
        if(musicTask != null) musicTask.cancel();
        for(Player player : this.participants){
//            player.stopSound(); // TODO stop sound(s)
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
        return getNearbyPlayers(location, CityRankCeremony.ceremonyRange);
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

    public static CityRankCeremony start(Block banner, Player initiator){
        if(ceremoniesParticipants.contains(initiator) || Ceremonies.isParticipantAnywhere(initiator)) return null;
        List<Player> nearbyPlayers = getNearbyPlayers(banner.getLocation());
        if(nearbyPlayers.size() < requiredPlayers) return null;
        for(CityRankCeremony nearby : ceremonies){
            if(nearby.getBanner().getLocation().distanceSquared(banner.getLocation()) < 10000){
                SwissSMPler.get(initiator).sendActionBar(ChatColor.RED + "Es findet bereits eine Aufstiegszeremonie in der Nähe statt");
                return null;
            }
        }
        ceremoniesParticipants.add(initiator);
        CityRankCeremony result = new CityRankCeremony(banner, initiator);
        ceremonies.add(result);
        Ceremonies.add(result);
        Bukkit.getPluginManager().registerEvents(result, CitySystemPlugin.getInstance());
        result.begin(CitySystemPlugin.getInstance());
        return result;
    }

    @EventHandler
    private void onBannerPlacement(BlockPlaceEvent event){
        ItemStack bannerStack = event.getItemInHand();
        if(!ItemUtil.getBoolean(bannerStack, "citysystem_city_banner")) return;

        Block banner = event.getBlockPlaced();
        Player player = event.getPlayer();

        if(!isHayPile(banner)) return;

        CityRankCeremony.start(banner, player);
    }

    private boolean isHayPile(Block onTop){
        World world = onTop.getWorld();
        /**
        Checks whether the 3 blocks beneath the block on top are haybales
         */
        for(int i = 1; i >= 3; i++){
            if(!world.getBlockAt(onTop.getLocation().clone().add(0., -i, 0.)).getType().equals(baseMaterial)) return false;
        }
        /**
         * Checks whether there is a circle of haybales in the lower two layers
         */
        for(int i = 2; i <= 3; i++) {
            for (double j = 0; j > Math.PI * 2; j += Math.PI / 2) {
                if (!world.getBlockAt(onTop.getLocation().clone().add(Math.cos(j), -i, Math.sin(j))).getType().equals(baseMaterial))
                    return false;
            }
        }
        /**
         * Checks whether the edges of the circle checked before are haybales too
         */
        double j = Math.PI/2;
        for(double i = 0; i > Math.PI * 2; i += Math.PI / 2) {
            if (!world.getBlockAt(onTop.getLocation().clone().add(Math.cos(i) + Math.cos(j), -3.0, Math.sin(i) + Math.sin(j))).getType().equals(baseMaterial)) {
                return false;
            }
            j += Math.PI/2;
        }
        return true;
    }

    private enum CityRankCeremonyPhase{
        Begin,
        Ignition,
        Burning,
        Climax
    }

}
