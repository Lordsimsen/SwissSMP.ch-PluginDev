package ch.swisssmp.afkcontrol;

import ch.swisssmp.text.RawBase;
import ch.swisssmp.text.RawText;
import ch.swisssmp.utils.Mathf;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class AfkKicker {
    private static AfkKicker instance;

    private final long checkInterval;
    private final long idleTimeout;
    private final long afkTimeout;
    private final HashMap<UUID,PlayerTracker> trackers = new HashMap<>();

    private AfkTicker ticker;

    protected AfkKicker(long checkInterval, long idleTimeout, long afkTimeout){
        this.checkInterval = checkInterval;
        this.idleTimeout = idleTimeout;
        this.afkTimeout = afkTimeout;
    }

    protected void initialize(){
        instance = this;
        ticker = new AfkTicker(this, checkInterval);
        ticker.start();
        for(Player player : Bukkit.getOnlinePlayers()){
            trackPlayer(player);
        }
    }

    public long getIdleTimeout(){
        return idleTimeout;
    }

    public long getAfkTimeout(){
        return afkTimeout;
    }

    public static AfkTicker getTicker(){
        return instance!=null ? instance.ticker : null;
    }

    public void trackPlayer(Player player){
        instance.trackers.put(player.getUniqueId(), new PlayerTracker(player));
    }

    public void untrackPlayer(Player player){
        instance.trackers.remove(player.getUniqueId());
    }

    public synchronized Optional<PlayerTracker> getTracker(Player player){
        return trackers.containsKey(player.getUniqueId()) ? Optional.of(trackers.get(player.getUniqueId())) : Optional.empty();
    }

    public Collection<PlayerTracker> getAllTrackers(){
        return trackers.values();
    }

    public boolean toggleAfk(Player player){
        PlayerTracker tracker = getTracker(player).orElse(null);
        if(tracker==null) return false;
        return setAfk(player, !tracker.isAfk());
    }

    public boolean setAfk(Player player, boolean afk){
        PlayerTracker tracker = getTracker(player).orElse(null);
        if(tracker==null) return false;
        if(afk==tracker.isAfk()) return true;
        tracker.setAfk(afk);
        tracker.setWarned(false);
        if(afk){
            tracker.setTimeout(Math.max(idleTimeout, tracker.getTimeout()));
            tracker.setAfkLocation(player.getLocation());
            announceAfk(player);
        }
        else announceReturn(player);
        return true;
    }

    public void reset(Player player){
        PlayerTracker tracker = getTracker(player).orElse(null);
        if(tracker==null) return;
        boolean wasAfk = tracker.isAfk();
        tracker.reset();
        if(!wasAfk) return;
        announceReturn(player);
    }

    public boolean kick(Player player){
        player.kickPlayer("Du wurdest gekickt, weil du länger als "+ Mathf.ceilToInt(afkTimeout / 60f / 20)+" Minuten afk warst!");
        for(Player other : Bukkit.getOnlinePlayers().stream().filter(p->p.hasPermission("afk.kick.observe")).collect(Collectors.toList())){
            other.sendMessage(ChatColor.GRAY+player.getName()+" wurde wegen zu langer Inaktivität gekickt.");
        }

        return true;
    }

    protected static void announceAfk(Player player){
        RawBase text = new RawText(player.getDisplayName()).extra(new RawText(" ist abwesend").color(ChatColor.DARK_GRAY));
        announce(text);
    }

    protected static void announceReturn(Player player){
        RawBase text = new RawText(player.getDisplayName()).extra(new RawText(" ist wieder da").color(ChatColor.DARK_GRAY));
        announce(text);
    }

    private static void announce(RawBase text){
        BaseComponent spigotMessage = text.spigot();
        for(Player other : Bukkit.getOnlinePlayers()){
            other.spigot().sendMessage(spigotMessage);
        }
    }

    public static AfkKicker inst(){return instance;}
}
