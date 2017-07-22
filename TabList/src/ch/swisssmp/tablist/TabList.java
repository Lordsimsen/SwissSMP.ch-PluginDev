package ch.swisssmp.tablist;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonObject;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import ch.swisssmp.webcore.DataSource;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketDataSerializer;
import net.minecraft.server.v1_12_R1.PlayerConnection;

public class TabList extends JavaPlugin implements Listener{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static TabList plugin;
	
	public static WorldGuardPlugin worldGuardPlugin;
	public static WorldEditPlugin worldEditPlugin;
	
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginCommand("tablist").setExecutor(new PlayerCommand());
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll((JavaPlugin)this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		configurePlayer(player);
		event.setJoinMessage(ChatColor.RESET+"["+ChatColor.GREEN+"+"+ChatColor.RESET+"] "+player.getDisplayName());
		//setPlayerlistFooter(player, "Livemap: map.swisssmp.ch:8188");
	}
	@EventHandler
	private void onPlayerLeave(PlayerQuitEvent event){
		Player player = event.getPlayer();
		event.setQuitMessage(ChatColor.RESET+"["+ChatColor.RED+"-"+ChatColor.RESET+"] "+player.getDisplayName());
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onPlayerChat(AsyncPlayerChatEvent event){
		String displayName = event.getPlayer().getDisplayName();
		if(displayName.contains("[Gast]")){
			event.setFormat("%1$s"+ChatColor.RESET+ChatColor.GRAY+": "+ChatColor.RESET+"%2$s");
		}
		else{
			event.setFormat(ChatColor.RESET+"[%1$s"+ChatColor.RESET+"] %2$s");
		}
	}
	
	public static void configurePlayer(Player player){
		try {
			if(player==null) return;
			YamlConfiguration yamlConfiguration;
				yamlConfiguration = DataSource.getYamlResponse("tablist/info.php", new String[]{
					"player="+player.getUniqueId().toString(),
					"name="+URLEncoder.encode(player.getName(), "utf-8")
				});
			ConfigurationSection headerSection = yamlConfiguration.getConfigurationSection("header");
			String header = getChatString(headerSection);
			ConfigurationSection footerSection = yamlConfiguration.getConfigurationSection("footer");
			String footer = getChatString(footerSection);
			ConfigurationSection userSection = yamlConfiguration.getConfigurationSection("user");
			String user;
			String fullDisplayName;
			if(yamlConfiguration.getInt("rank")>1){
				user = getChatString(userSection);
				fullDisplayName = user+ChatColor.RESET;
			}
			else{
				user = userSection.getString("text");
				ChatColor color = ChatColor.valueOf(userSection.getString("color"));
				fullDisplayName = color+"[Gast]"+ChatColor.WHITE+" "+user+ChatColor.RESET;
			}
			if(debug){
				Bukkit.getLogger().info("Header: "+header);
				Bukkit.getLogger().info("Footer: "+footer);
				Bukkit.getLogger().info("Spielername: "+user);
			}
			player.setDisplayName(fullDisplayName);
			player.setPlayerListName(fullDisplayName);
			setHeaderFooter(player, header, footer);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String getChatString(ConfigurationSection dataSection){
		if(dataSection==null) return "";
		if(dataSection.getString("text")==null) return "";
		if(dataSection.getString("text").equals("null")) return "";
		ChatColor color = ChatColor.valueOf(dataSection.getString("color"));
		return color+dataSection.getString("text");
	}
	
	private static void setHeaderFooter(Player player, String header_text, String footer_text) {
	     
        CraftPlayer craftplayer = (CraftPlayer) player;
        PlayerConnection connection = craftplayer.getHandle().playerConnection;
        JsonObject jsonHeader = new JsonObject();
        JsonObject jsonFooter = new JsonObject();
        jsonHeader.addProperty("text", header_text);
        jsonFooter.addProperty("text", footer_text);
        IChatBaseComponent header = ChatSerializer.a(jsonHeader.toString());
        IChatBaseComponent footer = ChatSerializer.a(jsonFooter.toString());
        ByteBuf byteBuffer = ByteBufAllocator.DEFAULT.buffer(header_text.getBytes().length + footer_text.getBytes().length);

        PacketDataSerializer packetDataSerializer = new PacketDataSerializer(byteBuffer);

        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

        try {
            packetDataSerializer.a(header);
            packetDataSerializer.a(footer);
            packet.a(packetDataSerializer);

        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.sendPacket(packet);
    }
}
