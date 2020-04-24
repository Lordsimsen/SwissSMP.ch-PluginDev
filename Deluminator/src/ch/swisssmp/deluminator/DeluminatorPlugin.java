package ch.swisssmp.deluminator;

import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Lightable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;

public class DeluminatorPlugin extends JavaPlugin{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static DeluminatorPlugin plugin;
	
	protected static cz.ceph.lampcontrol.Main lampControl;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		Bukkit.getPluginCommand("deluminator").setExecutor(new DeluminatorCommand());
		Plugin lampControlPlugin = Bukkit.getPluginManager().getPlugin("LampControl");
		if(lampControlPlugin==null) return;
		lampControl = (cz.ceph.lampcontrol.Main) lampControlPlugin;
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	protected static ItemStack getItem(){
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder("DELUMINATOR");
		itemBuilder.setAmount(1);
		itemBuilder.setAttackDamage(0);
		itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemBuilder.setDisplayName(ChatColor.LIGHT_PURPLE+"Deluminator");
		ItemStack result = itemBuilder.build();
		ItemUtil.setString(result, "deluminator_id", UUID.randomUUID().toString());
		return result;
	}
	
	protected static void igniteLamp(Block lamp){
		if(lamp.getType()!=Material.REDSTONE_LAMP) return;
		Lightable lightable = (Lightable) lamp.getBlockData();
		if(lightable.isLit()) return;
		lightable.setLit(true);
		lamp.setBlockData(lightable, false);
	}
	
	protected static void extinguishLamp(Block lamp){
		if(lamp.getType()!=Material.REDSTONE_LAMP) return;
		Lightable lightable = (Lightable) lamp.getBlockData();
		if(!lightable.isLit()) return;
		lightable.setLit(false);
		lamp.setBlockData(lightable, false);
	}
}
