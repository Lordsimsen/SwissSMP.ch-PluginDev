package com.challengercity.plugins.realmail;

public final class LoginRunnable implements Runnable {
    
    private org.bukkit.event.player.PlayerJoinEvent event;

    public LoginRunnable(org.bukkit.event.player.PlayerJoinEvent e) {
        this.event = e;
    }

    @Override
    public void run() {
       event.getPlayer().sendMessage(RealMail.prefix+"Du hast Post! Prüfe deinen Briefkasten.");
    }
    
}
