package ch.swisssmp.flyinglanterns;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;

public class FlyingLanterns extends JavaPlugin{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static FlyingLanterns plugin;
	private static CustomItemBuilder lanternBuilder;
	
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		
		this.createLanternBuilder();
		this.registerRecipe();
		this.loadAllLanterns();

		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	protected static ItemStack getFlyingLantern(){
		return lanternBuilder.build();
	}
	
	protected static boolean isFlyingLantern(Entity entity){
		ArmorStand armorStand;
		String customEnum;
		if(!(entity instanceof ArmorStand)){
			return false;
		}
		armorStand = (ArmorStand) entity;
		if(armorStand.getEquipment().getHelmet()==null){
			return false;
		}
		customEnum = CustomItems.getCustomEnum(armorStand.getEquipment().getHelmet());
		if(customEnum==null || !customEnum.equals("FLYING_LANTERN")){
			return false;
		}
		return true;
	}
	
	private void loadAllLanterns(){
		for(World world : Bukkit.getWorlds()){
			for(Entity entity : world.getEntities()){
				if(!FlyingLanterns.isFlyingLantern(entity)) continue;
				FlyingLantern.load(entity);
			}
		}
	}
	
	private void createLanternBuilder(){
		lanternBuilder = CustomItems.getCustomItemBuilder("FLYING_LANTERN");
		lanternBuilder.setDisplayName(ChatColor.WHITE+"Fliegende Laterne");
		lanternBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		lanternBuilder.setAmount(1);
	}
	
	private void registerRecipe(){
		ItemStack lantern = FlyingLanterns.getFlyingLantern();
		ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, "flyinglanterns.lantern"), lantern);
		recipe.shape("ppp", "p p", "ptp");
		recipe.setIngredient('p', Material.PAPER);
		recipe.setIngredient('t', Material.TORCH);
		Bukkit.addRecipe(recipe);
	}
}
