package ch.swisssmp.city.ceremony;

import ch.swisssmp.ceremonies.Ceremony;
import ch.swisssmp.ceremonies.Spectator;
import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.city.ceremony.founding.CityFoundingCeremony;
import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CityCeremony extends Ceremony {

    protected static Collection<Player> ceremoniesParticipants = new ArrayList<Player>();

    private final Player initiator;
    private final List<Player> participants = new ArrayList<Player>();
    private BukkitTask musicTask;

    public CityCeremony(Player initiator) {
        super(CitySystemPlugin.getInstance());
        this.initiator = initiator;
        participants.add(initiator);
    }

    public Player getInitiator() {
        return initiator;
    }

    public List<Player> getParticipants() {
        return participants;
    }

    public List<Player> getPlayers() {
        return Stream.concat(
                participants.stream(),
                getSpectators().stream().map(Spectator::getPlayer)
        ).collect(Collectors.toList());
    }

    public void addParticipant(Player player) {
        if (isParticipant(player)) return;
        participants.add(player);
        ceremoniesParticipants.add(player);
    }

    public void setMusic(Location location, String music, long length) {
        if (musicTask != null) musicTask.cancel();
        musicTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CitySystemPlugin.getInstance(), () -> {
            for (Player player : getPlayers()) {
                setMusic(player, location, music);
            }
        }, 0, length);
    }

    private void setMusic(Player player, Location location, String music) {
        player.stopSound(music, SoundCategory.RECORDS);
        player.playSound(location, music, SoundCategory.RECORDS, 15, 1);
    }

    protected void stopMusic() {
        if (musicTask != null) musicTask.cancel();
        for (Player player : this.getPlayers()) {
            stopMusic(player);
        }
    }

    private void stopMusic(Player player) {
        player.stopSound("founding_ceremony_shaker", SoundCategory.RECORDS);
        player.stopSound("founding_ceremony_drums", SoundCategory.RECORDS);
    }

    protected abstract String getPrefix();

    @Override
    public boolean isParticipant(Player player) {
        return this.participants.contains(player);
    }

    @Override
    public void broadcast(String message) {
        for (Player player : this.participants) {
            player.sendMessage(this.getPrefix() + message);
        }
    }

    @Override
    public void broadcastTitle(String title, String subtitle) {
        for (Player player : this.participants) {
            SwissSMPler.get(player).sendTitle(title, subtitle);
        }
    }

    @Override
    public void broadcastActionBar(String message) {
        for (Player player : this.participants) {
            SwissSMPler.get(player).sendActionBar(message);
        }
    }

    @Override
    protected boolean isMatch(String key) {
        return (this.initiator.getName().toLowerCase().contains(key) || this.initiator.getDisplayName().toLowerCase().contains(key));
    }

    @Override
    public Location getInitialSpectatorLocation() {
        return initiator.getLocation().add(1, 0, 0);
    }

    public static List<Player> getNearbyPlayers(Location location) {
        return getNearbyPlayers(location, CityFoundingCeremony.ceremonyRange);
    }

    public static List<Player> getNearbyPlayers(Location location, float radius) {
        List<Player> result = new ArrayList<Player>();
        for (Entity entity : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
            if (!(entity instanceof Player)) continue;
            Player player = (Player) entity;
            if (player.getGameMode() != GameMode.SURVIVAL) continue;
            if (ceremoniesParticipants.contains(player) || !player.hasPermission("citysystem.found")) continue;
            result.add(player);
        }
        return result;
    }

    @Override
    protected void finish() {
        super.finish();
        for (Player player : this.participants) {
            ceremoniesParticipants.remove(player);
        }
    }
}
