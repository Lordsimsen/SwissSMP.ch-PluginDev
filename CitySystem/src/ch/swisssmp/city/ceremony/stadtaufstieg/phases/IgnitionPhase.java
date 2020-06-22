package ch.swisssmp.city.ceremony.stadtaufstieg.phases;

import ch.swisssmp.ceremonies.Phase;
import ch.swisssmp.ceremonies.effects.FireBurstEffect;
import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.city.ceremony.stadtaufstieg.CityRankCeremony;
import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;

public class IgnitionPhase extends Phase implements Listener {

    private final CityRankCeremony ceremony;
    private final Block banner;

    private Collection<Player> nearbyPlayers = new ArrayList<Player>();

    private BukkitTask reminderTask;
    private BukkitTask autoContinueTask;

    private boolean burning;

    public IgnitionPhase(CityRankCeremony ceremony){
        this.ceremony = ceremony;
        this.banner = ceremony.getBanner();
    }

    @Override
    public void begin(){
        this.updateNearbyPlayers();
        if(this.nearbyPlayers.size() == 0){
            this.ceremony.cancel();
            return;
        }
        this.broadcastTitle("", "Entzündet das Feuer!");
        this.reminderTask = Bukkit.getScheduler().runTaskTimer(CitySystemPlugin.getInstance(), () ->{
            this.updateNearbyPlayers();
            if(this.nearbyPlayers.size() == 0){
                if(ceremony.getPlayers().size() < CityRankCeremony.requiredPlayers) ceremony.cancel();
                else setCompleted();
                return;
            }
            broadcastActionbar(ChatColor.YELLOW + "Entzündet das Feuer!");
        }, 0, 100);
        this.autoContinueTask = Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), () ->{
            if(ceremony.getPlayers().size() < CityRankCeremony.requiredPlayers){
                ceremony.cancel();
                return;
            }
            setCompleted();
        }, 600L);
    }

    @Override
    public void run() {
    }

    @Override
    public void complete(){
        super.complete();
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                int x = ceremony.getBanner().getX() - 1;
                int z = ceremony.getBanner().getZ() - 1;
                int y = ceremony.getBanner().getY() - 3;
                FireBurstEffect.play(CitySystemPlugin.getInstance(), ceremony.getBanner().getLocation().add(x,y,z).getBlock()
                        , 5, Color.fromRGB(255, 150, 0), Color.fromRGB(255, 100, 0));
            }
        }
        banner.getWorld().playSound(banner.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1, 1);
    }

    @Override
    public void finish(){
        super.finish();
        if(reminderTask != null) reminderTask.cancel();
        if(autoContinueTask != null) autoContinueTask.cancel();
        HandlerList.unregisterAll(this);
    }

    private void updateNearbyPlayers(){
        this.nearbyPlayers = CityRankCeremony.getNearbyPlayers(ceremony.getBanner().getLocation());
    }

    private void broadcastTitle(String title, String subtitle){
        for(Player player : this.nearbyPlayers){
            SwissSMPler.get(player).sendTitle(title, subtitle);
        }
    }

    private void broadcastActionbar(String message){
        for(Player player : this.nearbyPlayers){
            SwissSMPler.get(player).sendActionBar(message);
        }
    }

    private boolean hasBannerAbove(Block block){
        Location start = block.getLocation().add(-1,0,-1);
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                for(int k = 0; k < 3; k++){
                    if(block.getWorld().getBlockAt(start.add(i,j,k)) instanceof Banner) return true;
                }
            }
        }
        return false;
    }

    @EventHandler
    private void onIgnition(BlockIgniteEvent event){
        Block ignited = event.getIgnitingBlock();
        if(!ignited.getType().equals(CityRankCeremony.baseMaterial)) return;
        if(event.getCause() != BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) return;
        if(!(event.getIgnitingEntity() instanceof Player)) return;
        Player arsonist = (Player) event.getIgnitingEntity();
        if(arsonist == null) return;
        if(!this.ceremony.isParticipant(arsonist)) return;
        if(!this.nearbyPlayers.contains(arsonist)) return;
        if(!hasBannerAbove(ignited)) return;

        this.setCompleted();
    }
}
