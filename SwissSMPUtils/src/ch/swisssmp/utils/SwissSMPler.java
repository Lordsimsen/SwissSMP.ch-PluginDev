/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.swisssmp.utils;

import java.util.UUID;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;

import org.bukkit.entity.Player;
import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.minecraft.server.v1_11_R1.PacketPlayOutChat;
import net.minecraft.server.v1_11_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_11_R1.PlayerConnection;
import net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_11_R1.PacketPlayOutTitle.EnumTitleAction;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;

public class SwissSMPler {
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
    
    public void sendActionBar(String message){
    	if(player==null || message==null) return;
        CraftPlayer craftPlayer = (CraftPlayer) player;
        IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc,(byte) 2);
        ((CraftPlayer) craftPlayer).getHandle().playerConnection.sendPacket(ppoc);
    }
    
    public void sendTitle(String title, String subtitle) {
    	if(!player.isOnline())return;
        CraftPlayer craftplayer = (CraftPlayer) player;
        PlayerConnection connection = craftplayer.getHandle().playerConnection;
        IChatBaseComponent titleJSON = ChatSerializer.a(("{'text': '" + title + "'}").replace("'", "\""));
        IChatBaseComponent subtitleJSON = ChatSerializer.a(("{'text': '" + subtitle + "'}").replace("'", "\""));
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON);
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON);
        connection.sendPacket(titlePacket);
        connection.sendPacket(subtitlePacket);
    }
}
