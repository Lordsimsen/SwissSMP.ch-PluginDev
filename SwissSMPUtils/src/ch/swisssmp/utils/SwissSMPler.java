/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.swisssmp.utils;

import java.util.HashMap;
import java.util.UUID;

import com.google.gson.JsonParser;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;

public final class SwissSMPler {
	protected static HashMap<UUID, Vector> last_vectors = new HashMap<UUID, Vector>();
	protected static HashMap<UUID, BukkitTask> afk_tasks = new HashMap<UUID, BukkitTask>();
    protected final Player player;
    private SwissSMPler(Player player){
        this.player = player;
    }
    public static SwissSMPler get(Player player){
        return new SwissSMPler(player);
    }
    public static SwissSMPler get(UUID player_uuid){
        Player bukkitPlayer = Bukkit.getPlayer(player_uuid);
        if(bukkitPlayer==null) return null;
        return new SwissSMPler(bukkitPlayer);
    }
    
    public void teleport(Location to){
        player.teleport(to);
    }
    
    public void sendMessage(String message){
        player.sendMessage(message);
    }
    
    public UUID getUniqueId()
    {
        return player.getUniqueId();
    }
    
    public String getName()
    {
        return player.getName();
    }
    
    public String getDisplayName()
    {
        return player.getDisplayName();
    }
    
    public GameMode getGameMode(){
    	return player.getGameMode();
    }
    
    public int getLevel(){
    	return player.getLevel();
    }
    
    public float getExp(){
    	return player.getExp();
    }
    
    public void giveExp(int exp){
    	player.giveExp(exp);
    }
    
    public void setLevel(int level){
    	player.setLevel(level);
    }
    
    public void setExp(float exp){
    	player.setExp(exp);
    }
    
    public boolean hasPermission(String permission){
    	return player.hasPermission(permission);
    }
    
    public void setInvulnerable(boolean invulnerable){
    	player.setInvulnerable(invulnerable);
    }
    
    public Location getLocation(){
    	return player.getLocation();
    }
    
    public World getWorld(){
    	return player.getWorld();
    }

    public void sendRawMessage(String rawMessage){
        if(!player.isOnline())return;
        try {
            JsonParser parser = new JsonParser();
            parser.parse(rawMessage);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw "+player.getName()+" "+rawMessage);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Tried to send invalid raw message:\n"+rawMessage);
        }
    }

    public void sendRawMessage(BaseComponent... components){
    	if(!player.isOnline()) return;
        player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.CHAT, components);
    } 
    
    public void sendActionBar(String message){
    	if(player==null || message==null) return;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
    
    public void sendTitle(String title, String subtitle) {
    	if(!player.isOnline()) return;
        player.sendTitle(title, subtitle, 10, 70, 20);
    }
}
