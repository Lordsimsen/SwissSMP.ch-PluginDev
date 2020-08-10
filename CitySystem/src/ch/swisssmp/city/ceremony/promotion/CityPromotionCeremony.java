package ch.swisssmp.city.ceremony.promotion;

import ch.swisssmp.ceremonies.Ceremonies;
import ch.swisssmp.ceremonies.Phase;
import ch.swisssmp.city.City;
import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.city.ceremony.CityCeremony;
import ch.swisssmp.city.ceremony.effects.CityCeremonyCircleEffect;
import ch.swisssmp.city.ceremony.promotion.phases.*;
import ch.swisssmp.utils.Random;
import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class CityPromotionCeremony extends CityCeremony implements Listener {

    public static final Material baseMaterial = Material.HAY_BLOCK;
    public static final float ceremonyRange = 20;

    private static final Collection<CityPromotionCeremony> ceremonies = new ArrayList<CityPromotionCeremony>();

    public final ch.swisssmp.utils.Random random = new Random();

    private final City city;
    private final Block chest;

    private final PromotionCeremonyData ceremonyParameters;

    private CityCeremonyCircleEffect ringEffect;

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
        super(initiator);
        this.city = city;
        cityName = city.getName();
        this.chest = Chest;

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
    }

    @Override
    public void run(){
        super.run();
        this.updateSpectatorLocation();
    }

    @Override
    protected void finish(){
        super.finish();
        if(timeoutTask != null) timeoutTask.cancel();
        if(participantsCheckTask != null) participantsCheckTask.cancel();
        if(ringEffectTask != null) ringEffectTask.cancel();
        stopMusic();
        HandlerList.unregisterAll(this);
        ceremonies.remove(this);
    }

    @Override
    public Location getSpectatorLocation(){
        return spectatorLocation.clone();
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

    public static CityPromotionCeremony start(Block tributeChest, Player initiator, City city, PromotionCeremonyData data){
        if(ceremoniesParticipants.contains(initiator) || Ceremonies.isParticipantAnywhere(initiator)) return null; //Todo permission ?
        // List<Player> nearbyPlayers = getNearbyPlayers(Chest.getLocation());
        for(CityPromotionCeremony nearby : ceremonies){
            if(nearby.getChest().getLocation().distanceSquared(tributeChest.getLocation()) < 10000){
                SwissSMPler.get(initiator).sendActionBar(ChatColor.RED + "Es findet bereits eine Aufstiegszeremonie in der Nähe statt");
                return null;
            }
        }
        Inventory inventory = ((org.bukkit.block.Chest) tributeChest.getState()).getBlockInventory();
        for(ItemStack required : data.getTribute()){
            int proposedAmount = 0;
            for(ItemStack proposed : inventory){
                if(proposed==null || required == null || required.getType() == Material.AIR) continue;
                if(proposed.getType() != required.getType()) continue;
                proposedAmount += proposed.getAmount();
            }
            if(proposedAmount < required.getAmount()){
                tributeChest.getWorld().strikeLightning(initiator.getLocation());
                SwissSMPler.get(initiator).sendActionBar(ChatColor.RED + "Du versuchst mich zu hintergehen!?");
                return null;
            }
        }
        ceremoniesParticipants.add(initiator);
        CityPromotionCeremony result = new CityPromotionCeremony(tributeChest, initiator, city, data);
        ceremonies.add(result);
        result.begin(CitySystemPlugin.getInstance());
        Bukkit.getPluginManager().registerEvents(result, CitySystemPlugin.getInstance());
        return result;
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event){
        Collection<Player> participants = this.getParticipants();
        Player initiator = this.getInitiator();
        if(!participants.contains(event.getPlayer())) return;
        participants.remove(event.getPlayer());
        ceremoniesParticipants.remove(event.getPlayer());
        if(initiator!=event.getPlayer() && participants.size()>=2) return;
        this.cancel();
    }

    @Override
    protected String getPrefix(){
        return ChatColor.WHITE + "[" + ChatColor.DARK_PURPLE + "Stadtaufstieg" + ChatColor.WHITE + "]" + ChatColor.RESET;
    }

    @EventHandler
    private void onTributeChestDestruction(BlockBreakEvent event){
        if(phase == PromotionPhase.Climax  || phase == PromotionPhase.Ending) return;
        Block block = event.getBlock();
        if(!block.getType().equals(Material.CHEST)) return;
        if(!block.equals(chest)) return;
        this.cancel();
    }

    @EventHandler
    private void onTributeChestInteract(PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if(block.equals(chest)) event.setCancelled(true);
    }

    public static Optional<CityPromotionCeremony> get(City city){
        return ceremonies.stream().filter(c->c.getCity().getUniqueId().equals(city.getUniqueId())).findAny();
    }

    private enum PromotionPhase {
        Beginning,
        Burning,
        Climax,
        Ending
    }
}
