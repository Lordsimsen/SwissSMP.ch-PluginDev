package ch.swisssmp.utils;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public final class SwissSMPUtils extends JavaPlugin{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static File dataFolder;
	protected static SwissSMPUtils plugin;
	protected static boolean debug;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		
		//Bukkit.resetRecipes();
		
		Bukkit.getPluginManager().registerEvents(new SignEditorListener(), this);
		
		//checkAfk();
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static void broadcastMessage(String message){
		for(Player player : Bukkit.getOnlinePlayers()){
			player.sendMessage(message);
		}
	}
	
	public static String encodeItemStack(ItemStack itemStack){
		if(itemStack==null) return null;
		org.bukkit.configuration.file.YamlConfiguration yamlConfiguration;
		yamlConfiguration = new org.bukkit.configuration.file.YamlConfiguration();
		yamlConfiguration.set("item", itemStack);
		return Base64.encodeBase64URLSafeString(yamlConfiguration.saveToString().getBytes());
	}
	
	public static PluginSender getPluginSender(){
		return new PluginSender();
	}
}
