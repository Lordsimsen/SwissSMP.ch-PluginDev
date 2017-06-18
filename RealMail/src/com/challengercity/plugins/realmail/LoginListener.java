package com.challengercity.plugins.realmail;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;

public final class LoginListener implements org.bukkit.event.Listener {
    @SuppressWarnings("unchecked")
	@org.bukkit.event.EventHandler(priority = org.bukkit.event.EventPriority.MONITOR)
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        if (RealMail.mailboxesConfig.getList("players", new LinkedList<String>()).contains(e.getPlayer().getUniqueId().toString())) {
            if (RealMail.plugin.getConfig().getBoolean("login_notification")) {
                if (RealMail.mailboxesConfig.getBoolean(e.getPlayer().getUniqueId()+".unread", false)) {
                    try {
                        Bukkit.getScheduler().runTaskLater(RealMail.plugin, new LoginRunnable(e), 20*10);
                    } catch (IllegalArgumentException ex) {
                        e.getPlayer().sendMessage(RealMail.prefix+"Du hast Post! Prüfe deinen Briefkasten.");
                    }
                }
            }
        } else {
            List<String> knownPlayers = (List<String>) RealMail.mailboxesConfig.getList("players", new LinkedList<String>());
            knownPlayers.add(e.getPlayer().getUniqueId().toString());
            RealMail.mailboxesConfig.set("players", knownPlayers);
            try {
            	RealMail.mailboxesConfig.save(RealMail.mailboxesFile);
            } catch (IOException ex) {
            	RealMail.plugin.getLogger().log(Level.WARNING, "Failed to add {0} to the mail list.", e.getPlayer().getName());
            }
        }
    }
}
