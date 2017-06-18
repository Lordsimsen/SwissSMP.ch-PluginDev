package ch.swisssmp.craftshopkeepers;

import java.util.Arrays;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftShopkeepers extends JavaPlugin implements Listener{
	public Logger logger;
	public ItemStack shopkeeper;
	public Server server;
	public ShapedRecipe shopkeeperRecipe;
	protected NamespacedKey namespacedKey;
	
	public void onEnable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		server = getServer();
		namespacedKey = new NamespacedKey(this, "Marktplatz");
		
		server.getPluginManager().registerEvents(this, this);
		createRecipe();
		
	}
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	public void createRecipe(){
		logger = Logger.getLogger("Minecraft");
		MaterialData mat_data = new MaterialData(Material.MONSTER_EGG);
		shopkeeper = mat_data.toItemStack(1);
		shopkeeper.setDurability((short)120);
		ItemMeta meta = shopkeeper.getItemMeta();
		meta.setDisplayName("Händler");
		meta.setLore(Arrays.asList("Platziere diesen Händler", "auf einem Marktplatz,", "um einen Stand zu eröffnen."));
		shopkeeper.setItemMeta(meta);
		shopkeeperRecipe = new ShapedRecipe(namespacedKey, shopkeeper);
		shopkeeperRecipe.shape("eee","drd","eee");
		shopkeeperRecipe.setIngredient('d', Material.DIAMOND);
		shopkeeperRecipe.setIngredient('r', Material.ROTTEN_FLESH);
		shopkeeperRecipe.setIngredient('e', Material.EMERALD);
		server.addRecipe(shopkeeperRecipe);
		logger.info("Custom Recipe has been created");
	}
    @EventHandler(ignoreCancelled=true)
    public void craftItem(PrepareItemCraftEvent event) {
    	if(event.getRecipe()==null) return;
    	ItemStack result = event.getRecipe().getResult();
    	if(!result.hasItemMeta())
    		return;
    	if(!result.getItemMeta().hasLore())
    		return;
    	if(result.getItemMeta().getLore().hashCode() == shopkeeper.getItemMeta().getLore().hashCode()){
            HumanEntity human = event.getView().getPlayer();
            if(human instanceof Player){
            	Player crafter = (Player)human;
            	if(!crafter.hasPermission("craftshopkeepers.craft")){
            		event.getInventory().setResult(null);
            	}
            }
    	}
    }
}
