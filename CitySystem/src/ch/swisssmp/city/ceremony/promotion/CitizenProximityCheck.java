package ch.swisssmp.city.ceremony.promotion;

import ch.swisssmp.ceremonies.Ceremonies;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class CitizenProximityCheck extends BukkitRunnable {

    private final CityPromotionCeremony ceremony;
    private final Location location;
    private final double CEREMONY_RADIUS = 50;

    private List<Player> citizens = new ArrayList<>();

    protected CitizenProximityCheck(CityPromotionCeremony ceremony){
        this.ceremony = ceremony;
        this.location = ceremony.getChest().getLocation();
    }

    @Override
    public void run() {
        for(Entity entity : location.getWorld().getNearbyEntities(location, CEREMONY_RADIUS, CEREMONY_RADIUS, CEREMONY_RADIUS)){
            if(!(entity instanceof Player)) continue;
            Player player = (Player) entity;
            if(player.getGameMode()!= GameMode.SURVIVAL) continue;
            if(Ceremonies.isParticipantAnywhere(player) || !player.hasPermission("citysystem.promote") || !ceremony.getCity().isCitizen(player.getUniqueId())) continue;
            citizens.add(player);
        }
    }

    protected List<Player> getCeremonyParticipants(){
        return citizens;
    }
}
