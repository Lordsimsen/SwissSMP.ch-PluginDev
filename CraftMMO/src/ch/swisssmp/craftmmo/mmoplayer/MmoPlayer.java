package ch.swisssmp.craftmmo.mmoplayer;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoattribute.MmoAttributes;
import ch.swisssmp.craftmmo.mmoitem.MmoItem;
import ch.swisssmp.craftmmo.mmoitem.MmoItemManager;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_12_R1.PlayerConnection;

public abstract class MmoPlayer{
	
	public static void login(Player player){
		MmoQuestbook.get(player.getUniqueId());
		Runnable runnable = new MmoDelayedLoginTask(player);
		Bukkit.getScheduler().runTaskLater(Main.plugin, runnable, 5L);
	}
	public static void sendTitle(Player player, String title){
		sendTitle(player, title, "");
	}
	public static void sendTitle(Player player, String title, int stay){
		sendTitle(player, title, "", stay);
	}
	public static void sendTitle(Player player, String title, int fadeIn, int stay, int fadeOut){
		sendTitle(player, title, "", fadeIn, stay, fadeOut);
	}
	public static void sendTitle(Player player, String title, String subtitle){
		sendTitle(player, title, subtitle, 3);
	}
	public static void sendTitle(Player player, String title, String subtitle, int stay){
		sendTitle(player, title, subtitle, 1, stay, 1);
	}
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
    	if(!player.isOnline())return;
        CraftPlayer craftplayer = (CraftPlayer) player;
        PlayerConnection connection = craftplayer.getHandle().playerConnection;
        IChatBaseComponent titleJSON = ChatSerializer.a(("{'text': '" + title + "'}").replace("'", "\""));
        IChatBaseComponent subtitleJSON = ChatSerializer.a(("{'text': '" + subtitle + "'}").replace("'", "\""));
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON);
        connection.sendPacket(titlePacket);
        connection.sendPacket(subtitlePacket);
    }
    public static void sendMessage(UUID player_uuid, String message){
    	Player player = Bukkit.getPlayer(player_uuid);
    	if(player!=null)sendMessage(player, message);
    }
    public static void sendMessage(Player player, String message){
    	if(!player.isOnline())return;
    	player.sendMessage(message);
    }
    public static void sendRawMessage(UUID player_uuid, String message){
    	Player player = Bukkit.getPlayer(player_uuid);
    	if(player!=null)sendRawMessage(player, message);
    }
    public static void sendRawMessage(Player player, String message){
    	if(!player.isOnline())return;
        CraftPlayer craftplayer = (CraftPlayer) player;
        PlayerConnection connection = craftplayer.getHandle().playerConnection;
        IChatBaseComponent rawJSON = ChatSerializer.a(message.replace("'", "\""));
        PacketPlayOutChat packet = new PacketPlayOutChat(rawJSON);
        connection.sendPacket(packet);
    }
    public static void updateInventory(UUID player_uuid){
    	Player player = Bukkit.getPlayer(player_uuid);
    	if(player!=null)MmoItemManager.updateInventory(player.getInventory());
    	
    }
    public static void updateInventory(Player player){
    	MmoItemManager.updateInventory(player.getInventory());
    }
    public static MmoAttributes getAttributes(Player player){
    	MmoAttributes attributes = new MmoAttributes();
    	return attributes;
    }    
    public static void sendActionBar(UUID player_uuid, String message){
    	Player player = Bukkit.getPlayer(player_uuid);
    	if(player!=null) sendActionBar(player, message);
    }
    public static void sendActionBar(Player player, String message){
    	if(player==null || message==null) return;
        CraftPlayer craftPlayer = (CraftPlayer) player;
        IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc,(byte) 2);
        ((CraftPlayer) craftPlayer).getHandle().playerConnection.sendPacket(ppoc);
    }
    public static void neutralizeAttackSpeed(Player player, ItemStack itemStack){
    	if(player==null) return;
		double baseAttackSpeed;
		double baseAttackDamage;
    	if(itemStack!=null){
    		if(MmoItem.get(itemStack)!=null){
    			baseAttackSpeed = 0;
    			baseAttackDamage = 0;
    		}
    		else{
    			baseAttackSpeed = 4;
    			baseAttackDamage = 1;
    		}
    	}
    	else{
    		baseAttackSpeed = 4;
    		baseAttackDamage = 1;
    	}
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(baseAttackSpeed);
		player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(baseAttackDamage);
    }
}
