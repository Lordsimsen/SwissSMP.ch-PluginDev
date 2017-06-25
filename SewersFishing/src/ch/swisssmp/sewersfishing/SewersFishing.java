package ch.swisssmp.sewersfishing;

import java.util.Arrays;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import io.netty.util.internal.ThreadLocalRandom;
import net.md_5.bungee.api.ChatColor;

public class SewersFishing extends JavaPlugin implements Listener{
	public Logger logger;
	public Server server;
	public ThreadLocalRandom random;
	public static String[] poisonedFishLore;
	
	public void onEnable() {
		random = ThreadLocalRandom.current();
		PluginDescriptionFile pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		server = getServer();
		
		server.getPluginManager().registerEvents(this, this);
		poisonedFishLore = new String[]{"Schlabbrig und giftig...","Gourmet vom feinsten!"};
	}
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
    	Entity caught = event.getCaught();
		if(!(caught instanceof Item)){
			return;
		}
		//check if there is a toxic region nearby
		boolean toxicArea = false;
		ApplicableRegionSet regions = WorldGuardPlugin.inst().getRegionManager(caught.getWorld()).getApplicableRegions(caught.getLocation());
		for(ProtectedRegion region : regions){
			if(region.getId().toLowerCase().contains("kanalisation")){
				toxicArea = true;
				break;
			}
		}
		if(!toxicArea) return;
		//check what the player caught
    	Item item = (Item) caught;
    	ItemStack itemstack = item.getItemStack();
    	ItemMeta itemmeta = itemstack.getItemMeta();
    	if(itemstack.getType() != Material.RAW_FISH)
    		return;
		//At this point the fish must be poisoned
    	itemmeta.addEnchant(Enchantment.THORNS, 1, true);
    	itemmeta.setLore(Arrays.asList(poisonedFishLore));
    	itemstack.setItemMeta(itemmeta);
    	item.setItemStack(itemstack);
    }
    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event){
    	Player player = event.getPlayer();
    	ItemStack itemstack = event.getItem();
    	if(itemstack.getType() != Material.RAW_FISH)
    		return;
    	ItemMeta itemmeta = itemstack.getItemMeta();
    	if(!itemmeta.hasLore())
    		return;
    	else if(itemmeta.getLore().equals(Arrays.asList(poisonedFishLore))){
    		PotionEffectType[] poison_types = new PotionEffectType[]{
    				PotionEffectType.POISON, 
    				PotionEffectType.CONFUSION, 
    				PotionEffectType.BLINDNESS, 
    				PotionEffectType.HARM, 
    				PotionEffectType.GLOWING, 
    				PotionEffectType.HUNGER, 
    				PotionEffectType.SLOW, 
    				PotionEffectType.WEAKNESS, 
    				PotionEffectType.WITHER, 
    				PotionEffectType.UNLUCK,
    				PotionEffectType.INVISIBILITY,
    				PotionEffectType.SPEED
    				};
    		PotionEffect poisonEffect = new PotionEffect(poison_types[random.nextInt(0, poison_types.length+1)], random.nextInt(5, 900)*20, 4, true, true);
    		player.addPotionEffect(poisonEffect);
    		server.broadcastMessage(player.getDisplayName()+ChatColor.GRAY+" musste unbedingt am verdorbenen Fisch knabbern...");
    	}
    }
}
