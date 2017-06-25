package ch.swisssmp.adventuredungeons.mmoplayer;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import ch.swisssmp.adventuredungeons.Main;
import ch.swisssmp.adventuredungeons.mmosound.MmoSound;
import ch.swisssmp.adventuredungeons.mmoworld.MmoDungeon;
import ch.swisssmp.adventuredungeons.mmoworld.MmoDungeonInstance;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorld;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorldInstance;
import ch.swisssmp.webcore.DataSource;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_12_R1.PlayerConnection;

public abstract class MmoPlayer{
	
	private static HashMap<UUID, String> assignedResourcepacks = new HashMap<UUID, String>();
	
	public static void login(Player player){
		MmoDungeonInstance dungeonInstance = MmoDungeon.getInstance(player);
		if(dungeonInstance==null) {
			String worldName = player.getWorld().getName();
			if(worldName.contains("dungeon_instance")){
				int instance_id = Integer.parseInt(worldName.split("_")[2]);
				MmoDungeonInstance instance = MmoDungeon.getInstance(instance_id);
				if(instance==null){
					player.teleport(Bukkit.getWorld(Main.config.getString("default_world")).getSpawnLocation());
				}
				int dungeon_id = instance.mmo_dungeon_id;
				MmoDungeon mmoDungeon = MmoDungeon.get(dungeon_id);
				if(mmoDungeon!=null){
					player.teleport(mmoDungeon.getLeavePoint());
				}
				else{
					player.teleport(Bukkit.getWorld(Main.config.getString("default_world")).getSpawnLocation());
				}
			}
			if(player.getGameMode()==GameMode.ADVENTURE) player.setGameMode(GameMode.SURVIVAL);
		}
		MmoPlayer.updateResourcepack(player);
		Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable(){
			public void run(){
				MmoPlayer.updateMusic(player);
			}
		}, 60L);
	}
	public static void updateResourcepack(Player player){
		Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable(){
			public void run(){
				if(player==null) return;
				try {
					MmoWorldInstance worldInstance = MmoWorld.getInstance(player);
					String worldName = player.getWorld().getName();
					if(worldInstance!=null) worldName = worldInstance.system_name;
					String result = DataSource.getResponse("resourcepack.php", new String[]{
							"player="+URLEncoder.encode(player.getUniqueId().toString(), "UTF-8"),
							"world="+URLEncoder.encode(worldName, "utf-8")
					});
					if(!result.isEmpty()){
						if(assignedResourcepacks.containsKey(player.getUniqueId())&&assignedResourcepacks.get(player.getUniqueId()).equals(result))
							return;
						assignedResourcepacks.put(player.getUniqueId(), result);
						player.setResourcePack(result);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 5L);
	}
	public static void unregisterResourcepack(Player player){
		assignedResourcepacks.remove(player.getUniqueId());
	}
	public static String getResourcepack(UUID player_uuid){
		return assignedResourcepacks.get(player_uuid);
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
    public static void sendActionBar(UUID player_uuid, String message){
    	Player player = Bukkit.getPlayer(player_uuid);
    	if(player!=null) sendActionBar(player, message);
    }
    public static void sendActionBar(Player player, String message){
    	if(player==null || message==null) return;
        CraftPlayer craftPlayer = (CraftPlayer) player;
        IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc);
        ((CraftPlayer) craftPlayer).getHandle().playerConnection.sendPacket(ppoc);
    }
    public static void updateMusic(Player player){
    	if(MmoSound.musicLoops.containsKey(player.getUniqueId())){
    		return;
    	}
    	MmoDungeon dungeon = MmoDungeon.get(player);
    	if(dungeon!=null){
    		MmoDungeonInstance instance = MmoDungeon.getInstance(player);
    		if(instance.running && dungeon.background_music>0){
    			MmoSound.playMusic(player, dungeon.background_music, dungeon.looptime);
    		}
    	}
    }
}
