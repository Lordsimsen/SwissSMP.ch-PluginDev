package ch.swisssmp.afkcontrol;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class AfkTicker extends BukkitRunnable {

    private final AfkKicker kicker;
    private final long checkInterval;

    private long idleTimeout;
    private long afkTimeout;

    protected AfkTicker(AfkKicker kicker, long checkInterval){
        this.kicker = kicker;
        this.checkInterval = checkInterval;
    }

    protected void start(){
        runTaskTimer(AfkKickerPlugin.getInstance(), 0, checkInterval);
        idleTimeout = kicker.getIdleTimeout();
        afkTimeout = idleTimeout + kicker.getAfkTimeout();
    }

    @Override
    public void run() {
        for(PlayerTracker tracker : kicker.getAllTrackers()){
            tracker.tick(checkInterval);
            if(tracker.getTimeout()>afkTimeout && !tracker.getPlayer().hasPermission("afk.kick.excempt")){
                kicker.kick(tracker.getPlayer());
            }
            else if(tracker.getTimeout()>afkTimeout-1200 && !tracker.isWarned() && !tracker.getPlayer().hasPermission("afk.kick.excempt")){
                tracker.getPlayer().sendMessage(ChatColor.RED+"Du wirst bei weiterer Inaktivität in einer Minute vom Server gekickt.");
                tracker.setWarned(true);
            }
            else if(tracker.getTimeout()>idleTimeout && !tracker.isAfk() && !tracker.getPlayer().hasPermission("afk.auto.excempt")){
                kicker.setAfk(tracker.getPlayer(), true);
            }
        }
    }
}
