/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.swisssmp.utils;

import java.util.UUID;

import com.google.gson.JsonParser;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.entity.Player;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;

public final class SwissSMPler {

    private final UUID playerUid;
    private final Player player;

    private SwissSMPler(UUID playerUid) {
        this.playerUid = playerUid;
        this.player = null;
    }

    private SwissSMPler(Player player) {
        this.playerUid = player != null ? player.getUniqueId() : null;
        this.player = player;
    }

    public static SwissSMPler get(Player player) {
        return new SwissSMPler(player);
    }

    public static SwissSMPler get(UUID player_uuid) {
        Player bukkitPlayer = Bukkit.getPlayer(player_uuid);
        return bukkitPlayer != null ? new SwissSMPler(bukkitPlayer) : new SwissSMPler(player_uuid);
    }

    public void teleport(Location to) {
        if (player == null) return;
        player.teleport(to);
    }

    public void sendMessage(String message) {
        if (player == null) return;
        player.sendMessage(message);
    }

    public UUID getUniqueId() {
        return playerUid;
    }

    public String getName() {
        return player != null ? player.getName() : null;
    }

    public String getDisplayName() {
        return player != null ? player.getDisplayName() : null;
    }

    public GameMode getGameMode() {
        return player != null ? player.getGameMode() : null;
    }

    public int getLevel() {
        return player != null ? player.getLevel() : null;
    }

    public float getExp() {
        return player != null ? player.getExp() : null;
    }

    public void giveExp(int exp) {

        if (player == null) return;
        player.giveExp(exp);
    }

    public void setLevel(int level) {

        if (player == null) return;
        player.setLevel(level);
    }

    public void setExp(float exp) {
        if (player == null) return;
        player.setExp(exp);
    }

    public boolean hasPermission(String permission) {
        return player != null && player.hasPermission(permission);
    }

    public void setInvulnerable(boolean invulnerable) {
        if (player == null) return;
        player.setInvulnerable(invulnerable);
    }

    public Location getLocation() {
        return player != null ? player.getLocation() : null;
    }

    public World getWorld() {
        return player != null ? player.getWorld() : null;
    }

    public void sendRawMessage(String rawMessage) {
        if (player == null || !player.isOnline()) return;
        try {
            JsonParser parser = new JsonParser();
            parser.parse(rawMessage);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + rawMessage);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Tried to send invalid raw message:\n" + rawMessage);
        }
    }

    public void sendRawMessage(BaseComponent... components) {
        if (player == null || !player.isOnline()) return;
        player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.CHAT, components);
    }

    public void sendActionBar(String message) {
        if (player == null || message == null) return;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    public void sendTitle(String title, String subtitle) {
        if (player == null || !player.isOnline()) return;
        player.sendTitle(title, subtitle, 10, 70, 20);
    }
}
