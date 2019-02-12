package ch.swisssmp.tablist;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonObject;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_13_R2.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_13_R2.PacketDataSerializer;
import net.minecraft.server.v1_13_R2.PlayerConnection;

public class TabList extends JavaPlugin implements Listener{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static TabList plugin;
	
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		Bukkit.getPluginManager().registerEvents(this, this);
		if(Bukkit.getPluginManager().getPlugin("PermissionManager")!=null){
			Bukkit.getPluginManager().registerEvents(new PermissionsListener(), this);
		}
		Bukkit.getPluginCommand("tablist").setExecutor(new PlayerCommand());
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll((JavaPlugin)this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerJoin(PlayerJoinEvent event){
		event.setJoinMessage("");
		//setPlayerlistFooter(player, "Livemap: map.swisssmp.ch:8188");
	}
	@EventHandler
	private void onPlayerLeave(PlayerQuitEvent event){
		Player player = event.getPlayer();
		event.setQuitMessage(ChatColor.RESET+"["+ChatColor.RED+"-"+ChatColor.RESET+"] "+player.getDisplayName());
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onPlayerChat(AsyncPlayerChatEvent event){
		event.setFormat(ChatColor.RESET+"[%1$s"+ChatColor.RESET+"] %2$s");
	}
	
	public static HTTPRequest configurePlayer(Player player){
		return configurePlayer(player, false);
	}
	
	public static HTTPRequest configurePlayer(Player player, boolean joining){
		if(player==null) return null;
		HTTPRequest request = DataSource.getResponse(plugin, "info.php", new String[]{
				"player="+player.getUniqueId().toString(),
				"name="+URLEncoder.encode(player.getName())
			});
		request.onFinish(()->{
			configurePlayer(request.getYamlResponse(), player);
			if(!joining) return;
			Bukkit.broadcastMessage(ChatColor.RESET+"["+ChatColor.GREEN+"+"+ChatColor.RESET+"] "+player.getDisplayName());
		});
		return request;
	}
	
	private static void configurePlayer(YamlConfiguration yamlConfiguration, Player player){
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
