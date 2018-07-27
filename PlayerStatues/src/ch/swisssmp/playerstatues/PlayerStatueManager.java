package ch.swisssmp.playerstatues;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;
import net.minecraft.server.v1_12_R1.WorldServer;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.Location;

public class PlayerStatueManager {
	protected static boolean create(Location location, String playerName, String statueName){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("statues/create.php", new String[]{
				"player="+URLEncoder.encode(playerName),
				"name="+URLEncoder.encode(statueName),
				"x="+location.getX(),
				"y="+location.getY(),
				"z="+location.getZ(),
				"yaw="+location.getYaw(),
				"pitch="+location.getPitch(),
				"world="+URLEncoder.encode(location.getWorld().getName())
		});
		if(yamlConfiguration==null || !yamlConfiguration.contains("statue")) return false;
		ConfigurationSection dataSection = yamlConfiguration.getConfigurationSection("statue");
		place(dataSection);
		return true;
	}
	protected static boolean update(Location location, String statueName, int range){
		DataSource.getResponse("statues/update.php", new String[]{
				"name="+URLEncoder.encode(statueName),
				"range="+range,
				"x="+location.getX(),
				"y="+location.getY(),
				"z="+location.getZ(),
				"yaw="+location.getYaw(),
				"pitch="+location.getPitch(),
				"world="+URLEncoder.encode(location.getWorld().getName())
		});
		return true;
	}
	protected static boolean remove(Location location, String statueName, int range){
		DataSource.getResponse("statues/remove.php", new String[]{
				"name="+URLEncoder.encode(statueName),
				"range="+range,
				"x="+location.getX(),
				"y="+location.getY(),
				"z="+location.getZ(),
				"world="+URLEncoder.encode(location.getWorld().getName())
		});
		return true;
	}
	protected static void place(ConfigurationSection dataSection) {
    	String name = dataSection.getString("name");
    	String texture = dataSection.getString("texture");
    	String signature = dataSection.getString("signature");
        Location location = dataSection.getLocation();
        if(location==null){
        	Bukkit.getLogger().info("[PlayerStatues] Could not place figure "+name+" because the location is null.");
        	return;
        }
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);
        if(texture!=null && signature!=null){
            gameProfile.getProperties().put("textures", new Property("textures",texture,signature));
        }

        PlayerStatue npc = new PlayerStatue(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld));
        Player npcPlayer = npc.getBukkitEntity().getPlayer();
        npcPlayer.setPlayerListName("");
        npc.spawnIn(nmsWorld);
        ((CraftWorld)location.getWorld()).getHandle().addEntity(npc, SpawnReason.CUSTOM);
    }
	/*
	protected static void loadStatues(org.bukkit. world){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("statues/get.php");
		
	}
	*/
}
